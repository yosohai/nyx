package com.chint.dama.factory;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * 发布Spring容器中的Vertical
 *
 * @author lzqing
 */
@Component
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SpringVerticleFactory.class);

    private ApplicationContext applicationContext;

    @Override
    public String prefix() {
        return "spring-verticle";
    }

    @Override
    public void init(Vertx vertx) {
    }

    @Override
    public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
        String clazz = VerticleFactory.removePrefix(verticleName);
        promise.complete(() -> (Verticle) applicationContext.getBean(Class.forName(clazz)));
    }

    @Override
    public void close() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

