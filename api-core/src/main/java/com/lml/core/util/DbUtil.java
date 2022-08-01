package com.lml.core.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.db.Db;
import cn.hutool.db.ds.DSFactory;
import com.lml.core.dto.SettingDto;
import com.lml.core.enums.DataSourcePoolEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;

/**
 * @author yugi
 * @apiNote db的工具类
 * @since 2019-05-14
 */
@UtilityClass
@Slf4j
public class DbUtil {


    /**
     * 获取对应的数据源,可以用到的数据池
     * <code>
     * new HikariDSFactory()<br/>
     * new DruidDSFactory()<br/>
     * new TomcatDSFactory()<br/>
     * new BeeDSFactory()<br/>
     * new DbcpDSFactory()<br/>
     * new C3p0DSFactory()<br/>
     * new PooledDSFactory()<br/>
     * </code>
     *
     * @return 返回对应的数据源
     */
    public Db getDb() {
        SettingDto settingDto = InitUtil.getSettingDto();
        String dataSourcePool = settingDto.getDataSourcePool();
        if (StringUtils.isNotBlank(dataSourcePool)) {
            // 如果有指定的数据源连接池,则使用指定的
            DataSourcePoolEnum dataSourcePoolEnum = DataSourcePoolEnum.parse(dataSourcePool);
            if (dataSourcePoolEnum != null) {
                DSFactory obj = ReflectUtil.newInstance(dataSourcePoolEnum.getVal());
                DSFactory.setCurrentDSFactory(obj);
            }
        }
        // 如果为空则默认使用dev
        String dbConfig = settingDto.getDataSourceConfig();
        if (StringUtils.isBlank(dbConfig)) {
            dbConfig = "dev";
        }
        DataSource dataSource = DSFactory.get(dbConfig);
        return Db.use(dataSource);
    }


}
