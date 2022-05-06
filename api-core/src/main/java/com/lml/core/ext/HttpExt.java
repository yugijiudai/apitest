package com.lml.core.ext;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.MultiFileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.lml.core.dto.RequestContentDto;
import com.lml.core.dto.RequestDto;
import com.lml.core.exception.InitException;
import com.lml.core.exception.RequestException;
import com.lml.core.holder.ReqHolder;
import com.lml.core.service.RequestSubject;
import com.lml.core.util.InitUtil;
import com.lml.core.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 底层使用hutool的httpUtil来调用
 * @since 2019-08-15
 */
@Slf4j
public class HttpExt implements ReqExt {


    @Override
    public <T> RestVo<T> postForForm(RequestDto requestDto, Class<T> returnType) {
        HttpRequest post = HttpRequest.post(requestDto.getUrl());
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        // 处理上传文件,这里会改变reqObj,所以要重新设置到param里
        post = this.handleUploadFile(requestDto.getFile(), post);
        requestDto.setParam(JSONUtil.toJsonStr(reqObj));
        HttpResponse execute = doFormRequest(post, requestDto);
        return setResponseResult(returnType, execute);
    }


    @Override
    public <T> RestVo<T> post(RequestDto requestDto, Class<T> returnType) {
        HttpRequest post = HttpRequest.post(requestDto.getUrl());
        HttpResponse execute = doJsonRequest(post, requestDto);
        return setResponseResult(returnType, execute);
    }


    @Override
    public <T> RestVo<T> get(RequestDto requestDto, Class<T> returnType) {
        HttpRequest get = HttpRequest.get(requestDto.getUrl());
        HttpResponse execute = doFormRequest(get, requestDto);
        return setResponseResult(returnType, execute);
    }

    @Override
    public <T> RestVo<T> put(RequestDto requestDto, Class<T> returnType) {
        HttpRequest put = HttpRequest.put(requestDto.getUrl());
        HttpResponse execute = doJsonRequest(put, requestDto);
        return setResponseResult(returnType, execute);
    }

    @Override
    public <T> RestVo<T> delete(RequestDto requestDto, Class<T> returnType) {
        HttpRequest delete = HttpRequest.delete(requestDto.getUrl());
        HttpResponse execute = doFormRequest(delete, requestDto);
        return setResponseResult(returnType, execute);
    }

    /**
     * 使用form方式提交
     *
     * @param req        {@link HttpRequest}
     * @param requestDto {@link RequestDto}
     * @return {@link HttpResponse}
     */
    private HttpResponse doFormRequest(HttpRequest req, RequestDto requestDto) {
        JSONObject headers = requestDto.getHeaders();
        this.setRequestHeader(headers, req);
        JSONObject params = JSONUtil.parseObj(requestDto.getParam());
        RequestContentDto requestContentDto = buildRequestContentDtoCommon(requestDto, req);
        return exe(req.form(params), requestContentDto);
    }


    /**
     * 使用json方式提交
     *
     * @param req        {@link HttpRequest}
     * @param requestDto {@link RequestDto}
     * @return {@link HttpResponse}
     */
    private HttpResponse doJsonRequest(HttpRequest req, RequestDto requestDto) {
        this.setRequestHeader(requestDto.getHeaders(), req);
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        String content = JSONUtil.toJsonStr(reqObj);
        RequestContentDto requestContentDto = buildRequestContentDtoCommon(requestDto, req);
        return exe(req.body(content), requestContentDto);
    }

    /**
     * 构建公共的头部,内容,方法,url等公共参数
     *
     * @param requestDto  {@link RequestDto}
     * @param httpRequest 请求的对象
     * @return 返回构造好的dto
     */
    private RequestContentDto buildRequestContentDtoCommon(RequestDto requestDto, HttpRequest httpRequest) {
        RequestContentDto requestContentDto = new RequestContentDto();
        return requestContentDto.setContent(requestDto.getParam()).setName(requestDto.getName()).setHeaders(requestDto.getHeaders()).setUrl(httpRequest.getUrl())
                .setMethod(httpRequest.getMethod()).setRequestGroup(requestDto.getRequestGroup());
    }

    /**
     * 真正触发请求
     *
     * @param httpRequest       {@link HttpRequest}
     * @param requestContentDto 请求的dto
     * @return 返回请求的结果
     */
    private HttpResponse exe(HttpRequest httpRequest, RequestContentDto requestContentDto) {
        // 解析上传文件的参数,把resource的路径提取出来放在requestContentDto中,旧版的hutool可以把识别到字符串路径当成文件去处理,新版则不可以,所以不能在handleUploadFile的请求参数中设置成字符串的路径,需要在这里重新解析,存入数据库中
        this.setUploadFileParam(httpRequest, requestContentDto);
        this.setTraceId(requestContentDto);
        // 通知需要执行请求前的所有类进行相关操作
        RequestSubject requestSubject = InitUtil.getRequestSubject();
        requestSubject.notifyBeforeRequest(requestContentDto);
        HttpResponse execute;
        try {
            execute = httpRequest.execute();
            // 请求成功的操作
            requestSubject.notifySuccessRequest(requestContentDto);
        }
        catch (Throwable e) {
            // 请求失败的操作
            requestSubject.notifyFailRequest(requestContentDto, e);
            log.error(e.getMessage(), e);
            throw new RequestException(e);
        }
        finally {
            // 请求完成,无论失败或者成功都必须执行的操作
            requestSubject.notifyAfterRequest(requestContentDto);
        }
        return execute;
    }

