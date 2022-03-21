package com.lml.core.util;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2021-08-30
 */
public class InitUtilTest {


    @Test
    public void testVariable() {
        GlobalVariableUtil.setCache("{{allBrand}}", Lists.newArrayList("\"资生堂", "兰蔻", "雅诗兰黛\"", "\\'迪奥"));
        GlobalVariableUtil.setCache("{{post}}", "主贴");
        GlobalVariableUtil.setCache("{{money}}", 434.654);
        GlobalVariableUtil.setCache("{{startTime}}", 1622474);
        GlobalVariableUtil.setCache("{{endTime}}", 1623166029049L);
        GlobalVariableUtil.setCache("{{nullName}}", "");
        GlobalVariableUtil.setCache("{{nullEs}}", "{}");
        GlobalVariableUtil.setCache("{{noise}}", "{\"term\": {\"hello\": {\"value\": 1234}}}");
        System.out.println(InitUtil.loadReqContent("demo/variableRequest.json5"));
    }


    @Test
    public void testInitArr() {
        List<String> list = Lists.newArrayList("高质量广告", "\"杂音", "低质量广告\"", "\\'自发内容");
        String param1 = "{\"query\": {\"bool\": {\"must\": [{\"terms\": {\"content_ad_noise\": \"#{contentAdNoise}\"} } ] } } }";
        List<String> emptyList = Lists.newArrayList();
        GlobalVariableUtil.setCache("#{contentAdNoise}", emptyList);
        this.formatVariable(param1);
        GlobalVariableUtil.setCache("#{contentAdNoise}", list);
        this.formatVariable(param1);
        String param2 = "{\"query\": {\"bool\": {\"must\": [{\"terms\": {\"content_ad_noise\": [\"{{contentAdNoise}}\"]} } ] } } }";
        GlobalVariableUtil.setCache("{{contentAdNoise}}", emptyList);
        this.formatAll(param2);
        GlobalVariableUtil.setCache("{{contentAdNoise}}", list);
        this.formatAll(param2);
    }

    private void formatAll(String param) {
        System.out.println(JSONUtil.parseObj(ScriptFormatUtil.formatAllVariable(param)));
    }

    private void formatVariable(String param) {
        System.out.println(JSONUtil.parseObj(ScriptFormatUtil.formatVariable(param)));
    }

}