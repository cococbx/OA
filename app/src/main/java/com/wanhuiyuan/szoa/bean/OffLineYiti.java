package com.wanhuiyuan.szoa.bean;

import net.tsz.afinal.annotation.sqlite.Table;
/**
 * 离线议题与人员对应类
 * @author Administrator
 *
 */
@Table(name = "OffLineYiti")
public class OffLineYiti {

	private String id;//唯一ID
	private String yitiId;//议题ID
	private String userId;//用户ID

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYitiId() {
		return yitiId;
	}

	public void setYitiId(String yitiId) {
		this.yitiId = yitiId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


}
