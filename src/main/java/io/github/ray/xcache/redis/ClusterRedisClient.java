package io.github.ray.xcache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClusterRedisClient implements RedisClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusterRedisClient.class);
	private JedisCluster j;
	private volatile static RedisClient INSTANCE = null;
	
	public static RedisClient getRedisClient() {
		if (INSTANCE == null) {
			synchronized (ClusterRedisClient.class) {
				if (INSTANCE == null) {
					INSTANCE = new ClusterRedisClient();
				}
			}
		}
		return INSTANCE;
	}
	
	private ClusterRedisClient() {
		JedisPoolConfig config = RedisConfigHelper.getJedisPoolConfig();
		String addresses = RedisProperties.getAddresses();
		String ip = RedisConfigHelper.getRedisIp(addresses);
		Integer port = RedisConfigHelper.getRedisPort(addresses);
		Set<HostAndPort> hps = new HashSet<HostAndPort>();
		hps.add(new HostAndPort(ip, port));
		j = new JedisCluster(hps, RedisProperties.getConnectTimeout(), config);
	}
	
	public String get(String k) {
		if (null == k) {
			return null;
		}
		String v = null;
		try {
			v = j.get(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally { }
		return v;
	}
	
	public void set(String k, String v) {
		if (null == k) {
			return;
		}
		try {
			j.set(k, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void setex(String k, String v, int seconds) {
		if (null == k) {
			return;
		}
		try {
			j.setex(k, seconds, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public String get(String k, String f) {
		if (null == k) {
			return null;
		}
		String v = null;
		try {
			v = j.hget(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return v;
	}
	
	public void set(String k, String f, String v) {
		if (null == k) {
			return;
		}
		try {
			j.hset(k, f, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void setex(String k, String f, String v, int seconds) {
		if (null == k) {
			return;
		}
		try {
			j.hset(k, f, v);
			j.expire(k, seconds);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void set(String k, Map<String, String> hash) {
		if (k == null) {
			return;
		}
		try {
			j.hmset(k, hash);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void set(String k, Map<String, String> hash, int seconds) {
		if (k == null) {
			return;
		}
		try {
			j.hmset(k, hash);
			j.expire(k, seconds);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void del(String k) {
		if (k == null) {
			return;
		}
		try {
			j.del(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
		}
	}
	
	public void del(String k, String f) {
		if (k == null || f == null) {
			return;
		}
		try {
			j.hdel(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
		}
	}
	
	public boolean exists(String k) {
		if (k == null) {
			return false;
		}
		try {
			return j.exists(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return false;
	}
	
	public boolean exists(String k, String f) {
		if (k == null || f == null) {
			return false;
		}
		try {
			return j.hexists(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return false;
	}
	
	public long incrby(String k, long c) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		try {
			ret = j.incrBy(k, c);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return ret;
	}
	
	public long incr(String k) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		try {
			ret = j.incr(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return ret;
	}
	
	public long decrby(String k, long c) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		try {
			ret = j.decrBy(k, c);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return ret;
	}
	
	public long decr(String k) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		try {
			ret = j.decr(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return ret;
	}
	
	public void expire(String k, int seconds) {
		if (null == k) {
			return;
		}
		try {
			j.expire(k, seconds);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	public void flush() {
		try {
			// 集群不支持一个客户端清掉所有数据
			Map<String, JedisPool> poolsMap = j.getClusterNodes();
			for (JedisPool pool : poolsMap.values()) {
				if (null == pool) {
					continue;
				}
				Jedis jedis = pool.getResource();
				if (null == jedis) {
					continue;
				}
				// 如果从节点配了只读会抛出异常，这里捕获让流程进行
				try {
					jedis.flushAll();
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
				} finally {
					try {
						jedis.close();
					} catch (Exception ex) {
						LOGGER.warn(ex.getMessage(), ex);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
	}
	
	@Override
    public Map<String, String> getMap(String k) {
		try {
			return j.hgetAll(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return new HashMap<String, String>(0);
    }

	@Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		try {
			j.psubscribe(jedisPubSub, patterns);
		} catch(JedisConnectionException je){
			throw je;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
    }

	@Override
    public void publish(String channel, String message) {
		try {
			j.publish(channel, message);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
    }
	
	@Override
    public Set<String> getKeys(String k) {
		try {
			return j.hkeys(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
		}
		return new HashSet<String>(0);
    }
}
