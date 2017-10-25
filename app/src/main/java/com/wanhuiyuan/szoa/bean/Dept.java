package com.wanhuiyuan.szoa.bean;

import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 部门类
 * 
 * @author Administrator
 * 
 */
@Table(name = "offDept")
public class Dept {
	
	private int id;// 唯一id
	private String meetingId;// 会议ID
	private String detpId;// 部门id
	private String detpName;// 部门名称
	private int deptLevel;// 部门级别
	private String avatar;// 部门头像
	private List<Login> userList;// 用户列表
	private int deptOrder;// 排序

	public List<Login> getUserList() {
		return userList;
	}

	public void setUserList(List<Login> userList) {
		this.userList = userList;
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

	public int getDeptLevel() {
		return deptLevel;
	}

	public void setDeptLevel(int deptLevel) {
		this.deptLevel = deptLevel;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public int getDeptOrder() {
		return deptOrder;
	}

	public void setDeptOrder(int deptOrder) {
		this.deptOrder = deptOrder;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
