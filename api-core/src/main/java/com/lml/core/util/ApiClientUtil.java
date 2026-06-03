package com.lml.core.util;

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
import com.lml.core.dto.SettingDto;
import com.lml.core.enums.MethodEnum;
import com.lml.core.exception.BizException;
import com.lml.core.exception.InitException;
import com.lml.core.exception.RequestException;
import com.lml.core.handler.RequestCallBackHandler;
import com.lml.core.holder.ReqHolder;
import com.lml.core.service.RequestSubject;
import com.lml.core.vo.RestVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote http请求的客户端工具类, 用来加载脚本文件自动发出请求, 或者直接传构造好的json对象用于直接请求
 * @since 2026-06-03
 */
@UtilityClass
@Slf4j
public class ApiClientUtil {

    /**
     * 脚本request字段
     */
    public final String REQ_KEY = "request";

    /**
     * 脚本response字段
     */
    private final String RES_KEY = "response";

    /**
     * 脚本请求头字段
     */
    public final String HEADER = "headers";

    /**
     * 请求header的cookie字段
     */
    private final String COOKIE_KEY = "Set-Cookie";


    /**
     * 根据脚本配置,加载好脚本并且进行接口的请求
     *
     * @param fileName      要加载的脚本路径
     * @param callBackLists 请求接口后需要执行的回调,是个list,可以自己定义然后回调的处理顺序
     */
    public void doApiRequestCallBack(String fileName, List<RequestCallBackHandler> callBackLists) {
        JSONObject json = InitUtil.loadReqContent(fileName);
        doApiRequestCallBack(json, callBackLists);
    }

    /**
     * 直接传加载好的脚本并且进行接口的请求
     *
     * @param json          加载好的脚本
     * @param callBackLists 请求接口后需要执行的回调,是个list,可以自己定义然后回调的处理顺序
     */
    public void doApiRequestCallBack(JSONObject json, List<RequestCallBackHandler> callBackLists) {
        RestVo<JSONObject> actual = doApiRequest(json);
        // 获取断言的数据
        String response = json.getStr(RES_KEY);
        JSONObject expectJson = JSONUtil.parseObj(response);
        log.info("断言的数据是:{}", expectJson);
        // 进行回调处理
        if (CollectionUtils.isNotEmpty(callBackLists)) {
            for (RequestCallBackHandler requestCallBackHandler : callBackLists) {
                requestCallBackHandler.doCallBack(actual, expectJson);
            }
        }
    }

    /**
     * 读取请求脚本进行请求
     *
     * @param script 脚本地址
     * @return 返回请求后的数据
     */
    public RestVo<JSONObject> doApiRequest(String script) {
        JSONObject json = InitUtil.loadReqContent(script);
        return doApiRequest(json, JSONObject.class);
    }

    /**
     * 传封装好的请求对象用于直接请求,最底层的的请求,所有方法请求最终会调用这个方法,没有回调函数,需要根据实际自己断言
     *
     * @param json 请求的对象
     * @return 返回请求后的数据
     */
    public RestVo<JSONObject> doApiRequest(JSONObject json) {
        return doApiRequest(json, JSONObject.class);
    }

    /**
     * 传封装好的请求对象用于直接请求,最底层的的请求,所有方法请求最终会调用这个方法,没有回调函数,需要根据实际自己断言
     *
     * @param json       请求的对象
     * @param returnType 返回结果类型
     * @return 返回请求后的数据
     */
    public <T> RestVo<T> doApiRequest(JSONObject json, Class<T> returnType) {
        RequestDto requestDto = buildRequestDto(json);
        MethodEnum method = MethodEnum.parse(requestDto.getMethod());
        return switch (method) {
            case POST -> post(requestDto, returnType);
            case POST_FROM_DATA -> postForForm(requestDto, returnType);
            case GET -> get(requestDto, returnType);
            case PUT -> put(requestDto, returnType);
            case DELETE -> delete(requestDto, returnType);
        };
    }


    /**
     * 根据给定的key值,从请求的头部获取对应的cookie值
     *
     * @param httpHeaders 请求头部
     * @param key         cookie的key
     * @return 符合这个cookie的key的值列表
     */
    public List<Object> getCookieByKey(Map<String, List<String>> httpHeaders, String key) {
        ListValuedMap<String, Object> map = transCookieToMap(httpHeaders);
        List<Object> values = map.get(key);
        if (CollectionUtils.isEmpty(values)) {
            throw new BizException("没有" + key + "这个cookie!");
        }
        return values;
    }

    /**
     * 将cookie转成多值类型的map
     *
     * @param httpHeaders 请求头部
     * @return 返回一个多值类型的map
     */
    public ListValuedMap<String, Object> transCookieToMap(Map<String, List<String>> httpHeaders) {
        ListValuedMap<String, Object> map = MultiMapUtils.newListValuedHashMap();
        List<String> cookies = httpHeaders.get(COOKIE_KEY);
        if (CollectionUtils.isEmpty(cookies)) {
            return map;
        }
        for (String cookie : cookies) {
            String[] split = cookie.split(";");
            for (String tmp : split) {
                String[] values = tmp.trim().split("=", 2);
                map.put(values[0], values.length != 2 ? null : values[1]);
            }
        }
        return map;
    }

