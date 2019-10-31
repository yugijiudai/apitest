package com.lml.apitest.ext;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lml.apitest.vo.RestVo;

import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 请求类接口
 * @since 2019-08-15
 */
public interface ReqExt {


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
    <T> RestVo<T> postForForm(String url, Object obj, Class<T> returnType, Map<String, Object> headers);


    /**
     * post方法,使用json格式来传参,后端需要用@requestBody来接受
     *
     * @param url        请求url
     * @param obj        post请求的参数
     * @param returnType 返回值的类
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    <T> RestVo<T> post(String url, Object obj, Class<T> returnType, Map<String, Object> headers);


    /**
     * get方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param params     请求的参数
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    <T> RestVo<T> get(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers);


    /**
     * put方法
     *
     * @param url        请求url
     * @param obj        请求的内容
     * @param returnType 返回值的类
     * @param headers    请求的头部
     * @return 返回值的类
     */
    <T> RestVo<T> put(String url, Object obj, Class<T> returnType, Map<String, Object> headers);


    /**
     * delete方法
     *
     * @param url        请求url
     * @param returnType 返回值的类
     * @param params     请求的参数
     * @param headers    请求头
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    <T> RestVo<T> delete(String url, Class<T> returnType, Map<String, Object> params, Map<String, Object> headers);


    /**
     * 获得cookieList,因为要传cookie,header需要这样设置:requestHeaders.put(HttpHeaders.COOKIE, cookieList)
     *
     * @param cookiesJsonArr cookie的json数组字符串
     * @return 返回设置好的格式, 格式如下:["JSESSIONID=xxx", "name=lml"]
     */
    default List<String> getCookieList(Object cookiesJsonArr) {
        JSONArray array = JSONUtil.parseArray(cookiesJsonArr);
        List<String> cookieList = Lists.newArrayList();
        array.forEach(obj -> {
            JSONObject tmp = (JSONObject) obj;
            tmp.forEach((key, val) -> cookieList.add(key + "=" + val));
        });
        return cookieList;
    }

    /**
     * 获得cookie
     *
     * @param cookiesJsonArr cookie的json数组字符串
     * @return 返回设置好的格式, 格式如下:"TITAN_SESSION_ID=sss; TITAN_ACCID=910
     */
    default String toCookieList(Object cookiesJsonArr) {
        JSONArray array = JSONUtil.parseArray(cookiesJsonArr);
        StringBuilder sb = new StringBuilder();
        array.forEach(obj -> {
            JSONObject tmp = (JSONObject) obj;
            tmp.forEach((key, val) -> sb.append(key).append("=").append(val).append(";"));
        });
        return sb.substring(0, sb.length() - 1);
    }

}
