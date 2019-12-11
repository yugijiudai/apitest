package com.lml.apitest.ext;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.dto.RequestContentDto;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.RequestStatusEnum;
import com.lml.apitest.exception.InitException;
import com.lml.apitest.exception.RequestException;
import com.lml.apitest.po.RequestContent;
import com.lml.apitest.service.RequestContentService;
import com.lml.apitest.service.RequestContentServiceImpl;
import com.lml.apitest.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote 底层使用hutool的httpUtil来调用
 * @since 2019-08-15
 */
@Slf4j
public class HttpExt implements ReqExt {


    private RequestContentService requestContentServiceImpl = new RequestContentServiceImpl();

    @Override
    public <T> RestVo<T> postForForm(RequestDto requestDto, Class<T> returnType) {
        HttpRequest post = HttpRequest.post(requestDto.getUrl());
        // this.setRequestHeader(headers, post);
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        // 处理上传文件,这里会改变reqObj,所以要重新设置到param里
        post = this.handleUploadFile(requestDto.getFile(), post, reqObj);
        requestDto.setParam(JSONUtil.toJsonStr(reqObj));
        HttpResponse execute = doFormRequest(post, requestDto);
        return afterReq(returnType, execute);
    }


    @Override
    public <T> RestVo<T> post(RequestDto requestDto, Class<T> returnType) {
        HttpRequest post = HttpRequest.post(requestDto.getUrl());
        HttpResponse execute = doJsonRequest(post, requestDto);
        return afterReq(returnType, execute);
    }


    @Override
    public <T> RestVo<T> get(RequestDto requestDto, Class<T> returnType) {
        HttpRequest get = HttpRequest.get(requestDto.getUrl());
        HttpResponse execute = doFormRequest(get, requestDto);
        return afterReq(returnType, execute);
    }

    @Override
    public <T> RestVo<T> put(RequestDto requestDto, Class<T> returnType) {
        HttpRequest put = HttpRequest.put(requestDto.getUrl());
        HttpResponse execute = doJsonRequest(put, requestDto);
        return afterReq(returnType, execute);
    }

    @Override
    public <T> RestVo<T> delete(RequestDto requestDto, Class<T> returnType) {
        HttpRequest delete = HttpRequest.delete(requestDto.getUrl());
        HttpResponse execute = doFormRequest(delete, requestDto);
        return afterReq(returnType, execute);
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
        return requestContentDto.setContent(requestDto.getParam()).setName(requestDto.getName()).setHeaders(requestDto.getHeaders()).setUrl(httpRequest.getUrl()).setMethod(httpRequest.getMethod());
    }

    /**
     * 真正触发请求
     *
     * @param httpRequest       {@link HttpRequest}
     * @param requestContentDto 请求的dto
     * @return 返回请求的结果
     */
    private HttpResponse exe(HttpRequest httpRequest, RequestContentDto requestContentDto) {
        long start = System.currentTimeMillis();
        requestContentDto.setStartTime(DateUtil.date(start));
        // 请求前记录
        RequestContent requestContent = requestContentServiceImpl.beforeRequest(requestContentDto);
        HttpResponse execute;
        RequestContent update = new RequestContent().setId(requestContent.getId());
        try {
            execute = httpRequest.execute();
            log.debug("{}请求消耗了:{}ms", httpRequest.getUrl(), System.currentTimeMillis() - start);
            update.setRequestStatus(RequestStatusEnum.OK);
        }
        catch (Throwable e) {
            // 设置异常信息
            update.setExceptionMsg(ExceptionUtil.stacktraceToString(e)).setRequestStatus(RequestStatusEnum.FAIL);
            log.error(e.getMessage(), e);
            throw new RequestException(e);
        }
        finally {
            // 设置请求结束时间
            requestContentServiceImpl.afterRequest(update.setEndTime(new Date()));
        }
        return execute;
    }


    /**
     * 设置请求的头部
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
    private HttpHeaders setResponseHeader(HttpResponse execute) {
        Map<String, List<String>> responseHeaders = execute.headers();
        Map<String, List<String>> newResponseHeaders = Maps.newHashMap();
        responseHeaders.forEach((key, val) -> {
            // 过滤掉key为null的头部
            if (StringUtils.isNotBlank(key)) {
                newResponseHeaders.put(key, val);
            }
        });
        HttpHeaders resHeader = new HttpHeaders();
        resHeader.putAll(newResponseHeaders);
        return resHeader;
    }

    /**
     * 请求之后的处理
     *
     * @param returnType 请求返回的数据需要转成的类型
     * @param execute    请求返回的数据
     * @param <T>        要转成的类型
     * @return {@link RestVo}
     */
    @SuppressWarnings("unchecked")
    private <T> RestVo<T> afterReq(Class<T> returnType, HttpResponse execute) {
        RestVo<T> restVo = new RestVo<>();
        String body = execute.body();
        HttpHeaders resHeader = this.setResponseHeader(execute);
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
     * @param map        请求参数
     * @return {@link HttpRequest}
     */
    private HttpRequest handleUploadFile(Map<String, Object> uploadFile, HttpRequest post, Map<String, Object> map) {
        if (uploadFile == null) {
            return post;
        }
        if (uploadFile.keySet().size() != 1) {
            throw new InitException("上传文件的格式不对!");
        }
        for (Map.Entry<String, Object> entry : uploadFile.entrySet()) {
            JSONArray uploadFiles = JSONUtil.parseArray(entry.getValue());
            // 获取所有的上传文件
            List<File> files = uploadFiles.stream().map(fileName -> {
                URL resource = ResourceUtil.getResource(fileName.toString());
                return FileUtil.file(resource);
            }).collect(Collectors.toList());
            // 这里需要把list转成数组,因为参数是可变数组,不转会报错
            post.form(entry.getKey(), ArrayUtil.toArray(files, File.class));
        }
        // 把上传文件的参数放在请求上面,用于记录到数据库,暂时没发现问题
        map.putAll(uploadFile);
        return post;
    }


}
