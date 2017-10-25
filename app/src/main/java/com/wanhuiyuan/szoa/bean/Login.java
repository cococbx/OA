package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
/**
 * 用户对象
 * @author Administrator
 *
 */
@Table(name = "offLogin") 
public class Login implements Serializable {
	
	@Id(column="idc") 
	private int idc;//自增字段
	private String id;//用户唯一id
	private String username;//用户账号
	private String truename;//真实名称
	private String sex;//性别
	private String imgUrl;//头像
	private String deptId;//部门id
	private String deptName;//部门名称
	private String token;
	private boolean isLogin;//是否登录
	private String loginType;//0  是 未参会,1 是 参会,2替会
	private int deptOrder;//部门培训
	private String meetingId;//会议id
	private int showorder;//排序
	
	
	public int getShoworder() {
		return showorder;
	}
	public void setShoworder(int showorder) {
		this.showorder = showorder;
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
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
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
	public String getTruename() {
		return truename;
	}
	public void setTruename(String truename) {
		this.truename = truename;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
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
	
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
}
