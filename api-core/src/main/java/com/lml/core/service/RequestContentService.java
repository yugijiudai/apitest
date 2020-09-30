package com.lml.core.service;

import com.lml.core.dto.RequestContentDto;
import com.lml.core.po.RequestContent;

/**
 * @author yugi
 * @apiNote 请求的实体类服务层
 * @since 2019-09-23
 */
public interface RequestContentService {

    /**
     * 请求前做的处理
     *
     * @param requestContentDto {@link RequestContentDto}
     * @return RequestContent 插入后的对象
     */
    RequestContent beforeRequest(RequestContentDto requestContentDto);

    /**
     * 请求后做的处理
     *
     * @param requestContent {@link RequestContent}
     */
    void afterRequest(RequestContent requestContent);

}
