package io.github.ray.xcache.redis;

import io.github.ray.xcache.exception.CacheInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

public class RedisClientFactory {
	
	private static final Logger log = LoggerFactory.getLogger(RedisClientFactory.class);
	
	private static volatile RedisClient INS = null;
	
	private static void init(){
		String addresses = RedisProperties.getAddresses();
		String ip = RedisConfigHelper.getRedisIp(addresses);
		Integer port = RedisConfigHelper.getRedisPort(addresses);
		Jedis j = null;
		try{
			j = new Jedis(ip, port);
			String info = null;
			//判断是否是集群
			try{
				info = j.info("Cluster");
			}catch(JedisDataException jex){
				log.debug("Redis不是集群部署!");
			}
			if(info != null &&  info.indexOf("cluster_enabled:1") >-1){
				log.debug("探测到Redis为集群模式，使用集群模式!");
				INS = ClusterRedisClient.getRedisClient();
				return ;
			}
			//判断是不是Sentinel
			try{
				info = j.info("Sentinel");
			}catch(JedisDataException jex){
				log.debug("Redis不是Sentinel部署!");
			}
			if(info != null && info.indexOf("sentinel_masters:") > -1){
				log.debug("探测到Redis为Sentinel模式，使用Sentinel模式!");
				INS = SentinelRedisClient.getRedisClient();
				return ;
			}
			log.debug("探测到Redis为单机模式，使用单机模式!");
			INS = GenericRedisClient.getRedisClient();
		}catch(Exception e){
			log.error("Redis连接失败，缓存初始化失败！");
			throw new CacheInitException("Redis连接失败，缓存初始化失败！", e);
		}finally{
			if(j != null && j.isConnected()){
				try{
					j.close();
				}catch(Exception e){
					log.warn(e.getMessage(), e);
				}
			}
		}
	}
	
	public static RedisClient getRedisClient() {
		if(INS == null){
			synchronized (RedisClientFactory.class) {
				init();
            }
		}
		return INS;
	}
}
