package com.wanhuiyuan.szoa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.down.DownloadService2;
import com.wanhuiyuan.szoa.adapter.TabFragmentPagerAdapter;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.Postil;
import com.wanhuiyuan.szoa.bean.ResultString;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.fragment.MeetingInfoFragment;
import com.wanhuiyuan.szoa.fragment.MeetingPersonFragment;
import com.wanhuiyuan.szoa.fragment.MeetingRenyuanFragment;
import com.wanhuiyuan.szoa.fragment.MeetingServerFragment;
import com.wanhuiyuan.szoa.fragment.MeetingYitiFragment;
import com.wanhuiyuan.szoa.jingge.ConstantValue;
import com.wanhuiyuan.szoa.myview.AddPopWindow;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.myview.RoundProgressBar;
import com.wanhuiyuan.szoa.service.DownloadService;
import com.wanhuiyuan.szoa.service.UITimeReceiver;
import com.wanhuiyuan.szoa.service.UITimeReceiver.TimeBackCall;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.HttpClientHolder;
import com.wanhuiyuan.szoa.uiutil.IndexViewPager;
import com.wanhuiyuan.szoa.uiutil.MyLog;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;
import com.wanhuiyuan.szoa.uiutil.SDHandler;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;
import com.wanhuiyuan.szoa.uiutil.WSDLManager;

/**
 * 会议界面，保护会议通知、会议议题、参会人员。
 */
