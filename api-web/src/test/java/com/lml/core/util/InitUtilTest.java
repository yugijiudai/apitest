package com.lml.core.util;

import cn.hutool.json.JSONObject;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2021-08-30
 */
public class InitUtilTest {

    @Test
    public void testLoadReqContent() {
        JSONObject jsonObject = InitUtil.loadReqContent("demo/json5Demo.json5");
        System.out.println(jsonObject);
    }
}