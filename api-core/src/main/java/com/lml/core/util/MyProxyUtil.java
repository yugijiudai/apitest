package com.lml.core.util;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.interceptor.SpringCglibInterceptor;
import cn.hutool.core.util.ReflectUtil;
import com.lml.core.exception.InitException;
import lombok.experimental.UtilityClass;
import org.springframework.cglib.proxy.Enhancer;

/**
 * @author yugi
 * @apiNote 动态代理类
 * @since 2021-09-06
 */
@UtilityClass
public class MyProxyUtil {


    /**
     * 用来初始化需要用到aop的类
     *
     * @param clz           需要初始化的类
     * @param aspect        切面类
     * @param myInterceptor 自定义的SpringCglibInterceptor拦截器(cglib在高版本的jdk会报错，这里改成用spring的)
     * @param <T>           这个类的类型
     * @return 初始化的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T> clz, Class<? extends Aspect> aspect, Class<? extends SpringCglibInterceptor> myInterceptor) {
        try {
            Enhancer enhancer = new Enhancer();
            Aspect aspectClz = ReflectUtil.newInstance(aspect);
            T target = clz.getDeclaredConstructor().newInstance();
            enhancer.setSuperclass(target.getClass());
            SpringCglibInterceptor springCglibInterceptor = myInterceptor == null ? new SpringCglibInterceptor(target, aspectClz) : ReflectUtil.newInstance(myInterceptor, target, aspectClz);
            enhancer.setCallback(springCglibInterceptor);
            return (T) enhancer.create();
        }
        catch (Exception e) {
            throw new InitException("类初始化失败!");
        }
    }

}
