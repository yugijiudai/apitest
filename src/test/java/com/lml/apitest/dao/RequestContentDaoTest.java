package com.lml.apitest.dao;

import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.lml.apitest.po.RequestContent;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote
 * @since 2019-09-20
 */
public class RequestContentDaoTest {


    private RequestContentDao requestContentDao = new RequestContentDao();

    @Test
    public void testAdd() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "okc");
        map.put("cnt", 123456);
        JSONObject content = new JSONObject();
        content.put("sid", 111);
        content.put("pwd", "1234");
        RequestContent requestContent = new RequestContent().setUrl("http://").setStartTime(new Date()).setMethod(Method.POST).setHeaders(map).setContent(JSONUtil.toJsonStr(content));
        System.out.println(requestContentDao.add(requestContent));
    }

    @Test
    public void testFindAll() {
        List<RequestContent> all = requestContentDao.findAll();
        for (RequestContent requestContent : all) {
            System.out.println(requestContent);
        }
    }

}