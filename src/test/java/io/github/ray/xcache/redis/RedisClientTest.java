package io.github.ray.xcache.redis;

import org.junit.Assert;
import org.junit.Test;

public class RedisClientTest {
	
	@Test
	public void testGenericRedisClient(){
		RedisClient r = RedisClientFactory.getRedisClient();
		Assert.assertTrue(r instanceof GenericRedisClient);
	}
	
	@Test
	public void testClusterRedisClient(){
		RedisClient r = RedisClientFactory.getRedisClient();
		Assert.assertTrue(r instanceof ClusterRedisClient);
	}
}
