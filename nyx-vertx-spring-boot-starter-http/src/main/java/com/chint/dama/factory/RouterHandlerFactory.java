package com.chint.dama.factory;


import com.alibaba.fastjson.JSON;
import com.chint.dama.annotation.RequestMapping;
import com.chint.dama.annotation.RouteHandler;
import com.chint.dama.base.enums.RequestMethod;
import com.chint.dama.base.enums.ResultCodeEnum;
import com.chint.dama.base.result.BaseResp;
import com.chint.dama.base.result.BaseResponse;
import com.chint.dama.base.result.ErrorResult;
import com.chint.dama.utils.ParamUtil;
import com.chint.dama.utils.ReflectionUtil;
import com.chint.dama.utils.RouterUtil;
import com.chint.dama.utils.SpringContextUtil;
import com.chint.dama.utils.VertxUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Router 对象创建
 */
public class RouterHandlerFactory {

    private static final Logger logger = LogManager.getLogger(RouterHandlerFactory.class);

    // 方法返回值类型名称
    private static final String METHOD_RETURN_TYPE = "Handler";


    private static volatile Reflections reflections;
    private volatile String apiPrefix = "";

    public RouterHandlerFactory(String routerScanAddress) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddress);
    }

    public RouterHandlerFactory(List<String> routerScanAddresses) {
        Objects.requireNonNull(routerScanAddresses, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddresses);
    }

    public RouterHandlerFactory(String apiPrefix, String... routerScanAddress) {
        Objects.requireNonNull(routerScanAddress, "The router package address scan is empty.");
        reflections = ReflectionUtil.getReflections(routerScanAddress);
        this.apiPrefix = apiPrefix;
    }

    /**
     * 开始扫描并注册handler
     */
    public Router createRouter() {
        Router router = RouterUtil.getRouter();
        router.route().handler(ctx -> {
            logger.debug("The HTTP service request address information ===>path:{}, uri:{}, method:{}",
                    ctx.request().path(), ctx.request().absoluteURI(), ctx.request().method());
            ctx.response().headers().add(CONTENT_TYPE, "application/json; charset=utf-8");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
            ctx.response().headers().add(ACCESS_CONTROL_ALLOW_HEADERS,
                    "X-PINGOTHER, Origin,Content-Type, Accept, X-Requested-With, Dev, Authorization, Version, Token");
            ctx.response().headers().add(ACCESS_CONTROL_MAX_AGE, "1728000");
            ctx.next();
        });
        Set<HttpMethod> method = new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
            add(HttpMethod.OPTIONS);
            add(HttpMethod.PUT);
            add(HttpMethod.DELETE);
            add(HttpMethod.HEAD);
        }};
        /* 添加跨域的方法 **/
        router.route().handler(CorsHandler.create("*").allowedMethods(method));
        router.route().handler(SessionHandler.create(SessionStore.create(VertxUtil.getVertxInstance())));
        router.route().handler(BodyHandler.create());
        try {
            Set<Class<?>> handlers = reflections.getTypesAnnotatedWith(RouteHandler.class);

            Comparator<Class<?>> comparator = (c1, c2) -> {
                RouteHandler routeHandler1 = c1.getAnnotation(RouteHandler.class);
                RouteHandler routeHandler2 = c2.getAnnotation(RouteHandler.class);
                return Integer.compare(routeHandler1.order(), routeHandler2.order());
            };
            List<Class<?>> sortedHandlers = handlers.stream().filter(e -> {
                RouteHandler annotation = e.getAnnotation(RouteHandler.class);
                return annotation.opened(); // 过滤掉关闭的路由
            }).sorted(comparator).collect(Collectors.toList());
            for (Class<?> handler : sortedHandlers) {
                try {
                    registerHandler(router, handler);
                } catch (Exception e) {
                    logger.error("Error register {}", handler);
                }
            }
        } catch (Exception e) {
            logger.error("Manually Register Handler Fail，Error details：" + e.getMessage());
        }
        return router;
    }

    private void registerHandler(Router router, Class<?> clazz) throws Exception {
        String root = "";
        // 判断类是否是过滤器的标识
        boolean filterClassFlag = false;
        if (clazz.isAnnotationPresent(RouteHandler.class)) {
            RouteHandler routeHandler = clazz.getAnnotation(RouteHandler.class);
            if (routeHandler.isFilter()) {
                root += "/";
                filterClassFlag = true;
            } else {
                root = apiPrefix;
            }
            String routeUrl = routeHandler.value();
            if (routeUrl.startsWith("/")) {
                routeUrl = routeUrl.substring(1);
            }
            root = root + routeUrl;
        }
        if (!root.startsWith("/")) {
            root = "/" + root;
        }
        if (!root.endsWith("/")) {
            root = root + "/";
        }

        Object instance = clazz.newInstance();
        Comparator<Method> comparator = (m1, m2) -> {
            RequestMapping mapping1 = m1.getAnnotation(RequestMapping.class);
            RequestMapping mapping2 = m2.getAnnotation(RequestMapping.class);
            return Integer.compare(mapping1.order(), mapping2.order());
        };

        Set<Method> methodSet = ReflectionUtils.getAllMethods(clazz,
                ReflectionUtils.withAnnotation(RequestMapping.class));
        List<Method> methodList = methodSet.stream().sorted(comparator).collect(Collectors.toList());
        for (Method method : methodList) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                RequestMethod[] requestMethods = mapping.method();
                String routeUrl;
                String mapUrl = StringUtils.isBlank(mapping.value()) ? mapping.path() : mapping.value();
                if (mapUrl.startsWith("/:")) {
                    routeUrl = (method.getName() + mapUrl);
                } else {
                    routeUrl = (mapUrl.endsWith(method.getName()) ?
                            mapUrl : (mapping.isCover() ? mapUrl : mapUrl + method.getName()));
                    if (routeUrl.startsWith("/")) {
                        routeUrl = routeUrl.substring(1);
                    }
                }
                // 兼容路由变量为{var}的形式
                if (mapUrl.contains("{")) {
                    String temp = routeUrl.replace("{", ":").replace("}", "");
                    routeUrl = temp;
                }
                String url;
                if (!root.endsWith("/")) {
                    url = root.concat("/" + routeUrl);
                } else {
                    url = root.concat(routeUrl);
                }
                Class<?> returnType = method.getReturnType();
                String returnTypeName = returnType.getSimpleName();
                logger.debug("Register New Handler -> {}:{}", requestMethods, url);
                if (METHOD_RETURN_TYPE.equals(returnTypeName)) {
                    Object invoke = method.invoke(instance);
                    Handler<RoutingContext> handler = null;
                    if (invoke instanceof Handler) {
                        handler = (Handler<RoutingContext>) invoke;
                    }
                    Handler<RoutingContext> methodHandler = handler;
                    Arrays.stream(requestMethods).forEach(e -> {
                        switch (e) {
                            case POST:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.post(url).blockingHandler(methodHandler);
                                } else {
                                    router.post(url).handler(methodHandler);
                                }
                                break;
                            case PUT:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.put(url).blockingHandler(methodHandler);
                                } else {
                                    router.put(url).handler(methodHandler);
                                }
                                break;
                            case DELETE:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.delete(url).blockingHandler(methodHandler);
                                } else {
                                    router.delete(url).handler(methodHandler);
                                }
                                break;
                            case ROUTE:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.route(url).blockingHandler(methodHandler); // get、post、delete、put
                                } else {
                                    router.route(url).handler(methodHandler); // get、post、delete、put
                                }
                                break;
                            case GET: // fall through
                            default:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.get(url).blockingHandler(methodHandler);
                                } else {
                                    router.get(url).handler(methodHandler);
                                }
                                break;
                        }
                    });
                } else {
                    Arrays.stream(requestMethods).forEach(e -> {
                        switch (e) {
                            case POST:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.post(url).blockingHandler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                } else {
                                    router.post(url).handler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                }
                                break;
                            case PUT:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.put(url).blockingHandler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                } else {
                                    router.put(url).handler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                }
                                break;
                            case DELETE:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.delete(url).blockingHandler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                } else {
                                    router.delete(url).handler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                }
                                break;
                            case ROUTE:
                                // get、post、delete、put
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.route(url).blockingHandler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                } else {
                                    router.route(url).handler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                }
                                break;
                            case GET: // fall through
                            default:
                                if (mapping.isBlock()) {
                                    // 处理耗时操作
                                    router.get(url).blockingHandler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                } else {
                                    router.get(url).handler(ctx -> {
                                        methodHandle(ctx, method, instance);
                                    });
                                }
                                break;
                        }
                    });
                }
            }
        }
    }

    // 方法处理
    private void methodHandle(RoutingContext routingContext, Method method, Object instance) {
        try {
            // http request params
            JsonObject jsonObject = ParamUtil.getRequestParams(routingContext);
            // method params
            Parameter[] parameters = method.getParameters();
            List<Object> params = new ArrayList<>();
            // 父包
            Environment environment = SpringContextUtil.getBean("environment");
            String pack = environment.getProperty("application.package", "com.chint");
            // 处理形参  类型转换
            for (Parameter par : parameters) {
                String type = par.getType().getTypeName();
                if ("int".equals(type)) {
                    String it = jsonObject.getString(par.getName());
                    params.add(Integer.parseInt(it));
                } else if ("boolean".equals(type)) {
                    String it = jsonObject.getString(par.getName());
                    params.add(Boolean.getBoolean(it));
                } else if ("double".equals(type)) {
                    String it = jsonObject.getString(par.getName());
                    params.add(Double.parseDouble(it));
                } else if ("long".equals(type)) {
                    String it = jsonObject.getString(par.getName());
                    params.add(Long.parseLong(it));
                } else if ("java.lang.String".equals(type)) {
                    String it = jsonObject.getString(par.getName());
                    params.add(it);
                } else if (type.startsWith(pack)) {
                    // 自定义实体类处理
                    Class<?> aClass = Class.forName(type);
                    Field[] declaredFields = aClass.getDeclaredFields();
                    BeanWrapperImpl beanWrapper = new BeanWrapperImpl(aClass);
                    for (Field f : declaredFields) {
                        f.setAccessible(true);
                        if (!"serialVersionUID".equals(f.getName())) {
                            Object it = jsonObject.getValue(f.getName());
                            if (it != null) {
                                //Class requiredType = beanWrapper.getPropertyType(f.getName());
                                //beanWrapper.convertIfNecessary(it, requiredType, f);
                                beanWrapper.setPropertyValue(f.getName(), it);
                            }
                        }
                    }
                    params.add(beanWrapper.getWrappedInstance());
                } else if ("java.util.Map".equals(type)) {
                    params.add(jsonObject.getMap());
                } else if ("io.vertx.core.http.HttpServerResponse".equals(type)) {
                    params.add(jsonObject.getValue("response"));
                } else if ("io.vertx.core.http.HttpServerRequest".equals(type)) {
                    params.add(jsonObject.getValue("request"));
                } else if ("io.vertx.ext.web.RoutingContext".equals(type)) {
                    params.add(jsonObject.getValue("routingContext"));
                } else {
                    params.add(jsonObject);
                }
            }
            Object res = null;
            if (params.size() > 0) {
                res = method.invoke(instance, params.toArray());
            } else {
                res = method.invoke(instance);
            }
            if (res instanceof BaseResp || res instanceof BaseResponse) {
                routingContext.end(JSON.toJSONString(res));
            }
            routingContext.end(JSON.toJSONString(BaseResponse.succeed(res)));

        } catch (Exception e) {
            routingContext.end(JSON.toJSONString(ErrorResult.fail(ResultCodeEnum.SYSTEM_BUSY)));
            e.printStackTrace();
        }

    }
}
