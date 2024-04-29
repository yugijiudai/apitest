package com.lml.core.aspect;

import cn.hutool.core.util.IdUtil;
import com.lml.core.annotations.RequestTrace;
import com.lml.core.holder.ReqHolder;

import java.lang.reflect.Method;

/**
 * @author yugi
 * @apiNote 请求的处理器
 * @since 2024-04-29
 */
public class TraceHandler implements AnnotationHandler {

    @Override
    public void before(Object target, Method method, Object[] args) {
        RequestTrace annotation = method.getAnnotation(RequestTrace.class);
        if (annotation != null) {
            ReqHolder.addTraceId(IdUtil.fastSimpleUUID());
        }
    }

    @Override
    public void after(Object target, Method method, Object[] args, Object returnVal) {
        ReqHolder.removeTraceId();
    }

    @Override
    public void afterException(Object target, Method method, Object[] args, Throwable e) {
        ReqHolder.removeTraceId();
    }
}
