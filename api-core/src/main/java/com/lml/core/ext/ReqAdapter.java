package com.lml.core.ext;

import com.lml.core.dto.RequestDto;
import com.lml.core.vo.RestVo;
import lombok.AllArgsConstructor;
import lombok.ToString;

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
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> postForForm(RequestDto requestDto, Class<T> returnType) {
        return reqExt.postForForm(requestDto, returnType);
    }


    /**
     * post方法,使用json格式来传参,后端需要用@requestBody来接受(自定义请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> post(RequestDto requestDto, Class<T> returnType) {
        return reqExt.post(requestDto, returnType);
    }


    /**
     * get方法(没有请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> get(RequestDto requestDto, Class<T> returnType) {
        return reqExt.get(requestDto, returnType);
    }


    /**
     * put方法(有请求头)
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> put(RequestDto requestDto, Class<T> returnType) {
        return reqExt.put(requestDto, returnType);
    }


    /**
     * delete方法
     *
     * @param requestDto {@link RequestDto}
     * @param returnType 返回值的类
     * @param <T>        返回值的类
     * @return 返回值的类
     */
    public <T> RestVo<T> delete(RequestDto requestDto, Class<T> returnType) {
        return reqExt.delete(requestDto, returnType);
    }

}
