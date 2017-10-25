package com.wanhuiyuan.down;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.tsz.afinal.FinalDb;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.ProgressItem;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.MD5Utility;
import com.wanhuiyuan.szoa.uiutil.MyLog;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

import de.greenrobot.event.EventBus;

public class DownloadTask2 {
	private Context mContext = null;
	private YitiFile mFileInfo = null;
	private ThreadDAOImpl2 mThreadDAO2 = null;
	private long mFinished = 0;
	private FinalDb db;
	private int mThreadCount = DownloadService2.runThreadCount;
	public boolean isPause = false;

	// 线程池
	public static ExecutorService sExecutorService = Executors
			.newFixedThreadPool(2);

	private List<DownloadThread2> mThradList = null;

	public DownloadTask2(Context mContext, YitiFile mFileInfo, int threadCount) {
		this.mContext = mContext;
		db = FinalDb.create(mContext, "szoa.db");
		this.mFileInfo = mFileInfo;
		this.mThreadDAO2 = new ThreadDAOImpl2(mContext);
		this.mThreadCount = threadCount;
	}

	public void download() {
		// 读取数据库的线程信息
		List<ThreadInfo> threadInfos = mThreadDAO2.getThread(mFileInfo.getId());
		Log.e("threadsize==", threadInfos.size() + "");

		if (threadInfos.size() == 0) {
			// 获得每个线程下载的长度
			long length = mFileInfo.getFileIoSize();
			for (int i = 0; i < 1; i++) {
				ThreadInfo threadInfo = new ThreadInfo(mFileInfo.getId(),
								mFileInfo.getFileUrl(), length * i, (i + 1)
								* length - 1, 0);
				if (i + 1 == mThreadCount) {
					threadInfo.setEnd(mFileInfo.getFileIoSize());
				}
				// 添加到线程信息集合中
				threadInfos.add(threadInfo);
				// 向数据库插入线程信息
				mThreadDAO2.insertThread(threadInfo);
			}

			mThradList = new ArrayList<DownloadThread2>();
			// 启动多个线程进行下载
			for (ThreadInfo thread : threadInfos) {
				DownloadThread2 downloadThread = new DownloadThread2(thread);
				// downloadThread.start();
				sExecutorService.execute(downloadThread);
				// 添加线程到集合中
				mThradList.add(downloadThread);
			}
		}
	}

	/**
	 * 下载线程
	 */
	class DownloadThread2 extends Thread {
		private ThreadInfo threadInfo;
		public boolean isFinished = false;

		public DownloadThread2(ThreadInfo threadInfo) {
			this.threadInfo = threadInfo;
		}

