package com.lml.apitest;

import com.google.common.collect.Lists;
import com.lml.apitest.handler.AssertCallBackHandler;
import com.lml.apitest.handler.RequestCallBackHandler;
import com.lml.apitest.util.ApiClientUtil;

import java.util.List;

/**
 * @author yugi
 * @apiNote 测试的基础类
 * @since 2019-08-07
 */
public abstract class BaseTest {

    private List<RequestCallBackHandler> callBackLists = Lists.newArrayList(new AssertCallBackHandler());

    protected void doRequest(String script) {
        this.doRequest(script, true);
    }

    protected void doRequest(String script, boolean useDefaultCallBack) {
        ApiClientUtil.doApiRequest(script, useDefaultCallBack ? callBackLists : null);
    }

    protected void selfDoRequest(String script, boolean useDefaultCallBack, List<RequestCallBackHandler> selfHandler) {
        List<RequestCallBackHandler> list = Lists.newArrayList();
        if (useDefaultCallBack) {
            list.addAll(callBackLists);
        }
        list.addAll(selfHandler);
        ApiClientUtil.doApiRequest(script, list);
    }
}
