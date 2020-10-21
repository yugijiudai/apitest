package com.lml.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 配置文件的映射类, 不同项目返回来的三连可能名字不同, 通过修改配置文件的名字映射对应的接口返回的名字
 * @since 2019-08-07
 */
@Data
@Accessors(chain = true)
public class SettingDto {

    /**
     * 接口请求的基础地址
     */
    private String baseUrl;

    /**
     * 接口返回的状态码key
     */
    private String code;

    /**
     * 接口返回的消息key
     */
    private String msg;

    /**
     * 接口返回的内容key
     */
    private String data;

    /**
     * 请求使用的底层类
     */
    private String reqExt;

    /**
     * 自定义要加载的数据源池
     */
    private String dataSourcePool;

}
