package com.lml.apitest.handler;

import cn.hutool.json.JSONObject;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.vo.RestVo;

/**
 * @author yugi
 * @apiNote postJson处理器
 * @since 2019-08-06
 */
public class PostHandler implements RequestHandler {

    @Override
    public MethodEnum getMethod() {
        return MethodEnum.POST;
    }

    @Override
    public RestVo<JSONObject> handleRequest(RequestDto requestDto) {
        return REQ_ADAPTER.post(requestDto, JSONObject.class);
    }
}
