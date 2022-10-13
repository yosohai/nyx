package com.chint.dama.eventbusservice;

import com.chint.dama.eventbusservice.impl.MysqlEventBusHandlerImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;

/**
 * Created by Ruohong Cheng on 2021/11/26 19:34
 */
//@ProxyGen
//@VertxGen
public interface MysqlEventBusHandler {

    int NO_NAME_ERROR = 2;
    int BAD_NAME_ERROR = 3;

    // A couple of factory methods to create an instance and a proxy
    static MysqlEventBusHandler create(Vertx vertx) {
        return new MysqlEventBusHandlerImpl();
    }

    static MysqlEventBusHandler createProxy(Vertx vertx, String address) {
        return new ServiceProxyBuilder(vertx)
                .setAddress(address)
                .build(MysqlEventBusHandler.class);
        // Alternatively, you can create the proxy directly using:
        // return new ProcessorServiceVertxEBProxy(vertx, address);
        // The name of the class to instantiate is the service interface + `VertxEBProxy`.
        // This class is generated during the compilation
    }

    // The service methods
    Future<JsonObject> getList(JsonObject jsonObject);
}
