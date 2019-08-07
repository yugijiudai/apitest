package com.lml.apitest.util;

import com.lml.apitest.vo.ApiVo;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
@UtilityClass
public class ApiUtil {


    public <T> ApiVo<T> buildSuccess(T data) {
        return new ApiVo<>(data);
    }

    public <T> ApiVo<T> error(int code, String codeMsg) {
        return new ApiVo<>(code, codeMsg);
    }

    public <T> ApiVo<T> error(String codeMsg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), codeMsg);
    }

}
