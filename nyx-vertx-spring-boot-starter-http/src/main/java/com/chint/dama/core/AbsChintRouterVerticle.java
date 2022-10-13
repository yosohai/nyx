package com.chint.dama.core;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;

/**
 * @Author: lzqing
 * @Description: 路由Verticle 一些公共常量或方法
 * @Date: Created in 2021/11/11
 */
public abstract class AbsChintRouterVerticle extends AbstractVerticle {
    org.apache.logging.log4j.Logger logger = LogManager.getLogger(AbsChintRouterVerticle.class);
}
