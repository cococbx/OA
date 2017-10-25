package com.wanhuiyuan.szoa.bean;

/**
 * 刷新下载进度条类
 * @author Administrator
 *
 */
public class ProgressItem {
	public String fileId;//文件id 
	public String yitiId;//议题ID
	public String fileName;//文件名
	
	public int progress;//进度

	public String getYitiId() {
		return yitiId;
	}

	public void setYitiId(String yitiId) {
		this.yitiId = yitiId;
	}

	

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}