package com.lml.apitest;

import com.google.common.collect.Lists;
import com.lml.apitest.util.ApiClientUtil;
import com.lml.apitest.handler.AssertCallBackHandler;
import com.lml.apitest.handler.RequestCallBackHandler;

import java.util.List;

/**
 * @author yugi
 * @apiNote 测试的基础类
 * @since 2019-08-07
 */
public abstract class BaseTest {

    private List<RequestCallBackHandler> callBackLists = Lists.newArrayList(new AssertCallBackHandler());

    protected void doRequest(String script) {
        ApiClientUtil.doApiRequest(script, callBackLists);
    }

    protected void selfDoRequest(String script, List<RequestCallBackHandler> selfHandler) {
        List<RequestCallBackHandler> list = Lists.newArrayList(callBackLists);
        list.addAll(selfHandler);
        ApiClientUtil.doApiRequest(script, list);
    }
}
