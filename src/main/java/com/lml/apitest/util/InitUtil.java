package com.lml.apitest.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import com.lml.apitest.dto.SettingDto;
import com.lml.apitest.exception.InitException;
import com.lml.apitest.factory.RequestHandlerFactory;
import com.lml.apitest.handler.RequestHandler;
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

    static {
        initRequestHandle();
        // TODO yugi: 2019/8/8  待删除
        GlobalVariableUtil.setCache("${test_username}", "lml");
        GlobalVariableUtil.setCache("${test_sessionId}", "10086");
        GlobalVariableUtil.setCache("${test_helloWorld}", "helloWorld");
    }

    /**
     * 获取脚本数据
     *
     * @param fileName 脚本的名字
     * @return 返回脚本的json格式
     */
    public JSONObject loadReqContent(String fileName) {
        URL resource = ResourceUtil.getResource(fileName);
        if (resource == null) {
            throw new InitException("找不到要加载的脚本 " + fileName);
        }
        String script = FileUtil.readString(FileUtil.file(resource), StandardCharsets.UTF_8);
        return JSONUtil.parseObj(formatVariable(script));
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
        Properties prop = Props.getProp("application.properties");
        //获取key对应的value值
        SettingDto settingDto = new SettingDto();
        BeanUtil.copyProperties(prop, settingDto);
        log.debug("初始化配置成功:{}", settingDto);
        return settingDto;
    }

    /**
     * 替换有占位符的脚本
     *
     * @param script 加载好的脚本
     * @return 返回替换好的占位符
     */
    private String formatVariable(String script) {
        log.debug("原始脚本是:{}", JSONUtil.parse(script).toString());
        List<String> all = ReUtil.findAll(REGEX, script, 0);
        for (String match : all) {
            script = script.replace(match, GlobalVariableUtil.getCache(match).toString());
        }
        log.debug("替换后的脚本是:{}", JSONUtil.parse(script).toString());
        return script;
    }
}
