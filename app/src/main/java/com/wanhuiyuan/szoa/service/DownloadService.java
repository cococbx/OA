package com.wanhuiyuan.szoa.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.wanhuiyuan.down.DownloadService2;
import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.ProgressItem;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.HttpClientHolder;
import com.wanhuiyuan.szoa.uiutil.MD5Utility;
import com.wanhuiyuan.szoa.uiutil.MyFileUtil;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;
import com.wanhuiyuan.szoa.uiutil.SDHandler;

import de.greenrobot.event.EventBus;

/**
 * 下载议题文件的服务
 * 
 * @author Administrator
 * 
 */
@SuppressLint("NewApi")
public class DownloadService extends IntentService {
	public static final int UPDATE_PROGRESS = 8344;
	Meeting meeting;
	FinalDb db;
	List<MeetingYiti> yitiList;
	FinalHttp fh;
	private final static String TAG = "break_point_download";
	public final static int OK = 0;// 下载成功
	public final static int SD_ERROR = 1;// 没有sd卡
	public final static int NET_ERROR = 2;// 网络错误
	public ExecutorService pool = Executors.newFixedThreadPool(5);
	MyFileUtil fileUtil;
	Handler handler = new Handler();
	Map<String, Object> params = new HashMap<String, Object>();

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	public void onCreate() {
		fileUtil = new MyFileUtil();
		EventBus.getDefault().register(this);
		super.onCreate();
	}

