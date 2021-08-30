package com.lml.core.util;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2021-08-30
 */
public class InitUtilTest {

    @Test
    public void testJson5() {
        JSONObject jsonObject = InitUtil.loadReqContent("demo/json5Demo.json5");
        System.out.println(jsonObject);
    }

    @Test
    public void testVariable() {
        GlobalVariableUtil.setCache("{{allBrand}}", Lists.newArrayList("资生堂", "兰蔻"));
        GlobalVariableUtil.setCache("{{post}}", "主贴");
        GlobalVariableUtil.setCache("{{money}}", 434.654);
        GlobalVariableUtil.setCache("{{startTime}}", 1622474);
        GlobalVariableUtil.setCache("{{endTime}}", 1623166029049L);
        GlobalVariableUtil.setCache("{{nullName}}", "");
        GlobalVariableUtil.setCache("{{nullEs}}", "{}");
        GlobalVariableUtil.setCache("{{noise}}", "{\"term\": {\"hello\": {\"value\": 1234}}}");
        System.out.println(InitUtil.loadReqContent("demo/variableRequest.json5").toStringPretty());
    }
}