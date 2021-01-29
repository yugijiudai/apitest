package com.lml.core.service;

import com.lml.core.dto.RequestContentDto;

/**
 * @author yugi
 * @apiNote 请求操作的抽象服务层, 通过实现此类为请求的前置或者后置操作提供额外的操作
 * @since 2019-09-23
 */
public interface RequestObserver {

    /**
     * 请求前置处理
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    void beforeRequest(RequestContentDto requestContentDto);


    /**
     * 请求后置处理(无论成功或者失败都会被调用)
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    void afterRequest(RequestContentDto requestContentDto);

    /**
     * 请求成功的后置处理
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    void onSuccessRequest(RequestContentDto requestContentDto);

    /**
     * 请求失败的后置处理
     *
     * @param requestContentDto {@link RequestContentDto}
     * @param throwable         请求失败的相关异常
     */
    void onFailRequest(RequestContentDto requestContentDto, Throwable throwable);

    /**
     * 是否需要注册到监听者列表
     *
     * @return true表示注册, false表示不注册
     */
    boolean isRegister();

    /**
     * 注册到监听者列表的顺序
     *
     * @return 数字越大顺序越后
     */
    int registerOrder();

}
