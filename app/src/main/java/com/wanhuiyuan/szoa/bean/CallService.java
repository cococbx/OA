package com.wanhuiyuan.szoa.bean;

import net.tsz.afinal.annotation.sqlite.Table;
/**
 * 数据服务内容类
 * @author Administrator
 *
 */
@Table(name = "callService") 
public class CallService {
	
	private String id;//唯一id 
	private String time;//发送时间
	private String meetingId;//会议ID
	private String meetingName;//会议名称
	private String serviceContent;//服务内容
	private String deptName;//部门名称
	private String userId;//用户唯一ID
	private String userName;//用户姓名
	private String doTime;//服务处理时间
	private String doUser;//服务处理人
	private int isServer;//0未服务,1以服务
	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}

	public String getServiceContent() {
		return serviceContent;
	}

	public void setServiceContent(String serviceContent) {
		this.serviceContent = serviceContent;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	
	

	public String getDoUser() {
		return doUser;
	}

	public void setDoUser(String doUser) {
		this.doUser = doUser;
	}

	public String getDoTime() {
		return doTime;
	}

	public void setDoTime(String doTime) {
		this.doTime = doTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIsServer() {
		return isServer;
	}

	public void setIsServer(int isServer) {
		this.isServer = isServer;
	}
	

}
