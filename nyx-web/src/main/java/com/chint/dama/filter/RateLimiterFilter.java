package com.chint.dama.filter;


import com.alibaba.fastjson.JSON;
import com.chint.dama.annotation.RequestMapping;
import com.chint.dama.annotation.RouteHandler;
import com.chint.dama.base.enums.RequestMethod;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.base.result.Result;
import com.chint.dama.core.AbsChintFilter;
import com.chint.dama.properties.VertxProperties;
import com.chint.dama.utils.SpringContextUtil;
import com.google.common.util.concurrent.RateLimiter;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 限流器-控制超出设定值的请求熔断 从而保护服务
 */
@RouteHandler(order = 10, opened = true, isFilter = true, descript = "限流熔断过滤器")
public class RateLimiterFilter extends AbsChintFilter {

    final private static long GET_TOKEN_TIMEOUT = 10L;

    final VertxProperties props = SpringContextUtil.getBean(VertxProperties.class);
    Logger logger = LogManager.getLogger(RateLimiterFilter.class);
    // 获取配置的QPS-吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询）
    // 限流，每秒钟 qps 个访问
    RateLimiter limiter = RateLimiter.create(props.getQps());
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @RequestMapping(path = "/api/*", method = RequestMethod.ROUTE, descript = "限流熔断业务处理方法")
    public Handler<RoutingContext> doFilter() {

        // 获取当前类的 类名
        String clazz = this.getClass().getPackage() + this.getClass().getSimpleName();
        // 获取当前方法
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();

        return ctx -> {
            try {
                boolean limitOpened = props.getLimitOpened();
                if (!limitOpened) { // 限流器关闭直接放行
                    ctx.next();
                } else {
                    logger.info("【------限流熔断过滤器【{}.{}】 starting...-----------】", () -> clazz, () -> method);
                    System.err.println("【--------限流熔断过滤器，RateLimiterFilter！------】");
                    // xxx毫秒内， 未获取到令牌，则进入服务降级
                    boolean tryAcquire = limiter.tryAcquire(GET_TOKEN_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (!tryAcquire) {
                        logger.warn("在【{}】时间内获取令牌失败，进入限流熔断，当前时间【{}】", () -> GET_TOKEN_TIMEOUT, () -> simpleDateFormat.format(new Date()));
                        System.err.println(String.format("在【%n】时间内获取令牌失败，进入限流熔断，当前时间【%s】", GET_TOKEN_TIMEOUT, simpleDateFormat.format(new Date())));
                        ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
                    } else {
                        System.out.println("获取令牌成功");
                        logger.info("获取令牌成功，时间{}", () -> simpleDateFormat.format(new Date()));
                        ctx.next();
                    }
                }
            } catch (Exception e) {
                logger.error("【------限流熔断过滤器【{}.{}】 error：【{}】-----------】", () -> clazz, () -> method, () -> e);
                e.printStackTrace();
                ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
            }
        };
    }
}