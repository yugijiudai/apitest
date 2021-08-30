package com.lml.core.util;

import cn.hutool.json.JSONObject;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2021-08-30
 */
public class RequestUtilTest {


    @Test
    public void testDoPost() {
        GlobalVariableUtil.setCache("${url}", "/postForJson");
        JSONObject result = RequestUtil.doPost("demo/simpleRequest/param.json5", "postJson请求", null);
        System.out.println(result);
    }

    @Test
    public void testDoPostForm() {
        JSONObject header = new JSONObject().set("hello", "no_cn");
        GlobalVariableUtil.setCache("${url}", "/postForForm");
        JSONObject result = RequestUtil.doPostForm("demo/simpleRequest/param.json5", "postForm请求", header);
        System.out.println(result);
    }

    @Test
    public void testDoPut() {
        GlobalVariableUtil.setCache("${url}", "/put");
        JSONObject result = RequestUtil.doPut("demo/simpleRequest/param.json5", "put请求", null);
        System.out.println(result);
    }

    @Test
    public void testDoGet() {
        GlobalVariableUtil.setCache("${url}", "/get");
        JSONObject result = RequestUtil.doGet("demo/simpleRequest/param.json5", "get请求", null);
        System.out.println(result);
    }

    @Test
    public void testDoDelete() {
        GlobalVariableUtil.setCache("${url}", "/delete");
        JSONObject result = RequestUtil.doDelete("demo/simpleRequest/param.json5", "delete请求", null);
        System.out.println(result);
    }
}