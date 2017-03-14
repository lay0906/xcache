package io.github.ray.xcache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenericRedisClient implements RedisClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericRedisClient.class);
	private JedisPool pool;
	private volatile static RedisClient INSTANCE = null;

	public static RedisClient getRedisClient() {
		if (INSTANCE == null) {
			synchronized (GenericRedisClient.class) {
				if (INSTANCE == null) {
					INSTANCE = new GenericRedisClient();
				}
			}
		}
		return INSTANCE;
	}

	private GenericRedisClient() {
		JedisPoolConfig config = RedisConfigHelper.getJedisPoolConfig();
		String addresses = RedisProperties.getAddresses();
		String ip = RedisConfigHelper.getRedisIp(addresses);
		Integer port = RedisConfigHelper.getRedisPort(addresses);
		String password = RedisProperties.getPassword();
		if(null != password && !password.isEmpty()) {
			pool = new JedisPool(config, ip, port, RedisProperties.getConnectTimeout(), password);
		} else {
			pool = new JedisPool(config, ip, port, RedisProperties.getConnectTimeout());
		}
	}

	private Jedis getJedis() {
		return pool.getResource();
	}

	private void returnJedis(Jedis j) {
		if (null != j) {
			try {
				if (j != null) {
					j.close();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public String get(String k) {
		if (null == k) {
			return null;
		}
		Jedis j = null;
		String v = null;
		try {
			j = getJedis();
			v = j.get(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return v;
	}

	public void set(String k, String v) {
		if (null == k) {
			return;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.set(k, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	public void setex(String k, String v, int seconds) {
		if (null == k) {
			return;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.setex(k, seconds, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	public String get(String k, String f) {
		if (null == k) {
			return null;
		}
		Jedis j = null;
		String v = null;
		try {
			j = getJedis();
			v = j.hget(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return v;
	}

	public void set(String k, String f, String v) {
		if (null == k) {
			return;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.hset(k, f, v);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	public void setex(String k, String f, String v, int seconds) {
		if (null == k) {
			return;
		}
		Jedis j = null;
		try {
			j = getJedis();
			Pipeline pipe = j.pipelined();
			pipe.hset(k, f, v);
			pipe.expire(k, seconds);
			pipe.sync();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	public void set(String k, Map<String, String> hash) {
		if(k == null){
			return ;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.hmset(k, hash);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}
	
	public void set(String k, Map<String, String> hash, int seconds) {
		if(k == null){
			return ;
		}
		Jedis j = null;
		try {
			j = getJedis();
			Pipeline pipe = j.pipelined();
			pipe.hmset(k, hash);
			pipe.expire(k, seconds);
			pipe.sync();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}
	
	

	public void del(String k) {
		if(k == null){
			return ;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.del(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			returnJedis(j);
		}
	}

	public void del(String k, String f) {
		if(k == null || f == null){
			return ;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.hdel(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		} finally {
			returnJedis(j);
		}
	}

	public boolean exists(String k) {
		if(k == null){
			return false;
		}
		Jedis j = null;
		try {
			j = getJedis();
			return j.exists(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return false;
	}

	public boolean exists(String k, String f) {
		if(k == null || f == null){
			return false;
		}
		Jedis j = null;
		try {
			j = getJedis();
			return j.hexists(k, f);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return false;
	}

	public long incrby(String k, long c) {
		if (null == k) {
			return 0;
		}
		Jedis j = null;
		long ret = 0;
		try {
			j = getJedis();
			ret = j.incrBy(k, c);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return ret;
	}

	public long incr(String k) {
		if (null == k) {
			return 0;
		}
		Jedis j = null;
		long ret = 0;
		try {
			j = getJedis();
			ret = j.incr(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return ret;
	}

	public long decrby(String k, long c) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		Jedis j = null;
		try {
			j = getJedis();
			ret = j.decrBy(k, c);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return ret;
	}

	public long decr(String k) {
		if (null == k) {
			return 0;
		}
		long ret = 0;
		Jedis j = null;
		try {
			j = getJedis();
			ret = j.decr(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return ret;
	}

	public void expire(String k, int seconds) {
		if (null == k) {
			return;
		}
		Jedis j = null;
		try {
			j = getJedis();
			j.expire(k, seconds);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	public void flush() {
		Jedis j = null;
		try {
			j = getJedis();
			j.flushAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
	}

	@Override
    public Map<String, String> getMap(String k) {
		Jedis j = null;
		try {
			j = getJedis();
			return j.hgetAll(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return new HashMap<String, String>(0);
    }

	@Override
    public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		Jedis j = null;
		try {
			j = getJedis();
			j.psubscribe(jedisPubSub, patterns);
		} catch(JedisConnectionException je){
			throw je;
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
    }

	@Override
    public void publish(String channel, String message) {
		Jedis j = null;
		try {
			j = getJedis();
			j.publish(channel, message);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
    }

	@Override
    public Set<String> getKeys(String k) {
		Jedis j = null;
		try {
			j = getJedis();
			return j.hkeys(k);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			returnJedis(j);
		}
		return new HashSet<String>(0);
    }
}
