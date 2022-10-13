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
import com.chint.dama.utils.Jose4jUtil;
import com.chint.dama.utils.SpringContextUtil;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Token安全过滤器
 */
@RouteHandler(order = 100, opened = true, isFilter = true, descript = "Token安全过滤器")
public class TokenFilter extends AbsChintFilter {

    Logger logger = LogManager.getLogger(TokenFilter.class);

    @Override
    @RequestMapping(path = "/api/nyx-service/*", method = RequestMethod.ROUTE, descript = "Token安全过滤执行方法")
    public Handler<RoutingContext> doFilter() {
        // 获取当前类的 类名
        String clazz = this.getClass().getPackage() + this.getClass().getSimpleName();
        // 获取当前方法
        String method = Thread.currentThread().getStackTrace()[1].getMethodName();
        return ctx -> {
            try {
                final VertxProperties props = SpringContextUtil.getBean(VertxProperties.class);
                boolean authOpened = props.getAuthOpened();
                if (!authOpened) {
                    ctx.next();
                } else {
                    logger.info("【------Token安全过滤器【{}.{}】 starting...-----------】", () -> clazz, () -> method);
                    System.err.println("进入Token安全过滤器，TokenFilter！");
                    String whiteListStr = props.getWhiteList();
                    boolean isPass = false;
                    if (StringUtils.isNotBlank(whiteListStr)) {
                        String[] whiteList;
                        if (whiteListStr.contains(",")) {
                            whiteList = whiteListStr.split(",");
                        } else {
                            whiteList = new String[1];
                            whiteList[0] = whiteListStr;
                        }
                        // 白名单包含则通过
                        isPass = Arrays.stream(whiteList).anyMatch(e -> ctx.request().uri().contains(e));
                    }
                    if (isPass) {
                        ctx.next();
                    } else {
                        String accessToken = ctx.request().getHeader("accessToken");
                        if (Jose4jUtil.checkJwt(accessToken)) {
                            ctx.next();
                        } else {
                            ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.API_NO_AUTHORIZATION)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("【------Token安全过滤器【{}.{}】 error：【{}】-----------】", () -> clazz, () -> method, () -> e);
                ctx.response().end(JSON.toJSONString(BaseResponse.create(ResultCodeEnum.SERVER_BUSY)));
            }
        };
    }
}
