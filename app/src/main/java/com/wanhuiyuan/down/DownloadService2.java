package com.wanhuiyuan.down;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

public class DownloadService2 extends Service {

	public static final int runThreadCount = 1;

	private static final String TAG = "DownloadService2";
	// 初始化
	private static final int MSG_INIT = 0x2;
	// 开始下载
	public static final String ACTION_START = "ACTION_START_2";
	// 暂停下载
	public static final String ACTION_PAUSE = "ACTION_PAUSE_2";
	// 结束下载
	public static final String ACTION_FINISHED = "ACTION_FINISHED_2";
	// 更新UI
	public static final String ACTION_UPDATE = "ACTION_UPDATE_2";
	// 下载路径

	// 下载任务集合
	private Map<String, DownloadTask2> tasks = new LinkedHashMap<String, DownloadTask2>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			// 获得Activity传来的参数
			if (ACTION_START.equals(intent.getAction())) {
				YitiFile fileInfo = (YitiFile) intent
						.getSerializableExtra("fileinfo");
				Log.e(TAG,
						"onStartCommand: ACTION_START-" + fileInfo.toString());
				new InitThread(fileInfo).start();

			} else if (ACTION_PAUSE.equals(intent.getAction())) {
				YitiFile fileInfo = (YitiFile) intent
						.getSerializableExtra("fileinfo");
				Log.e(TAG,
						"onStartCommand:ACTION_PAUSE- " + fileInfo.toString());
				// 从集合在取出下载任务
				DownloadTask2 task2 = tasks.get(fileInfo.getId());
				if (task2 != null) {
					// 停止下载任务
					task2.isPause = true;
				}

			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				YitiFile fileinfo = (YitiFile) msg.obj;
				Log.e("mHandler--fileinfo:", fileinfo.toString());
				// 启动下载任务
				DownloadTask2 downloadTask2 = new DownloadTask2(
						DownloadService2.this, fileinfo, runThreadCount);
				downloadTask2.download();
				// 将下载任务添加到集合中
				tasks.put(fileinfo.getId(), downloadTask2);
				break;
			}
		}
	};

	/**
	 * 初始化 子线程
	 */
	class InitThread extends Thread {
		private YitiFile tFileInfo;

		public InitThread(YitiFile tFileInfo) {
			this.tFileInfo = tFileInfo;
		}

		@Override
		public void run() {
			HttpURLConnection conn;
			RandomAccessFile raf;
			try {
				// 连接网络文件
				URL url = new URL(tFileInfo.getFileUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				int length = -1;
				Log.e("getResponseCode==", conn.getResponseCode() + "");
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// 获取文件长度
					length = tFileInfo.getFileIoSize();
					Log.e("length==", length + "");
				}
				if (length < 0) {
					return;
				}
				// 在本地创建文件
				File file = new File(MyApplication.DOWNLOAD_PATH,
						tFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				// 设置本地文件长度
				raf.setLength(length);
				mHandler.obtainMessage(MSG_INIT, tFileInfo).sendToTarget();

				raf.close();
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
