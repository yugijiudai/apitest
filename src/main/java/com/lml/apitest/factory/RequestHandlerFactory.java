package com.lml.apitest.factory;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.exception.InitException;
import com.lml.apitest.handler.RequestHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author yugi
 * @apiNote 请求处理器工厂
 * @since 2019-08-06
 */
@Slf4j
@UtilityClass
public class RequestHandlerFactory {

    /**
     * 存放需要查找元素的处理器的map
     */
    private final Map<MethodEnum, RequestHandler> requestHandler = MapUtil.newHashMap();

    /**
     * 初始化对应的处理器
     *
     * @param cla 要初始化的超类
     */
    public void initHandler(Class<? extends RequestHandler> cla) {
        // 获取所有的实现类
        Set<Class<?>> classes = ClassUtil.scanPackageBySuper(cla.getPackage().getName(), cla);
        classes.stream().findFirst().ifPresent(tmp -> {
            try {
                setHandler(classes, eh -> requestHandler.put(eh.getMethod(), eh));
                log.debug("===================初始化{}的子类完成===================", cla.getSimpleName());
            }
            catch (Exception e) {
                throw new InitException(e);
            }
        });
    }


    /**
     * 初始化对应的实现类并且放在map中
     *
     * @param classes  要初始化的类的列表
     * @param consumer 强转后的回调函数
     */
    private void setHandler(Set<Class<?>> classes, Consumer<RequestHandler> consumer) throws IllegalAccessException, InstantiationException {
        for (Class<?> clz : classes) {
            RequestHandler eh = (RequestHandler) clz.newInstance();
            consumer.accept(eh);
            log.debug("初始化{},成功", eh);
        }
    }

    /**
     * 获取ElementHandler
     *
     * @param methodEnum 对应的方法类型
     * @return {@link RequestHandler}
     */
    public RequestHandler getHandler(MethodEnum methodEnum) {
        return requestHandler.get(methodEnum);
    }

}
