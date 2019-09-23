package com.lml.apitest.service;

import cn.hutool.core.bean.BeanUtil;
import com.lml.apitest.dao.RequestContentDao;
import com.lml.apitest.dto.RequestContentDto;
import com.lml.apitest.po.RequestContent;

/**
 * @author yugi
 * @apiNote 请求的实体类服务层实现类
 * @since 2019-09-23
 */
public class RequestContentServiceImpl implements RequestContentService {

    private RequestContentDao requestContentDao = new RequestContentDao();

    @Override
    public RequestContent beforeRequest(RequestContentDto requestContentDto) {
        RequestContent requestContent = new RequestContent();
        BeanUtil.copyProperties(requestContentDto, requestContent);
        return requestContentDao.add(requestContent);
    }

    @Override
    public void afterRequest(RequestContent requestContent) {
        requestContentDao.update(requestContent);
    }
}
