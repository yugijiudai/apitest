package com.lml.apitest.dto;

import com.lml.apitest.vo.ApiVo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 用来封装传参断言的dto
 * @since 2019-08-06
 */
@Data
@Accessors(chain = true)
public class ParaDto {

    /**
     * 请求的dto
     */
    private RequestDto requestDto;

    /**
     * 需要断言的vo
     */
    private ApiVo response;

}
