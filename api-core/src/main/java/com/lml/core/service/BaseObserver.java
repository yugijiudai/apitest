package com.lml.core.service;

/**
 * @author yugi
 * @apiNote 观察者的基类
 * @since 2022-05-06
 */
public interface BaseObserver {


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
