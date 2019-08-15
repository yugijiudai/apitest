package com.lml.apitest.handler;

import cn.hutool.json.JSONObject;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.dto.SettingDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.ext.HttpExt;
import com.lml.apitest.ext.ReqAdapter;
import com.lml.apitest.util.InitUtil;
import com.lml.apitest.vo.RestVo;

/**
 * @author yugi
 * @apiNote 请求处理器
 * @since 2019-08-06
 */
public interface RequestHandler {

    // ReqAdapter REQ_ADAPTER = new ReqAdapter(new RestUtilExt());
    ReqAdapter REQ_ADAPTER = new ReqAdapter(new HttpExt());

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
