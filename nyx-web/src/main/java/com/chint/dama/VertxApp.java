package com.chint.dama;

import com.chint.dama.annotation.EnableVertx;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@EnableVertx
@SpringBootApplication(scanBasePackages = {"com.chint.iot", "com.chint.dama"})
public class VertxApp {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(VertxApp.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}