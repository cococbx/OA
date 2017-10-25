package com.wanhuiyuan.szoa.bean;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 离线登录时用户信息
 * @author Administrator
 *
 */
@Table(name = "offLineUser") 
public class OffLineUser {

	private String id ;//唯一ID
	private String username ;//账号
	private String password ;//密码
	private int showOrder;//排序
	private String xingming ;//姓名
	

	public String getXingming() {
		return xingming;
	}
	public void setXingming(String xingming) {
		this.xingming = xingming;
	}
	public int getShowOrder() {
		return showOrder;
	}
	public void setShowOrder(int showOrder) {
		this.showOrder = showOrder;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
