package io.github.ray.xcache.redis;

import java.io.Serializable;

public class Person implements Serializable{
	/**
     * 序列化ID
     */
    private static final long serialVersionUID = -3266773536777295980L;
    
	String s;
	
	public Person(){
	}
	
	public Person(String s){
		this.s = s;
	}
	
	public String getS(){
		return s;
	}
	
	public void setS(String s){
		this.s = s;
	}
}