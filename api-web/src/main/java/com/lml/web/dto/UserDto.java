package com.lml.web.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-07
 */
@Data
@Accessors(chain = true)
public class UserDto {

    private String name;

    private String pwd;

}
