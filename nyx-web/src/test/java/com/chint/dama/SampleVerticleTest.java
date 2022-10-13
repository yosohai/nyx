package com.chint.dama;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ruohong Cheng on 2021/11/25 16:17
 */
@DisplayName("a fairly basic test example")
@ExtendWith(VertxExtension.class)
public class SampleVerticleTest {

    @Test
    @DisplayName("count 3 timer ticks")
    void countThreeTicks(Vertx vertx, VertxTestContext testContext) {
        AtomicInteger counter = new AtomicInteger();
        vertx.setPeriodic(100, id -> {
            if(counter.incrementAndGet() == 3) {
                testContext.completeNow();
            }
        });
    }

    @Test
    @DisplayName("⏱ Count 3 timer ticks, with a checkpoint")
    void countThreeTicksWithCheckpoints(Vertx vertx, VertxTestContext testContext) {
        Checkpoint checkpoint = testContext.checkpoint(3);
        vertx.setPeriodic(100, id -> checkpoint.flag());
    }

    @Test
    @DisplayName("🚀 Deploy a HTTP service verticle and make 10 requests")
    void useSampleVerticle(Vertx vertx, VertxTestContext testContext) {
        WebClient webClient = WebClient.create(vertx);
        Checkpoint deploymentCheckpoint = testContext.checkpoint();
        Checkpoint requestCheckpoint = testContext.checkpoint(10);

        vertx.deployVerticle(new SampleVerticle(), testContext.succeeding(id -> {
            deploymentCheckpoint.flag();

            for (int i = 0; i < 10; i++) {
                webClient.get(8080, "localhost", "/")
                        .as(BodyCodec.string())
                        .send(testContext.succeeding(resp -> {
                            testContext.verify(() -> {
                                Assertions.assertThat(resp.statusCode()).isEqualTo(200);
                                Assertions.assertThat(resp.body()).contains("Yo!");
                                requestCheckpoint.flag();
                            });
                        }));
            }
        }));
    }

}
