package com.chint.dama.verticle.worker;

import com.chint.dama.annotation.EventBusAddress;
import com.chint.dama.annotation.VerticleOptions;
import com.chint.dama.core.AbsChintRouterVerticle;
import com.chint.dama.eventbusservice.MysqlEventBusHandler;
import com.chint.dama.eventbusservice.impl.MysqlEventBusHandlerImpl;
import com.chint.dama.service.impl.TGatewayApiServiceImpl;
import com.chint.dama.utils.ReflectionUtil;
import com.chint.dama.utils.RouterUtil;
import com.chint.dama.utils.SpringContextUtil;
import com.google.common.base.Predicate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.serviceproxy.ServiceBinder;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * Created by Ruohong Cheng on 2021/11/25 14:18
 */
@VerticleOptions(worker = true)
public class EventBusVerticle extends AbsChintRouterVerticle {

    public static final String SERVICE_ADDRESS = "mysql:gateway-api";

    MessageConsumer consumer;

    @Override
    public void start() throws Exception {
        System.out.println("EventBusVerticle execute...");
        ServiceBinder serviceBinder = new ServiceBinder(vertx);
        Predicate<AnnotatedElement> annotatedElementPredicate = ReflectionUtils.withAnnotation(EventBusAddress.class);
        Reflections f = new Reflections("com.chint.dama.eventbusservice");
        Set<Class<?>> classSet = f.getTypesAnnotatedWith(EventBusAddress.class);
       // MysqlEventBusHandlerImpl mysqlEventBusHandler = new MysqlEventBusHandlerImpl();
        for (Class cls: classSet) {
            EventBusAddress eventBusAddress = (EventBusAddress) cls.getAnnotation(EventBusAddress.class);
            String address = eventBusAddress.value();
            if(StringUtils.isEmpty(address)) {
                address = eventBusAddress.address();
            }
            Class[] interfaces = cls.getInterfaces();
            serviceBinder.setAddress(address)
                .register(interfaces[0], cls.newInstance());
        }
//        consumer = serviceBinder.setAddress(SERVICE_ADDRESS)
//                .register(MysqlEventBusHandler.class, mysqlEventBusHandler);

//        System.out.println("MysqlVerticle start");
//        TGatewayApiServiceImpl tGatewayApiServiceImpl = SpringContextUtil.getBean("tGatewayApiServiceImpl");
//        vertx.eventBus()
//                // 接收消息
//                .consumer(SERVICE_ADDRESS)

//                .handler(msg -> {
//                    JsonObject body = JsonObject.mapFrom(msg.body());
//                    switch (body.getString("methodName")) {
//                        case "list" :
//                            List<Map<String, Object>> list = tGatewayApiServiceImpl.listMaps();
//                            // 获取事件内容后，调用service服务
//                            System.out.println("bus msg body is:" + msg.body());
//                            String helloMsg = "hello ";
//                            System.out.println("msg from hello service is: "
//                                    + helloMsg);
//                            // 将service返回的字符串，回应给消息返回体
//                            JsonObject jsonObject = new JsonObject();
//                            jsonObject.put("data", list);
//                            msg.reply(jsonObject.toString());
//                            break;
//                        case "queryById" :
//                            TGatewayApiPO tGatewayApiPO = tGatewayApiServiceImpl.getById(body.getString("id"));
//                            JsonObject jt = new JsonObject();
//                            jt.put("data", tGatewayApiPO);
//                            msg.reply(tGatewayApiPO);
//                    }
//
//                });
    }

    @Override
    public void stop(){
        consumer.unregister();
    }
}
