package com.chint.iot.protocol.tcp.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglei5
 * @Description: 缓存服务类,所有存的都是JSON的字符串形式
 * key：String
 * value:String
 * @date 2021-06-16 15:57
 */
@Component
public class IgniteCacheUtils {

	private static Ignite ignite;

	@Resource
	private Ignite igniteInit;

	@PostConstruct
	public void init(){
		ignite = igniteInit;
	}
	/**
	 * 保存信息
	 * @param cacheKey
	 * @param object
	 */
	public static void put(String cacheKey, JSONObject object) {
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(cacheKey);
		cache.putAsync(cacheKey, object);
	}

	public static void put(String cacheKey, JSONObject object, long expire) {
		CacheConfiguration<String, JSONObject> cacheOnlyPersonCfg = new CacheConfiguration(cacheKey);
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(cacheOnlyPersonCfg)
				.withExpiryPolicy(new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, expire)));
		cache.putAsync(cacheKey, object);
	}

	/**
	 * 保存String (json)
	 * @param cacheKey
	 * @param message
	 */
	public static void putString(String cacheKey, String message) {
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheKey);
		cache.putAsync(cacheKey, message);
	}

	/**
	 * 获取信息
	 * @param cacheKey
	 * @return
	 */
	public static JSONObject get(String cacheKey) {
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(cacheKey);
		return cache.get(cacheKey);
	}

	/**
	 * 删除cacheKey
	 * @param cacheKey
	 * @return
	 */
	public static boolean remove(String cacheKey) {
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(cacheKey);
		return cache.remove(cacheKey);
	}

	/**
	 * 处理设备状态
	 */
	public static void putDeviceStatus(String cacheKey, String message) {
		CacheConfiguration<String, String> cacheOnlyPersonCfg = new CacheConfiguration<>("LINK_DEVICE_STATUS_DCS_KEY@V1");
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheOnlyPersonCfg);
		cache.putAsync(cacheKey, message);
	}

	public static void removeDeviceStatus(String cacheKey) {
		CacheConfiguration<String, String> cacheOnlyPersonCfg = new CacheConfiguration<>("LINK_DEVICE_STATUS_DCS_KEY@V1");
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheOnlyPersonCfg);
		cache.remove(cacheKey);
	}
}
