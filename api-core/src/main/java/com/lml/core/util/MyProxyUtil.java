package com.lml.core.util;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.interceptor.CglibInterceptor;
import cn.hutool.core.util.ReflectUtil;
import com.lml.core.aspect.TraceAspect;
import com.lml.core.exception.InitException;
import lombok.experimental.UtilityClass;
import net.sf.cglib.proxy.Enhancer;

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
     * @param clz    需要初始化的类
     * @param aspect 切面类
     * @param <T>    这个类的类型
     * @return 初始化的对象
     */
    public <T> T initAspect(Class<T> clz, Class<? extends Aspect> aspect) {
        try {
            return proxy(clz, aspect, null);
        }
        catch (Exception e) {
            throw new InitException("类初始化失败!");
        }
    }


    /**
     * 用来初始化需要用到aop的类
     *
     * @param clz           需要初始化的类
     * @param aspect        切面类
     * @param myInterceptor 自定义的cglib拦截器
     * @param <T>           这个类的类型
     * @return 初始化的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T> clz, Class<? extends Aspect> aspect, Class<? extends CglibInterceptor> myInterceptor) {
        final Enhancer enhancer = new Enhancer();
        try {
            Aspect aspectClz = ReflectUtil.newInstance(aspect);
            T target = clz.getDeclaredConstructor().newInstance();
            enhancer.setSuperclass(target.getClass());
            CglibInterceptor cglibInterceptor = myInterceptor == null ? new CglibInterceptor(target, aspectClz) : ReflectUtil.newInstance(myInterceptor, target, aspectClz);
            enhancer.setCallback(cglibInterceptor);
            return (T) enhancer.create();
        }
        catch (Exception e) {
            throw new InitException("类初始化失败!");
        }
    }

}
