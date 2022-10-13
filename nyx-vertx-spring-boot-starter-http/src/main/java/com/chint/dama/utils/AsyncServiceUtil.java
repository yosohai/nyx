package com.chint.dama.utils;

import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceProxyBuilder;

/**
 * AsyncServiceUtil
 */
public final class AsyncServiceUtil {

    public static <T> T getAsyncServiceInstance(Class<T> asClazz, Vertx vertx) {
        String address = asClazz.getName();
        return new ServiceProxyBuilder(vertx).setAddress(address).build(asClazz);
    }

    public static <T> T getAsyncServiceInstance(Class<T> asClazz) {
        String address = asClazz.getName();
        return new ServiceProxyBuilder(VertxUtil.getVertxInstance()).setAddress(address).build(asClazz);
    }
}
