package io.github.ray.xcache.redis;

import io.github.ray.xcache.CacheService;
import io.github.ray.xcache.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisCacheServiceImpl implements CacheService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

	private Serializer serializer;
	private RedisClient redis;

	public RedisCacheServiceImpl(Serializer serializer) {
		this.serializer = serializer;
		redis = RedisClientFactory.getRedisClient();
	}
	
	private void logIlleageArgument(){
		LOGGER.debug("参数不合法！");
	}

	private <T> T deserialize(String v, Class<T> clazz) {
		try {
			return serializer.deserialize(v, clazz);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public <T> T get(String k, Class<T> clazz) {
		if(k == null || clazz == null){
			logIlleageArgument();
			return null;
		}
		String v = redis.get(k);
		if (v != null) {
			return deserialize(v, clazz);
		}
		return null;
	}

	public <T> T get(String k, String f, Class<T> clazz) {
		if(k == null || f == null || clazz == null){
			logIlleageArgument();
			return null;
		}
		String v = redis.get(k, f);
		if (v != null) {
			return serializer.deserialize(v, clazz);
		}
		return null;
	}

	public <T> void set(String k, T v) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		setex(k, v, RedisProperties.getExpireTime());
	}

	public <T> void setex(String k, T v, int seconds) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		String sv = null;
		try {
			sv = serializer.serialize(v);
		} catch (Exception e) {
			LOGGER.error("key[{}]对应value序列化失败，缓存设置失败！", k);
			return;
		}
		redis.setex(k, sv, seconds);
	}

	public <T> void setNotExpire(String k, T v) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		String sv = null;
		try {
			sv = serializer.serialize(v);
		} catch (Exception e) {
			LOGGER.error("key[{}]对应value序列化失败，缓存设置失败！", k);
			return;
		}
		redis.set(k, sv);
	}

	public <T> void set(String k, String f, T v) {
		if(k == null || f == null){
			logIlleageArgument();
			return ;
		}
		setex(k, f, v, RedisProperties.getExpireTime());
	}

	public <T> void setex(String k, String f, T v, int seconds) {
		if(k == null || f == null){
			logIlleageArgument();
			return ;
		}
		String sv = null;
		try {
			sv = serializer.serialize(v);
		} catch (Exception e) {
			LOGGER.error("key[{}],filed[{}]对应value序列化失败，缓存设置失败！", k, f);
			return;
		}
		redis.setex(k, f, sv, seconds);
	}

	public <T> void setNotExpire(String k, String f, T v) {
		if(k == null || f == null){
			logIlleageArgument();
			return ;
		}
		String sv = null;
		try {
			sv = serializer.serialize(v);
		} catch (Exception e) {
			LOGGER.error("key[{}],filed[{}]对应value序列化失败，缓存设置失败！", k, f);
			return;
		}
		redis.set(k, f, sv);
	}

	public <T> void mset(String k, Map<String, T> hash) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		if(hash == null || hash.size() == 0){
			return ;
		}
		msetex(k, hash, RedisProperties.getExpireTime());
	}
	
	public <T> void msetex(String k, Map<String, T> hash, int secords) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		if (hash == null || hash.size() == 0) {
			return;
		}
		Map<String, String> shash = new HashMap<String, String>(hash.size());
		String sk = null;
		T v = null;
		Serializer s = serializer;
		try {
			for (Map.Entry<String, T> entry : hash.entrySet()) {
				sk = entry.getKey();
				v = entry.getValue();
				shash.put(sk, s.serialize(v));
			}
			redis.set(k, shash, secords);
		} catch (Exception e) {
			LOGGER.error("key:[{}]对应的value:[{}]序列化失败整个hash保存失败!", sk, v);
		}
	}
	
	public <T> void msetNotExpire(String k, Map<String, T> hash) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		if (hash == null || hash.size() == 0) {
			return;
		}
		Map<String, String> shash = new HashMap<String, String>(hash.size());
		String sk = null;
		T v = null;
		try {
			Serializer s = serializer; 
			for (Map.Entry<String, T> entry : hash.entrySet()) {
				sk = entry.getKey();
				v = entry.getValue();
				shash.put(sk, s.serialize(v));
			}
			redis.set(k, shash);
		} catch (Exception e) {
			LOGGER.error("key:[{}]对应的value:[{}]序列化失败整个hash保存失败!", sk, v);
		}
	}

	public void remove(String k) {
		if(k == null){
			logIlleageArgument();
			return ;
		}
		redis.del(k);
	}

	public void remove(String k, String f) {
		if(k == null || f == null){
			logIlleageArgument();
			return ;
		}
		redis.del(k, f);
	}

	public boolean exists(String k) {
		if(k == null ){
			logIlleageArgument();
			return false;
		}
		return redis.exists(k);
	}

	public boolean exists(String k, String f) {
		if(k == null || f == null){
			logIlleageArgument();
			return false;
		}
		return redis.exists(k, f);
	}

	@Override
    public <T> Map<String, T> getMap(String k, Class<T> clazz) {
		if(k == null ){
			logIlleageArgument();
			return new HashMap<String, T>(0);
		}
		Map<String, String> kvs = redis.getMap(k);
		Map<String, T> ret = new HashMap<String, T>(kvs.size());
		String f = null, v=null;
		try{
			Serializer serv = serializer;
			for(Map.Entry<String, String> entry : kvs.entrySet()){
				f = entry.getKey();
				v = entry.getValue();
				ret.put(f, serv.deserialize(v, clazz));
			}
			return ret;
		}catch (Exception e) {
			LOGGER.error("key:[{}]对应的value:[{}]序列化失败整个hash获取失败!", f, v);
		}
		return null;
    }

	@Override
    public Set<String> getKeys(String k) {
		if(k == null){
			logIlleageArgument();
			return new HashSet<String>(0);
		}
		return redis.getKeys(k);
    }
}
