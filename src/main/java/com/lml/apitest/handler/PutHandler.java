package com.lml.apitest.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.util.RestUtil;
import com.lml.apitest.vo.ApiVo;
import org.apache.commons.lang3.StringUtils;

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
    public ApiVo handleRequest(RequestDto requestDto) {
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        String header = reqObj.getStr(HEADER_KEY);
        if (StringUtils.isBlank(header)) {
            return RestUtil.put(requestDto.getUrl(), reqObj, ApiVo.class);
        }
        JSONObject headerJson = JSONUtil.parseObj(header);
        return RestUtil.put(requestDto.getUrl(), reqObj, ApiVo.class, headerJson);
    }
}
