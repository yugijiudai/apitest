package com.lml.apitest.util;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.demo.UserDto;
import com.lml.apitest.ext.HttpExt;
import com.lml.apitest.ext.ReqAdapter;
import com.lml.apitest.ext.ReqExt;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
@Slf4j
public class HttpExtTest {

    private static final String URL = InitUtil.getSettingDto().getBaseUrl();

    private ReqExt reqExt = new HttpExt();

    private ReqAdapter reqAdapter = new ReqAdapter(reqExt);

    private UserDto reqUserDto = new UserDto().setName("测试中文").setPwd("111111");


    @Test
    public void getTest() {
        String reqUrl = URL + "/get";
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", reqUserDto.getName());
        // 参数在map上
        JSONObject vo = reqAdapter.get(reqUrl, JSONObject.class, map, null).getResult();
        Assert.assertEquals(reqAdapter.toString(), reqUserDto.getName(), URLUtil.decode(vo.getStr("data")));

        // 参数直接在url上
        reqUrl = URL + "/get?name=" + URLUtil.encode(reqUserDto.getName(), StandardCharsets.UTF_8);
        vo = reqAdapter.get(reqUrl, JSONObject.class, null, null).getResult();
        Assert.assertEquals(reqAdapter.toString(), reqUserDto.getName(), URLUtil.decode(vo.getStr("data")));
    }

    @Test
    public void postForJsonTest() {
        String reqUrl = URL + "/postForJson";
        JSONObject vo = reqAdapter.post(reqUrl, reqUserDto, JSONObject.class, null).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());
    }

    @Test
    public void postForFormTest() {
        String reqUrl = URL + "/postForForm";
        Map<String, Object> map = Maps.newHashMap();
        map.put("hello", "hello");
        map.put("okc", "okc");
        JSONObject vo = reqAdapter.postForForm(reqUrl, reqUserDto, JSONObject.class, map).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), response.getName());
        Assert.assertEquals(reqUserDto.getPwd(), response.getPwd());

    }

    @Test
    public void deleteTest() {
        String reqUrl = URL + "/delete?name=" + URLUtil.encode(reqUserDto.getName(), StandardCharsets.UTF_8);
        JSONObject vo = reqAdapter.delete(reqUrl, JSONObject.class, null, null).getResult();
        Assert.assertEquals(reqUserDto.getName(), vo.getStr("data"));

    }

    @Test
    public void putTest() {
        String reqUrl = URL + "/put";
        String result = reqAdapter.put(reqUrl, reqUserDto, String.class, null).getResult();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject data = jsonObject.getJSONObject("data");
        UserDto user = JSONUtil.toBean(data.toString(), UserDto.class);
        Assert.assertEquals(reqUserDto.getName(), user.getName());
        Assert.assertEquals(reqUserDto.getPwd(), user.getPwd());
    }

}