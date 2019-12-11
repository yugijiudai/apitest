package com.lml.apitest.handler;

import cn.hutool.json.JSONObject;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.vo.RestVo;

/**
 * @author yugi
 * @apiNote postFormData处理器
 * @since 2019-08-06
 */
public class PostFormHandler implements RequestHandler {

    @Override
    public MethodEnum getMethod() {
        return MethodEnum.POST_FROM_DATA;
    }

    @Override
    public RestVo<JSONObject> handleRequest(RequestDto requestDto) {
        return REQ_ADAPTER.postForForm(requestDto, JSONObject.class);
    }
}
