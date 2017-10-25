package com.wanhuiyuan.szoa.bean;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 批注类
 * 
 * @author Administrator
 * 
 */
@Table(name = "postil")
public class Postil {
	private String id;//ID
	private int version;//版本号
	private String content;//批注内容（金格生成的json信息）
	private String fileId;//文件id 
	private String userId;//用户ID
	//11.17 tom
	private String meetingId;//会议ID
	private boolean isupload = false;//是否上传

	public boolean isIsupload() {
		return isupload;
	}

	public void setIsupload(boolean isupload) {
		this.isupload = isupload;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
