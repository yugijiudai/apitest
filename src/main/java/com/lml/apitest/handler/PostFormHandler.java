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
 * @apiNote postFormData处理器
 * @since 2019-08-06
 */
public class PostFormHandler implements RequestHandler {

    @Override
    public MethodEnum getMethod() {
        return MethodEnum.POST_FROM_DATA;
    }

    @Override
    public ApiVo handleRequest(RequestDto requestDto) {
        JSONObject reqObj = JSONUtil.parseObj(requestDto.getParam());
        String header = reqObj.getStr(HEADER_KEY);
        if (StringUtils.isBlank(header)) {
            return RestUtil.postForForm(requestDto.getUrl(), reqObj, ApiVo.class);
        }
        JSONObject headerJson = JSONUtil.parseObj(header);
        return RestUtil.postForForm(requestDto.getUrl(), reqObj, ApiVo.class, headerJson);
    }
}
