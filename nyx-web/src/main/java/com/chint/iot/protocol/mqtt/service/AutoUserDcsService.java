package com.chint.iot.protocol.mqtt.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

/**
 * 和MSGCENTER里面的AutoUserDcsService 保持一直
 * @author zhanglei5
 * @Description: (这里用一句话描述这个类的作用)
 * @date 2021-08-24 14:35
 */
@Service
public class AutoUserDcsService
{
	private Ignite ignite = null;
	private final String KEY = "LINK_AUTO_USER_DCS_KEY@V1";

	public AutoUserDcsService()
	{

	}

	public AutoUserDcsService(Ignite ignite)
	{
		this.ignite = ignite;
	}

	private String getDcsKey()
	{
		return KEY;
	}

	public void put(String key, JSONObject object)
	{
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(getDcsKey());

		cache.putAsync(key, object);
	}

	public JSONObject get(String key)
	{
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(getDcsKey());

		return cache.get(key);
	}

	public boolean remove(String key)
	{
		IgniteCache<String, JSONObject> cache = ignite.getOrCreateCache(getDcsKey());

		return cache.remove(key);
	}



}
