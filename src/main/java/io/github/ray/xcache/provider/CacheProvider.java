package io.github.ray.xcache.provider;

import io.github.ray.xcache.CacheService;
import io.github.ray.xcache.redis.RedisCacheServiceImpl;
import io.github.ray.xcache.serializer.FastJsonSerializer;
import io.github.ray.xcache.serializer.JdkSerializer;
import io.github.ray.xcache.serializer.Serializer;

public class CacheProvider {
	public static CacheService getCacheService() {
		return getCacheService(FastJsonSerializer.getSerializer());
	}
	public static CacheService getJdkSerializerCacheService(){
		return getCacheService(JdkSerializer.getSerializer());
	}
	public static CacheService getCacheService(Serializer serializer) {
		return new RedisCacheServiceImpl(serializer);
	}
}
