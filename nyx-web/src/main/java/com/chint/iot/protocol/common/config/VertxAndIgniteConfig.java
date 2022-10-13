package com.chint.iot.protocol.common.config;

import com.chint.iot.protocol.http.protocol.HttpProtocol;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.AtomicConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglei5
 * @Description: VertxAndIgniteConfig配置类
 * @date 2021-03-24 14:38
 */
@Slf4j
@Configuration
public class VertxAndIgniteConfig {

    @Resource
    private Environment env;

    @Bean
    public Vertx getVertxInstance(HttpProtocol httpProtocol) {
        return httpProtocol.getVertx();
    }

    @Bean
    public HttpProtocol getHttpProtocolInstance(IgniteConfiguration igniteConfiguration) {
        return new HttpProtocol(igniteConfiguration);
    }

    @Bean
    public Ignite getIgniteInstance(HttpProtocol httpProtocol) {
        return httpProtocol.getIgnite();
    }

    /**
     * 通用ignite配置
     *
     * @return
     */
    @Bean
    public IgniteConfiguration getIgniteConfig() {
        String dockerType = env.getProperty("chint.ignite.dockerType");
        String nameSpace = env.getProperty("chint.ignite.nameSpace");
        String serviceName = env.getProperty("chint.ignite.serviceName");
        log.info("IgniteConfiguration.dockerType={},nameSpace={},serviceName={}", dockerType, nameSpace, serviceName);
        if (StringUtils.isEmpty(dockerType) || StringUtils.isEmpty(nameSpace) || StringUtils.isEmpty(serviceName)) {
            throw new RuntimeException("加载 ignite配置信息失败");
        }

        IgniteConfiguration cfg = new IgniteConfiguration();
        // Configuration code (omitted)

        // Durable Memory configuration.
        DataStorageConfiguration storageCfg = new DataStorageConfiguration();
        // Creating a new data region.
        DataRegionConfiguration regionCfg = new DataRegionConfiguration();
        // Region name.
        regionCfg.setName("501MB_Region");
        // Setting initial RAM size.
        regionCfg.setInitialSize(101L * 1024 * 1024);
        // Setting maximum RAM size.
        regionCfg.setMaxSize(501L * 1024 * 1024);
        // Enable persistence for the region.
        regionCfg.setPersistenceEnabled(false);
        // Setting the data region configuration.
        storageCfg.setDataRegionConfigurations(regionCfg);
        // Applying the new configuration.
        cfg.setDataStorageConfiguration(storageCfg);

        AtomicConfiguration atomicConfiguration = new AtomicConfiguration();
        atomicConfiguration.setAtomicSequenceReserveSize(10000);
        cfg.setAtomicConfiguration(atomicConfiguration);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();

        if ("k8s".equalsIgnoreCase(dockerType)) {
            TcpDiscoveryKubernetesIpFinder k8sFinder = new TcpDiscoveryKubernetesIpFinder();
            k8sFinder.setNamespace(nameSpace);
            k8sFinder.setServiceName(serviceName);
            spi.setIpFinder(k8sFinder);
        } else {
            List<String> ips = new ArrayList<String>();
            ips.add("127.0.0.1");
            ips.add("127.0.0.1:47500..47509");

            TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
            ipFinder.setAddresses(ips);

            spi.setIpFinder(ipFinder);
        }

        spi.setLocalPort(TcpDiscoverySpi.DFLT_PORT);
        cfg.setDiscoverySpi(spi);

        return cfg;
    }
}
