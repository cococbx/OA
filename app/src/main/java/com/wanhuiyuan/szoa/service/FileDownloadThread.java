package com.wanhuiyuan.szoa.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ListIterator;

import net.tsz.afinal.FinalDb;

import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.ProgressItem;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.uiutil.MD5Utility;

import de.greenrobot.event.EventBus;
import android.util.Log;

/**
 * 文件下载类
 * 
 * @author 左大明
 */
public class FileDownloadThread extends Thread {

	private static final String TAG = FileDownloadThread.class.getSimpleName();
	FinalDb db;
	/** 当前下载是否完成 */
	private boolean isCompleted = false;
	/** 当前下载文件长度 */
	private int downloadLength = 0;
	/** 文件保存路径 */
	private File file;
	/** 文件下载路径 */
	private URL downloadUrl;
	/** 当前下载线程ID */
	private int threadId;
	/** 线程下载数据长度 */
	private int blockSize;

	/** 议题文件实体类 */
	private YitiFile yitiFile;
	/** 下载服务 */
	DownloadService downService;

	/**
	 * 
	 * @param url
	 *            :文件下载地址
	 * @param file
	 *            :文件保存路径
	 * @param blocksize
	 *            :下载数据长度
	 * @param threadId
	 *            :线程ID
	 * @param yitiFile
	 *            :议题实体类对象
	 * @param DownloadService
	 *            :下载服务
	 */
	public FileDownloadThread(URL downloadUrl, File file, int blocksize,
			int threadId, YitiFile yitiFile, FinalDb db,
			DownloadService downService) {
		this.downloadUrl = downloadUrl;
		this.file = file;
		this.threadId = threadId;
		this.blockSize = blocksize;
		this.yitiFile = yitiFile;
		this.db = db;
		this.downService = downService;
	}

	@Override
	public void run() {

		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		try {

			URLConnection conn = downloadUrl.openConnection();
			conn.setConnectTimeout(5 * 100000);
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", downloadUrl.toString());
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setAllowUserInteraction(true);

			int startPos = blockSize * (threadId - 1);// 开始位置
			int endPos = blockSize * threadId - 1;// 结束位置

			// 设置当前线程下载的起点、终点
			conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
			System.out.println(Thread.currentThread().getName() + "  bytes="
					+ startPos + "-" + endPos);

			byte[] buffer = new byte[1024];
			bis = new BufferedInputStream(conn.getInputStream());

			raf = new RandomAccessFile(file, "rwd");
			raf.seek(startPos);
			int oldPercent = 0;
			int len;
			while ((len = bis.read(buffer, 0, 1024)) != -1) {
				raf.write(buffer, 0, len);
				downloadLength += len;
				float pf = (downloadLength * 1.0f) / endPos;
				int percent = (int) (pf * 100);
				// 保证只有在数值有变化之后才会执行update
				if (percent > oldPercent && percent < 100) {
					// 通知议题列表界面刷新进度条
					oldPercent = percent;
					ProgressItem item = new ProgressItem();
					item.setFileName(yitiFile.getFileName());
					item.setYitiId(yitiFile.getYitiId());
					item.setFileId(yitiFile.getId());
					item.setProgress(percent);
//					MyApplication.progress.put(yitiFile.getId(), percent);
					EventBus.getDefault().post(item);
				}
			}
			// 下载完成
			isCompleted = true;
			Log.d(TAG, "current thread task has finished,all size:"
					+ downloadLength);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			MyApplication.downedFilesNum++;

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isCompleted) {
				// 进行md5校验防止文件下载过程损坏
				if (MD5Utility.verifyInstallPackage(file.getPath(),
						yitiFile.getMD5())) {
					// 文件下载状态改完1,表示已下载。
					yitiFile.setIsDown(1);
					try {
						db.save(yitiFile);// 保存到数据库
					} catch (Exception e) {
						db.update(yitiFile);
					}
					// 发送进度条进度
					ProgressItem item = new ProgressItem();
					item.setFileName(yitiFile.getFileName());
					item.setYitiId(yitiFile.getYitiId());
					item.setFileId(yitiFile.getId());
					item.setProgress(100);
					EventBus.getDefault().post(item);
				} else {
					yitiFile.setIsDown(1);
					ProgressItem item = new ProgressItem();
					item.setFileName(yitiFile.getFileName());
					item.setYitiId(yitiFile.getYitiId());
					item.setFileId(yitiFile.getId());
					item.setProgress(100);
					yitiFile.setFileName("(校验失败)"+yitiFile.getFileName());
					db.update(yitiFile);
//					MyApplication.progress.put(yitiFile.getId(), 100);
					EventBus.getDefault().post(item);
				}
			}
			if (MyApplication.downedFilesNum == MyApplication.downFilesNum) {
				downService.down();
			}
		}
	}

	/**
	 * 线程文件是否下载完毕
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * 线程下载文件长度
	 */
	public int getDownloadLength() {
		return downloadLength;
	}

}
