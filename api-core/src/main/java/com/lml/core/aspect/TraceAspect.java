package com.lml.core.aspect;

import cn.hutool.aop.aspects.SimpleAspect;
import cn.hutool.core.util.IdUtil;
import com.lml.core.annotations.RequestTrace;
import com.lml.core.holder.ReqHolder;

import java.lang.reflect.Method;

/**
 * @author yugi
 * @apiNote 请求链路切面
 * @since 2021-09-06
 */
public class TraceAspect extends SimpleAspect {


    @Override
    public boolean before(Object target, Method method, Object[] args) {
        RequestTrace annotation = method.getAnnotation(RequestTrace.class);
        if (annotation != null) {
            ReqHolder.addTraceId(IdUtil.fastSimpleUUID());
        }
        return true;
    }

    @Override
    public boolean after(Object target, Method method, Object[] args, Object returnVal) {
        ReqHolder.removeTraceId();
        return true;
    }

}
