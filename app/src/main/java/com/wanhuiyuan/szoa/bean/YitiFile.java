package com.wanhuiyuan.szoa.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
/**
 * 议题文件类
 * @author Administrator
 *
 */
@Table(name = "yitiFile")
public class YitiFile implements Serializable {
	private String id;//文件ID
	private String meetingId;//会议ID
	private String yitiId;//议题ID
	private String fileName;//文件名
	private String version;//文件版本
	private String fileUrl;//下载链接
	private String realName;//真实名称
	private int isDown;//是否下载0未下载，1已下载
	private String MD5;//md5值
	private int showOrder;//排序
	private int fileIoSize;//文件字节流长度

	public int getFileIoSize() {
		return fileIoSize;
	}

	public void setFileIoSize(int fileIoSize) {
		this.fileIoSize = fileIoSize;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getYitiId() {
		return yitiId;
	}

	public void setYitiId(String yitiId) {
		this.yitiId = yitiId;
	}

	public int getShowOrder() {
		return showOrder;
	}

	public void setShowOrder(int showOrder) {
		this.showOrder = showOrder;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getIsDown() {
		return isDown;
	}

	public void setIsDown(int isDown) {
		this.isDown = isDown;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public static final Parcelable.Creator<YitiFile> CREATOR = new Creator<YitiFile>() {

		@Override
		public YitiFile createFromParcel(Parcel source) {
			YitiFile contact = new YitiFile();
			contact.id = source.readString();
			contact.fileName = source.readString();
			contact.version = source.readString();
			contact.fileUrl = source.readString();
			return contact;
		}

		@Override
		public YitiFile[] newArray(int size) {
			return new YitiFile[size];
		}
	};
}
