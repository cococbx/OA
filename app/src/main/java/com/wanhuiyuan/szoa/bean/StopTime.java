package com.wanhuiyuan.szoa.bean;
/**
 * 议题时间推送类
 * @author Administrator
 *
 */
public class StopTime {
	private int timing;//时间
	private int pushType;//推送类型= 0  设置定时  =1 为暂停 =2为继续
	private String meetingId;//会议ID
	public int getTiming() {
		return timing;
	}
	public void setTiming(int timing) {
		this.timing = timing;
	}
	public int getPushType() {
		return pushType;
	}
	public void setPushType(int pushType) {
		this.pushType = pushType;
	}
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	
	
	
}
