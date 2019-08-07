package com.lml.apitest.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ApiVo<T> {

    private Integer code;

    private String msg;

    private T data;

    public ApiVo(T data) {
        this.data = data;
        this.code = HttpStatus.OK.value();
    }

    public ApiVo(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
