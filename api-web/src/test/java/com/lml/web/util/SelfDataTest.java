package com.lml.web.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lml.core.util.InitUtil;
import com.lml.web.BaseTest;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class SelfDataTest extends BaseTest {


    @Test
    public void loadSelfDataTest() {
        JSONArray objects = InitUtil.loadSelfData("demo/apiClient/data/userData.json", "demo/apiClient/userDelete.json");
        objects.forEach(json -> {
            this.doRequest((JSONObject) json, true);
        });
    }
}