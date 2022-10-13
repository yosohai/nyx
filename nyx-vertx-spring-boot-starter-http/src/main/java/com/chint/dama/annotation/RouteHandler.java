package com.chint.dama.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Router API类 标识注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteHandler {

    String value() default "";

    /**
     * 值越小优先级越高
     */
    int order() default 2147483647;

    /**
     * 开启状态标识
     */
    boolean opened() default true;

    /**
     * Filter标记
     */
    boolean isFilter() default false;

    /**** 描述 *****/
    String descript() default "";
}