    /**
     * 使用multipart/form-data方式提交post请求,支持携带上传文件
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回来的类型
     * @param <T>        要转成的类型
     * @return 返回结果
     */
    public <T> RestVo<T> postForForm(RequestDto requestDto, Class<T> returnType) {
        HttpRequest post = handleUploadFile(requestDto.getFile(), HttpRequest.post(requestDto.getUrl()));
        return executeAndBuildResponse(post, requestDto, false, returnType);
    }


    /**
     * 使用post body方式提交
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回来的类型
     * @return 返回结果
     */
    public <T> RestVo<T> post(RequestDto requestDto, Class<T> returnType) {
        return executeAndBuildResponse(HttpRequest.post(requestDto.getUrl()), requestDto, true, returnType);
    }

    /**
     * 使用get方式提交
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回来的类型
     * @return 返回结果
     */
    public <T> RestVo<T> get(RequestDto requestDto, Class<T> returnType) {
        return executeAndBuildResponse(HttpRequest.get(requestDto.getUrl()), requestDto, false, returnType);
    }

    /**
     * 使用put方式提交
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回来的类型
     * @return 返回结果
     */
    public <T> RestVo<T> put(RequestDto requestDto, Class<T> returnType) {
        return executeAndBuildResponse(HttpRequest.put(requestDto.getUrl()), requestDto, true, returnType);
    }

    /**
     * 使用delete方式提交
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回来的类型
     * @return 返回结果
     */
    public <T> RestVo<T> delete(RequestDto requestDto, Class<T> returnType) {
        return executeAndBuildResponse(HttpRequest.delete(requestDto.getUrl()), requestDto, false, returnType);
    }

    /**
     * 从脚本json中构建请求对象,并按配置补全相对路径的baseUrl
     *
     * @param json 脚本请求内容
     * @return 构建好的请求对象
     */
    private RequestDto buildRequestDto(JSONObject json) {
        RequestDto requestDto = JSONUtil.toBean(json.getStr(REQ_KEY), RequestDto.class);
        if (requestDto.isUseRelativeUrl()) {
            SettingDto settingDto = InitUtil.getSettingDto();
            requestDto.setUrl(settingDto.getBaseUrl() + requestDto.getUrl());
        }
        return requestDto;
    }

    /**
     * 执行请求并将响应体封装成指定类型的RestVo
     *
     * @param req        hutool请求对象
     * @param requestDto {@link RequestDto}
     * @param jsonBody   是否使用json body方式提交参数,true为json提交,false为form提交
     * @param returnType 返回来的类型
     * @param <T>        要转成的类型
     * @return 返回结果
     */
    private <T> RestVo<T> executeAndBuildResponse(HttpRequest req, RequestDto requestDto, boolean jsonBody, Class<T> returnType) {
        HttpResponse execute = jsonBody ? doJsonRequest(req, requestDto) : doFormRequest(req, requestDto);
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
        setRequestHeader(headers, req);
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
        setRequestHeader(requestDto.getHeaders(), req);
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
        return new RequestContentDto()
                .setContent(requestDto.getParam())
                .setName(requestDto.getName())
                .setHeaders(requestDto.getHeaders())
                .setUrl(httpRequest.getUrl())
                .setMethod(httpRequest.getMethod())
                .setRequestGroup(requestDto.getRequestGroup());
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
        setUploadFileParam(httpRequest, requestContentDto);
        setTraceId(requestContentDto);
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
                List<String> cookieList = getCookieList(val);
                post.header(HttpHeaders.COOKIE, cookieList.toString());
                // 这里要设置cookie,因为这个http请求框架不会把header的cookie当成是真的cookie
                String cookie = toCookieString(cookieList);
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
        Map<String, List<String>> resHeader = Maps.newLinkedHashMap();
        execute.headers().forEach((key, val) -> {
            if (StringUtils.isNotBlank(key)) {
                resHeader.put(key, val);
            }
        });
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
        Map<String, List<String>> resHeader = setResponseHeader(execute);
        restVo.setHttpHeaders(resHeader);
        // 如果返回的内容是json格式,则把他转成对应的json对象类
        if (!JSONUtil.isTypeJSONObject(body)) {
            return restVo.setResult((T) body);
        }
        if (JSONUtil.isTypeJSONArray(body)) {
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
        if (uploadFile.size() != 1) {
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


    /**
     * 获得cookieList,因为要传cookie,header需要这样设置:requestHeaders.put(HttpHeaders.COOKIE, cookieList)
     *
     * @param cookiesJsonArr cookie的json数组字符串
     * @return 返回设置好的格式, 格式如下:["JSESSIONID=xxx", "name=lml"]
     */
    private List<String> getCookieList(Object cookiesJsonArr) {
        List<com.alibaba.fastjson.JSONObject> array = com.alibaba.fastjson.JSONArray.parseArray(cookiesJsonArr.toString(), com.alibaba.fastjson.JSONObject.class);
        return array.stream()
                .flatMap(obj -> obj.entrySet().stream())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toList());
    }

    /**
     * 获得cookie
     *
     * @param cookieList cookie列表
     * @return 返回设置好的格式, 格式如下:"TITAN_SESSION_ID=sss; TITAN_ACCID=910
     */
    private String toCookieString(List<String> cookieList) {
        return String.join(";", cookieList);
    }


}
