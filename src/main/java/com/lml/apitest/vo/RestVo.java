package com.lml.apitest.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpHeaders;

/**
 * @author yugi
 * @apiNote 用于存放restUtil请求返回参数的类
 * @since 2019-08-07
 */
@Data
@Accessors(chain = true)
public class RestVo<T> {

    private HttpHeaders httpHeaders;

    private T result;

}
