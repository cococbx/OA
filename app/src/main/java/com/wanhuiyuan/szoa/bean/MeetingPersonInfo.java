package com.wanhuiyuan.szoa.bean;

public class MeetingPersonInfo {
	static final public int ATTEND_MEETING = 1;
	static final public int REPLACE_MEETING = 2;
	static final public int LEAVE_MEETING = 3;
	static final public int ABSENCE_MEETING = 4;
	
	String deptId;
	String deptName;
	String id;
	boolean login;
	String loginType;
	String truename;
	String willName;
	
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isLogin() {
		return login;
	}
	public void setLogin(boolean login) {
		this.login = login;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
	}
	public String getWillName() {
		return willName;
	}
	public void setWillName(String willName) {
		this.willName = willName;
	}
	
	
}
