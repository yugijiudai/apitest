package com.lml.core.service;

import cn.hutool.core.date.DateUtil;
import com.lml.core.dto.RequestContentDto;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote 请求日志观察者
 * @since 2021-08-30
 */
@Slf4j
public class RequestLogObserver implements RequestObserver {


    @Override
    public void beforeRequest(RequestContentDto requestContentDto) {
        long start = System.currentTimeMillis();
        requestContentDto.setStartTime(DateUtil.date(start)).setThreadName(Thread.currentThread().getName());
        log.debug("请求的参数是:{}", requestContentDto.getContent());
    }

    @Override
    public void afterRequest(RequestContentDto requestContentDto) {
        log.debug("{}请求消耗了:{}ms", requestContentDto.getUrl(), System.currentTimeMillis() - requestContentDto.getStartTime().getTime());
    }

    @Override
    public void onSuccessRequest(RequestContentDto requestContentDto) {

    }

    @Override
    public void onFailRequest(RequestContentDto requestContentDto, Throwable throwable) {

    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public int registerOrder() {
        return 0;
    }


}
