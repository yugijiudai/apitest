package com.lml.apitest.util;

import com.lml.apitest.handler.ApiClientUtil;
import org.junit.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class ApiClientUtilTest {

    @Test
    public void userPostTest() {
        ApiClientUtil.doApiRequest("demo/userPost.json");
    }

    @Test
    public void userPostFormTest() {
        ApiClientUtil.doApiRequest("demo/userPostForm.json");
    }

    @Test
    public void userPutTest() {
        ApiClientUtil.doApiRequest("demo/userPut.json");
    }

    @Test
    public void userGetTest() {
        ApiClientUtil.doApiRequest("demo/userGet.json");
    }

    @Test
    public void userDeleteTest() {
        ApiClientUtil.doApiRequest("demo/userDelete.json");
    }
}