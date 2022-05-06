package com.lml.core.util;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.aspects.Aspect;
import com.lml.core.aspect.TraceAspect;
import com.lml.core.exception.InitException;
import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 动态代理类
 * @since 2021-09-06
 */
@UtilityClass
public class MyProxyUtil {


    /**
     * 用来初始化需要用到请求链追踪的类型
     *
     * @param clz 需要初始化的类
     * @param <T> 这个类的类型
     * @return 初始化的对象
     */
    public <T> T init(Class<T> clz) {
        return initAspect(clz, TraceAspect.class);
    }

    /**
     * 用来初始化需要用到aop的类
     *
     * @param clz 需要初始化的类
     * @param <T> 这个类的类型
     * @return 初始化的对象
     */
    public <T> T initAspect(Class<T> clz, Class<? extends Aspect> aspect) {
        try {
            return ProxyUtil.proxy(clz.getDeclaredConstructor().newInstance(), aspect);
        }
        catch (Exception e) {
            throw new InitException("类初始化失败!");
        }
    }
}
