package com.lml.core.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author yugi
 * @apiNote 脚本格式化工具类
 * @since 2021-08-30
 */
@UtilityClass
@Slf4j
public class ScriptFormatUtil {


    /**
     * ${XXX}的正则匹配
     */
    private final String REGEX = "\\$\\{(.*?)}";

    /**
     * "#{XXX}"的正则匹配
     */
    private final String ARR_REGEX = "\"\\#\\{(.*?)}\"";


    /**
     * ?{XXX}的正则匹配
     */
    private final String DYNAMIC_REGEX = "\\?\\{(.*?)}";

    /**
     * {{}}的正则匹配
     */
    private final String ANY_REGEX = "\"\\{\\{(.*?)}}\"";

    /**
     * 替换有占位符${xxx}的脚本
     *
     * @param script 加载好的脚本
     * @return 返回替换好的占位符
     */
    public String formatVariable(String script) {
        // 这里用fastjson读取文件再转换成原来的jsonObject,原因是hutool的jsonObject无法支持json5格式
        script = JSONObject.parseObject(script).toString();
        log.debug("原始脚本是:{}", script);
        script = formatNormalVariable(script);
        script = formatArrayVariable(script);
        script = formatDynamicVariable(script);
        script = formatAllVariable(script);
        log.debug("替换后的脚本是:{}", script);
        return script;
    }


    /**
     * 格式化普通类型的变量
     *
     * @param script 加载的脚本
     * @return 把${xxx}替换成对应的变量
     * @deprecated 此方法后面不会再使用，请使用{@link #formatAllVariable(String)}
     */
    @Deprecated
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
     * @deprecated 此方法后面不会再使用，请使用{@link #formatAllVariable(String)}
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    private String formatArrayVariable(String script) {
        List<String> arrAll = ReUtil.findAll(ARR_REGEX, script, 0);
        for (String match : arrAll) {
            // 匹配"#{xxx}",所以要截取第一个和最后一个双引号即#{xxx}
            String cacheKey = match.substring(1, match.length() - 1);
            Object val = GlobalVariableUtil.getCache(cacheKey);
            script = script.replace(match, val.toString().length() == 0 ? "[]" : JSONUtil.parseArray(val).toString());
        }
        return script;
    }

    /**
     * 格式化动态变量
     *
     * @param script 加载的脚本
     * @return 把?{xxx}替换成对应的内容
     * @deprecated 此方法后面不会再使用，请使用{@link #formatAllVariable(String)}
     */
    @Deprecated
    private String formatDynamicVariable(String script) {
        // 匹配""?{xxx}""这种
        List<String> all = ReUtil.findAll("\"" + DYNAMIC_REGEX + "\"", script, 0);
        for (String match : all) {
            // 截取前面和后面的双引号,变成"?{xxx}"这种
            String cacheKey = match.substring(1, match.length() - 1);
            script = script.replace(match, GlobalVariableUtil.getCache(cacheKey).toString());
        }
        return script;
    }

    /**
     * 格式化{{}}类型的变量,适用各种类型的参数,后续建议使用这个来格式化
     *
     * @param script 加载的脚本
     * @return 把{{xxx}}替换成对应的内容
     */
    @SuppressWarnings("unchecked")
    public String formatAllVariable(String script) {
        List<String> arrAll = ReUtil.findAll(ANY_REGEX, script, 0);
        for (String match : arrAll) {
            String cacheKey = match.substring(1, match.length() - 1);
            Object val = GlobalVariableUtil.getCache(cacheKey);
            if (val instanceof String) {
                script = handleString(script, match, val.toString());
                continue;
            }
            if (val instanceof List) {
                script = handleList(script, match, (List<Object>) val);
                continue;
            }
            if (val instanceof Number) {
                script = script.replace(match, val.toString());
            }
        }
        return script;
    }

    /**
     * 处理list类型的替换
     *
     * @param script 需要替换的脚本
     * @param match  正则匹配到的内容
     * @param val    要替换的值
     * @return 返回被替换的的脚本
     */
    private String handleList(String script, String match, List<Object> val) {
        return script.replace(match, getArrayScript(val));
    }

    /**
     * 处理string类型的替换
     *
     * @param script 需要替换的脚本
     * @param match  正则匹配到的内容
     * @param val    要替换的值
     * @return 返回被替换的的脚本
     */
    private String handleString(String script, String match, String val) {
        // 如果空字符串则删除后面的逗号
        if (StringUtils.isBlank(val)) {
            return script.replace(match + ",", val);
        }
        // 如果是json格式则直接替换,如果不是证明是某一个值,需要往前后各补双引号
        String replace = JSONUtil.isJsonObj(val) ? val : "\"" + val + "\"";
        return script.replace(match, replace);
    }


    /**
     * 根据提供的列表动态拼接成["x", "b", "c"]这种格式返回
     *
     * @param list 列表的数据
     */
    public String getArrayScript(List<Object> list) {
        if (list.size() == 0) {
            return "";
        }
        // CollUtil.join有bug,如果list里面有元素带有一个转义的双引号,会被直接去掉,导致出来后无法转成json
        // return CollUtil.join(list, ",", "\"", "\"");
        String arrString = JSONUtil.parseArray(list).toString();
        return arrString.substring(1, arrString.length() - 1);
    }
}
