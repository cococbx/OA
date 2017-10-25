package com.wanhuiyuan.szoa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.down.DownloadService2;
import com.wanhuiyuan.down.ThreadInfo;
import com.wanhuiyuan.szoa.MainActivity.ThreadShow;
import com.wanhuiyuan.szoa.adapter.NowMeetingListAdapter;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.MeetPerson;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.ProgressItem;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.myview.AddPopWindowMeetingList;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.HttpClientHolder;
import com.wanhuiyuan.szoa.uiutil.MyLog;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;
import com.wanhuiyuan.szoa.uiutil.SDHandler;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

import de.greenrobot.event.EventBus;

/**
 * 会议列表页面
 * 
 * @author 左大明
 * 
 */
public class MeetingListActivity extends BaseActivity {
	FinalDb db;
	List<Meeting> nowList;// 当前进行或者材料分发状态的会议
	List<Meeting> waiteList;// 已经结束的会议
	// List<Meeting> meetingList;
	ListView nowListView;// 当前进行或者材料分发状态的会议ListView
	ListView waiteListView;// 已经结束的会议ListView
	ListResult<Meeting> result;// 当前进行或者材料分发状态的会议结果集合
	ListResult<Meeting> waiteresult;// 已经结束的会议结果集合
	NowMeetingListAdapter nowAdpter;// 当前进行或者材料分发状态的会议适配器
	NowMeetingListAdapter waiteAdpter;// 已经结束的会议结果适配器
	TextView no_data, historyTextView;// 没有请求到数据的提示，历史会议文字侠士
	ProgressBar load_progressBar;// 加载数据时的进度条
	ImageButton refush;// 配置按钮,刷新按钮config,
	Intent regIntent;
	boolean isUpdateIp = false;// 是否更换IP 发现更换刷新数据
	public final static int OK = 0;// 文件成功下载
	public final static int SD_ERROR = 1;// 没有sd卡
	public final static int NET_ERROR = 2;// 网络错误

	protected boolean mIsExit; // 是否退出

	// 点击刷新
	private ImageButton top_more;// 刷新按钮
	private MainActivity mainActivity;

