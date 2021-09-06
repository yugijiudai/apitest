package com.lml.core.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 请求的dto
 * @since 2019-08-06
 */
@Data
@Accessors(chain = true)
public class RequestDto {

    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口名字
     */
    private String name;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 请求的相对路径
     */
    private String url;

    /**
     * 请求头
     */
    private JSONObject headers;

    /**
     * url是否使用相对路径,如果这个值为false,则url要写全路径
     */
    private boolean useRelativeUrl = true;

    /**
     * 上传的文件,如果有才需要填
     */
    private JSONObject file;

    /**
     * 请求组的名字
     * 如果请求组为空的时候,使用ReqHolder+RequestTrace的方式来动态生成请求组
     */
    private String requestGroup;

}
