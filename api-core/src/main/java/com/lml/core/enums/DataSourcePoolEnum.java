package com.lml.core.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 数据源池枚举
 * @since 2019-06-11
 */
@Getter
public enum DataSourcePoolEnum {

    /**
     * hikari连接池
     */
    HIKARI("hikari", "cn.hutool.db.ds.hikari.HikariDSFactory"),

    /**
     * druid连接池
     */
    DRUID("druid", "cn.hutool.db.ds.druid.DruidDSFactory"),

    /**
     * tomcat连接池
     */
    TOMCAT("tomcat", "cn.hutool.db.ds.tomcat.TomcatDSFactory"),

    /**
     * bee连接池
     */
    BEE("bee", "cn.hutool.db.ds.bee.BeeDSFactory"),

    /**
     * dbcp连接池
     */
    DBCP("dbcp", "cn.hutool.db.ds.dbcp.DbcpDSFactory"),

    /**
     * c3p0连接池
     */
    C3P0("c3p0", "cn.hutool.db.ds.c3p0.C3p0DSFactory"),

    /**
     * pool连接池
     */
    POOL("pool", "cn.hutool.db.ds.pooled.PooledDSFactory");

    /**
     * 连接池名字
     */
    private String name;

    /**
     * 连接池对应的类名
     */
    private String val;

    DataSourcePoolEnum(String name, String val) {
        this.name = name;
        this.val = val;
    }

    public static DataSourcePoolEnum parse(String name) {
        DataSourcePoolEnum[] values = DataSourcePoolEnum.values();
        for (DataSourcePoolEnum value : values) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
