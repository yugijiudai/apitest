package com.lml.apitest.ext;

import com.lml.apitest.vo.RestVo;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * @author yugi
 * @apiNote 请求的适配器
 * @since 2019-08-15
 */
@AllArgsConstructor
@ToString
public class ReqAdapter {


    private ReqExt reqExt;


    /**
     * post方法,使用formData格式来传参
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> postForForm(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        return reqExt.postForForm(url, obj, returnType, headers);
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
    public <T> RestVo<T> post(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        return reqExt.post(url, obj, returnType, headers);
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
    public <T> RestVo<T> get(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        return reqExt.get(url, returnType, params, headers);
    }


    /**
     * put方法(有请求头)
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> put(String url, Object obj, Class<T> returnType, Map<String, Object> headers) {
        return reqExt.put(url, obj, returnType, headers);
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
    public <T> RestVo<T> delete(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers) {
        return reqExt.delete(url, returnType, params, headers);
    }

}
