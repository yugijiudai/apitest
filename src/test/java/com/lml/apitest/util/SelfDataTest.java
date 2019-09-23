package com.lml.apitest.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lml.apitest.BaseTest;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class SelfDataTest extends BaseTest {


    @Test
    public void loadSelfDataTest() {
        JSONArray objects = InitUtil.loadSelfData("demo/data/userData.json", "demo/userDelete.json");
        objects.forEach(json -> {
            this.doRequest((JSONObject) json, true);
        });
    }
}