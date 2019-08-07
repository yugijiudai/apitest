package com.lml.apitest.util;

import com.lml.apitest.BaseTest;
import org.junit.Test;

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
}