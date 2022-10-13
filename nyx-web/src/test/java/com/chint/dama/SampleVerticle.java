package com.chint.dama;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ruohong Cheng on 2021/11/25 17:19
 */
public class SampleVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(SampleVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response()
                            .putHeader("Content-Type", "plain/text")
                            .end("Yo!");
                    logger.info("Handled a request on path {} from {}", req.path(), req.remoteAddress().host());
                })
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(ar.cause());
                    }
                });
    }
}
