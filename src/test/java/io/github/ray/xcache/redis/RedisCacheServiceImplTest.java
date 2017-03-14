package io.github.ray.xcache.redis;

import io.github.ray.xcache.CacheService;
import io.github.ray.xcache.provider.CacheProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class RedisCacheServiceImplTest {
	
	private static  CacheService cache;
	private static Person p;
	
	@BeforeClass
	public static void before(){
		cache = CacheProvider.getCacheService();
//		cache = CacheProvider.getCacheService(JdkSerializer.getSerializer());
		p = new Person("test");
	}
	
	@Test
	public void testGetSet(){
		cache.set("p", p);
		Person tp = cache.get("p", Person.class);
		Assert.assertEquals(p.getS(), tp.getS());
		
		cache.set("fp", "ff", p);
		tp = cache.get("fp", "ff", Person.class);
		Assert.assertEquals(p.getS(), tp.getS());
	}
	
	@Test
	public void testExistsRemove(){
		cache.remove("p");
		cache.remove("fp");
		cache.set("p", p);
		Assert.assertTrue(cache.exists("p"));
		cache.remove("p");
		Assert.assertTrue(!cache.exists("p"));
		
		cache.set("fp", "ff",  p);
		cache.set("fp", "fff",  p);
		Assert.assertTrue(cache.exists("fp", "ff"));
		cache.remove("fp", "ff");
		Assert.assertTrue(!cache.exists("fp", "ff"));
		Assert.assertTrue(cache.exists("fp", "fff"));
		cache.remove("p");
		Assert.assertTrue(!cache.exists("p"));
	}
	
	@Test
	public void testExpire(){
		try{
			cache.set("p", p);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("p"));
			Thread.sleep(2000);
			Assert.assertTrue(!cache.exists("p"));
			
			cache.set("fp", "ff",  p);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("fp", "ff"));
			Thread.sleep(2000);
			Assert.assertTrue(!cache.exists("fp", "ff"));
			
			cache.setex("p", p, 4);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("p"));
			Thread.sleep(4000);
			Assert.assertTrue(!cache.exists("p"));
			
			cache.setex("fp", "ff",  p, 4);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("fp", "ff"));
			Thread.sleep(4000);
			Assert.assertTrue(!cache.exists("fp", "ff"));
			
			cache.remove("p");
			cache.setNotExpire("p", p);
			Thread.sleep(3000);
			Assert.assertTrue(cache.exists("p"));
			
			cache.remove("fp", "ff");
			cache.setNotExpire("fp", "ff", p);
			Thread.sleep(3000);
			Assert.assertTrue(cache.exists("fp", "ff"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMap(){
		Map<String, Person> map = new HashMap<String, Person>();
		map.put("p1", new Person("test1"));
		map.put("p2", new Person("test2"));
		
		cache.remove("map");
		cache.mset("map", map);
		
		Person tp = cache.get("map", "p1", Person.class);
		Assert.assertEquals(map.get("p1").getS(), tp.getS());
		
		tp = cache.get("map", "p2", Person.class);
		Assert.assertEquals(map.get("p2").getS(), tp.getS());
		try{
			cache.remove("map");
			cache.msetex("map", map, 4);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("map"));
			Assert.assertTrue(cache.exists("map", "p1"));
			Thread.sleep(4000);
			Assert.assertTrue(!cache.exists("map", "p1"));
			
			
			cache.remove("map");
			cache.mset("map", map);
			Thread.sleep(1000);
			Assert.assertTrue(cache.exists("map", "p1"));
			Thread.sleep(2000);
			Assert.assertTrue(!cache.exists("map", "p1"));
			
			cache.remove("map");
			cache.msetNotExpire("map", map);
			Thread.sleep(4000);
			Assert.assertTrue(cache.exists("map", "p1"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

