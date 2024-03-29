package com.lml.core.handler;

import cn.hutool.json.JSONObject;
import com.lml.core.dto.RequestDto;
import com.lml.core.dto.SettingDto;
import com.lml.core.enums.MethodEnum;
import com.lml.core.ext.ReqAdapter;
import com.lml.core.util.InitUtil;
import com.lml.core.vo.RestVo;

/**
 * @author yugi
 * @apiNote 请求处理器
 * @since 2019-08-06
 */
public interface RequestHandler {

    ReqAdapter REQ_ADAPTER = InitUtil.initReqAdapter(null);

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
        SettingDto settingDto = InitUtil.getSettingDto();
        String baseUrl = settingDto.getBaseUrl();
        // 如果是使用相对路径,则重新拼接好要请求的url地址
        if (requestDto.isUseRelativeUrl()) {
            requestDto.setUrl(baseUrl + requestDto.getUrl());
        }
        return handleRequest(requestDto);
    }


}
