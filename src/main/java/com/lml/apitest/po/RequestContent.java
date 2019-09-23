package com.lml.apitest.po;

import cn.hutool.http.Method;
import com.lml.apitest.enums.RequestStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 请求的实体类, 用于记录请求的各种详情
 * @since 2019-09-20
 */
@Data
@Accessors(chain = true)
public class RequestContent {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 请求的方法
     */
    private Method method;

    /**
     * 请求开始时间
     */
    private Date startTime;

    /**
     * 请求结束时间
     */
    private Date endTime;

    /**
     * 请求头部
     */
    private Map<String, Object> headers;

    /**
     * 请求内容体
     */
    private String content;

    /**
     * 请求状态
     */
    private RequestStatusEnum requestStatus;


    /**
     * 请求的路径
     */
    private String url;

    /**
     * 相关异常信息,只有请求失败才会出现
     */
    private String exceptionMsg;

}
