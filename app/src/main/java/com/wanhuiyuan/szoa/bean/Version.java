package com.wanhuiyuan.szoa.bean;

/**
 * 版本号
 * @author Administrator
 *
 */
public class Version {
	private int verCode;//版本号
	private String fileId;//文件ID,通过文件id ，获取字节流写文件

	public int getVerCode() {
		return verCode;
	}

	public void setVerCode(int verCode) {
		this.verCode = verCode;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}
