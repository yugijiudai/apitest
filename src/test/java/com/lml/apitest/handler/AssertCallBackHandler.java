package com.lml.apitest.handler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.vo.RestVo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.util.Map;

/**
 * @author yugi
 * @apiNote 断言回调处理器
 * @since 2019-08-07
 */
public class AssertCallBackHandler implements RequestCallBackHandler {


    @Override
    public void doCallBack(RestVo<JSONObject> actual, JSONObject ext) {
        // TODO yugi: 2019/8/7  获取code,msg,data需要在配置文件里配置
        JSONObject actualVo = actual.getResult();
        Assert.assertEquals(ext.getInt("code"), actualVo.getInt("code"));
        String expectMsg = ext.getStr("msg");
        if (StringUtils.isNotBlank(expectMsg)) {
            Assert.assertEquals(expectMsg, actualVo.getStr("msg"));
        }
        String expectData = ext.getStr("data");
        if (StringUtils.isNotBlank(expectData)) {
            String actualData = actualVo.getStr("data");
            if (JSONUtil.isJsonObj(expectData)) {
                JSONObject expectJsonData = JSONUtil.parseObj(expectData);
                JSONObject actualJsonData = JSONUtil.parseObj(actualData);
                for (Map.Entry<String, Object> entry : expectJsonData.entrySet()) {
                    Assert.assertEquals(entry.getValue(), actualJsonData.get(entry.getKey()));
                }
            }
            else if (JSONUtil.isJsonArray(expectData)) {
                JSONArray expectJsonArray = JSONUtil.parseArray(expectData);
                JSONArray actualJsonArray = JSONUtil.parseArray(actualData);
                Assert.assertEquals(expectJsonArray, actualJsonArray);
            }
        }
    }
}
