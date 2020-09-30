package com.lml.core.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Maps;
import com.lml.core.annotations.ExcelColumn;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote excel工具类
 * @since 2019-10-28
 */
@UtilityClass
@Slf4j
public class MyExcelUtil {

    /**
     * 加载excel到实体类列表
     *
     * @param fileName excel的名字
     * @param clz      实体类对应的class
     * @param <T>      对应的实体类类型
     * @return 实体类列表
     */
    public <T> List<T> loadExcel(String fileName, Class<T> clz) {
        return loadExcel(fileName, clz, null, null, null);
    }

    /**
     * 加载excel到实体类列表
     *
     * @param fileName       excel的名字
     * @param clz            实体类对应的class
     * @param headerRowIndex 标题所在行，如果标题行在读取的内容行中间，这行做为数据将忽略
     * @param startRowIndex  起始行（包含，从0开始计数）
     * @param endRowIndex    读取结束行（包含，从0开始计数）
     * @param <T>            对应的实体类类型
     * @return 实体类列表
     */
    private <T> List<T> loadExcel(String fileName, Class<T> clz, Integer headerRowIndex, Integer startRowIndex, Integer endRowIndex) {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(fileName));
        headerRowIndex = headerRowIndex == null ? 0 : headerRowIndex;
        startRowIndex = startRowIndex == null ? 0 : startRowIndex;
        endRowIndex = endRowIndex == null ? reader.getSheetCount() : endRowIndex;
        Map<String, String> mapping = fieldMapping(clz);
        List<Map<String, Object>> read = reader.read(headerRowIndex, startRowIndex, endRowIndex);
        return read.stream().map(map -> BeanUtil.mapToBean(map, clz, CopyOptions.create().setFieldMapping(mapping))).collect(Collectors.toList());
    }

    /**
     * 获取属性的映射
     *
     * @param clz excel最终映射到的实体类
     * @return 返回映射的map
     */
    private Map<String, String> fieldMapping(Class clz) {
        Map<String, String> mapping = Maps.newHashMap();
        Field[] fields = ReflectUtil.getFields(clz);
        for (Field field : fields) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            mapping.put(annotation != null ? annotation.alias() : field.getName(), field.getName());
        }
        return mapping;
    }


}
