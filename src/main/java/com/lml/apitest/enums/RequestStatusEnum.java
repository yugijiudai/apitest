package com.lml.apitest.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote
 * @since 2019-09-20
 */
@Getter
public enum RequestStatusEnum {

    /**
     * 请求成功
     */
    OK("ok", "请求成功"),

    /**
     * 请求失败
     */
    FAIL("fail", "请求失败"),

    ;

    private String code;

    private String description;

    RequestStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
