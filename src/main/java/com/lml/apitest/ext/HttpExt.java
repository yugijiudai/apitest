package com.lml.apitest.ext;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.dto.RequestContentDto;
import com.lml.apitest.enums.RequestStatusEnum;
import com.lml.apitest.exception.RequestException;
import com.lml.apitest.po.RequestContent;
import com.lml.apitest.service.RequestContentService;
import com.lml.apitest.service.RequestContentServiceImpl;
import com.lml.apitest.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 底层使用hutool的httpUtil来调用
 * @since 2019-08-15
 */
@Slf4j
public class HttpExt implements ReqExt {


    private RequestContentService requestContentServiceImpl = new RequestContentServiceImpl();

    @Override
    public <T> RestVo<T> postForForm(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        HttpRequest post = HttpRequest.post(url);
        this.setRequestHeader(headers, post);
        Map<String, Object> map = Maps.newHashMap();
        BeanUtil.copyProperties(obj, map);
        HttpResponse execute = doFormRequest(post, headers, map);
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
        RequestContentDto requestContentDto = buildRequestContentDtoCommon(params, headers, req);
        return exe(req.form(params), requestContentDto);
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
        String content = JSONUtil.toJsonStr(obj);
        RequestContentDto requestContentDto = buildRequestContentDtoCommon(obj, headers, req);
        return exe(req.body(content), requestContentDto);
    }

    /**
     * 构建公共的头部,内容,方法,url等公共参数
     *
     * @param content     请求的内容
     * @param headers     请求的头部
     * @param httpRequest 请求的对象
     * @return 返回构造好的dto
     */
    private RequestContentDto buildRequestContentDtoCommon(Object content, Map<String, Object> headers, HttpRequest httpRequest) {
        RequestContentDto requestContentDto = new RequestContentDto();
        return requestContentDto.setContent(JSONUtil.toJsonStr(content)).setHeaders(headers).setUrl(httpRequest.getUrl()).setMethod(httpRequest.getMethod());
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
