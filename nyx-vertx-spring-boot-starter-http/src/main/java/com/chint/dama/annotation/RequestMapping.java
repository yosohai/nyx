package com.chint.dama.annotation;

import com.chint.dama.base.enums.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {
    String name() default "";

    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";

    RequestMethod[] method() default {RequestMethod.GET};

    String[] params() default {};

    String[] headers() default {};

    String[] consumes() default {};

    String[] produces() default {};

    /**** 接口描述 *****/
    String descript() default "";

    /**
     * 值越小优先级越高
     */
    int order() default 2147483647;

    /**** 是否覆盖 *****/
    boolean isCover() default true;

    /**
     * Filter标记  true为过滤器，false不为过滤器
     */
    boolean isFilter() default false;

    /**
     * true : 不阻塞执行
     * false: 阻塞执行
     */
    boolean isBlock() default true;
}