package com.lml.apitest.handler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.SettingDto;
import com.lml.apitest.util.InitUtil;
import com.lml.apitest.vo.RestVo;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

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
        this.assertCode(ext, actualVo);
        this.assertMsg(ext, actualVo);
        this.assertData(ext, actualVo);
    }

    /**
     * 断言状态码
     *
     * @param ext      需要断言的json格式
     * @param actualVo 实际返回的数据
     */
    private void assertCode(JSONObject ext, JSONObject actualVo) {
        SettingDto settingDto = InitUtil.getSettingDto();
        // 断言状态码,状态码不能为空
        Assert.assertEquals(ext.getInt(settingDto.getCode()), actualVo.getInt(settingDto.getCode()));
    }

    /**
     * 断言返回的内容
     *
     * @param ext      需要断言的json格式
     * @param actualVo 实际返回的数据
     */
    private void assertData(JSONObject ext, JSONObject actualVo) {
        SettingDto settingDto = InitUtil.getSettingDto();
        String expectData = ext.getStr(settingDto.getData());
        if (StringUtils.isBlank(expectData)) {
            return;
        }
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

    /**
     * 断言消息体
     *
     * @param ext      需要断言的json格式
     * @param actualVo 实际返回的数据
     */
    private void assertMsg(JSONObject ext, JSONObject actualVo) {
        SettingDto settingDto = InitUtil.getSettingDto();
        String expectMsg = ext.getStr(settingDto.getMsg());
        // 断言返回信息,提示消息可以为空
        if (StringUtils.isNotBlank(expectMsg)) {
            Assert.assertEquals(expectMsg, actualVo.getStr(settingDto.getMsg()));
        }
    }


}
