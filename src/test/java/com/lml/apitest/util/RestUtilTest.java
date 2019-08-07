package com.lml.apitest.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.UserDto;
import com.lml.apitest.vo.ApiVo;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class RestUtilTest {

    private static final String URL = "http://localhost:8080/";

    private UserDto reqUserDto = new UserDto().setName("测试中文").setPwd("111111");


    @Test
    public void getTest() {
        String reqUrl = URL + "/get?name=" + reqUserDto.getName();
        ApiVo vo = RestUtil.get(reqUrl, ApiVo.class, null);
        Assert.assertEquals(reqUserDto.getName(), vo.getData());
    }

    @Test
    public void postForJsonTest() {
        String reqUrl = URL + "/postForJson";
        ApiVo vo = RestUtil.post(reqUrl, reqUserDto, ApiVo.class);
        UserDto response = RestUtil.getResponse(vo, UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());
    }

    @Test
    public void postForFormTest() {
        String reqUrl = URL + "/postForForm";
        ApiVo vo = RestUtil.postForForm(reqUrl, reqUserDto, ApiVo.class);
        UserDto response = RestUtil.getResponse(vo, UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());
    }

    @Test
    public void deleteTest() {
        String reqUrl = URL + "/delete?name=" + reqUserDto.getName();
        ApiVo vo = RestUtil.delete(reqUrl, ApiVo.class, null);
        Assert.assertEquals(reqUserDto.getName(), vo.getData());
    }

    @Test
    public void putTest() {
        String reqUrl = URL + "/put";
        String result = RestUtil.put(reqUrl, reqUserDto, String.class);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject data = jsonObject.getJSONObject("data");
        UserDto user = JSONUtil.toBean(data.toString(), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), user.getName());
        Assert.assertEquals(reqUserDto.getPwd(), user.getPwd());
    }

}