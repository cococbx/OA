package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 议题对象
 * @author Administrator
 *
 */
@Table(name = "meetingYiti")
public class MeetingYiti implements Serializable {
	private String id;//唯一ID
	private String name;//议题名称
	private String meetingId;//会议ID
	private int showOrder;//排序
	private List<YitiFile> files;//文件列表
	
	//议题绑定到用户
	private String userid;//用户ID

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<YitiFile> getFiles() {
		return files;
	}

	public void setFiles(List<YitiFile> files) {
		this.files = files;
	}

}
