package com.chint.dama.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Ruohong Cheng on 2021/12/13 9:42
 * description: event bus register
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventBusAddress {

    String value() default "";
    // 和value的作用相同
    String address() default "";
}
