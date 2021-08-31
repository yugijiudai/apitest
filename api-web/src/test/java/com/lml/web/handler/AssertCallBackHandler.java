package com.lml.web.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.core.dto.SettingDto;
import com.lml.core.handler.RequestCallBackHandler;
import com.lml.core.util.ApiClientUtil;
import com.lml.core.util.InitUtil;
import com.lml.core.vo.RestVo;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import java.util.List;
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
        this.assertHeader(ext, actual.getHttpHeaders());
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
        Assert.assertEquals(actualVo.getInt(settingDto.getCode()), ext.getInt(settingDto.getCode()));
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
                Assert.assertEquals(actualJsonData.get(entry.getKey()), entry.getValue());
            }
        }
        else if (JSONUtil.isJsonArray(expectData)) {
            JSONArray expectJsonArray = JSONUtil.parseArray(expectData);
            JSONArray actualJsonArray = JSONUtil.parseArray(actualData);
            Assert.assertEquals(expectJsonArray, actualJsonArray);
        }
        else {
            Assert.assertEquals(actualData, expectData);
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
            Assert.assertEquals(actualVo.getStr(settingDto.getMsg()), expectMsg);
        }
    }

    /**
     * 断言响应头
     *
     * @param ext           需要断言的json格式
     * @param actualHeaders 实际返回来的响应头
     */
    private void assertHeader(JSONObject ext, Map<String, List<String>> actualHeaders) {
        JSONObject expectHeader = ext.getJSONObject(ApiClientUtil.HEADER);
        if (expectHeader == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : expectHeader.entrySet()) {
            String key = entry.getKey();
            List<String> actualHeaderValue = actualHeaders.getOrDefault(key, null);
            Assert.assertNotNull(actualHeaderValue, StrUtil.format("找不到key为:{}的响应头", key));
            Assert.assertEquals(actualHeaderValue.get(0), entry.getValue(), StrUtil.format("响应头:【{}】断言失败", key));
        }
    }


}
