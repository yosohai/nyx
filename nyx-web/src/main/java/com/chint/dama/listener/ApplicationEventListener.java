package com.chint.dama.listener;

import com.chint.dama.properties.VertxProperties;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class ApplicationEventListener implements ApplicationListener {

    private static final Logger logger = LogManager.getLogger(ApplicationEventListener.class);

    private static final int MAX_WEBSOCKET_FRAME_SIZE = 1000000;

    @Resource
    private Vertx vertx;

    @Resource
    private VertxProperties properties;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            logger.info("环境初始化！！！");
        } else if (event instanceof ApplicationPreparedEvent) {
            logger.info("初始化完成！！！");
        } else if (event instanceof ContextRefreshedEvent) {
            logger.info("应用刷新！！");
        } else if (event instanceof ApplicationReadyEvent) {
            logger.info("项目启动完成！！");
        } else if (event instanceof ContextStartedEvent) {
            logger.info("应用启动！！");
        } else if (event instanceof ContextStoppedEvent) {
            logger.warn("项目停止！！");
        } else if (event instanceof ContextClosedEvent) {
            logger.error("应用关闭！！");
        }
    }
}
