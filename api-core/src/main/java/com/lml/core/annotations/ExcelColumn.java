package com.lml.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yugi
 * @apiNote excel别名注释
 * @since 2019-10-28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * excel的别名
     */
    String alias();
}
