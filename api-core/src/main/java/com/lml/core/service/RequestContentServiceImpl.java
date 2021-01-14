package com.lml.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.lml.core.dao.RequestContentDao;
import com.lml.core.dto.RequestContentDto;
import com.lml.core.po.RequestContent;

/**
 * @author yugi
 * @apiNote 请求的实体类服务层实现类
 * @since 2019-09-23
 */
public class RequestContentServiceImpl implements RequestContentService {

    private RequestContentDao requestContentDao = new RequestContentDao();

    @Override
    public void beforeRequest(RequestContentDto requestContentDto) {
        RequestContent requestContent = new RequestContent();
        BeanUtil.copyProperties(requestContentDto, requestContent);
        requestContentDto.setRequestId(requestContentDao.add(requestContent).getId());
    }

    @Override
    public void afterRequest(RequestContent requestContent) {
        requestContentDao.update(requestContent);
    }
}
