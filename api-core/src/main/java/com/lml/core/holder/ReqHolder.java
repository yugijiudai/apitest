package com.lml.core.holder;

import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 用于存放请求的一些统一数据
 * @since 2021-09-06
 */
@UtilityClass
public final class ReqHolder {

    /**
     * 用来保存traceId
     */
    private final ThreadLocal<String> traceIdHolder = new ThreadLocal<>();


    /**
     * 添加traceId
     */
    public void addTraceId(String traceId) {
        traceIdHolder.set(traceId);
    }

    /**
     * 获取traceId
     */
    public String getTraceId() {
        return traceIdHolder.get();
    }

    /**
     * 清除traceId
     */
    public void removeTraceId() {
        traceIdHolder.remove();
    }

}
