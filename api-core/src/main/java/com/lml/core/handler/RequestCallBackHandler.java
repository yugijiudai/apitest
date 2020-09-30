package com.lml.core.handler;

import cn.hutool.json.JSONObject;
import com.lml.core.vo.RestVo;

/**
 * @author yugi
 * @apiNote 请求回调处理器
 * @since 2019-08-07
 */
public interface RequestCallBackHandler {

    /**
     * 请求回来后自定义的回调方法
     *
     * @param actual 请求回来的数据
     * @param ext    预留参数
     */
    void doCallBack(RestVo<JSONObject> actual, JSONObject ext);
}
