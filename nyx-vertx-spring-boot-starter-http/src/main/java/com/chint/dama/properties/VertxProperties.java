package com.chint.dama.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Data
@Configuration
@ConfigurationProperties(prefix = VertxProperties.VERTX_PROPERTIES_PREFIX, ignoreInvalidFields = true)
@Order(1)
public class VertxProperties {

    static final String VERTX_PROPERTIES_PREFIX = "chint.vertx";

    @Value("${chint.vertx.controller.packages:}")
    private String controllerPackages;

    @Value("${chint.vertx.verticle.packages:}")
    private String verticlePackages;

    @Value("${chint.vertx.filter.packages:}")
    private String filterPackages;

    @Value("${chint.vertx.port:30000}")
    private Integer port;

    @Value("${chint.vertx.api.prefix:}")
    private String apiPrefix;

    @Value("${chint.vertx.url.white-list:}")
    private String whiteList;

    @Value("${chint.vertx.auth.opened:false}")
    private Boolean authOpened;

    @Value("${chint.vertx.limit.qps:10000}")
    private double qps;

    @Value("${chint.vertx.limit.opened:false}")
    private Boolean limitOpened;
}