    /**
     * 添加链路追踪id
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    private void setTraceId(RequestContentDto requestContentDto) {
        String requestGroup = requestContentDto.getRequestGroup();
        // 添加链路追踪id
        requestGroup = requestGroup != null ? requestGroup : ReqHolder.getTraceId();
        requestContentDto.setRequestGroup(requestGroup);
    }


    /**
     * 设置请求的头部,请求头不能有中文,否则需要转码,如果要转码,服务器获取的时候也是需要转码的
     *
     * @param headers 需要设置的头部
     * @param post    请求对象
     */
    private void setRequestHeader(Map<String, Object> headers, HttpRequest post) {
        if (MapUtils.isEmpty(headers)) {
            return;
        }
        for (Map.Entry<String, Object> header : headers.entrySet()) {
            String key = header.getKey();
            Object val = header.getValue();
            if (HttpHeaders.COOKIE.equals(key)) {
                post.header(HttpHeaders.COOKIE, getCookieList(val).toString());
                // 这里要设置cookie,因为这个http请求框架不会把header的cookie当成是真的cookie
                String cookie = toCookieList(val);
                log.debug("要传输的cookies如下:{}", cookie);
                post.cookie(cookie);
                continue;
            }
            post.header(key, val.toString());
        }
        log.debug("要传输的headers如下:{}", headers);
    }


    /**
     * 处理结果集的头部
     *
     * @param execute 请求响应体
     * @return 返回设置好的头部
     */
    private Map<String, List<String>> setResponseHeader(HttpResponse execute) {
        Map<String, List<String>> responseHeaders = execute.headers();
        Map<String, List<String>> newResponseHeaders = Maps.newHashMap();
        responseHeaders.forEach((key, val) -> {
            // 过滤掉key为null的头部
            if (StringUtils.isNotBlank(key)) {
                newResponseHeaders.put(key, val);
            }
        });
        Map<String, List<String>> resHeader = Maps.newLinkedHashMap();
        resHeader.putAll(newResponseHeaders);
        return resHeader;
    }

    /**
     * 设置返回的结果
     *
     * @param returnType 请求返回的数据需要转成的类型
     * @param execute    请求返回的数据
     * @param <T>        要转成的类型
     * @return {@link RestVo}
     */
    @SuppressWarnings("unchecked")
    private <T> RestVo<T> setResponseResult(Class<T> returnType, HttpResponse execute) {
        RestVo<T> restVo = new RestVo<>();
        String body = execute.body();
        Map<String, List<String>> resHeader = this.setResponseHeader(execute);
        restVo.setHttpHeaders(resHeader);
        // 如果返回的内容是json格式,则把他转成对应的json对象类
        if (!JSONUtil.isJson(body)) {
            return restVo.setResult((T) body);
        }
        if (JSONUtil.isJsonArray(body)) {
            return restVo.setResult((T) JSONUtil.parseArray(body));
        }
        return restVo.setResult(JSONUtil.toBean(body, returnType));
    }


    /**
     * 处理上传文件
     *
     * @param uploadFile 上传文件的参数
     * @param post       {@link HttpRequest}
     * @return {@link HttpRequest}
     */
    private HttpRequest handleUploadFile(Map<String, Object> uploadFile, HttpRequest post) {
        if (uploadFile == null) {
            return post;
        }
        if (uploadFile.keySet().size() != 1) {
            throw new InitException("上传文件的格式不对!");
        }
        for (Map.Entry<String, Object> entry : uploadFile.entrySet()) {
            JSONArray uploadFiles = JSONUtil.parseArray(entry.getValue());
            // 获取所有的上传文件
            File[] files = uploadFiles.stream().map(fileName -> FileUtil.file(ResourceUtil.getResource(fileName.toString()))).toArray(File[]::new);
            post.form(entry.getKey(), files);
        }
        log.debug("上传请求的参数是:{}", post.fileForm());
        return post;
    }


    /**
     * 设置上传文件的的参数,把resource解析成字符串路径
     *
     * @param post              请求对象
     * @param requestContentDto {@link RequestContentDto}
     */
    private void setUploadFileParam(HttpRequest post, RequestContentDto requestContentDto) {
        // 这里不能用post.fileForm()来判断,如果不是上传的请求会有空指针
        if (post.form() == null) {
            return;
        }
        Map<String, Resource> fileForm = post.fileForm();
        // 需要请求的参数
        JSONObject requestJson = JSONUtil.parseObj(requestContentDto.getContent());
        for (Map.Entry<String, Resource> entry : fileForm.entrySet()) {
            MultiFileResource fileResource = (MultiFileResource) entry.getValue();
            List<Object> fileUrlList = Lists.newArrayList();
            fileResource.forEach(resource -> fileUrlList.add(resource.getUrl().getPath()));
            requestJson.set(entry.getKey(), fileUrlList);
        }
        // 把上传的参数重新设置回到content里
        requestContentDto.setContent(requestJson.toString());
    }


}
