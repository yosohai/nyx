package com.chint.dama.mvc;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lzqing
 * @date 2021/9/24
 * @time 下午13:05
 * @discription
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBootKafkaApplicationTests {

    @Test
    public void sendMessage() throws InterruptedException {
        // 发送 1000 个消息
        for (int i = 0; i < 1000; i++) {
            long orderId = i + 1;
            String orderNum = UUID.randomUUID().toString();
            System.out.println(orderId + orderNum);
        }

        TimeUnit.MINUTES.sleep(1);
    }
}
