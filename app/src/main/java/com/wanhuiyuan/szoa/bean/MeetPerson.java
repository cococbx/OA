package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 替会人员名单
 * 
 * @author Administrator
 * 
 */
@Table(name = "MeetPerson")
public class MeetPerson implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4647941961598040633L;
	
//	@JSONField(name="id")  
	private String id;// 被替会人的id
	private String username;// 人员姓名
	private String detpId;// 部门id
	private String detpName;// 部门名称
	private boolean isLogin;// 是否登录
	private String meetingId;// 会议ID
	private String userid;// 当前用户ID
//	@JSONField(name="idss")  
//	private int id;// 唯一id
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public String getDetpId() {
		return detpId;
	}

	public void setDetpId(String detpId) {
		this.detpId = detpId;
	}

	public String getDetpName() {
		return detpName;
	}

	public void setDetpName(String detpName) {
		this.detpName = detpName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}

}
