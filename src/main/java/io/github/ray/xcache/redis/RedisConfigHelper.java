package io.github.ray.xcache.redis;

import io.github.ray.xcache.exception.CacheInitException;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConfigHelper {

	public static JedisPoolConfig getJedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		Boolean testOnBorrow = RedisProperties.getTestOnBorrow();
		if (testOnBorrow != null) {
			config.setTestOnBorrow(testOnBorrow);
		}
		Boolean testOnReturn = RedisProperties.getTestOnReturn();
		if (testOnReturn != null) {
			config.setTestOnReturn(testOnReturn);
		}
		Boolean testWhileIdle = RedisProperties.getTestWhileIdle();
		if (testOnReturn != null) {
			config.setTestWhileIdle(testWhileIdle);
		}
		Integer maxIdle = RedisProperties.getMaxIdle();
		if (maxIdle != null) {
			config.setMaxIdle(maxIdle);
		}
		Integer minIdle = RedisProperties.getMinIdle();
		if (minIdle != null) {
			config.setMinIdle(minIdle);
		}
		Integer maxActive = RedisProperties.getMaxActive();
		if (maxActive != null) {
			config.setMaxTotal(maxActive);
		}
		Long maxWait = RedisProperties.getMaxWait();
		if (maxWait != null) {
			config.setMaxWaitMillis(maxWait);
		}
		Integer evictNums = RedisProperties.getEvictNums();
		if (evictNums != null) {
			config.setNumTestsPerEvictionRun(evictNums);
		}
		Long minEvictTime = RedisProperties.getMinEvictTime();
		if (minEvictTime != null) {
			config.setMinEvictableIdleTimeMillis(minEvictTime);
		}
		Long evictTime = RedisProperties.getEvictTime();
		if (evictTime != null) {
			config.setTimeBetweenEvictionRunsMillis(evictTime);
		}
		return config;
	}
	


	public static String getRedisIp(String addresses){
		if (addresses == null) {
			throw new CacheInitException("redis地址未配置，请在redis.properties里面配置redis.addresses = ip:port");
		}
		String[] ipport = addresses.split(":");
		if (ipport == null || ipport.length != 2) {
			throw new CacheInitException("redis地址格式[" + addresses + "]有误，请在redis.properties里面配置redis.addresses = ip:port");
		}
		return ipport[0];
	}

	public static Integer getRedisPort(String addresses){
		if (addresses == null) {
			throw new CacheInitException("redis地址未配置，请在redis.properties里面配置redis.addresses = ip:port");
		}
		String[] ipport = addresses.split(":");
		if (ipport == null || ipport.length != 2) {
			throw new CacheInitException("redis地址格式[" + addresses + "]有误，请在redis.properties里面配置redis.addresses = ip:port");
		}
		Integer port = null;
		try {
			port = Integer.parseInt(ipport[1]);
			if (port < 1 || port > 65535) {
				throw new CacheInitException("redis配置的port[" + port + "]格式有误，请在redis.properties里面配置redis.addresses = ip:port");
			}
		} catch (Exception e) {
			throw new CacheInitException("redis配置的port[" + ipport[1] + "格式有误，请在redis.properties里面配置redis.addresses = ip:port");
		}
		return port;
	}
}
