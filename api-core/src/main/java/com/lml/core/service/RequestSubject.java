package com.lml.core.service;

import com.lml.core.dto.RequestContentDto;
import com.lml.core.po.RequestContent;
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
    private List<RequestContentService> requestList = Lists.newArrayList();

    /**
     * 增加订阅者
     *
     * @param observer {@link RequestContentService}
     */
    public void add(RequestContentService observer) {
        requestList.add(observer);
    }

    /**
     * 删除订阅者
     *
     * @param observer {@link RequestContentService}
     */
    public void remove(RequestContentService observer) {
        requestList.remove(observer);
    }


    /**
     * 通知订阅者做请求前的操作
     *
     * @param requestContentDto {@link RequestContentDto}
     */
    public void notifyBeforeRequest(RequestContentDto requestContentDto) {
        for (RequestContentService requestContentService : requestList) {
            requestContentService.beforeRequest(requestContentDto);
        }
    }

    /**
     * 通知订阅者做请求后的操作
     *
     * @param requestContent {@link RequestContent}
     */
    public void notifyAfterRequest(RequestContent requestContent) {
        for (RequestContentService requestContentService : requestList) {
            requestContentService.afterRequest(requestContent);
        }
    }

}
