package com.chint.dama.vertx;


import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;

public class VertxTest {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
        vertx.setPeriodic(1000, id -> {
            // This handler will get called every second
            System.out.println("timer fired!");
        });

        HttpServer httpServer = vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
            req.end();
        });
        httpServer.invalidRequestHandler(req->{
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("非法运行");
            req.end();
        });
        // Now bind the server:
        httpServer.listen(7777);



        HttpClient client = vertx.createHttpClient();

        client.request(HttpMethod.POST, "some-uri")
                .onSuccess(request -> {
                    request.response().onSuccess(response -> {
                        System.out.println("Received response with status code " + response.statusCode());
                    });

                    // Now do stuff with the request
                    request.putHeader("content-length", "1000");
                    request.putHeader("content-type", "text/plain");

//                    request.write(body);

                    // Make sure the request is ended when you're done with it
                    request.end();
                });

    }
}
