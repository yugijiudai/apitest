package com.lml.web.util;

import com.lml.core.dto.ExcelDto;
import com.lml.core.util.MyExcelUtil;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2019-10-28
 */
public class MyExcelUtilTest {

    @Test
    public void testLoadExcel() {
        List<ExcelDto> list = MyExcelUtil.loadExcel("student.xls", ExcelDto.class);
        for (ExcelDto excelDto : list) {
            System.out.println(excelDto);
        }
    }

}