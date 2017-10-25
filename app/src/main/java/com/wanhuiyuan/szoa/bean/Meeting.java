package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 会议对象
 */
@Table(name = "meeting")
public class Meeting implements Serializable {

	private String id;// 唯一ID
	private String name;// 会议名称
	private String content;// 会议内容
	private String time;// 开始时间
	private String meetingType;// 会议类型
	private int status;// 会议状态
	private String hytzwjId;// 会议通知文件id
	private DownFile hytzwj;// 会议通知文件url
	private List<Dept> dept;// 参会部门
	private boolean isDown;// 文件是否全部下载完成
	private List<MeetingYiti> yitiList;// 议题列表集
	private int attentType;	//参加会议权限，0可参考，1可替会
	// 绑定到用户
	private String userid;// 用户ID

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public int getAttentType() {
		return attentType;
	}

	public void setAttentType(int attentType) {
		this.attentType = attentType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}

	public String getHytzwjId() {
		return hytzwjId;
	}

	public void setHytzwjId(String hytzwjId) {
		this.hytzwjId = hytzwjId;
	}

	public DownFile getHytzwj() {
		return hytzwj;
	}

	public void setHytzwj(DownFile hytzwj) {
		this.hytzwj = hytzwj;
	}

	public List<Dept> getDept() {
		return dept;
	}

	public void setDept(List<Dept> dept) {
		this.dept = dept;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<MeetingYiti> getYitiList() {
		return yitiList;
	}

	public void setYitiList(List<MeetingYiti> yitiList) {
		this.yitiList = yitiList;
	}

	public String getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(String meetingType) {
		this.meetingType = meetingType;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
