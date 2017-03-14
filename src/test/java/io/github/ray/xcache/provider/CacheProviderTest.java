package io.github.ray.xcache.provider;

import io.github.ray.xcache.CacheService;
import io.github.ray.xcache.redis.RedisCacheServiceImpl;
import org.junit.Assert;
import org.junit.Test;


public class CacheProviderTest {
	@Test
	public void testRedis(){
		CacheService cache = CacheProvider.getCacheService();
		Assert.assertTrue(cache instanceof RedisCacheServiceImpl);
	}
}
