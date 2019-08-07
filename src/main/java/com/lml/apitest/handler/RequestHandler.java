package com.lml.apitest.handler;

import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.vo.ApiVo;

/**
 * @author yugi
 * @apiNote 请求处理器
 * @since 2019-08-06
 */
public interface RequestHandler {

    String HEADER_KEY = "header";

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
     * @return {@link ApiVo}
     */
    ApiVo handleRequest(RequestDto requestDto);

    /**
     * 默认的处理
     *
     * @param requestDto {@link RequestDto}
     * @return {@link ApiVo}
     */
    default ApiVo doHandle(RequestDto requestDto) {
        // TODO yugi: 2019/8/6  url可以相对路径或者全路径
        String baseUrl = ApiClientUtil.getUrl();
        requestDto.setUrl(baseUrl + requestDto.getUrl());
        return handleRequest(requestDto);
    }


}
