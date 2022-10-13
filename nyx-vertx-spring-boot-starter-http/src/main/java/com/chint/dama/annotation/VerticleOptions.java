package com.chint.dama.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ruohong Cheng on 2021/11/25 13:37
 * description: 用于设置verticle是否是worker线程以及实例个数、线程池大小
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerticleOptions {

    boolean worker() default false;

    int workerPoolSize() default 20;

    int instances() default 1;

    String workerPoolName() default "";

    long maxWorkerExecuteTime() default 60000000000L;

    TimeUnit maxWorkerExecuteTimeUnit() default TimeUnit.NANOSECONDS;
}