		@SuppressWarnings("resource")
		@Override
		public void run() {
			// 向数据库插入线程信息
			// Log.e("isExists==", mThreadDAO2.isExists(threadInfo.getUrl(),
			// threadInfo.getId()) + "");
			// if (!mThreadDAO2.isExists(threadInfo.getUrl(),
			// threadInfo.getId())) {
			// mThreadDAO2.insertThread(threadInfo);
			// }
			HttpURLConnection connection;
			RandomAccessFile raf;
			InputStream is;
			File file = null;
			try {
				URL url = new URL(threadInfo.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(3000);
				connection.setRequestMethod("GET");
				// 设置下载位置
				long start = threadInfo.getStart() + threadInfo.getFinish();
				connection.setRequestProperty("Range", "bytes=" + start + "-"
						+ threadInfo.getEnd());
				// 设置文件写入位置
				file = new File(MyApplication.DOWNLOAD_PATH,
						mFileInfo.getRealName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);

				Intent intent = new Intent(DownloadService2.ACTION_UPDATE);
				mFinished += threadInfo.getFinish();
				Log.e("threadInfo.getFinish==", threadInfo.getFinish() + "");

				// Log.e("getResponseCode ===", connection.getResponseCode() +
				// "");
				// 开始下载
				if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
					Log.e("getContentLength==", connection.getContentLength()
							+ "");

					// 读取数据
					is = connection.getInputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					long time = System.currentTimeMillis();
					SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
					String startTime = formatter.format(new Date(time)); 
					while ((len = is.read(buffer)) != -1) {

						if (isPause) {
							Log.e("mfinished==pause===", mFinished + "");
							// 下载暂停时，保存进度到数据库
							threadInfo.setUrl(mFileInfo.getFileUrl());

							mThreadDAO2.updateThread(threadInfo);
							return;
						}

						// 写入文件
						raf.write(buffer, 0, len);
						// 累加整个文件下载进度
						mFinished += len;
						// 累加每个线程完成的进度
						threadInfo.setFinish(threadInfo.getFinish() + len);

						// 每隔1秒刷新UI
						if (System.currentTimeMillis() - time > 1000) {// 减少UI负载
							time = System.currentTimeMillis();
							// 把下载进度发送广播给Activity
							intent.putExtra("id", mFileInfo.getId());
							intent.putExtra("finished", mFinished * 100
									/ mFileInfo.getFileIoSize());
							mContext.sendBroadcast(intent);
							Log.e(" mFinished==update==", mFinished * 100
									/ mFileInfo.getFileIoSize() + "");
							ProgressItem item = new ProgressItem();
							item.setFileName(mFileInfo.getFileName());
							item.setYitiId(mFileInfo.getYitiId());
							item.setFileId(mFileInfo.getId());
							long percent = mFinished * 100
									/ mFileInfo.getFileIoSize();
							item.setProgress((int) percent);
							threadInfo.setProgress((int) percent);
							mThreadDAO2.updateThread(threadInfo);
							// MyApplication.progress.put(mFileInfo.getId(),
							// percent);
							EventBus.getDefault().post(item);
						}

					}
					// 标识线程执行完毕
					isFinished = true;

					if (MD5Utility.verifyInstallPackage(file.getPath(),
							mFileInfo.getMD5())) {
						// 文件下载状态改完1,表示已下载。
						mFileInfo.setIsDown(1);
						try {
							db.save(mFileInfo);// 保存到数据库
						} catch (Exception e) {
							db.update(mFileInfo);
						}
						mThreadDAO2.updateThread(threadInfo);
						// 发送进度条进度
						ProgressItem item = new ProgressItem();
						item.setFileName(mFileInfo.getFileName());
						item.setYitiId(mFileInfo.getYitiId());
						item.setFileId(mFileInfo.getId());
						item.setProgress(100);
						EventBus.getDefault().post(item);
						AppDataControl.getInstance().sendDownSuccessLog(mContext, mFileInfo.getId(), mFileInfo.getMeetingId(), mFileInfo.getYitiId(), startTime);
					} else {
						mFileInfo.setIsDown(1);
						ProgressItem item = new ProgressItem();
						item.setFileName(mFileInfo.getFileName());
						item.setYitiId(mFileInfo.getYitiId());
						item.setFileId(mFileInfo.getId());
						item.setProgress(100);
						mFileInfo.setFileName("(校验失败)"
								+ mFileInfo.getFileName());
						db.update(mFileInfo);
						EventBus.getDefault().post(item);
						AppDataControl.getInstance().sendDownFailedLog(mContext, mFileInfo.getId(), mFileInfo.getMeetingId(), mFileInfo.getYitiId(), startTime);
					}
					// 检查下载任务是否完成
					checkAllThreadFinished();
					// //删除线程信息
					// mThreadDAO2.deleteThread(SharePreferences,
					// mFileInfo.getId());
					is.close();
				}
				raf.close();
				connection.disconnect();
			} catch (Exception e) {
				MyLog.e("下载过程出错了！", e.getMessage());
//				e.printStackTrace();
			} finally {
			}
		}
	}

	/**
	 * 判断所有线程是否都执行完毕
	 */
	private synchronized void checkAllThreadFinished() {
		boolean allFinished = true;
		// 编辑线程集合 判断是否执行完毕
		for (DownloadThread2 thread : mThradList) {
			if (!thread.isFinished) {
				allFinished = false;
				break;
			}
		}
		if (allFinished) {
			// 删除线程信息
			mThreadDAO2.deleteThread(mFileInfo.getId());
			// 发送广播给Activity下载结束
			Intent intent = new Intent(DownloadService2.ACTION_FINISHED);
			intent.putExtra("fileinfo", mFileInfo);
			mContext.sendBroadcast(intent);
			checkDownloadFinish();
		}
	}

	void checkDownloadFinish(){
		List<ThreadInfo> downThreads = db.findAll(ThreadInfo.class);
		// 获取下载进度的通知
		if (downThreads.size() == 0){
			AppDataControl.getInstance().sendDownFinishLog(mContext);
		}
	}
}
