package com.lml.core.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote 用于存放restUtil请求返回参数的类
 * @since 2019-08-07
 */
@Data
@Accessors(chain = true)
public class RestVo<T> {

    private Map<String, List<String>> httpHeaders;

    private T result;

}
