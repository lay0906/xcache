package io.github.ray.xcache.redis;

import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.Set;

public interface RedisClient {
	String get(String k);

	void set(String k, String v);
	void setex(String k, String v, int seconds);
	
	String get(String k, String f);

	Map<String, String> getMap(String k);

	void set(String k, String f, String v);
	void setex(String k, String f, String v, int seconds);

	void set(String k, Map<String, String> vmap);
	void set(String k, Map<String, String> vmap, int seconds);
	
	void del(String k);
	void del(String k, String f);
	
	boolean exists(String k);
	boolean exists(String k, String f);
	
	long incrby(String k, long c);
	long incr(String k);
	long decrby(String k, long c);
	long decr(String k);
	
	void expire(String k, int seconds);
	
	void psubscribe(JedisPubSub jedisPubSub, String... patterns);
	
	void publish(String channel, String message);

	Set<String> getKeys(String k);
}
