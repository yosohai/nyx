package com.chint.dama.filter;

import com.alibaba.fastjson.JSON;
import com.chint.dama.annotation.RequestMapping;
import com.chint.dama.annotation.RouteHandler;
import com.chint.dama.base.enums.RequestMethod;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.base.result.Result;
import com.chint.dama.core.AbsChintFilter;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 全局过滤器
 */
@RouteHandler(order = 20, opened = true, isFilter = true, descript = "全局过滤器")
public class GlobalFilter extends AbsChintFilter {

    Logger logger = LogManager.getLogger(GlobalFilter.class);

    @Override
    @RequestMapping(path = "/token/*", method = RequestMethod.ROUTE, descript = "全局过滤器执行方法")
    public Handler<RoutingContext> doFilter() {
        // 获取当前类的 类名
        String clazz = this.getClass().getPackage() + this.getClass().getSimpleName();
        // 获取当前方法
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        return ctx -> {
            try {
                logger.info("【------全局过滤器【{}.{}】 starting...-----------】", () -> clazz, () -> method);
                System.err.println("进入全局过滤器，GlobalFilter！");
                ctx.next();
            } catch (Exception e) {
                logger.error("【------全局过滤器【{}.{}】 error：【{}】-----------】", () -> clazz, () -> method, () -> e);
                e.printStackTrace();
                ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
            }
        };
    }
}
