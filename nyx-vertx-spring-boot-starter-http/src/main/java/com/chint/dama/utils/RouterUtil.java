package com.chint.dama.utils;

import io.vertx.ext.web.Router;

/**
 * router单例
 */
public final class RouterUtil {

    private volatile static Router router;

    private RouterUtil() {
    }

    public static Router getRouter() {
        if (router == null) {
            synchronized (RouterUtil.class) {
                if(router == null) {
                    router = Router.router(VertxUtil.getVertxInstance());
                }
            }
        }
        return router;
    }
}
