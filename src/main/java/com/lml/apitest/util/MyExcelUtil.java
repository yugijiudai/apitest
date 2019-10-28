package com.lml.apitest.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.apitest.annotations.ExcelColumn;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
    public <T> List<T> loadExcel(String fileName, Class<T> clz, Integer headerRowIndex, Integer startRowIndex, Integer endRowIndex) {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(fileName));
        headerRowIndex = headerRowIndex == null ? 0 : headerRowIndex;
        startRowIndex = startRowIndex == null ? 0 : startRowIndex;
        endRowIndex = endRowIndex == null ? reader.getSheetCount() : endRowIndex;
        Map<String, Field> fieldMap = aliasField(clz);
        List<Map<String, Object>> read = reader.read(headerRowIndex, startRowIndex, endRowIndex);
        List<T> result = Lists.newLinkedList();
        for (Map<String, Object> map : read) {
            T newClz = ReflectUtil.newInstance(clz);
            map.forEach((key, val) -> {
                Field field = MapUtils.getObject(fieldMap, key);
                // 获取set方法
                Method methodByName = ReflectUtil.getMethodByName(clz, true, "set" + field.getName());
                // 将excel的值转成field的对应类型,不然这里反射set不到
                Object convert = Convert.convert(field.getType(), val);
                ReflectUtil.invoke(newClz, methodByName.getName(), convert);
            });
            result.add(newClz);
        }
        return result;
    }


    /**
     * 如果列有别名注释,则设置成别名的注释
     *
     * @return 最后返回一个key是列的名字, val是列的Field类map
     */
    private Map<String, Field> aliasField(Class clz) {
        Field[] fields = ReflectUtil.getFields(clz);
        Map<String, Field> fieldMap = Maps.newHashMap();
        for (Field field : fields) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            fieldMap.put(annotation != null ? annotation.alias() : field.getName(), field);
        }
        return fieldMap;
    }
}
