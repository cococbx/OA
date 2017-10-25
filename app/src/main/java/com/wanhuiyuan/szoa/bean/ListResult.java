package com.wanhuiyuan.szoa.bean;

import java.util.List;

/*
 * 
 * 请求结果封装
 */
public class ListResult<T> {
	private int state;//返回状态
	private int page_total;//页数
	private String error;//错误信息
	private List<T> data;//结果集
	private boolean hasmore;//是否还有更多数据
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getPage_total() {
		return page_total;
	}

	public void setPage_total(int page_total) {
		this.page_total = page_total;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public boolean isHasmore() {
		return hasmore;
	}

	public void setHasmore(boolean hasmore) {
		this.hasmore = hasmore;
	}

}
