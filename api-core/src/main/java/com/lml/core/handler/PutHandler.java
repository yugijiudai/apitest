package com.lml.core.handler;

import cn.hutool.json.JSONObject;
import com.lml.core.dto.RequestDto;
import com.lml.core.enums.MethodEnum;
import com.lml.core.vo.RestVo;

/**
 * @author yugi
 * @apiNote put请求处理器
 * @since 2019-08-06
 */
public class PutHandler implements RequestHandler {

    @Override
    public MethodEnum getMethod() {
        return MethodEnum.PUT;
    }

    @Override
    public RestVo<JSONObject> handleRequest(RequestDto requestDto) {
        return REQ_ADAPTER.put(requestDto, JSONObject.class);
    }
}
