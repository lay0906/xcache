package io.github.ray.xcache;

import java.util.Map;
import java.util.Set;

public interface CacheService {

	<T> T get(String k, Class<T> clazz);

	<T> T get(String k, String f, Class<T> clazz);

	<T> Map<String, T> getMap(String k, Class<T> clazz);

	Set<String> getKeys(String k);

	<T> void set(String k, T v);
	<T> void setex(String k, T v, int seconds);
	<T> void setNotExpire(String k, T v);

	<T> void set(String k, String f, T v);
	<T> void setex(String k, String f, T v, int seconds);
	<T> void setNotExpire(String k, String f, T v);

	<T> void mset(String k, Map<String, T> hash);
	<T> void msetex(String k, Map<String, T> hash, int seconds);
	<T> void msetNotExpire(String k, Map<String, T> hash);

	void remove(String k);
	void remove(String k, String f);
	
	boolean exists(String k);
	boolean exists(String k, String f);
}
