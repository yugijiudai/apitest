package com.lml.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import com.lml.core.dto.SettingDto;
import com.lml.core.exception.InitException;
import com.lml.core.ext.ReqAdapter;
import com.lml.core.ext.ReqExt;
import com.lml.core.factory.RequestHandlerFactory;
import com.lml.core.handler.RequestHandler;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

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
     * ${XXX}的正则匹配
     */
    private final String REGEX = "\\$\\{(.*?)}";

    /**
     * "#{XXX}"的正则匹配
     */
    private final String ARR_REGEX = "\"\\#\\{(.*?)}\"";

    static {
        initRequestHandle();
    }

    /**
     * 获取脚本数据
     *
     * @param fileName 脚本的名字
     * @return 返回脚本的json格式
     */
    public JSONObject loadReqContent(String fileName) {
        String script = loadScript(fileName);
        return JSONUtil.parseObj(formatVariable(script));
    }


    /**
     * 根据配置文件配置的来初始化http请求的底层调用类
     *
     * @param clz 指定使用的请求类,如果没有则使用配置里面的
     * @return {@link ReqAdapter}
     */
    public ReqAdapter initReqAdapter(Class clz) {
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
            JSONObject newObj = JSONUtil.parseObj(formatVariable(script));
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
     * 替换有占位符${xxx}的脚本
     *
     * @param script 加载好的脚本
     * @return 返回替换好的占位符
     */
    private String formatVariable(String script) {
        log.debug("原始脚本是:{}", JSONUtil.parse(script).toString());
        script = formatNormalVariable(script);
        script = formatArrayVariable(script);
        log.debug("替换后的脚本是:{}", JSONUtil.parse(script).toString());
        return script;
    }

    /**
     * 格式化普通类型的变量
     *
     * @param script 加载的脚本
     * @return 把${xxx}替换成对应的变量
     */
    private String formatNormalVariable(String script) {
        List<String> all = ReUtil.findAll(REGEX, script, 0);
        for (String match : all) {
            script = script.replace(match, GlobalVariableUtil.getCache(match).toString());
        }
        return script;
    }

    /**
     * 格式化数组类型的变量
     *
     * @param script 加载的脚本
     * @return 把#{xxx}转成["a", "b"]这种形式,并且替换对应的变量
     */
    @SuppressWarnings("unchecked")
    private String formatArrayVariable(String script) {
        List<String> arrAll = ReUtil.findAll(ARR_REGEX, script, 0);
        for (String match : arrAll) {
            // 匹配"#{xxx}",所以要截取第一个和最后一个双引号即#{xxx}
            String cacheKey = match.substring(1, match.length() - 1);
            List<String> cache = (List<String>) GlobalVariableUtil.getCache(cacheKey);
            StringBuilder arrayString = new StringBuilder("[");
            for (String tmp : cache) {
                arrayString.append("\"").append(tmp).append("\"").append(",");
            }
            script = script.replace(match, arrayString.substring(0, arrayString.length() - 1) + "]");
        }
        return script;
    }

}
