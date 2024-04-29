package com.lml.core.aspect;

import java.lang.reflect.Method;

/**
 * @author yugi
 * @apiNote 注解处理器接口，用来做aop处理
 * @since 2024-04-29
 */
public interface AnnotationHandler {

    /**
     * 统一的前置处理
     *
     * @param target 目标类
     * @param method 调用的方法
     * @param args   方法的参数
     */
    void before(Object target, Method method, Object[] args);


    /**
     * 统一的后置处理
     *
     * @param target 目标类
     * @param method 调用的方法
     * @param args   方法的参数
     * @param returnVal   返回的参数
     */
    void after(Object target, Method method, Object[] args, Object returnVal);


    /**
     * 统一的异常处理
     *
     * @param target 目标类
     * @param method 调用的方法
     * @param args   方法的参数
     * @param e   异常
     */
    void afterException(Object target, Method method, Object[] args, Throwable e);

}
