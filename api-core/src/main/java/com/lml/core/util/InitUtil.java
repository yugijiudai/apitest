package com.lml.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lml.core.dto.SettingDto;
import com.lml.core.exception.InitException;
import com.lml.core.ext.ReqAdapter;
import com.lml.core.ext.ReqExt;
import com.lml.core.factory.RequestHandlerFactory;
import com.lml.core.handler.RequestHandler;
import com.lml.core.service.BaseObserver;
import com.lml.core.service.CustomerInitObserver;
import com.lml.core.service.CustomerInitSubject;
import com.lml.core.service.RequestLogObserver;
import com.lml.core.service.RequestObserver;
import com.lml.core.service.RequestRecordObserver;
import com.lml.core.service.RequestSubject;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author yugi
 * @apiNote 一些初始化工作的工具类
 * @since 2019-08-07
 */
@UtilityClass
@Slf4j
public class InitUtil {

    @Getter
    private final SettingDto settingDto = loadSetting();

    /**
     * 请求的观察者
     */
    @Getter
    private RequestSubject requestSubject = new RequestSubject();

    /**
     * 自定义初始化的观察者
     */
    @Getter
    private CustomerInitSubject customerInitSubject = new CustomerInitSubject();

    static {
        initAll();
    }


    /**
     * 初始化所有
     */
    public void initAll() {
        initRequestHandle();
        initDefaultRequestContent();
        initCustomerObserver();
    }

    /**
     * 获取脚本数据
     *
     * @param fileName 脚本的名字
     * @return 返回脚本的json格式
     */
    public JSONObject loadReqContent(String fileName) {
        String script = loadScript(fileName);
        return JSONUtil.parseObj(ScriptFormatUtil.formatVariable(script));
    }


    /**
     * 根据配置文件配置的来初始化http请求的底层调用类
     *
     * @param clz 指定使用的请求类,如果没有则使用配置里面的
     * @return {@link ReqAdapter}
     */
    public ReqAdapter initReqAdapter(Class<?> clz) {
        String initClassName = clz != null ? clz.getName() : settingDto.getReqExt();
        ReqExt reqExt = ReflectUtil.newInstance(initClassName);
        log.info("默认的http请求类是{}.............", reqExt);
        return new ReqAdapter(reqExt);
    }

    /**
     * 获取脚本
     *
     * @param fileName 脚本的名字
     * @return 返回读取的脚本
     */
    public String loadScript(String fileName) {
        URL resource = ResourceUtil.getResource(fileName);
        if (resource == null) {
            throw new InitException("找不到要加载的脚本 " + fileName);
        }
        return FileUtil.readString(FileUtil.file(resource), StandardCharsets.UTF_8);
    }

    /**
     * 将data数据源文件的值替换到需要加载的脚本的变量里
     *
     * @param dataName 数据源文件,json数组格式
     * @param fileName 脚本文件
     * @return 返回替换好的请求数据
     */
    public JSONArray loadSelfData(String dataName, String fileName) {
        String data = loadScript(dataName);
        String script = loadScript(fileName);
        JSONArray needHandle = JSONUtil.createArray();
        JSONArray array = JSONUtil.parseArray(data);
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            obj.forEach((key, val) -> GlobalVariableUtil.setCache("${" + key + "}", val));
            JSONObject newObj = JSONUtil.parseObj(ScriptFormatUtil.formatVariable(script));
            needHandle.add(newObj);
        }
        return needHandle;
    }


    /**
     * 初始化请求处理器
     */
    private void initRequestHandle() {
        RequestHandlerFactory.initHandler(RequestHandler.class);
    }

    /**
     * 初始化自定义初始化观察者
     */
    private void initCustomerObserver() {
        Map<String, BaseObserver> map = initObserver(settingDto.getCustomerInitObserverPackage(), CustomerInitObserver.class);
        for (BaseObserver observer : map.values()) {
            customerInitSubject.add((CustomerInitObserver) observer);
        }
        // 排序
        customerInitSubject.order();
        log.debug("===================初始化自定义初始化观察者{}的子类完成===================", CustomerInitObserver.class.getSimpleName());
        log.debug("总共注册了的观者列表:{}", customerInitSubject.getCustomerInitObserverList());
        customerInitSubject.notifyInit();
    }

    /**
     * 初始化application.properties,把内容load到SettingDto里
     *
     * @return {@link SettingDto}
     */
    private SettingDto loadSetting() {
        Properties prop = Props.getProp("lml.properties");
        //获取key对应的value值
        SettingDto settingDto = new SettingDto();
        BeanUtil.copyProperties(prop, settingDto);
        log.debug("初始化配置成功:{}", settingDto);
        return settingDto;
    }


    /**
     * 初始化默认的请求处理器
     */
    public void initDefaultRequestContent() {
        Map<String, RequestObserver> observerMap = Maps.newLinkedHashMap();
        if (settingDto.getRegisterDefaultRequestObserver()) {
            RequestObserver requestLogObserverImpl = new RequestLogObserver();
            RequestObserver requestObserverImpl = new RequestRecordObserver();
            // 默认需要注册的观察者
            observerMap.put(requestLogObserverImpl.getClass().getName(), requestLogObserverImpl);
            observerMap.put(requestObserverImpl.getClass().getName(), requestObserverImpl);
        }
        // 配置指定需要注册的观察者
        Map<String, BaseObserver> extraMap = initObserver(settingDto.getRequestObserverPackage(), RequestObserver.class);
        for (Map.Entry<String, BaseObserver> entry : extraMap.entrySet()) {
            observerMap.put(entry.getKey(), (RequestObserver) entry.getValue());
        }
        for (RequestObserver observer : observerMap.values()) {
            requestSubject.add(observer);
        }
        // 排序
        requestSubject.order();
        log.debug("===================初始化请求观察者{}的子类完成===================", RequestObserver.class.getSimpleName());
        log.debug("总共注册了的观者列表:{}", requestSubject.getRequestList());
    }

    /**
     * 初始化对应的观察者,这个需要在lml.properties中声明需要扫描的包
     *
     * @param observerPackage 要扫描的包
     * @param observerClz     BaseObserver的子类
     * @return key是所在的包名, value是对应的子类, 这样做是为了出现同名同包的子类, 防止重复注册
     */
    private Map<String, BaseObserver> initObserver(String observerPackage, Class<? extends BaseObserver> observerClz) {
        Map<String, BaseObserver> needToRegisterMap = Maps.newLinkedHashMap();
        if (StringUtils.isBlank(observerPackage)) {
            log.debug("没有额外需要注册的请求监听者,额外注册结束......");
            return needToRegisterMap;
        }
        String[] packageList = observerPackage.split(",");
        Set<Class<?>> allChildren = Sets.newLinkedHashSet();
        for (String packageName : packageList) {
            Set<Class<?>> childrenClazz = ClassUtil.scanPackageBySuper(packageName, observerClz);
            log.debug("{}包下找到{}个需要额外注册的类", packageName, childrenClazz.size());
            allChildren.addAll(childrenClazz);
        }
        // 遍历所有扫描到的子类
        for (Class<?> clz : allChildren) {
            try {
                BaseObserver eh = (BaseObserver) clz.getDeclaredConstructor().newInstance();
                log.debug("初始化{}成功,这个是否要注册到列表:{}", eh, eh.isRegister());
                if (eh.isRegister()) {
                    needToRegisterMap.put(eh.getClass().getName(), eh);
                }
            }
            catch (Exception e) {
                throw new InitException("注册监听者失败!", e);
            }
        }
        return needToRegisterMap;
    }


}
