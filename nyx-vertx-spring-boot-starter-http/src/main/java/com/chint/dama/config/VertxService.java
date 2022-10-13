package com.chint.dama.config;

import com.chint.dama.annotation.VerticleOptions;
import com.chint.dama.base.mapstruct.dto.TGatewayApiDTO;
import com.chint.dama.core.AbsChintRouterVerticle;
import com.chint.dama.factory.RouterHandlerFactory;
import com.chint.dama.properties.VertxProperties;
import com.chint.dama.utils.ReflectionUtil;
import com.chint.dama.utils.VertxUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.HttpProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VertxService {


    private static final Logger logger = LogManager.getLogger(VertxService.class);

    @Resource
    private Vertx vertx;

    @Resource
    private VertxProperties properties;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Bean
    @DependsOn({"vertxAndIgniteConfig", "jdbcTemplate"})
    private void deployVerticle() {
        VertxUtil.init(vertx);
        Reflections controllerReflections = ReflectionUtil.getReflections(properties.getVerticlePackages());
        Set<Class<? extends AbsChintRouterVerticle>> controllerSet = controllerReflections.getSubTypesOf(AbsChintRouterVerticle.class);
        String prefix = properties.getApiPrefix();
        String pag = properties.getControllerPackages();
        String filter = properties.getFilterPackages();
        Router router = new RouterHandlerFactory(prefix, pag, filter).createRouter();
        router.route().handler(BodyHandler.create());
        HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setTcpFastOpen(true)
                .setTcpCork(true)
                .setTcpQuickAck(true);
        List<Map<String, Object>> routesArr = getRouteFromDB();
        route(router, routesArr);
        vertx.createHttpServer(httpServerOptions).
                requestHandler(router).listen(properties.getPort(), server -> {
            if (server.succeeded()) {
                if (logger.isInfoEnabled()) {
                    logger.info("【------vertx createHttpServer was successful finally,listenned on port:【{}】", properties.getPort());
                }
            } else {
                logger.error("【------Server failed:【{}】------】", server.cause());
            }
        });
        controllerSet.forEach(set -> {
            VerticleOptions verticleOptions = set.getDeclaredAnnotation(VerticleOptions.class);
            DeploymentOptions deploymentOptions = new DeploymentOptions();
            if (verticleOptions != null) {
                if (verticleOptions.worker()) {
                    deploymentOptions.setWorker(true);
                }
                if (verticleOptions.instances() > 1) {
                    deploymentOptions.setInstances(verticleOptions.instances());
                }
                if (verticleOptions.workerPoolSize() > 0) {
                    deploymentOptions.setWorkerPoolSize(verticleOptions.workerPoolSize());
                }
                if (verticleOptions.workerPoolName() != null && !"".equals(verticleOptions.workerPoolName())) {
                    deploymentOptions.setWorkerPoolName(verticleOptions.workerPoolName());
                }
                if (verticleOptions.maxWorkerExecuteTime() > 0) {
                    deploymentOptions.setMaxWorkerExecuteTime(verticleOptions.maxWorkerExecuteTime());
                }
                if (verticleOptions.maxWorkerExecuteTimeUnit() != null) {
                    deploymentOptions.setMaxWorkerExecuteTimeUnit(verticleOptions.maxWorkerExecuteTimeUnit());
                }
            }
            vertx.deployVerticle(set, deploymentOptions, stringAsyncResult -> {
                if (stringAsyncResult.succeeded()) {
                    logger.info("【------Vertx service deploy successful-----------】", stringAsyncResult);
                } else {
                    logger.info("【------Vertx service deploy by unsuccessful,cause by【{}】-----------】", stringAsyncResult.cause());
                }
            });
        });
    }

    /**
     * 从数据库中获取代理地址
     *
     * @return
     */
    private List<Map<String, Object>> getRouteFromDB() {
        List<TGatewayApiDTO> lists = jdbcTemplate.query("select url,path from t_gateway_api ", new BeanPropertyRowMapper<TGatewayApiDTO>(TGatewayApiDTO.class));
        logger.info("RouteFromDB result: " + lists.toString());
        List<Map<String, Object>> resArr = new ArrayList<>();
        // 过滤掉不符合规则的数据防止报错
        List<TGatewayApiDTO> collect = lists.stream().filter(e -> e.getUrl().contains("://")).collect(Collectors.toList());
        for (TGatewayApiDTO item : collect) {
            String url = item.getUrl();
            String[] temp = url.split("://");
            String[] uri = temp[1].split(":");
            String host = uri[0];
            String[] portTemp = uri[1].split("/");
            int port = Integer.parseInt(portTemp[0]);
            Map<String, Object> map = new HashMap<>();
            map.put("host", host);
            map.put("port", port);
            map.put("path", item.getPath());
            resArr.add(map);
        }
        return resArr;
    }

    /**
     * 路由代理
     *
     * @param proxyRouter
     * @param routesArr
     * @throws Exception
     */
    private void route(Router proxyRouter, List<Map<String, Object>> routesArr) {
        HttpClient proxyClient = vertx.createHttpClient();
        for (Map<String, Object> item : routesArr) {
            if (item.get("port") == null || "".equals(item.get("port"))) {
                logger.error("port端口为空: ", item.toString());
            } else if (item.get("host") == null || "".equals(item.get("host"))) {
                logger.error("host为空: ", item.toString());
            } else if (item.get("path") == null || "".equals(item.get("path"))) {
                logger.error("path为空: ", item.toString());
            } else {
                try {
                    int port = Integer.parseInt(item.get("port").toString());
                    proxyRouter.route(item.get("path").toString()).handler(ProxyHandler.create(HttpProxy.reverseProxy(proxyClient).origin(port, item.get("host").toString())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}