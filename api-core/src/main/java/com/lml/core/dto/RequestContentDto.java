package com.lml.core.dto;

import cn.hutool.http.Method;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author yugi
 * @apiNote RequestContent数据传输类
 * @since 2019-09-23
 */
@Data
@Accessors(chain = true)
public class RequestContentDto {

    /**
     * 请求的id,数据库保存的请求表主键
     */
    private Integer requestId;

    /**
     * 请求的路径
     */
    private String url;

    /**
     * 接口的名字
     */
    private String name;

    /**
     * 请求头
     */
    private Map<String, Object> headers;

    /**
     * 请求的内容
     */
    private String content;

    /**
     * 请求的方法
     */
    private Method method;

    /**
     * 请求开始时间
     */
    private Date startTime;


    /**
     * 请求组的名字
     */
    private String requestGroup;

    /**
     * 当前请求的线程
     */
    private String threadName;


}
