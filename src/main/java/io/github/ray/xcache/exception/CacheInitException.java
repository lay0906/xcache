package io.github.ray.xcache.exception;


public class CacheInitException extends RuntimeException{
    private static final long serialVersionUID = 3770477290884711704L;
    
	private String errCode;
	
	public CacheInitException(String msg){
		super(msg);
	}

	public CacheInitException(String msg, Exception e){
		super(msg, e);
	}

	public CacheInitException(String errCode, String msg){
		super(msg);
		this.errCode = errCode;
	}
	
	public CacheInitException(String errCode, String msg, Exception e){
		super(msg, e);
		this.errCode = errCode;
	}
	
    public String getErrCode() {
    	return errCode;
    }

    public void setErrCode(String errCode) {
    	this.errCode = errCode;
    }
}
