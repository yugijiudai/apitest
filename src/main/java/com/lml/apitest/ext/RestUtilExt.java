package com.lml.apitest.ext;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 底层使用RestTemplate去请求
 * @since 2019-08-06
 */
@Slf4j
public class RestUtilExt implements ReqExt {


    private RestTemplate restTemplate = new RestTemplate();


    /**
     * post方法,使用formData格式来传参(自定义请求头)
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @param headers    请求头
     * @param uploadFile 上传的文件
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> postForForm(String url, Object obj, Class<T> returnType, Map<String, Object> headers, Map<String, Object> uploadFile) {
        Map<String, Object> map = Maps.newHashMap();
        BeanUtil.copyProperties(obj, map);
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            formData.add(entry.getKey(), entry.getValue());
        }
        return request(url, HttpMethod.POST, formData, returnType, headers);
    }


    /**
     * post方法,使用json格式来传参,后端需要用@requestBody来接受(自定义请求头)
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> post(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        return request(url, HttpMethod.POST, obj, returnType, headers);
    }


    /**
     * get方法(没有请求头)
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param params     请求的参数
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> get(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        return request(url, HttpMethod.GET, params, returnType, headers);
    }


    /**
     * put方法(有请求头)
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> put(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        return request(url, HttpMethod.PUT, obj, returnType, headers);
    }


    /**
     * delete方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> delete(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        return request(url, HttpMethod.DELETE, params, returnType, headers);
    }


    /**
     * 发送请求(post,put调用)
     *
     * @param url        要请求的url
     * @param method     请求方法
     * @param returnType 返回的类型
     * @param headers    要设置的头部
     */
    private <T> RestVo<T> request(String url, HttpMethod method, Object obj, Class<T> returnType, Map<String, Object> headers) {
        //获取header信息
        HttpHeaders requestHeaders = buildHttpHeaders(headers);
        HttpEntity<Object> requestEntity = new HttpEntity<>(obj, requestHeaders);
        return handleRequest(url, method, returnType, requestEntity);
    }

    /**
     * 发送请求(get,delete调用)
     *
     * @param url        要请求的url
     * @param method     请求方法
     * @param params     请求的参数(会拼接到url上)
     * @param returnType 返回的类型
     * @param headers    要设置的头部
     */
    private <T> RestVo<T> request(String url, HttpMethod method, Map<String, Object> params, Class<T> returnType, Map<String, Object> headers) {
        //获取header信息
        HttpHeaders requestHeaders = buildHttpHeaders(headers);
        // 发送请求参数
        if (MapUtils.isNotEmpty(params)) {
            url = HttpUtil.urlWithForm(url, params, StandardCharsets.UTF_8, true);
            log.info("请求参数:{}", url);
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, requestHeaders);
        return handleRequest(url, method, returnType, requestEntity);
    }

    /**
     * 处理请求,并且设置好返回的参数
     *
     * @param url           要请求的url
     * @param method        请求方法
     * @param returnType    返回的类型
     * @param requestEntity 请求结果
     * @param <T>           请求返回的内容
     * @return {@link RestVo}
     */
    private <T> RestVo<T> handleRequest(String url, HttpMethod method, Class<T> returnType, HttpEntity<Object> requestEntity) {
        long start = System.currentTimeMillis();
        ResponseEntity<T> exchange = restTemplate.exchange(url, method, requestEntity, returnType);
        log.debug("{}请求消耗了:{}ms", url, System.currentTimeMillis() - start);
        T body = exchange.getBody();
        log.info("请求回来的参数是:{}", body);
        RestVo<T> restVo = new RestVo<>();
        restVo.setHttpHeaders(exchange.getHeaders()).setResult(body);
        return restVo;
    }


    /**
     * 设置头信息
     *
     * @param headers 头部的参数
     */
    private HttpHeaders buildHttpHeaders(Map<String, Object> headers) {
        HttpHeaders requestHeaders = new HttpHeaders();
        if (MapUtils.isEmpty(headers)) {
            return requestHeaders;
        }
        // 遍历headers对象
        for (Map.Entry<String, Object> head : headers.entrySet()) {
            String key = head.getKey();
            Object value = head.getValue();
            if (HttpHeaders.COOKIE.equals(key)) {
                requestHeaders.put(HttpHeaders.COOKIE, getCookieList(value));
                continue;
            }
            requestHeaders.add(key, value.toString());
        }
        log.debug("要传输的headers如下:{}", headers);
        return requestHeaders;
    }


}
