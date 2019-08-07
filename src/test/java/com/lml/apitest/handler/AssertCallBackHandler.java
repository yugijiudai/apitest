package com.lml.apitest.handler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.SettingDto;
import com.lml.apitest.util.InitUtil;
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
        JSONObject actualVo = actual.getResult();
        SettingDto settingDto = InitUtil.getSettingDto();
        // 断言状态码
        Assert.assertEquals(ext.getInt(settingDto.getCode()), actualVo.getInt(settingDto.getCode()));
        String expectMsg = ext.getStr(settingDto.getMsg());
        // 断言返回信息
        if (StringUtils.isNotBlank(expectMsg)) {
            Assert.assertEquals(expectMsg, actualVo.getStr(settingDto.getMsg()));
        }
        // 断言内容体
        String expectData = ext.getStr(settingDto.getData());
        if (StringUtils.isNotBlank(expectData)) {
            String actualData = actualVo.getStr(settingDto.getData());
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
            else {
                Assert.assertEquals(expectData, actualData);
            }
        }
    }
}
