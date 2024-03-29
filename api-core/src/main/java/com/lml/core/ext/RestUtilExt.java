package com.lml.core.ext;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.core.dto.RequestDto;
import com.lml.core.exception.InitException;
import com.lml.core.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
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
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> postForForm(RequestDto requestDto, Class<T> returnType) {
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : reqObj.entrySet()) {
            formData.add(entry.getKey(), entry.getValue());
        }
        formData = handleUploadFile(requestDto.getFile(), formData);
        return requestForm(requestDto, HttpMethod.POST, returnType, formData);
    }


    /**
     * post方法,使用json格式来传参,后端需要用@requestBody来接受(自定义请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> post(RequestDto requestDto, Class<T> returnType) {
        return requestJson(requestDto, HttpMethod.POST, returnType);
    }


    /**
     * get方法(没有请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> get(RequestDto requestDto, Class<T> returnType) {
        return requestForm(requestDto, HttpMethod.GET, returnType, null);
    }


    /**
     * put方法(有请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> put(RequestDto requestDto, Class<T> returnType) {
        return requestJson(requestDto, HttpMethod.PUT, returnType);
    }


    /**
     * delete方法
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    @Override
    public <T> RestVo<T> delete(RequestDto requestDto, Class<T> returnType) {
        return requestForm(requestDto, HttpMethod.DELETE, returnType, null);
    }


    /**
     * 发送请求(json类型的参数使用)
     *
     * @param requestDto {@link RequestDto}
     * @param method     请求方法
     * @param returnType 返回的类型
     */
    private <T> RestVo<T> requestJson(RequestDto requestDto, HttpMethod method, Class<T> returnType) {
        //获取header信息
        HttpHeaders requestHeaders = buildHttpHeaders(requestDto.getHeaders());
        JSONObject obj = JSONUtil.parseObj(requestDto.getParam());
        HttpEntity<Object> requestEntity = new HttpEntity<>(obj, requestHeaders);
        return handleRequest(requestDto.getUrl(), method, returnType, requestEntity);
    }

    /**
     * 发送请求(form类型的参数使用)
     *
     * @param requestDto {@link RequestDto}
     * @param method     请求方法
     * @param returnType 返回的类型
     * @param param      postForm才需要使用,get和delete都是null
     */
    private <T> RestVo<T> requestForm(RequestDto requestDto, HttpMethod method, Class<T> returnType, Object param) {
        String url = requestDto.getUrl();
        //获取header信息
        HttpHeaders requestHeaders = buildHttpHeaders(requestDto.getHeaders());
        JSONObject params = JSONUtil.parseObj(requestDto.getParam());
        // 发送请求参数
        if (!HttpMethod.POST.equals(method) && MapUtils.isNotEmpty(params)) {
            url = HttpUtil.urlWithForm(url, params, StandardCharsets.UTF_8, true);
            log.info("请求参数:{}", url);
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(param, requestHeaders);
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

    /**
     * 处理上传文件
     *
     * @param uploadFile 需要上传的文件
     * @param map        上传的参数
     * @return 返回新的上传参数
     */
    private MultiValueMap<String, Object> handleUploadFile(Map<String, Object> uploadFile, MultiValueMap<String, Object> map) {
        if (uploadFile == null) {
            return map;
        }
        if (uploadFile.keySet().size() != 1) {
            throw new InitException("上传文件的格式不对!");
        }
        for (Map.Entry<String, Object> entry : uploadFile.entrySet()) {
            JSONArray uploadFiles = JSONUtil.parseArray(entry.getValue());
            // 获取所有的上传文件
            uploadFiles.forEach(fileName -> {
                URL resource = ResourceUtil.getResource(fileName.toString());
                FileSystemResource fileSystemResource = new FileSystemResource(FileUtil.file(resource));
                map.add(entry.getKey(), fileSystemResource);
            });
        }
        return map;
    }


}
