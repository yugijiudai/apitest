package com.lml.core.dto;

import com.lml.core.annotations.ExcelColumn;
import lombok.Data;

/**
 * @author yugi
 * @apiNote 用来存放excel的数据传输类
 * @since 2019-10-28
 */
@Data
public class ExcelDto {

    private Integer id;

    @ExcelColumn(alias = "姓名")
    private String name;

    private Integer score;

}
