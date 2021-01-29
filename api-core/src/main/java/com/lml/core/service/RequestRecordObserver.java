package com.lml.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.lml.core.dao.RequestContentDao;
import com.lml.core.dto.RequestContentDto;
import com.lml.core.enums.RequestStatusEnum;
import com.lml.core.po.RequestContent;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author yugi
 * @apiNote 用来记录请求的前置和后置操作的观察者
 * @since 2019-09-23
 */
@Slf4j
public class RequestRecordObserver implements RequestObserver {

    private final RequestContentDao requestContentDao = new RequestContentDao();

    @Override
    public void beforeRequest(RequestContentDto requestContentDto) {
        long start = System.currentTimeMillis();
        requestContentDto.setStartTime(DateUtil.date(start)).setThreadName(Thread.currentThread().getName());
        RequestContent requestContent = new RequestContent();
        BeanUtil.copyProperties(requestContentDto, requestContent);
        requestContentDto.setRequestId(requestContentDao.add(requestContent).getId());
    }

    @Override
    public void afterRequest(RequestContentDto requestContentDto) {
        log.debug("{}请求消耗了:{}ms", requestContentDto.getUrl(), System.currentTimeMillis() - requestContentDto.getStartTime().getTime());
    }

    @Override
    public void onSuccessRequest(RequestContentDto requestContentDto) {
        RequestContent update = new RequestContent();
        update.setRequestStatus(RequestStatusEnum.OK);
        this.updateRequest(requestContentDto, update);
    }

    @Override
    public void onFailRequest(RequestContentDto requestContentDto, Throwable throwable) {
        RequestContent update = new RequestContent();
        update.setExceptionMsg(ExceptionUtil.stacktraceToString(throwable)).setRequestStatus(RequestStatusEnum.FAIL);
        this.updateRequest(requestContentDto, update);
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public int registerOrder() {
        return 0;
    }

    /**
     * 更新请求的通用操作
     *
     * @param requestContentDto {@link RequestContentDto}
     * @param update            需要更新的实体类
     */
    private void updateRequest(RequestContentDto requestContentDto, RequestContent update) {
        update.setId(requestContentDto.getRequestId());
        update.setEndTime(new Date());
        requestContentDao.update(update);
    }

}
