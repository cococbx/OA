package com.wanhuiyuan.szoa.bean;

import java.util.List;

public class MeetingPersonLabel {
	String label;
	List<MeetingPersonInfo> userList;
	boolean isNewLine;
	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<MeetingPersonInfo> getUserList() {
		return userList;
	}
	public void setUserList(List<MeetingPersonInfo> userList) {
		this.userList = userList;
	}
	public boolean isNewLine() {
		return isNewLine;
	}
/*	public void setNewLine(boolean isNewLine) {
		this.isNewLine = isNewLine;
	}*/
	public void setIsNewLine(boolean isNewLine) {
		this.isNewLine = isNewLine;
	}

}
