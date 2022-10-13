package com.chint.dama.core;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 过滤器抽象类，方便拓展
 */
public abstract class AbsChintFilter implements IChintHandler {

    Logger logger = LogManager.getLogger(AbsChintFilter.class);

    public abstract Handler<RoutingContext> doFilter();

    @Override
    public void handle(RoutingContext rcx) {

    }
}