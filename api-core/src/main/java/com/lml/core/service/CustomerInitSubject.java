package com.lml.core.service;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

/**
 * @author yugi
 * @apiNote 请求的观察者, 允许通过自定义的方法往里面动态的添加自己想要的请求前置或者后置的处理器
 * @since 2021-01-14
 */
public class CustomerInitSubject {

    /**
     * 需要自定义初始化的列表处理器
     */
    @Getter
    private List<CustomerInitObserver> customerInitObserverList = Lists.newArrayList();

    /**
     * 增加订阅者
     *
     * @param observer {@link CustomerInitObserver}
     */
    public void add(CustomerInitObserver observer) {
        customerInitObserverList.add(observer);
    }


    /**
     * 清除所有的观察者
     */
    public void clear() {
        customerInitObserverList.clear();
    }

    /**
     * 对列表执行顺序重新排序,order的值越小排的越前
     */
    public void order() {
        customerInitObserverList.sort(Comparator.comparingInt(CustomerInitObserver::registerOrder));
    }

    /**
     * 通知订阅者做请求前的操作
     */
    public void notifyInit() {
        for (CustomerInitObserver customerInitObserver : customerInitObserverList) {
            customerInitObserver.initCustomer();
        }
    }


}
