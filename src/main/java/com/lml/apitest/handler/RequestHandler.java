package com.lml.apitest.handler;

import cn.hutool.json.JSONObject;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.util.ApiClientUtil;
import com.lml.apitest.vo.RestVo;

/**
 * @author yugi
 * @apiNote 请求处理器
 * @since 2019-08-06
 */
public interface RequestHandler {

    /**
     * 获取这个处理器需要处理的方法类型
     *
     * @return {@link MethodEnum}
     */
    MethodEnum getMethod();

    /**
     * 处理请求
     *
     * @param requestDto {@link RequestDto}
     * @return {@link RestVo}
     */
    RestVo<JSONObject> handleRequest(RequestDto requestDto);

    /**
     * 默认的处理
     *
     * @param requestDto {@link RequestDto}
     * @return {@link RestVo}
     */
    default RestVo<JSONObject> doHandle(RequestDto requestDto) {
        // TODO yugi: 2019/8/6  url可以相对路径或者全路径
        String baseUrl = ApiClientUtil.getUrl();
        requestDto.setUrl(baseUrl + requestDto.getUrl());
        return handleRequest(requestDto);
    }


}
