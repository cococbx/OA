package com.wanhuiyuan.szoa.bean;

public class Result<T> {
	private int returnValue;//返回值
	private String errMsg;//错误信息
	private T data;//结果
	private long timestamp;//时间戳
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
