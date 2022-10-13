package com.chint.dama.utils;

import com.chint.dama.base.result.Result;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;


/**
 *
 */
public class HttpUtil {

    public static void fireJsonResponse(HttpServerResponse response, int statusCode, Result replyObj) {
        response.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(statusCode).end(replyObj.toString());
    }

    public static void fireTextResponse(RoutingContext routingContext, String text) {
        routingContext.response().putHeader("content-type", "text/html; charset=utf-8").end(text);
    }

}
