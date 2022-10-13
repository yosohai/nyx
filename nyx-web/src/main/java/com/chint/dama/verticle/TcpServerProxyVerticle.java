package com.chint.dama.verticle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chint.dama.dao.po.TTcpApiPO;
import com.chint.dama.core.AbsChintRouterVerticle;
import com.chint.dama.service.impl.TTcpApiServiceImpl;
import com.chint.dama.utils.SpringContextUtil;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.streams.Pump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Ruohong Cheng on 2021/11/30 16:58
 */
public class TcpServerProxyVerticle extends AbsChintRouterVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TcpServerProxyVerticle.class);

    @Override
    public void start() throws Exception {
        // 查询需代理的tcp地址
        List<TTcpApiPO> list = getAPiFromDB();
        // 处理代理
        for (TTcpApiPO item : list) {
            NetServerOptions options = new NetServerOptions().setPort(item.getSourcePort()).setHost(item.getSourceHost());
            NetServer server = vertx.createNetServer(options);
            server.connectHandler(socket -> {
                LOG.info("Incoming connection!");
                vertx.createNetClient().connect(item.getTargetPort(), item.getTargetHost(), netSocketAsyncResult -> {
                    Pump.pump(socket, netSocketAsyncResult.result()).start();
                    Pump.pump(netSocketAsyncResult.result(), socket).start();
                });
                socket.handler(res -> {
                    Object o = res.toString();
                    // TODO 处理消息
                });
                socket.exceptionHandler(t -> {
                    LOG.error(t.getMessage());
                    socket.close();
                });
                socket.endHandler(v -> {

                });
            });
            server.close(res -> {
                if(res.succeeded()) {
                    // TCP server fully closed
                    LOG.info("server close succeeded");
                } else {
                    LOG.info("server status: " + res.result().toString());
                }
            });
            server.listen();
        }
    }

    // 查询需代理的tcp地址
    private List<TTcpApiPO> getAPiFromDB() {
        TTcpApiServiceImpl tTcpApiService = SpringContextUtil.getBean(TTcpApiServiceImpl.class);
        QueryWrapper<TTcpApiPO> wrapper = Wrappers.query();
        wrapper.eq("deleteflag", "0");
        List<TTcpApiPO> list = tTcpApiService.list(wrapper);
        return list;
    }
}
