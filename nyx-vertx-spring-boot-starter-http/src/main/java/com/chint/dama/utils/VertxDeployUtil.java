package com.chint.dama.utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.function.Consumer;

/**
 * vertx deploy util
 */
public final class VertxDeployUtil {

//    static Vertx vertx;

    public static void deployVerticle(Class<? extends AbstractVerticle> clazz, VertxOptions options, DeploymentOptions deploymentOptions, Vertx vertx) {
        if (options == null) {
            options = new VertxOptions();
        }
        if (deploymentOptions != null) {
            vertx.deployVerticle(clazz, deploymentOptions);
        } else {
            vertx.deployVerticle(clazz, new DeploymentOptions());
        }
    }


    public static void deployVerticle(String verticleID, VertxOptions options, DeploymentOptions deploymentOptions, boolean clustered, Handler handler) {
        if (options == null) {
            options = new VertxOptions();
        }
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(verticleID, deploymentOptions, handler);
                } else {
                    vertx.deployVerticle(verticleID);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (clustered) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }

    public static void deployVerticle(Class<? extends AbstractVerticle> clazz, VertxOptions options, DeploymentOptions deploymentOptions, boolean clustered, Handler handler) {
        if (options == null) {
            options = new VertxOptions();
        }
        Consumer<Vertx> runner = vertx -> {
            try {
                VertxUtil.init(vertx);
                if (deploymentOptions != null) {
                    vertx.deployVerticle(clazz, deploymentOptions, handler);
                } else {
                    vertx.deployVerticle(clazz, new DeploymentOptions(), handler);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (clustered) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    VertxUtil.init(vertx);
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }

    public static void deployVerticle(Class<? extends AbstractVerticle> clazz, VertxOptions options, DeploymentOptions deploymentOptions, boolean clustered) {
        if (options == null) {
            options = new VertxOptions();
        }
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(clazz, deploymentOptions);
                } else {
                    vertx.deployVerticle(clazz, new DeploymentOptions());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (clustered) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
//            if (vertx == null) {
//                vertx = Vertx.vertx(options);
//            }
//            runner.accept(vertx);
        }
    }
}
