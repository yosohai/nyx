package com.chint.dama.verticle;

import com.alibaba.fastjson.JSON;
import com.chint.dama.core.AbsChintRouterVerticle;
import com.chint.dama.dao.po.VisitorPO;
import com.chint.dama.eventbusservice.MysqlEventBusHandler;
import com.chint.dama.service.impl.VisitorServiceImpl;
import com.chint.dama.utils.ParamUtil;
import com.chint.dama.utils.RouterUtil;
import com.chint.dama.utils.SpringContextUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * 基本代码注释，请参见vert.x笔记：3.使用vert.x发布restful接口
 *
 * @author john
 */
@Deprecated
public class ServerVerticle extends AbsChintRouterVerticle {


    @Override
    public void start() throws Exception {

        Router router = RouterUtil.getRouter();
        router.route().handler(BodyHandler.create());
        router.route("/spring/hello").handler(this::handler);
        router.route("/spring/hello1").handler(this::handler1);
        router.route("/spring/hello2").handler(this::handler2);
    }

    private void handler(RoutingContext ctx) {
        JsonObject param = ParamUtil.getRequestParams(ctx);
        VisitorServiceImpl visitorServiceImpl = (VisitorServiceImpl) SpringContextUtil.getBean(VisitorServiceImpl.class);
        VisitorPO visitorPO = visitorServiceImpl.getById(param.getLong("id"));
        String s = JSON.toJSONString(visitorPO);
        ctx.response()
                .putHeader("content-type",
                        "application/json")
                .end("hello" + s);

    }


    private void handler1(RoutingContext ctx) {
        Vertx vertx = ctx.vertx();
        JsonObject params = new JsonObject();
//        HeadersMultiMap headers = new HeadersMultiMap();
//        JsonObject headers = new JsonObject();
//        headers.put("action", "getList");
//        params.put("headers", headers);
//        vertx.eventBus().request("mysql:gateway-api","你好", new DeliveryOptions(params)).onSuccess(res -> {
//            ctx.response().putHeader("content-type", "application/json").end(res.body().toString());
//        });

        // *******
        MysqlEventBusHandler mysqlEventBusHandler = MysqlEventBusHandler.createProxy(vertx, "mysql:gateway-api");
        JsonObject msg = new JsonObject().put("name", "vertx");
        mysqlEventBusHandler.getList(msg).onSuccess(json -> {
            System.out.println(json.toString());
            ctx.end(json.toString());
        }).onFailure(failure -> {
            System.out.println(failure);
            ctx.end();
        });

    }

    private void handler2(RoutingContext ctx) {
        ctx.response()
                .putHeader("content-type",
                        "application/json")
                .end("hello2");

    }
}