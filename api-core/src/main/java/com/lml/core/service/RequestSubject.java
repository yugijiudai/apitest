package com.lml.core.service;

import com.lml.core.dto.RequestContentDto;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author yugi
 * @apiNote 请求的观察者, 允许通过自定义的方法往里面动态的添加自己想要的请求前置或者后置的处理器
 * @since 2021-01-14
 */
public class RequestSubject {

    /**
     * 请求的列表处理器
     */
    private List<RequestObserver> requestList = Lists.newArrayList();

    /**
     * 增加订阅者
     *
     * @param observer {@link RequestObserver}
     */
    public void add(RequestObserver observer) {
        requestList.add(observer);
    }


    /**
     * 清除所有的观察者
     */
    public void clear() {
        requestList.clear();
    }

    /**
     * 通知订阅者做请求前的操作
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    public void notifyBeforeRequest(RequestContentDto requestContentDto) {
        for (RequestObserver requestObserver : requestList) {
            requestObserver.beforeRequest(requestContentDto);
        }
    }

    /**
     * 通知订阅者做请求后的操作
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    public void notifyAfterRequest(RequestContentDto requestContentDto) {
        for (RequestObserver requestObserver : requestList) {
            requestObserver.afterRequest(requestContentDto);
        }
    }

    public void notifySuccessRequest(RequestContentDto requestContentDto) {
        for (RequestObserver requestObserver : requestList) {
            requestObserver.onSuccessRequest(requestContentDto);
        }
    }

    public void notifyFailRequest(RequestContentDto requestContentDto, Throwable throwable) {
        for (RequestObserver requestObserver : requestList) {
            requestObserver.onFailRequest(requestContentDto, throwable);
        }
    }

}
