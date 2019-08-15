package com.lml.apitest.ext;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

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
    public <T> RestVo<T> postForForm(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        HttpRequest post = HttpRequest.post(url);
        this.setRequestHeader(headers, post);
        Map<String, Object> map = Maps.newHashMap();
        BeanUtil.copyProperties(obj, map);
        HttpResponse execute = post.form(map).execute();
        return afterReq(returnType, execute);
    }


    @Override
    public <T> RestVo<T> post(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        HttpRequest post = HttpRequest.post(url);
        HttpResponse execute = doJsonRequest(post, headers, obj);
        return afterReq(returnType, execute);
    }


    @Override
    public <T> RestVo<T> get(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        HttpRequest get = HttpRequest.get(url);
        HttpResponse execute = doFormRequest(get, headers, params);
        return afterReq(returnType, execute);
    }

    @Override
    public <T> RestVo<T> put(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        HttpRequest put = HttpRequest.put(url);
        HttpResponse execute = doJsonRequest(put, headers, obj);
        return afterReq(returnType, execute);
    }

    @Override
    public <T> RestVo<T> delete(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        HttpRequest delete = HttpRequest.delete(url);
        HttpResponse execute = doFormRequest(delete, headers, params);
        return afterReq(returnType, execute);
    }

    /**
     * 使用form方式提交
     *
     * @param req     {@link HttpRequest}
     * @param headers 对应的头部
     * @param params  请求的参数
     * @return {@link HttpResponse}
     */
    private HttpResponse doFormRequest(HttpRequest req, Map<String, Object> headers, Map<String, Object> params) {
        this.setRequestHeader(headers, req);
        return req.form(params).execute();
    }

    /**
     * 使用json方式提交
     *
     * @param req     {@link HttpRequest}
     * @param headers 对应的头部
     * @param obj     请求的参数
     * @return {@link HttpResponse}
     */
    private HttpResponse doJsonRequest(HttpRequest req, Map<String, Object> headers, Object obj) {
        this.setRequestHeader(headers, req);
        return req.body(JSONUtil.toJsonStr(obj)).execute();
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
        // 如果返回的内容是json格式,则把他转成对应的json对象类
        T result = JSONUtil.isJson(body) ? JSONUtil.toBean(body, returnType) : (T) body;
        return restVo.setHttpHeaders(resHeader).setResult(result);
    }


}
