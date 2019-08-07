package com.lml.apitest.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.demo.UserDto;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class RestUtilTest {

    private static final String URL = InitUtil.getSettingDto().getBaseUrl();

    private UserDto reqUserDto = new UserDto().setName("测试中文").setPwd("111111");

    @Test
    public void getTest() {
        String reqUrl = URL + "/get?name=" + reqUserDto.getName();
        JSONObject vo = RestUtil.get(reqUrl, JSONObject.class, null).getResult();
        Assert.assertEquals(reqUserDto.getName(), vo.getStr("data"));
    }

    @Test
    public void postForJsonTest() {
        String reqUrl = URL + "/postForJson";
        JSONObject vo = RestUtil.post(reqUrl, reqUserDto, JSONObject.class).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());
    }

    @Test
    public void postForFormTest() {
        String reqUrl = URL + "/postForForm";
        JSONObject vo = RestUtil.postForForm(reqUrl, reqUserDto, JSONObject.class).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());
    }

    @Test
    public void deleteTest() {
        String reqUrl = URL + "/delete?name=" + reqUserDto.getName();
        JSONObject vo = RestUtil.delete(reqUrl, JSONObject.class, null).getResult();
        Assert.assertEquals(reqUserDto.getName(), vo.getStr("data"));
    }

    @Test
    public void putTest() {
        String reqUrl = URL + "/put";
        String result = RestUtil.put(reqUrl, reqUserDto, String.class).getResult();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject data = jsonObject.getJSONObject("data");
        UserDto user = JSONUtil.toBean(data.toString(), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), user.getName());
        Assert.assertEquals(reqUserDto.getPwd(), user.getPwd());
    }

}