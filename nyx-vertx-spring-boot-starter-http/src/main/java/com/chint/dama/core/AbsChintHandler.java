package com.chint.dama.core;

import com.alibaba.fastjson.JSON;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.common.MediaType;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbsChintHandler<T> implements IChintHandler {

    Logger logger = LogManager.getLogger(AbsChintHandler.class);

    @Override
    public void handle(RoutingContext routingContext) {

    }

    /**
     * 处理异步结果集
     *
     * @param ctx
     * @return
     */
    public Handler<AsyncResult<T>> asyncResultHandler(RoutingContext ctx) {
        return res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("content-type", MediaType.APPLICATION_JSON_VALUE).end(JSON.toJSONString(BaseResponse.succeed(res.result())));
            } else {
                res.cause().printStackTrace();
                ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
            }
        };
    }
}
