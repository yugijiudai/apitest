package com.lml.core.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 请求的方式枚举
 * @since 2019-08-06
 */
@Getter
public enum MethodEnum {

    /**
     * postJson方式
     */
    POST("post"),

    /**
     * postFormData方式
     */
    POST_FROM_DATA("postFromData"),

    /**
     * get方式
     */
    GET("get"),

    /**
     * put方式
     */
    PUT("put"),

    /**
     * delete方式
     */
    DELETE("delete"),

    ;

    /**
     * 请求的方式
     */
    private String method;

    MethodEnum(String method) {
        this.method = method;
    }

    public static MethodEnum parese(String method) {
        MethodEnum[] values = MethodEnum.values();
        for (MethodEnum value : values) {
            if (value.getMethod().equals(method)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(MethodEnum.class, method);
    }

}
