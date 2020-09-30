package com.lml.core.util;

import cn.hutool.db.Db;
import com.lml.core.enums.DataSourceEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote db的工具类
 * @since 2019-05-14
 */
@UtilityClass
@Slf4j
public class DbUtil {


    /**
     * 获取对应的数据源
     *
     * @param dataSourceEnum 数据源枚举
     * @return 返回对应的数据源
     */
    public Db getDb(DataSourceEnum dataSourceEnum) {
        return Db.use(dataSourceEnum.getDataSource());
    }


}
