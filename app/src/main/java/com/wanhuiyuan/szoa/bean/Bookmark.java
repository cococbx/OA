package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * 书签类
 * @author Administrator
 *
 */
@Table(name = "bookmark") 
public class Bookmark  implements Serializable{
	private String id;//唯一ID
	private String userId;//用户唯一id
	private String userName;//用户名
	private String fileId;//文件ID
	private String fileName;//文件名
	private String booknotes;//书签内容
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBooknotes() {
		return booknotes;
	}
	public void setBooknotes(String booknotes) {
		this.booknotes = booknotes;
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
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
