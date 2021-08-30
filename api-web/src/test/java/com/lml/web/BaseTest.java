package com.lml.web;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.lml.core.handler.RequestCallBackHandler;
import com.lml.core.util.ApiClientUtil;
import com.lml.web.handler.AssertCallBackHandler;

import java.util.List;

/**
 * @author yugi
 * @apiNote 测试的基础类
 * @since 2019-08-07
 */
public abstract class BaseTest {

    private List<RequestCallBackHandler> callBackLists = Lists.newArrayList(new AssertCallBackHandler());

    /**
     * 加载脚本执行请求
     *
     * @param script 需要加载的脚本
     */
    protected void doRequest(String script) {
        this.doRequest(script, true);
    }


    /**
     * 加载脚本执行请求
     *
     * @param script             需要加载的脚本
     * @param useDefaultCallBack 是否要执行默认的回调
     */
    protected void doRequest(String script, boolean useDefaultCallBack) {
        ApiClientUtil.doApiRequestCallBack(script, useDefaultCallBack ? callBackLists : null);
    }


    /**
     * 传加载好的脚本执行请求
     *
     * @param json               加载好的脚本
     * @param useDefaultCallBack 是否要执行默认的回调
     */
    protected void doRequest(JSONObject json, boolean useDefaultCallBack) {
        ApiClientUtil.doApiRequestCallBack(json, useDefaultCallBack ? callBackLists : null);
    }

    /**
     * 传加载好的脚本执行请求,并且执行自定义的回调
     *
     * @param script             需要加载的脚本
     * @param useDefaultCallBack 是否要执行默认的回调
     * @param selfHandler        自定义的回调
     */
    protected void selfDoRequest(String script, boolean useDefaultCallBack, List<RequestCallBackHandler> selfHandler) {
        List<RequestCallBackHandler> list = Lists.newArrayList();
        if (useDefaultCallBack) {
            list.addAll(callBackLists);
        }
        list.addAll(selfHandler);
        ApiClientUtil.doApiRequestCallBack(script, list);
    }
}
