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
 * @apiNote delete请求处理器
 * @since 2019-08-06
 */
public class DeleteHandler implements RequestHandler {

    @Override
    public MethodEnum getMethod() {
        return MethodEnum.DELETE;
    }

    @Override
    public ApiVo handleRequest(RequestDto requestDto) {
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        String header = reqObj.getStr(HEADER_KEY);
        if (StringUtils.isBlank(header)) {
            return RestUtil.delete(requestDto.getUrl(), ApiVo.class, reqObj);
        }
        JSONObject headerJson = JSONUtil.parseObj(header);
        return RestUtil.delete(requestDto.getUrl(), ApiVo.class, reqObj, headerJson);
    }
}