	// 文件下载状态
	List<YitiFile> fileList;// 文件列表
	List<MeetingYiti> yitiList;// 议题列表
	int notDownCount = 0;// 未下载数量
	int isDownCount = 0;// 已下载数量
	int haseCount = 0;// 本地已存在数量
	TextView fileStateTextView; // 下载状态Textview

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		db = FinalDb.create(this, "szoa.db");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				downloadFiles();
			}
		}).start();

		// 初始化服务器访问地址
		// MyApplication.SERVICE_HOST = "http://" +
		// getResources().getString(R.string.ip) + ":" +
		// getResources().getString(R.string.port) + "/" +
		// getResources().getString(R.string.project);

		// 信鸽注册服务
		// XGPushManager.registerPush(context, new XGIOperateCallback() {
		//
		// @Override
		// public void onSuccess(Object arg0, int arg1) {
		// // TODO Auto-generated method stub
		// Log.e("xg", arg0.toString());
		// }
		//
		// @Override
		// public void onFail(Object arg0, int arg1, String arg2) {
		// // TODO Auto-generated method stub
		// Log.e("xg", arg0.toString());
		// }
		// });

		setContentView(R.layout.meeting_list_layout);

		// 文件下载状态
		fileList = new ArrayList<YitiFile>();

		// db = FinalDb.create(this, "szoa.db");
		nowList = new ArrayList<Meeting>();
		waiteList = new ArrayList<Meeting>();
		// meetingList = new ArrayList<Meeting>();
		no_data = (TextView) findViewById(R.id.no_data);
		load_progressBar = (ProgressBar) findViewById(R.id.load_progressBar);
		TextView title = (TextView) findViewById(R.id.sys_title_txt);
		historyTextView = (TextView) findViewById(R.id.historyTextView);
		title.setText("会议列表");
		refush = (ImageButton) findViewById(R.id.refuse);
		refush.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mIsExit = true;
				exit();

			}
		});

		// mainActivity=(MainActivity) this.getApplicationContext();
		top_more = (ImageButton) findViewById(R.id.top_more);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AddPopWindowMeetingList(MeetingListActivity.this)
						.showPopupWindow(top_more);
			}
		});

		nowListView = (ListView) findViewById(R.id.nowList);
		waiteListView = (ListView) findViewById(R.id.waiteList);
		waiteListView.setVisibility(View.GONE);
		// Map<String, Object> params1 = new HashMap<String, Object>();
		// params1.put("verFlag", "0");

		// 检查版本更新
		// new VersionThread(this, params1).start();

		// 下载状态
		fileStateTextView = (TextView) findViewById(R.id.fileStateTextView);
		if(MyApplication.RESTART_DOWNLOAD){
			new Thread(new ThreadShow()).start();
		}
	}

	private void initNow(final boolean isOnline) {
		nowAdpter = new NowMeetingListAdapter(MeetingListActivity.this, nowList);
		nowListView.setAdapter(nowAdpter);
		nowListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 具有权限直接参会
				if (nowList.get(arg2).getAttentType() == 0) {
					Intent intent = null;
					intent = new Intent(MeetingListActivity.this,
							MainActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("meeting", nowList.get(arg2));
					b.putBoolean("isOnline", isOnline);
					intent.putExtras(b);
					startActivity(intent);

				}// 替会
				else if (nowList.get(arg2).getAttentType() == 1) {
					Intent intent = new Intent(MeetingListActivity.this,
							ReplaceUserActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("meeting", nowList.get(arg2));
					intent.putExtras(b);
					startActivityForResult(intent, 10);
				}

			}
		});
	}

	// 接收替会的选择结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) {
			if (resultCode == 11) {
				MeetPerson result = (MeetPerson) data
						.getSerializableExtra("user");
				Meeting meeting = (Meeting) data
						.getSerializableExtra("meeting");
				Intent intent = null;
				intent = new Intent(MeetingListActivity.this,
						MainActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("meeting", meeting);
				b.putInt("loginType", 2);
				// b.putString("replaceName", result.getUsername());
				b.putString("replaceName", MyApplication.USERXINGMING);
				b.putString("oTherUserid", result.getId());
				intent.putExtras(b);
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 貌似没用注释掉 tom 11.15
	// private void initWaite(final boolean isOnline) {
	// historyTextView.setVisibility(View.VISIBLE);
	// waiteListView.setVisibility(View.VISIBLE);
	// waiteAdpter = new NowMeetingListAdapter(MeetingListActivity.this,
	// waiteList);
	// waiteListView.setAdapter(waiteAdpter);
	// waiteListView.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// Intent intent = null;
	// intent = new Intent(MeetingListActivity.this,
	// MainActivity.class);
	// // }
	// Bundle b = new Bundle();
	// b.putSerializable("meeting", waiteList.get(arg2));
	// intent.putExtras(b);
	// startActivity(intent);
	//
	// }
	// });
	// }

	/**
	 * 材料分发、会议开始状态的会议列表，在线获取不到的话获取本地
	 * 
	 * @author 左大明
	 * 
	 */
	class MeetingListThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public MeetingListThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			try {
				result = AppDataControl.getInstance().getMeetingList(context,
						params);
				MyLog.e("会议列表返回数据", result+"");
				refush.setClickable(true);
				if (null != result) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (result.getState() == 0) {
									if (result.getData().size() > 0) {
										nowList = result.getData();
										initNow(true);
										for (int i = 0; i < nowList.size(); i++) {
											final Meeting meeting = nowList
													.get(i);
											meeting.setUserid(MyApplication.USERID);
											try {
												db.save(meeting);
											} catch (Exception e) {
												db.update(meeting);
											}
											final String hytzwjId = nowList
													.get(i).getHytzwjId();

											// 会议通知改为pdf展示，在这里下载通知的pdf
											List<Meeting> tempList = db
													.findAllByWhere(
															Meeting.class,
															" id='"
																	+ nowList
																			.get(i)
																			.getId()
																	+ "' and hytzwjId='"
																	+ hytzwjId
																	+ "' and isDown='true'");
											if (tempList.size() == 0) {
												new Thread(new Runnable() {

													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														doBreakpointResumeDownload(
																MeetingListActivity.this,
																meeting.getHytzwj().getUrl(),
																meeting);
													}
												}).start();

											}
										}

										load_progressBar
												.setVisibility(View.GONE);
										no_data.setVisibility(View.GONE);
									} else {
										load_progressBar
												.setVisibility(View.GONE);
										MyToast.show(context, "接口没有返回数据");
										no_data.setVisibility(View.VISIBLE);
									}
								} else {
									MyToast.show(context, result.getError());
								}
							} catch (Exception e) {
								e.printStackTrace();
								load_progressBar.setVisibility(View.GONE);
							} finally {

							}
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							MyLog.e("会议列表没有返回数据", "没有返回数据");
							// 加载离线数据
							nowList = db.findAllByWhere(Meeting.class,
									" status<>4 and userid = '"
											+ MyApplication.USERID + "'");// 非历史会议
							load_progressBar.setVisibility(View.GONE);
							if (nowList != null && nowList.size() > 0) {
								initNow(false);
								no_data.setVisibility(View.GONE);
							} else if (waiteList.size() == 0
									&& nowList.size() == 0)
								no_data.setVisibility(View.VISIBLE);
						}
					});
				}

			} finally {
			}
		}
	}

	// 刷新数据方法
	public void refush() {
		refush.setClickable(false);
		load_progressBar.setVisibility(View.VISIBLE);
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("verFlag", "0");
		// new VersionThread(this, params1).start();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("persionId", MyApplication.USERID);
		params.put("timestamp", "0");
		new MeetingListThread(this, params).start();
		// new PastMeetingListThread(this, params).start();
		// new OffLineUserSelectThread(this, params).start();

		// startService(regIntent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		EventBus.getDefault().register(this);
		// if (isUpdateIp) {
		// daming 重新获取议题
		List<ThreadInfo> downThreads = db.findAll(ThreadInfo.class);

		// 获取下载进度的通知
		if (downThreads.size() > 0)
			fileStateTextView.setText("[正在下载]");
		else
			fileStateTextView.setText("");

		refush();
		// isUpdateIp = false;
		// }

		// init();
		super.onResume();
	}

	// 网络监听回调，发现网络恢复刷新数据
	public void onEventMainThread(String state) {
		if (state.equals("connect"))
			refush();
	}

	public void onEventMainThread(ProgressItem item) {
		List<ThreadInfo> downThreads = db.findAll(ThreadInfo.class);
		// 获取下载进度的通知
		if (downThreads.size() > 0)
			fileStateTextView.setText("[正在下载]");
		else{
			fileStateTextView.setText("[下载完成]");
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// 按下键盘上返回按�?
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	public void exit() {
		if (!mIsExit) {
			mIsExit = true;
			MyToast.show(getApplicationContext(), "再按一次返回键将退出登录！");
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(MeetingListActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
			// exitService();
			// android.os.Process.killProcess(android.os.Process.myPid());
			// 关闭Activity到列表
			// MyApplication.getInstance().exit();

		}
	}

	public void exitService() {

		// 关闭后台Service
		// stopService(regIntent);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			mIsExit = false;
		}

	};

	// 断点续传方法
	public int doBreakpointResumeDownload(Context context, String url,
			Meeting meeting) {
		if (SDHandler.isSDAvaliable()) {
			String filename = null;
			if (url != null && url.length() > 0) {
				filename = url
						.substring(url.lastIndexOf("/") + 1, url.length());

				File tempFile = new File(context.getFilesDir().getPath(),
						filename);
				// 没有可用网络连接
				if (!NetworkStatusHandler.isNetWorkAvaliable(context)) {
					return NET_ERROR;
				} else {
					HttpGet httpGet = new HttpGet(url);
					HttpResponse response = null;
					InputStream is = null;
					// 上次中断位置
					long breakPoint = 0l;
					if (tempFile.exists() && tempFile.isFile()) {
						breakPoint = tempFile.length();
					}
					httpGet.addHeader("RANGE", "bytes=" + breakPoint + "-");
					try {
						response = HttpClientHolder.getInstance().execute(
								httpGet);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						return NET_ERROR;
					} catch (IOException e) {
						e.printStackTrace();
						return NET_ERROR;
					}
					// 返回码不是206
					if (response.getStatusLine().getStatusCode() != 206) {
						String result;
						try {
							meeting.setDown(true);
							try {
								db.save(meeting);
							} catch (Exception e) {
								db.update(meeting);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return NET_ERROR;
					}
					HttpEntity entity = response.getEntity();
					long total = entity.getContentLength() + breakPoint;
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
								filename);
						if (!tempFile.exists()) {
							try {
								tempFile.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
								return SD_ERROR;
							}
						}
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
						for (int len = is.read(buffer); len != -1; len = is
								.read(buffer)) {
							oSavedFile.write(buffer, 0, len);
							breakPoint += len;
						}

					} catch (IOException e) {
						return NET_ERROR;
					} finally {
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

					meeting.setDown(true);
					try {
						db.save(meeting);
					} catch (Exception e) {
						db.update(meeting);
					}
				}
			} else {
				return SD_ERROR;
			}
		} else {
			return SD_ERROR;
		}
		return OK;
	}

	public void downloadFiles() {
		List<MeetingYiti> yitiList;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("PerId", MyApplication.USERID);
		// 获取所有文件
		ListResult<MeetingYiti> result = AppDataControl.getInstance()
				.getAllFilelist(this, params);
		MyLog.e("下载会议文件接口返回数据", result+"");
		if (null != result) {
			MyApplication.isGetDownFileMes = true;
			if (result.getState() == 0) {
				if (result.getData() != null && result.getData().size() > 0) {
					yitiList = result.getData();
					if (yitiList != null) {
						for (int i = 0; i < yitiList.size(); i++) {
							MeetingYiti yiti = yitiList.get(i);
							yiti.setUserid(MyApplication.USERID);
							try {
								db.save(yiti);// 将议题保存到数据库
							} catch (Exception e) {
								db.update(yiti);
							}
							for (int j = 0; j < yiti.getFiles().size(); j++) {
								YitiFile file = yiti.getFiles().get(j);
								List<YitiFile> dbfiles = db.findAllByWhere(
										YitiFile.class, "id='" + file.getId()
												+ "' ");
								// 判断该文件在数据库是否存在
								if (dbfiles.size() == 0) {
									file.setYitiId(yiti.getId());// 设置议题ID
									file.setMeetingId(yiti.getMeetingId());
									file.setIsDown(0);// 设置状态为未下载
									try {
										db.save(file);// 文件信息保存到数据库
									} catch (Exception e) {
										db.update(file);
									}
								}
							}
						}
						// 调用下载方法
						List<YitiFile> dbfiles = db.findAllByWhere(
								YitiFile.class, " isDown<>1 ");
					    int listCount = dbfiles.size();
						// 判断未下载完成文件的数量
						for (int i = 0; i < listCount; i++) {
							YitiFile file = dbfiles.get(i);
							Intent intent = new Intent(this,
									DownloadService2.class);
							intent.setAction(DownloadService2.ACTION_START);
							intent.putExtra("fileinfo", file);
							startService(intent);

						}

					}
				}
			}
			AppDataControl.getInstance().sendDownStartLog(getApplicationContext());
		} else {
			MyLog.e("会议文件没有返回数据", "没有返回数据");
			MyApplication.isGetDownFileMes = false;
		}
	}

	class ThreadShow implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(60000);
					if (MyApplication.isGetDownFileMes == false) {
						downloadFiles();
					} else {
						// 调用下载方法
						List<YitiFile> dbfiles = db.findAllByWhere(
								YitiFile.class, " isDown<>1 ");
						int listCount = dbfiles.size();
						if (listCount == 0) {
							break;
						}
						// 判断未下载完成文件的数量
						for (int i = 0; i < listCount; i++) {
							YitiFile file = dbfiles.get(i);
							Intent intent = new Intent(
									MeetingListActivity.this,
									DownloadService2.class);
							intent.setAction(DownloadService2.ACTION_START);
							intent.putExtra("fileinfo", file);
							startService(intent);

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					MyLog.e("抛异常", e.getMessage());
					System.out.println("thread error...");
				}
			}
		}
	}
}