public class MainActivity extends FragmentActivity implements TimeBackCall,
		ConstantValue {

	/* 底部Tab */
	private RadioGroup radioGroup;
	/* Fragment 集合,供切换显示 */
	private ArrayList<Fragment> fragments;
	/* 滑动 ViewPager */
	private IndexViewPager viewPager;
	/* 退出提示窗 */
	/* 主模块Fragment */
	private MeetingInfoFragment infoFragment;// 会议通知界面
	MeetingYitiFragment yitiFragment;// 议题界面
	//MeetingRenyuanFragment renyuanFragment;// 参会人员界面
	MeetingPersonFragment personFragament;
	MeetingServerFragment serverFragment;// 呼叫服务jie
	/* 底部Tab单选按钮id */
	private static final int[] radBtnID = { R.id.tab_meeting_info,
			R.id.tab_meeting_yiti, R.id.tab_meeting_renyuan,
			R.id.tab_meeting_service };
	TextView title, username, time, open, close, refush;// 标题，姓名，时间，打开按钮，关闭按钮 refush;
	RadioButton serverBtn, yitiBtn;// 底部呼叫服务按，会议议题按钮
	Meeting meeting;// 会议信息对象
	RoundProgressBar roundProgressBar;// 倒计时进度条，后台推送倒计时时候显示
	UITimeReceiver timeReceiver;// 倒计时接收器
	ImageButton btn_back, imgbtn_right;// 左侧返回按钮，右侧更多按钮
	TextView right_txt;
	Intent regIntent;
	FinalDb db;
	List<Postil> postilList;// 批注集合
	LinkedHashMap args = new LinkedHashMap<String, Object>();// 网络请求参数集合
	ProgressDialog uploadDialog;// 上传批注提示对话框
	int uploadIndex = 0;// 上传批注数量，用来记录是否完成所有上传任务

	// 参会替会结果
	ResultString result;
	// 被替会人ID
	private String oTherUserid = "";

	/**
	 * 
	 */

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		if (arg0 != null) {
			String FRAGMENTS_TAG = "Android:support:fragments";
			// remove掉保存的Fragment
			arg0.remove(FRAGMENTS_TAG);
		}
		db = FinalDb.create(this, "szoa.db");
		uploadDialog = new ProgressDialog(this);
		uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		uploadDialog.setMessage("正在上传批注...");
		uploadDialog.setCancelable(false);
		regIntent = new Intent(this, DownloadService.class);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.tab_main_activity);
		timeReceiver = UITimeReceiver.newInstance(this, this);
		meeting = (Meeting) getIntent().getSerializableExtra("meeting");
		int loginType = getIntent().getIntExtra("loginType", 1);
		// 被替会人姓名
		String replaceName = getIntent().getStringExtra("replaceName");
		// 被替会人id
		oTherUserid = getIntent().getStringExtra("oTherUserid");
		fragments = new ArrayList<Fragment>();
		initView();

		// 更改参会状态
		Map<String, Object> params = new HashMap<String, Object>();
		// 如果有被替会人，就请求该用户的议题信息
		if (null == oTherUserid || "".equals(oTherUserid)){
			params.put("userId", MyApplication.USERID);
		}
		else{
			params.put("userId", oTherUserid);
		}
		params.put("loginType", loginType);
		params.put("meetingId", meeting.getId());
		params.put("replaceName", replaceName);
		new attendMeeting(MainActivity.this, params, false).start();
		if(MyApplication.RESTART_DOWNLOAD || MyApplication.isOtherLoginMode()){
			new Thread(new ThreadShow()).start();
		}
		if(MyApplication.isOtherLoginMode()){
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					downNotifyDoc();
				}
			}.start();
		}
	}

	@Override
	protected void onDestroy() {
		MyApplication.setVistorMode(false);
		MyApplication.setAuthorizeMode(false);
		super.onDestroy();
	}

	// 更改参会状态异步操作
	class attendMeeting extends Thread {
		private Context context;
		private Map<String, Object> params;
		private boolean fresh;

		public attendMeeting(Context context, Map<String, Object> params, boolean fresh) {
			this.context = context;
			this.params = params;
			this.fresh = fresh;
		}

		@Override
		public void run() {
			result = AppDataControl.getInstance()
					.attendMeeting(context, params);
			if(fresh){
				handler.sendEmptyMessage(0);
			}
		}
	}

	// 获取异步状态信息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//personFragament.pullFresh();
			if(personFragament != null){
				personFragament.pullFresh();
			}
		}
	};

	/* 初始化视图控件 */
	private void initView() {
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		title = (TextView) findViewById(R.id.sys_title_txt);
		serverBtn = (RadioButton) findViewById(R.id.tab_meeting_service);
		yitiBtn = (RadioButton) findViewById(R.id.tab_meeting_yiti);
		roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);

		/**
		 * 需求修改：要求显示会议名称，不在显示人员姓名，所以将原有的显示会议人员姓名注释，增加显示当前会议名称
		 */
		// 显示会议名称 name
		title.setText(meeting.getName());
		// title.setText("会议通知");
		// username = (TextView) findViewById(R.id.username);
		// username.setVisibility(View.VISIBLE);
		// 如果是替会显示替会人姓名
		// if (SharePreferences.getInstance(this).getReplaceName().length() > 1)
		// {
		// title.setText(SharePreferences.getInstance(this).getRealName()
		// + "(" + SharePreferences.getInstance(this).getReplaceName()
		// + ")");
		// } else
		// title.setText(SharePreferences.getInstance(this).getRealName());
		open = (TextView) findViewById(R.id.open);
		close = (TextView) findViewById(R.id.close);
		refush = (TextView) findViewById(R.id.refush);
		refush.setText(R.string.meeting_replace_btn);
		refush.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showReplaceDialog();
			}
		});
		imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.GONE);
		right_txt = (TextView) findViewById(R.id.username);
		right_txt.setText(MyApplication.USERXINGMING);
		right_txt.setVisibility(View.VISIBLE);
		radioGroup = (RadioGroup) findViewById(R.id.tab_rdogroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_meeting_info:
					// Intent intent = new Intent(MainActivity.this,
					// com.kinggrid.iapppdf.demo.MainActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(intent);
					// viewPager.setCurrentItem(0);
					doOpenFile(meeting);// 打开会议通知详情pdf
					break;
				case R.id.tab_meeting_yiti:
					viewPager.setCurrentItem(1);// 显示议题列表
					break;
				case R.id.tab_meeting_renyuan:
					viewPager.setCurrentItem(2);// 显示参会人员
					break;
				case R.id.tab_meeting_service:
					viewPager.setCurrentItem(3);// 显示呼叫服务
					break;
				default:
					break;
				}
			}
		});
		viewPager = (IndexViewPager) findViewById(R.id.tab_viewpager);
		infoFragment = new MeetingInfoFragment(meeting);
		fragments.add(infoFragment);
		// 传递被替会人id过去
		yitiFragment = new MeetingYitiFragment(meeting.getId() + "",
				oTherUserid);
		fragments.add(yitiFragment);
		personFragament = new MeetingPersonFragment(meeting.getId());
		fragments.add(personFragament);
		// if (meeting.getStatus() != 1 && meeting.getStatus() != 4) {
		// serverFragment = new MeetingServerFragment(meeting);
		// fragments.add(serverFragment);
		// } else {
		serverBtn.setVisibility(View.GONE);
		// }
		// 创建ViewPager的Adapter
		TabFragmentPagerAdapter mAdapetr = new TabFragmentPagerAdapter(
				getSupportFragmentManager(), fragments);

		viewPager.setAdapter(mAdapetr);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// 更改展现方式以后这个暂时不用了
		open.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// renyuanFragment.open();
				close.setVisibility(View.VISIBLE);
				open.setVisibility(View.GONE);
			}
		});
		// 更改展现方式以后这个暂时不用了
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// renyuanFragment.close();
				close.setVisibility(View.GONE);
				open.setVisibility(View.VISIBLE);
			}
		});
		// 更多按钮
		imgbtn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AddPopWindow(MainActivity.this)
						.showPopupWindow(imgbtn_right);
			}
		});
		// refush.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if ((System.currentTimeMillis() - clickTime) > 10000) {
		// clickTime = System.currentTimeMillis();
		// startService(regIntent);
		// yitiFragment.getRefush(null);
		// }
		// }
		// });

		btn_back.setVisibility(View.VISIBLE);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SavePiZhuDialog();
			}
		});
	}
	
	void showReplaceDialog(){

		final EditText inputServer = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入替会人员名字").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
				.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String replaceName = inputServer.getText().toString();
				if(replaceName == null){
					return;
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("userId", MyApplication.USERID);
				params.put("meetingId", meeting.getId());
				if(replaceName.length() == 0){
					params.put("loginType", 1);
					params.put("replaceName", "");
				}else{
					params.put("loginType", 2);
					params.put("replaceName", replaceName);
				}
				new attendMeeting(MainActivity.this, params, true).start();
			}
		});
		builder.show();
	}

	/**
	 * 上传批注方法
	 * 
	 * @author Administrator
	 * 
	 */
	private class uploadPizhuTask extends
			AsyncTask<LinkedHashMap, Float, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			uploadDialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (uploadIndex == postilList.size()) {
				// 更新批注上传状态 tom 11.17
				UpdatePiZhuUploadState();

				uploadDialog.cancel();
				// SharePreferences.getInstance(MainActivity.this).loginOut();
				finish();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(LinkedHashMap... params1) {
			uploadIndex++;
			String str = "";
			try {
				str = WSDLManager.shareManager(MainActivity.this).getDataXML(
						SharePreferences.getInstance(MainActivity.this)
								.getServerUrl(MyApplication.SERVICE_HOST)
								+ "/services/MeetingService", "saveFilePostil",
						params1[0]);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return str;
		}
	}

	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("您有批注信息是否上传")
				.setCancelable(false)
				.setPositiveButton("上传", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						for (int i = 0; i < postilList.size(); i++) {
							args.put("userId", postilList.get(i).getUserId());
							args.put("fileId", postilList.get(i).getFileId());
							args.put("version", postilList.get(i).getVersion());
							args.put("filePostil", postilList.get(i)
									.getContent());

							new uploadPizhuTask().execute(args);

						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						// SharePreferences.getInstance(MainActivity.this)
						// .loginOut();
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * ViewPager页面改变监听器
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			if (position == 0) {
				// title.setText("会议通知");
				close.setVisibility(View.GONE);
				open.setVisibility(View.GONE);
				if(MyApplication.isAuthorize()){
					refush.setVisibility(View.GONE);
					right_txt.setVisibility(View.GONE);
				}
				//imgbtn_right.setVisibility(View.GONE);
			}
			if (position == 1) {
				// title.setText("会议议题");
				//imgbtn_right.setVisibility(View.VISIBLE);
				close.setVisibility(View.GONE);
				open.setVisibility(View.GONE);
				if(MyApplication.isAuthorize()){
					refush.setVisibility(View.GONE);
					right_txt.setVisibility(View.VISIBLE);
				}
			}
			if (position == 2) {
				//imgbtn_right.setVisibility(View.GONE);
				if(MyApplication.isAuthorize()){
					refush.setVisibility(View.VISIBLE);
					right_txt.setVisibility(View.GONE);
				}
			}
			if (position == 3) {
				//imgbtn_right.setVisibility(View.GONE);
				close.setVisibility(View.GONE);
				open.setVisibility(View.GONE);
				// title.setText("呼叫服务");
			}
			viewPager.setCurrentItem(position);
			selectTab(position);
		}
	}

	/**
	 * 选择到Tab
	 */
	private void selectTab(int tab_postion) {
		RadioButton radioButton = (RadioButton) findViewById(radBtnID[tab_postion]);
		radioButton.setChecked(true);
	}

	/**
	 * 选择到pager
	 */
	public void selectViewPager(int index) {
		viewPager.setCurrentItem(index);
	}

	/**
	 * 监听返回按键
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			SavePiZhuDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 退出或者返回时提示保存批注 11.17 tom
	 */
	private void SavePiZhuDialog() {
		if(MyApplication.isVisit()){
			backMethod();
			return;
		}
		postilList = db.findAllByWhere(Postil.class,
				"meetingId='"
						+ meeting.getId()
						+ "' and (not isupload) and userId='"
						+ MyApplication.USERID + "'");
		if (postilList.size() > 0) {
			dialog();
		} else {
			// SharePreferences.getInstance(MainActivity.this).loginOut();
			if(MyApplication.isAuthorize()){
				backMethod();
			}else {
				finish();
			}
		}
	}

	/*
	 * 上传批注成功后更新状态 11.17 tom
	 */
	private void UpdatePiZhuUploadState() {

		postilList = db.findAllByWhere(Postil.class,
				"meetingId='"
						+ meeting.getId()
						+ "' and (not isupload) and userId='"
						+ MyApplication.USERID + "'");
		for (int i = 0; i < postilList.size(); i++) {
			postilList.get(i).setIsupload(true);
			try {
				db.save(postilList.get(i));
			} catch (Exception e) {
				db.update(postilList.get(i));
			}
		}
	}

	/**
	 * 会议时间提醒回调，服务端发送一个还有几分钟的时间消息，这边获取到会先出一个倒计时的进度条。
	 */
	@Override
	public void refushTime(Context context, Intent intent) {
		if (meeting.getId().equals(intent.getStringExtra("meetingId"))) {
			if (intent.getIntExtra("progress", 0) > 0
					&& intent.getIntExtra("progress", 0) <= 100)
				roundProgressBar.setVisibility(View.VISIBLE);
			roundProgressBar.setProgress(intent.getIntExtra("progress", 0),
					intent.getStringExtra("time"));
			if (intent.getIntExtra("progress", 0) == 0) {
				roundProgressBar.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {
		// startService(regIntent);
		yitiBtn.setChecked(true);
		viewPager.setCurrentItem(1);
		timeReceiver.registerReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		timeReceiver.unregisterReceiver();
		super.onPause();
	}

	public void refushYiti() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				downloadFiles();
			}
		}).start();
		;
		// if (MyApplication.downedFilesNum == MyApplication.downFilesNum) {
		// startService(regIntent);
		// }
		yitiFragment.getRefush(null);

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
						final int listCount = dbfiles.size();
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
		} else {
			MyLog.e("会议文件没有返回数据", "没有返回数据");
			MyApplication.isGetDownFileMes = false;
		}
	}



	public final static int OK = 0;// 文件成功下载
	public final static int SD_ERROR = 1;// 没有sd卡
	public final static int NET_ERROR = 2;// 网络错误


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
	
	void downNotifyDoc(){


		List<Meeting> tempList = db
				.findAllByWhere(
						Meeting.class,
						" id='"
								+ meeting.getId()
								+ "' and hytzwjId='"
								+ meeting.getHytzwjId()
								+ "' and isDown='true'");
		if (tempList.size() == 0) {
					doBreakpointResumeDownload(
							MainActivity.this,
							meeting.getHytzwj().getUrl(),
							meeting);

		}
	}
	
	class ThreadShow implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Thread.sleep(60000);
					// 调用下载方法
					if (MyApplication.isGetDownFileMes == false) {
						downloadFiles();
					} else {

						List<YitiFile> dbfiles = db.findAllByWhere(
								YitiFile.class, " isDown<>1 ");
						int listCount = dbfiles.size();
						if (listCount == 0) {
							break;
						}
						// 判断未下载完成文件的数量
						for (int i = 0; i < listCount; i++) {
							YitiFile file = dbfiles.get(i);
							Intent intent = new Intent(MainActivity.this,
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

	/**
	 * 打开会议通知的pdf
	 * 
	 * @param meeting
	 */
	private void doOpenFile(Meeting meeting) {
		String filename = null;
		try {
			filename = meeting.getHytzwj().getUrl().substring(
					meeting.getHytzwj().getUrl().lastIndexOf("/") + 1,
					meeting.getHytzwj().getUrl().length());
			if (null == filename || "".equals(filename)) {
				MyToast.show(MainActivity.this, "会议通知文件不存在！");
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MyToast.show(MainActivity.this, "会议通知文件不存在！");
			e.printStackTrace();
			return;
		}
		File file = new File(getFilesDir().getAbsolutePath() + "/" + filename);
		if (file == null) {
			MyToast.show(MainActivity.this, "文件未找到！");
		} else if (!file.exists()) {
			MyToast.show(MainActivity.this, "文件尚未下载！");
		} else {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent("android.intent.action.VIEW", uri);
			intent.setClassName(MainActivity.this,
					"com.wanhuiyuan.szoa.jingge.BookShower");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 传递用户名，默认admin
			intent.putExtra(NAME,
					MyApplication.USERID);
			// 传递授权码(必传)
			//String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU3CQHU1ScyOebPLnpsDlQDzLvKeTdxzwi2Wk0Yn+WSxoXx6aD2yiupr6ji7hzsE6/QqGcC+eseQV1yrWJ/1FwxLCjX+xEgRoNggvmgA8zkJXOVWEbHWAH22+t7LdPt+jENwSCMsAYnhGWJ0gXIIaLjG32poSbszHQQyNDZrHtqZuuSgCNRP4FpYjl8hG/IVrYXo/POmAlKHHVOdR0F5DQjr+W8fXpxdRHfEuWC1PB9ruQ=";
			intent.putExtra(LIC, MyApplication.copyRight);
			// 是否支持域编辑功能，在表单PDF文件中可体现此功能，默认为false
			intent.putExtra(CANFIELDEIDT, false);// 可选值为布尔值
			// 文档保存之后批注是否只读，默认为false,不需要修改请忽略此参
			// intent.putExtra(ANNOTPROTECTNAME, isAnnotProtect.isChecked());
			// 是否为E人E本模式，默认为false

			// 是否保留PDF上次阅读位置，默认为true,为false时每次都从第一页开始阅读
			intent.putExtra(LOADCACHENAME, false);
			// 阅读模式，默认PageViewMode.VSCROLL，竖向连续翻页,不需要重设可忽略
			if (SharePreferences.getInstance(MainActivity.this).getChangePage()
					.equals("vertical")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEV);
			} else if (SharePreferences.getInstance(MainActivity.this)
					.getChangePage().equals("horizontal")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEH);
			} else
				intent.putExtra(VIEWMODENAME, VIEWMODE_VSCROLL);
			intent.putExtra("fileName", filename);
			intent.putExtra("fileRealName", "会议通知");
			intent.putExtra("fileId", meeting.getHytzwjId());
			startActivity(intent);
		}
	}
	
    // -----------------------------------
    // 退出
    // -----------------------------------
    private long exitTime = 0;

/*    @Override
    public void onBackPressed() {
    	if(!MyApplication.isOtherLoginMode()){
    		super.onBackPressed();
    	}else{
    		backMethod();
    	}
    }*/
    
    void backMethod(){
        if ((System.currentTimeMillis() - exitTime) > 2000) {
			MyToast.show(getApplicationContext(), "再按一次返回键将退出会议！");
            exitTime = System.currentTimeMillis();
        } else {
        	finish();
        }
    }
}