	public void onEventMainThread(ProgressItem item) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		db = FinalDb.create(this, "szoa.db");
		// 接收其他界面发送的指令11.17 tom
		String msgRecv = intent.getStringExtra("Command");
		// 接收到重新下载指令后重新下载文件
		if ("REDOWNLOAD".equals(msgRecv)) {
			down();
			return;
		}

		

	}

	public void down() {
		MyApplication.downFilesNum = 0;
		MyApplication.downedFilesNum = 0;
		List<YitiFile> dbfiles = db.findAllByWhere(YitiFile.class,
				" isDown<>1 ");
		final int listCount = dbfiles.size();
		MyApplication.downFilesNum = listCount;
		// 判断未下载完成文件的数量
		for (int i = 0; i < listCount; i++) {
			final YitiFile file = dbfiles.get(i);
			Intent intent = new Intent(this, DownloadService2.class);
            intent.setAction(DownloadService2.ACTION_START);
            intent.putExtra("fileinfo", file);
            startService(intent);
//			boolean isHave = false;
//			for (int j = 0; j < MyApplication.files.size(); j++) {
//				if (file.getId().equals(MyApplication.files.get(j))) {
//					isHave = true;
//					break;
//				}
//			}
//			if (!isHave) {
//				MyApplication.files.add(file.getId());
//				// 调用断点续传的方法
//				// new WorkerThread(DownloadService.this,
//				// SharePreferences.getInstance(
//				// DownloadService.this).getServerUrl(
//				// MyApplication.SERVICE_HOST)
//				// + file.getFileUrl(), file, db).run();
//				// 调用下载方法
//				// executor.execute(new Runnable() {
//				//
//				// @Override
//				// public void run() {
//				// new Thread(new Runnable() {
//				//
//				// @Override
//				// public void run() {
//				// // TODO Auto-generated method stub
//				// doBreakpointResumeDownload(
//				// DownloadService.this,
//				// SharePreferences.getInstance(
//				// DownloadService.this).getServerUrl(
//				// MyApplication.SERVICE_HOST)
//				// + file.getFileUrl(), file, 1,
//				// listCount - 1);
//				// }
//				// }).start();
//				// executor.execute(new Runnable() {
//				//
//				// @Override
//				// public void run() {
//				// TODO Auto-generated method stub
//				downloadTask task = new downloadTask(SharePreferences
//						.getInstance(DownloadService.this).getServerUrl(
//								MyApplication.SERVICE_HOST)
//						+ file.getFileUrl(), 1, getFilesDir().getPath(), file);
//				task.start();
//				// }
//				// });
//
//				// }
//				// });
//			}
		}
	}

	/**
	 * 支持断点续传的方式进行下载
	 * 
	 * @param url
	 * @param fileDir
	 * @param fileName
	 */
	public int doBreakpointResumeDownload(Context context, String url,
			YitiFile file, int index, int listCount) {
		// Log.v(TAG, "进行断点续传下载，地址：" + url + "|文件：" + Environment
		// .getExternalStorageDirectory().getAbsolutePath()
		// + "/" + file.getFileName());
		if (SDHandler.isSDAvaliable()) {

			// 追加错误捕获，已经发现在文件路径不对或为空时程序奔溃的情况。9-19 tom
			File tempFile;
			try {
				tempFile = new File(context.getFilesDir().getPath(),
						file.getRealName());
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				return NET_ERROR;
			}
			// 没有可用网络连接
			if (!NetworkStatusHandler.isNetWorkAvaliable(context)) {
				Log.v(TAG, "没有可用网络连接，下载失败！");
				return NET_ERROR;
			} else {
				HttpGet httpGet = new HttpGet(url);
				HttpResponse response = null;
				InputStream is = null;
				// 上次中断位置
				long breakPoint = 0l;
				if (tempFile.exists() && tempFile.isFile()) {
					Log.v(TAG,
							"该apk文件已经下载过一部分，从上次中断位置开始下载：" + tempFile.toString());
					breakPoint = tempFile.length();
					Log.v(TAG, "断点位置：" + breakPoint + "b" + " | " + breakPoint
							* 1.0f / 1024 + "k" + " | " + breakPoint * 1.0f
							/ 1024 / 1024 + "m");
				}
				httpGet.addHeader("RANGE", "bytes=" + breakPoint + "-");
				try {
					response = HttpClientHolder.getInstance().execute(httpGet);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return NET_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					return NET_ERROR;
				}
				// 返回码不是206
				if (response.getStatusLine().getStatusCode() != 206) {
					Log.e(TAG, "下载失败，返回码不是206"
							+ response.getStatusLine().getStatusCode());
					// 认为下载完成
					// String result;
					// try {
					// file.setIsDown(1);
					// try {
					// db.save(file);
					// } catch (Exception e) {
					// db.update(file);
					// }
					// //通知议题列表界面刷新进度条
					// ProgressItem item = new ProgressItem();
					// item.setFileName(file.getFileName());
					// item.setFileId(file.getId());
					// item.setYitiId(file.getYitiId());
					// item.setProgress(100);
					// EventBus.getDefault().post(item);
					// // }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					return NET_ERROR;
				}
				HttpEntity entity = response.getEntity();
				long total = file.getFileIoSize();
				try {
					is = entity.getContent();
				} catch (IllegalStateException e) {
					e.printStackTrace();
					return NET_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					return NET_ERROR;
				}

				// 没有断点续传文件,重新新建文件
				if (!tempFile.exists()) {
					tempFile = new File(context.getFilesDir().getPath(),
							file.getRealName());
					if (!tempFile.exists()) {
						try {
							tempFile.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Log.v(TAG, "下载失败，新建文件失败：\n" + e.getMessage());
							return SD_ERROR;
						}
					}
					Log.v(TAG, "没有可用断点续传文件，新建一个文件:" + tempFile.toString());
				}
				byte[] buffer = new byte[1024];
				RandomAccessFile oSavedFile = null;
				try {
					oSavedFile = new RandomAccessFile(tempFile, "rw");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					return SD_ERROR;
				}
				// 定位文件指针到断点位置
				try {
					oSavedFile.seek(breakPoint);
				} catch (IOException e1) {
					e1.printStackTrace();
					return SD_ERROR;
				}
				try {
					int oldPercent = 0;
					int len;
					while ((len = is.read(buffer, 0, 1024)) != -1) {
						oSavedFile.write(buffer, 0, len);
						breakPoint += len;

						int percent = (int) (breakPoint * 1.0f / total * 100);
						// 保证只有在数值有变化之后才会执行update
						if (percent > oldPercent && percent < 100) {
							// 通知议题列表界面刷新进度条
							oldPercent = percent;
							ProgressItem item = new ProgressItem();
							item.setFileName(file.getFileName());
							item.setYitiId(file.getYitiId());
							item.setFileId(file.getId());
							item.setProgress(percent);
//							MyApplication.progress.put(file.getId(), percent);
							EventBus.getDefault().post(item);
						}

					}

				} catch (IOException e) {
					// e.printStackTrace();
					Log.v(TAG, "下载失败，从流中读取字节时候异常,保存下载文件位置，等待下次下载");
					return NET_ERROR;
				} finally {
					MyApplication.downedFilesNum++;
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						oSavedFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// todo
				// String result;
				// try {
				// result = fileUtil.decrypt(DownloadService.this, Environment
				// .getExternalStorageDirectory().getAbsolutePath()
				// + "/hyxttemp/" + file.getRealName(), file
				// .getRealName(), 6, file.getMD5());
				//
				// if (result != null) {
				if (MD5Utility.verifyInstallPackage(tempFile.getPath(),
						file.getMD5())) {
					// 文件下载状态改完1,表示已下载。
					file.setIsDown(1);
					try {
						db.save(file);// 保存到数据库
					} catch (Exception e) {
						db.update(file);
					}
					// 发送进度条进度
					ProgressItem item = new ProgressItem();
					item.setFileName(file.getFileName());
					item.setYitiId(file.getYitiId());
					item.setFileId(file.getId());
					item.setProgress(100);
					EventBus.getDefault().post(item);
				} else {
					file.setIsDown(1);
					ProgressItem item = new ProgressItem();
					item.setFileName(file.getFileName());
					item.setYitiId(file.getYitiId());
					item.setFileId(file.getId());
					item.setProgress(100);
					file.setFileName("(校验失败)" + file.getFileName());
					db.update(file);
//					MyApplication.progress.put(file.getId(), 100);
					EventBus.getDefault().post(item);
				}
			}
			if (MyApplication.downedFilesNum == MyApplication.downFilesNum) {
				down();
			}

			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		} else {
			Log.v(TAG, "sd卡不可用，下载失败！");
			return SD_ERROR;
		}
		if (index == listCount)
			down();
		return OK;
	}

	@Override
	public void onDestroy() {
//		Intent localIntent = new Intent(this, DownloadService.class);
//		this.startService(localIntent);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/**
	 * 多线程文件下载
	 * 
	 * @author yangxiaolong
	 * @2014-8-7
	 */
	class downloadTask extends Thread {
		private String downloadUrl;// 下载链接地址
		private int threadNum;// 开启的线程数
		private String filePath;// 保存文件路径地址
		private int blockSize;// 每一个线程的下载量
		private YitiFile yitiFile;

		public downloadTask(String downloadUrl, int threadNum, String fileptah,
				YitiFile yitiFile) {
			this.downloadUrl = downloadUrl;
			this.threadNum = threadNum;
			this.filePath = fileptah;
			this.yitiFile = yitiFile;
		}

		@Override
		public void run() {
			pool.execute(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					FileDownloadThread[] threads = new FileDownloadThread[1];
					try {
						URL url = new URL(downloadUrl);
						Log.d(TAG, "download file http path:" + downloadUrl);

						// URLConnection conn = url.openConnection();
						// 读取下载文件总大小
						int fileSize = yitiFile.getFileIoSize();
						if (fileSize <= 0) {
							System.out.println("读取文件失败");
							return;
						}
						// 设置ProgressBar最大的长度为文件Size

						// 计算每条线程下载的数据长度
						blockSize = (fileSize % threadNum) == 0 ? fileSize
								/ threadNum : fileSize / threadNum + 1;

						Log.d(TAG, "fileSize:" + fileSize + "  blockSize:"
								+ blockSize);

						File file = new File(filePath, yitiFile.getRealName());
						// 没有断点续传文件,重新新建文件
						if (!file.exists()) {
							try {
								file.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < threads.length; i++) {
							// 启动线程，分别下载每个线程需要下载的部分

							threads[i] = new FileDownloadThread(url, file,
									blockSize, (i + 1), yitiFile, db,
									DownloadService.this);
							threads[i].setName("Thread:" + i);
							threads[i].start();
						}

						boolean isfinished = false;
						int downloadedAllSize = 0;
						while (!isfinished) {
							isfinished = true;
							// 当前所有线程下载总量
							downloadedAllSize = 0;
							for (int i = 0; i < threads.length; i++) {
								downloadedAllSize += threads[i]
										.getDownloadLength();
								if (!threads[i].isCompleted()) {
									isfinished = false;
								}
							}
							// 通知handler去更新视图组件
						}
						Log.d(TAG, " all of downloadSize:" + downloadedAllSize);

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

		}
	}

	public interface reDown {
		public void redown();
	}

}