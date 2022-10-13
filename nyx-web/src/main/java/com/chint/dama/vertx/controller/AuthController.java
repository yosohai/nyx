package com.chint.dama.vertx.controller;


import com.alibaba.fastjson.JSON;
import com.chint.dama.annotation.RequestMapping;
import com.chint.dama.annotation.RouteHandler;
import com.chint.dama.base.enums.RequestMethod;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.base.result.Result;
import com.chint.dama.core.AbsChintHandler;
import com.chint.dama.utils.Jose4jUtil;
import com.chint.dama.utils.ParamUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 访问者控制层
 */
@RouteHandler("/auth")
public class AuthController extends AbsChintHandler {

    Logger logger = LogManager.getLogger(AuthController.class);

    /**
     * 获取Token
     *
     * @return Handler<RoutingContext>
     */
    @RequestMapping(value = "/token", method = {RequestMethod.GET, RequestMethod.POST})
    public Handler<RoutingContext> token() {
        return ctx -> {
            JsonObject param = ParamUtil.getRequestParams(ctx);
            ctx.vertx().<Map>executeBlocking(promise -> {
                try {
                    Map map = new HashMap();
                    String accessToken = Jose4jUtil.generateToken();
                    map.put("accessToken", accessToken);
                    promise.complete(map);
                } catch (Exception e) {
                    logger.debug("错误：{}", () -> e);
                    e.printStackTrace();
                    ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
                }

            }, asyncResultHandler(ctx));
        };
    }

}
