package com.lml.apitest.util;

import com.google.common.collect.Lists;
import com.lml.apitest.BaseTest;
import com.lml.apitest.handler.RequestCallBackHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class ApiClientUtilTest extends BaseTest {


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
    public void getUserTest() {
        RequestCallBackHandler loginHandler = (actual, ext) -> {
            List<String> cookies = actual.getHttpHeaders().get("Set-Cookie");
            if (CollectionUtils.isNotEmpty(cookies)) {
                for (String cookie : cookies) {
                    String[] split = cookie.split(";");
                    for (String tmp : split) {
                        String[] values = tmp.split("=");
                        if (values[0].equals("JSESSIONID")) {
                            GlobalVariableUtil.setCache("${sessionId}", values[1]);
                        }
                    }
                }
            }
            GlobalVariableUtil.setCache("${username}", "lml");
        };
        this.selfDoRequest("demo/userLogin.json", true, Lists.newArrayList(loginHandler));
        this.doRequest("demo/getUser.json");
    }

    @Test
    public void userLoginTest() {
        this.doRequest("demo/userLogin.json");
    }

    @Test
    public void formatTest() {
        InitUtil.loadReqContent("demo/testFormat.json");
    }
}