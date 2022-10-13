package com.chint.dama.utils;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 集群管理ClusterManager工厂类
 * 
 *
 */
public class VertxClusterManagerFactory {
	/**
	 * zookeeper的实现名称
	 */
	public final static String IGNITE_CLUSTER = "ignite";

	/**
	 * 获得所有实现类的名字
	 * 
	 * @return
	 */
	public static List<String> getImplNames() {
		List<String> result = new ArrayList<>();
		result.add(IGNITE_CLUSTER);
		return result;
	}

	/**
	 * 获得集群管理
	 * 
	 * @param name
	 *            实现类在集群中的名字
	 * @param options
	 *            集群实现所需要的配置文件
	 * @return
	 */
	public static ClusterManager getClusterManager(String name, JsonObject options) {
		if (IGNITE_CLUSTER.equalsIgnoreCase(name)) {
			return new IgniteClusterManager(options);
		}
		return null;
	}

}
