package com.chint.dama.eventbusservice.impl;

import com.chint.dama.annotation.EventBusAddress;
import com.chint.dama.eventbusservice.MysqlEventBusHandler;
import com.chint.dama.service.impl.TGatewayApiServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Ruohong Cheng on 2021/11/26 19:36
 */
@Service
@EventBusAddress(value = "mysql:gateway-api")
public class MysqlEventBusHandlerImpl implements MysqlEventBusHandler {

    @Resource
    TGatewayApiServiceImpl tGatewayApiService;


    @Override
    public Future<JsonObject> getList(JsonObject jsonObject) {
        JsonObject result = jsonObject.copy();
        System.out.println("Processing...");
        return Future.succeededFuture(result);
    }
}
