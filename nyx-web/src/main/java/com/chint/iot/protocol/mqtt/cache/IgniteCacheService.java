package com.chint.iot.protocol.mqtt.cache;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglei5
 * @Description: 缓存服务类,所有存的都是JSON的字符串形式
 * key：String
 * value:String
 * @date 2021-03-17 15:57
 */
@Component
public class IgniteCacheService {

	private Ignite ignite;
	public IgniteCacheService() {
	}
	public IgniteCacheService(Ignite ignite) {
		this.ignite = ignite;
	}

	/**
	 * 保存信息
	 * @param cacheKey
	 * @param data
	 */
	public void  put(String cacheKey, String data) {
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheKey);
		cache.putAsync(cacheKey, data);
	}

	/**
	 * get信息
	 * @param cacheKey
	 * @return
	 */
	public String get(String cacheKey) {
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheKey);
		return cache.get(cacheKey);
	}

	/**
	 * 删除cacheKey
	 * @param cacheKey
	 * @return
	 */
	public boolean remove(String cacheKey) {
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheKey);
		return cache.remove(cacheKey);
	}

	/**
	 * 获取cacheNameList
	 * @return
	 */
	public List<String> getAllCacheKeys() {
		List<String> cacheNameList = new ArrayList<>();
		Collection<String> cacheNames =  ignite.cacheNames();
		if(CollectionUtil.isNotEmpty(cacheNames)){
			cacheNames.forEach(cacheName->{
				cacheNameList.add(cacheName);
			});
		}
		return cacheNameList;
	}

	/**
	 * 返回所有的key->value
	 * @return
	 */
	public Map<String, String> getAllCacheDetail(){
		Map<String, String> cacheMap = new HashMap<String, String>();
		List<String> cacheKeyList =  getAllCacheKeys();
		if(CollectionUtil.isNotEmpty(cacheKeyList)){
			cacheKeyList.forEach(cacheKey->{
				cacheMap.put(cacheKey,get(cacheKey));
			});
		}
		return cacheMap;
	}
}
