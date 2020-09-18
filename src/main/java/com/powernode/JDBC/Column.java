package com.powernode.JDBC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @AlanLin 2020/9/15
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Column {

    String value() default "";

    boolean isPk() default false;

    boolean isInsert() default false;

    boolean isLike() default false;
}
