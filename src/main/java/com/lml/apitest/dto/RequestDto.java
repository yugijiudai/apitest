package com.lml.apitest.dto;

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
     * 请求参数
     */
    private String param;

    /**
     * 请求的相对路径
     */
    private String url;

}
