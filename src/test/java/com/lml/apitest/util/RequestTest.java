package com.lml.apitest.util;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.demo.UserDto;
import com.lml.apitest.ext.HttpExt;
import com.lml.apitest.ext.ReqAdapter;
import com.lml.apitest.ext.ReqExt;
import com.lml.apitest.ext.RestUtilExt;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
@Slf4j
public class RequestTest {

    private static final String URL = InitUtil.getSettingDto().getBaseUrl();

    private UserDto reqUserDto = new UserDto().setName("测试中文").setPwd("111111");


    @DataProvider(name = "reqAdapters")
    public Object[][] reqAdapters() {
        ReqExt restUtilExt = new RestUtilExt();
        ReqExt httpExt = new HttpExt();
        ReqAdapter reqAdapter1 = new ReqAdapter(restUtilExt);
        ReqAdapter reqAdapter2 = new ReqAdapter(httpExt);
        return new Object[][]{{reqAdapter1}, {reqAdapter2}};
    }


    @Test(dataProvider = "reqAdapters")
    public void getTest(ReqAdapter reqAdapter) {
        log.info("使用:{}...............", reqAdapter);
        String reqUrl = URL + "/get";
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", reqUserDto.getName());
        // 参数在map上
        JSONObject vo = reqAdapter.get(reqUrl, JSONObject.class, map, null).getResult();
        Assert.assertEquals(URLUtil.decode(vo.getStr("data")), reqUserDto.getName());

        // 参数直接在url上
        reqUrl = URL + "/get?name=" + URLUtil.encode(reqUserDto.getName(), StandardCharsets.UTF_8);
        vo = reqAdapter.get(reqUrl, JSONObject.class, null, null).getResult();
        Assert.assertEquals(URLUtil.decode(vo.getStr("data")), reqUserDto.getName());
    }

    @Test(dataProvider = "reqAdapters")
    public void postForJsonTest(ReqAdapter reqAdapter) {
        log.info("使用:{}...............", reqAdapter);
        String reqUrl = URL + "/postForJson";
        JSONObject vo = reqAdapter.post(reqUrl, reqUserDto, JSONObject.class, null).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(response.getName(), reqUserDto.getName());
        Assert.assertEquals(response.getPwd(), reqUserDto.getPwd());
    }

    @Test(dataProvider = "reqAdapters")
    public void postForFormTest(ReqAdapter reqAdapter) {
        log.info("使用:{}...............", reqAdapter);
        String reqUrl = URL + "/postForForm";
        Map<String, Object> map = Maps.newHashMap();
        map.put("hello", "hello");
        map.put("okc", "okc");
        JSONObject vo = reqAdapter.postForForm(reqUrl, reqUserDto, JSONObject.class, map, null).getResult();
        UserDto response = JSONUtil.toBean(vo.getStr("data"), UserDto.class);
        Assert.assertEquals(response.getName(), reqUserDto.getName());
        Assert.assertEquals(response.getPwd(), reqUserDto.getPwd());

    }

    @Test(dataProvider = "reqAdapters")
    public void deleteTest(ReqAdapter reqAdapter) {
        log.info("使用:{}...............", reqAdapter);
        String reqUrl = URL + "/delete?name=" + URLUtil.encode(reqUserDto.getName(), StandardCharsets.UTF_8);
        JSONObject vo = reqAdapter.delete(reqUrl, JSONObject.class, null, null).getResult();
        Assert.assertEquals(URLUtil.decode(vo.getStr("data")), reqUserDto.getName());

    }

    @Test(dataProvider = "reqAdapters")
    public void putTest(ReqAdapter reqAdapter) {
        log.info("使用:{}...............", reqAdapter);
        String reqUrl = URL + "/put";
        String result = reqAdapter.put(reqUrl, reqUserDto, String.class, null).getResult();
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject data = jsonObject.getJSONObject("data");
        UserDto user = JSONUtil.toBean(data.toString(), UserDto.class);
        Assert.assertEquals(user.getName(), reqUserDto.getName());
        Assert.assertEquals(user.getPwd(), reqUserDto.getPwd());
    }

}