package com.lml.web.util;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.lml.core.handler.RequestCallBackHandler;
import com.lml.core.util.ApiClientUtil;
import com.lml.core.util.GlobalVariableUtil;
import com.lml.web.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class ApiClientUtilTest extends BaseTest {


    @BeforeClass
    public void doBefore() {
        GlobalVariableUtil.setCache("${username}", "lml");
    }

    @Test
    public void userPostTest() {
        this.doRequest("demo/userPost.json");
    }

    @Test
    public void userPostFormTest() {
        this.doRequest("demo/userPostForm.json");
    }

    @Test
    public void userPutTest() {
        this.doRequest("demo/userPut.json");
    }

    @Test
    public void userGetTest() {
        this.doRequest("demo/userGet.json");
    }

    @Test
    public void userDeleteTest() {
        this.doRequest("demo/userDelete.json");
    }


    @Test
    public void uploadFileTest() {
        String file1 = "demo/uploadFile.json";
        String file2 = "demo/getUser.json";
        GlobalVariableUtil.setCache("${file1}", file1);
        GlobalVariableUtil.setCache("${file2}", file2);
        this.doRequest(file1);
    }


    @Test
    public void getUserTest() {
        RequestCallBackHandler loginHandler = (actual, ext) -> {
            JSONObject result = actual.getResult();
            // 登录返回了一个随机值
            GlobalVariableUtil.setCache("${random}", result.getStr("data"));
            List<Object> list = ApiClientUtil.getCookieByKey(actual.getHttpHeaders(), "JSESSIONID");
            GlobalVariableUtil.setCache("${sessionId}", list.get(0));
        };
        // 先登录,然后回调中获取sessionId和登录时返回的随机字符串
        this.selfDoRequest("demo/userLogin.json", true, Lists.newArrayList(loginHandler));
        this.doRequest("demo/getUser.json");
    }

    @Test
    public void userLoginTest() {
        this.doRequest("demo/userLogin.json");
    }


}