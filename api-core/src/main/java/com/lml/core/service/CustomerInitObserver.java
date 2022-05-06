package com.lml.core.service;

/**
 * @author yugi
 * @apiNote 自定义初始化处理器, 通过实现此类来实现自己定义的初始化内容
 * @since 2022-05-06
 */
public interface CustomerInitObserver extends BaseObserver {

    /**
     * 一些自定义的初始化工作
     */
    void initCustomer();


}
