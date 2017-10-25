package com.wanhuiyuan.szoa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.szoa.R;
import com.horizon.util.encrypt.DESEDE;
import com.wanhuiyuan.down.ThreadInfo;
import com.wanhuiyuan.szoa.bean.ApkFile;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.Result;
import com.wanhuiyuan.szoa.bean.Version;
import com.wanhuiyuan.szoa.bean.newListResult;
import com.wanhuiyuan.szoa.dialog.DismissRunnable;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.MyLog;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;
import com.wanhuiyuan.szoa.uiutil.VersionUtils;

/**
 * 登录界面
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity {

	private EditText userNameEditText;// 用户名输入框
	// private EditText replaceNameEditText;// 替会输入框
	private EditText passwordEditText;// 密码输入框
	private Button loginBtn;
	private TextView visitBtn, authorizeBtn;
	// private CheckBox user_save_checkBox;
	SharePreferences sharePreferences;
	private String loginusername;// 用户帐号
	String userpassword;// 用户密码
	// Meeting meeting;// 会议对象
	TextView meetingNameTextView, versionTextView, fileStateTextView;// 会议名Textview，版本号Textview，下载状态Textview
	FinalDb db;
	// 多部门会议修改10-18
	// List<YitiFile> fileList;// 文件列表
	// List<MeetingYiti> yitiList;// 议题列表
	// DownloadReceiver downReceiver;
	// int notDownCount = 0;// 未下载数量
	// int isDownCount = 0;// 已下载数量
	// int haseCount = 0;// 本地已存在数量

	ImageButton config;// 配置按钮
	boolean isUpdateIp = false;// 是否更换IP 发现更换刷新数据

	private CheckBox CBisSavePassowrd; // 是否保存密码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// EventBus.getDefault().register(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Context context = getApplicationContext();

		// 多部门会议修改10-18
		// meeting = (Meeting) getIntent().getSerializableExtra("meeting");
		// fileList = new ArrayList<YitiFile>();
		// yitiList = new ArrayList<MeetingYiti>();
		// String where = " meetingId='" + meeting.getId() + "'";
		// yitiList = db.findAllByWhere(MeetingYiti.class, where);

		// 初始化服务器访问地址
		MyApplication.SERVICE_HOST = SharePreferences.getInstance(this)
				.getServerUrl(
						"http://" + getResources().getString(R.string.ip) + ":"
								+ getResources().getString(R.string.port) + "/"
								+ getResources().getString(R.string.project));

		// 初始化服务器访问地址
		// MyApplication.SERVICE_HOST = "http://"
		// + getResources().getString(R.string.ip) + ":"
		// + getResources().getString(R.string.port) + "/"
		// + getResources().getString(R.string.project);

		// 检查版本更新
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("verFlag", "0");
		new VersionThread(this, params1).start();

		// 清除缓存，如果缓存中读取到的清除版本和当前版本不同，就执行缓存清除命令
		if (SharePreferences.getInstance(this).getClearVersionCode() != VersionUtils
				.getVerCode(getApplicationContext())) {
			ConfigIpActivity.clearAll(context);
			SharePreferences.getInstance(this).putClearVersionCode(
					VersionUtils.getVerCode(getApplicationContext()));
		}

		db = FinalDb.create(this, "szoa.db");
		if (MyApplication.firstLoad) {
			db.deleteAll(ThreadInfo.class);
			MyApplication.firstLoad = false;
		}
		sharePreferences = SharePreferences.getInstance(this);

		// userId = "xuq";
		findView();
		initControl();
	}

	@SuppressLint("CutPasteId")
	protected void findView() {
		userNameEditText = (EditText) findViewById(R.id.login_username);
		// if (NetworkStatusHandler.isNetWorkAvaliable(LoginActivity.this))
		// userNameEditText.setFocusable(false);
		userNameEditText.setText(sharePreferences.getUserName());
		//userNameEditText.setText("艾学峰");
		Drawable drawable1 = getResources().getDrawable(R.drawable.name);
		drawable1.setBounds(15, 0, 50, 45);// 第一0是距左边距离，第二0是距上边距离，40分别是长宽
		userNameEditText.setCompoundDrawables(drawable1, null, null, null);// 只放左边
		// replaceNameEditText = (EditText)
		// findViewById(R.id.login_replacename);
		// replaceNameEditText.setText(sharePreferences.getReplaceName());
		passwordEditText = (EditText) findViewById(R.id.login_password);
		passwordEditText.clearFocus();
		if (sharePreferences.getPassword() != null
				&& !"".equals(sharePreferences.getPassword()))
			passwordEditText.setText(sharePreferences.getPassword());
		Drawable drawable2 = getResources().getDrawable(R.drawable.password);
		drawable2.setBounds(15, 0, 50, 45);// 第一0是距左边距离，第二0是距上边距离，40分别是长宽
		passwordEditText.setCompoundDrawables(drawable2, null, null, null);// 只放左边
		loginBtn = (Button) findViewById(R.id.loginBtn);
		visitBtn = (TextView) findViewById(R.id.visitBtn);
		authorizeBtn = (TextView) findViewById(R.id.authorizeBtn);
		// if (meeting.getStatus() == 4)
		// user_save_checkBox.setClickable(false);
		meetingNameTextView = (TextView) findViewById(R.id.meetingNameTextView);

		// 多部门会议修改10-18
		// if (meeting != null && meeting.getName().length() > 1)
		// meetingNameTextView.setText(meeting.getName());
		// if (sharePreferences.getReplaceName().length() > 0)
		// user_save_checkBox.setChecked(true);

		versionTextView = (TextView) findViewById(R.id.versionTextView);
		versionTextView.setText("版本号:" + VersionUtils.getVerName(this));
		// fileStateTextView = (TextView) findViewById(R.id.fileStateTextView);
		// 判断议题文件是否下载完成
		// for (int i = 0; i < yitiList.size(); i++) {
		// notDownCount += db.findAllByWhere(YitiFile.class,
		// "yitiId='" + yitiList.get(i).getId() + "' and isDown=0 ")
		// .size();
		// haseCount += db.findAllByWhere(YitiFile.class,
		// "yitiId='" + yitiList.get(i).getId() + "'").size();
		// isDownCount += db.findAllByWhere(YitiFile.class,
		// "yitiId='" + yitiList.get(i).getId() + "' and isDown=1 ")
		// .size();
		// }
		// if (notDownCount > 0 || (haseCount == 0))
		// fileStateTextView.setText("[正在下载]");
		// else if (isDownCount > 0 && notDownCount == 0)
		// fileStateTextView.setText("[下载完成]");
		// else if (isDownCount == 0 && notDownCount == 0)
		// fileStateTextView.setText("[没有下载任务]");

		config = (ImageButton) findViewById(R.id.config);
		config.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isUpdateIp = true;
				Intent intent = new Intent(LoginActivity.this,
						ConfigIpActivity.class);
				startActivity(intent);
			}
		});

		CBisSavePassowrd = (CheckBox) findViewById(R.id.cBsavepwd);
	}

	protected void initControl() {
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userNameEditText.getText().toString().length() < 1) {
					MyToast.show(LoginActivity.this, "请填入帐号！");
					return;
				}
				loginusername = userNameEditText.getText().toString();
				if (passwordEditText.getText().toString().length() < 1) {
					MyToast.show(LoginActivity.this, "请输入密码！");
					return;
				}
				userpassword = passwordEditText.getText().toString();
				// MyToast.show(LoginActivity.this, "正在登录，请稍后！");
				// 联网的话调用在线登录，不联网在本地数据库里查询
				if (NetworkStatusHandler.isNetWorkAvaliable(LoginActivity.this)) {
/*					Map<String, Object> params = new HashMap<String, Object>();
					params.put("account", loginusername);
					// params.put("password", StringUtil.md5(passwordEditText
					// .getText().toString()));
					// params.put("password", "CAD390F8C3BD32DF");
					params.put("password", DESEDE.encryptIt(passwordEditText
							.getText().toString()));
					params.put("loginType", "1");
					new LoginThread(LoginActivity.this, params).start();*/
					String password = DESEDE.encryptIt(passwordEditText.getText().toString());
					new newLoginTask(loginusername, password).execute();
				} else {
					// 账号密码判断
					String sql = " username='"
							+ loginusername
							+ "' and password='"
							+ DESEDE.encryptIt(
									passwordEditText.getText().toString())
									.toUpperCase() + "'";
					List<OffLineUser> users = db.findAllByWhere(
							OffLineUser.class, sql);
					if (users.size() > 0) {
						sharePreferences
								.putUserName(users.get(0).getUsername());
						sharePreferences.putUserId(users.get(0).getId());
						MyApplication.USERID = users.get(0).getId();
						MyApplication.setVistorMode(false);
						MyApplication.setAuthorizeMode(false);
						Intent intent = new Intent(LoginActivity.this,
								MeetingListActivity.class);
						startActivity(intent);
						finish();
					} else {
						MyToast.show(LoginActivity.this, "用户名或密码错误！");
					}

				}
			}
		});
		visitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, VisitActivity.class);
				startActivity(intent);
			}
		});
		authorizeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, AuthorizeActivity.class);
				startActivity(intent);
			}
		});
		// 多部门会议修改10-18
		// userNameEditText.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(LoginActivity.this,
		// SelectUserActivity.class);
		// // intent.putExtra("meetingId", meeting.getId());
		// startActivityForResult(intent, 10);
		// }
		// });
	}

	// 多部门会议修改10-18
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (requestCode == 10) {
	// if (resultCode == 11) {
	// Login result = (Login) data.getSerializableExtra("user");
	// // userId = result.getId();
	// loginusername = result.getUsername();
	// userNameEditText.setText(result.getTruename());
	// // if (checkUser.isChecked())
	// saveInformation(result);
	// }
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	static final public int LOGIN_SUC = 0;
	static final public int LOGIN_CHOOSE = 1;
	static final public int LOGIN_ERR = 10001; 

	class newLoginTask extends AsyncTask<String , Integer , Integer>{
		
		String inputName;
		String passwordDec;
		String errMsg = "";
		StringBuffer saveJson = new StringBuffer();
		List<Login> loginList;
		
		public newLoginTask(String inputName, String passwordDec){
			this.inputName = inputName;
			this.passwordDec = passwordDec;
		}

		@Override
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				if(!isUserName(inputName)){
					newListResult<Login> result = 
							AppDataControl.getInstance().loginByRealName(getApplicationContext(), inputName, passwordDec, "", saveJson);
					if(result.getReturnValue() != LOGIN_SUC){
						errMsg = result.getErrMsg();
						return result.getReturnValue();
					}
					loginList = result.getData();
					if(loginList == null || loginList.isEmpty()){
						return -1;
					}
					if(loginList.size() == 1){
						Login loginInfo = loginList.get(0);
						saveUserInfo(loginInfo);
					}else{
						return LOGIN_CHOOSE;
					}
					return LOGIN_SUC;
				}else{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("account", inputName);
					params.put("password", passwordDec);
					Result<Login> result = AppDataControl.getInstance()
							.login(getApplicationContext(), params);
					if(result.getReturnValue() != LOGIN_SUC){
						errMsg = result.getErrMsg();
						return result.getReturnValue();
					}
					Login login = result.getData();
					if(login == null){
						return -1;
					}
					saveUserInfo(login);
					return LOGIN_SUC;
				}
			}catch(Exception e){
				errMsg = "无法连接服务器";
				return -1;
			}
		}

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer.intValue() == LOGIN_SUC){
				Intent intent = new Intent(LoginActivity.this,
						MeetingListActivity.class);
				startActivity(intent);
				finish();
            }else if(integer.intValue() == LOGIN_CHOOSE){
				Intent intent = new Intent(LoginActivity.this,
						ChooseDepartmentActivity.class);
				intent.putExtra("json", saveJson.toString());
				intent.putExtra("mima", passwordEditText.getText().toString());
				intent.putExtra("mimaDec", passwordDec);
				startActivity(intent);
            }else{
            	if(errMsg.length() != 0){
            		MyToast.show(getApplicationContext(), errMsg);
            	}
            }
        }
	}
	
	boolean isUserName(String inputName){
		String regex = "[a-zA-Z]+";
		return inputName.matches(regex);
	}
	
	void saveUserInfo(Login login){
		MyApplication.USERID = login.getId();
		MyApplication.USERXINGMING = login.getTruename();
		MyApplication.setVistorMode(false);
		MyApplication.setAuthorizeMode(false);
		saveInformation(login);
	}
	
	// 登录异步操作
	class LoginThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public LoginThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			final Result<Login> loginResult = AppDataControl.getInstance()
					.login(context, params);
			// 联网查询成功的话调用在线登录，不联网在本地数据库里查询
			if (loginResult != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (null != loginResult) {
								if (0 == loginResult.getReturnValue()) {
									// 登录成功
									// sharePreferences
									// .putReplaceName(replaceNameEditText
									// .getText().toString());
									// sharePreferences
									// .putUserName(userNameEditText
									// .getText().toString());
									// sharePreferences
									// .putPassword(passwordEditText
									// .getText().toString());

									// 存储用户id
									MyApplication.USERID = loginResult
											.getData().getId();
									MyApplication.USERXINGMING = loginResult
											.getData().getUsername();
									MyApplication.setVistorMode(false);
									MyApplication.setAuthorizeMode(false);
									saveInformation(loginResult.getData());

									Intent intent = new Intent(
											LoginActivity.this,
											MeetingListActivity.class);
									// Bundle b = new Bundle();
									// 多部门会议修改10-18
									// b.putSerializable("meeting", meeting);
									// intent.putExtras(b);
									startActivity(intent);
									finish();
								} else if (10001 == loginResult
										.getReturnValue()) {
									/**
									 * 错误提示保持一致 victory
									 */
									MyToast.show(context,
											loginResult.getErrMsg());
								} else {
									MyToast.show(context,
											loginResult.getErrMsg());
								}
							}
						} finally {
							handler.post(new DismissRunnable());
						}
					}
				});
			} else {
				// 用户名密码判断
				String sql = " username='"
						+ loginusername
						+ "' and password='"
						+ DESEDE.encryptIt(
								passwordEditText.getText().toString())
								.toUpperCase() + "'";
				List<OffLineUser> users = db.findAllByWhere(OffLineUser.class,
						sql);
				if (users.size() > 0) {
					// 用户和会议对应权限判断
					// String qxsql = " meetingId='" + meeting.getId()
					// + "' and id='" + userId + "'";
					// List<Login> logins = db.findAllByWhere(Login.class,
					// qxsql);
					// if (logins.size() > 0) {
					// sharePreferences.putRealName(users.get(0).getUsername());
					// sharePreferences.putUserName(users.get(0).getId());
					MyApplication.USERXINGMING = users.get(0).getXingming();
					MyApplication.USERID = users.get(0).getId();
					Intent intent = new Intent(LoginActivity.this,
							MeetingListActivity.class);
					// Bundle b = new Bundle();
					// 多部门会议修改10-18
					// b.putSerializable("meeting", meeting);
					// intent.putExtras(b);
					startActivity(intent);
					finish();
					// } else {
					// // 没有权限进入该会议
					// Message msg0 = new Message();
					// msg0.what = 0;
					// handler.sendMessage(msg0);
					//
					// }
				} else {
					// 用户名或密码错误
					Message msg1 = new Message();
					msg1.what = 1;
					handler.sendMessage(msg1);
				}

			}
		}
	}

	/**
	 * 登录成功保存数据
	 * 
	 * @param login
	 */
	public void saveInformation(Login login) {
		sharePreferences.putRealName(login.getTruename());
		sharePreferences.putDeptId(login.getDeptId());
		sharePreferences.putDeptName(login.getDeptName());
		sharePreferences.putUserName(loginusername);
		sharePreferences.putUserId(login.getId());
		if (CBisSavePassowrd.isChecked())
			sharePreferences.putPassword(userpassword);
		else
			sharePreferences.putPassword("");

		// 用户身份验证本地化
		OffLineUser offlineuser = new OffLineUser();
		offlineuser.setId(login.getId());
		offlineuser.setPassword(DESEDE.encryptIt(userpassword.toUpperCase()));
		offlineuser.setUsername(loginusername);
		offlineuser.setXingming(login.getUsername());
		try {
			db.save(offlineuser);
		} catch (Exception e) {
			db.update(offlineuser);
		}
	}

	@Override
	protected void onResume() {
		// downReceiver.registerReceiver();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// downReceiver.unregisterReceiver();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	// public void onEventMainThread(ProgressItem item) {
	// // 获取下载进度的通知
	// int progress = item.getProgress();
	// if (progress == 100) {
	// notDownCount = 0;
	// for (int i = 0; i < yitiList.size(); i++) {
	// notDownCount += db.findAllByWhere(
	// YitiFile.class,
	// "yitiId='" + yitiList.get(i).getId()
	// + "' and isDown=0 ").size();
	// }
	// if (notDownCount > 0)
	// fileStateTextView.setText("[正在下载]");
	// else
	// fileStateTextView.setText("[下载完成]");
	// }
	// }

	/**
	 * 横竖屏切换后重新获取数据
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.login);
		// 多部门会议修改10-18
		db = FinalDb.create(this, "szoa.db");
		// meeting = (Meeting) getIntent().getSerializableExtra("meeting");
		// fileList = new ArrayList<YitiFile>();
		// yitiList = new ArrayList<MeetingYiti>();
		// String where = " meetingId='" + meeting.getId() + "'";
		// yitiList = db.findAllByWhere(MeetingYiti.class, where);
		// for (int i = 0; i < yitiList.size(); i++) {
		// notDownCount += db.findAllByWhere(YitiFile.class,
		// "yitiId='" + yitiList.get(i).getId() + "' and isDown=0 ")
		// .size();
		// isDownCount += db.findAllByWhere(YitiFile.class,
		// "yitiId='" + yitiList.get(i).getId() + "' and isDown=1 ")
		// .size();
		// }
		sharePreferences = SharePreferences.getInstance(this);
		// userId = sharePreferences.getUserName();
		// userId = "ZR289781571bfda901571d070e23032e";
		findView();
		initControl();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// land do nothing is ok
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// port do nothing is ok
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				MyToast.show(LoginActivity.this, "您没有权限进入该会议！");
			} else if (msg.what == 1) {
				MyToast.show(LoginActivity.this, "用户名或密码错误！");
			}
		}
	};

	/**
	 * 获取版本号
	 * 
	 * @author 左大明
	 * 
	 */
	class VersionThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public VersionThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			try {
				Result result;
				try {
					result = AppDataControl.getInstance().getVersion(
							context, params);
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
				
				if(result == null){
					return;
				}
				
				if (result.getReturnValue() != 0) {
					return;
				}
				if (result.getData() == null) {
					return;
				}
				
				final Version version = (Version) result.getData();
				
				if (VersionUtils.getVerCode(LoginActivity.this) >= version.getVerCode()) {
					return;
				}
				
				ListResult<ApkFile> apkResult;
				try {
					apkResult = AppDataControl.getInstance().getApkFile(context, version.getFileId());
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
				
				if(apkResult == null){
					return;
				}

				final ListResult<ApkFile> finalResult = apkResult;
				handler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ApkFile apkFile = finalResult.getData().get(0);
						FinalHttp fh = new FinalHttp();
						fh.download(apkFile.getFileUrl(),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/hyxt.apk",
								new AjaxCallBack<File>() {

									@Override
									public void onFailure(Throwable t, int errorNo, String strMsg) {
										MyLog.e("更新apk出问题了", t.getMessage());
										super.onFailure(t, errorNo, strMsg);
									}

									@Override
									public void onLoading(long count, long current) {
										super.onLoading(count, current);
										// 这是布局文件中设置升级进度的TextView
									}

									@Override
									public void onSuccess(File t) {
										// 下载好后要进行安装
										super.onSuccess(t);
										Intent intent = new Intent();
										intent.setAction("android.intent.action.VIEW");
										intent.addCategory("android.intent.category.DEFAULT");
										intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
										startActivity(intent);
										android.os.Process.killProcess(android.os.Process.myPid());
									}

								});
					}
					
				});
			} finally {
			}
		}
	}

	/**
	 * 下载更新安装包
	 * 
	 * @author Administrator
	 * 
	 */
//	class DownThread extends Thread {
//		String fileUrl;
//
//		public DownThread(String fileUrl) {
//			this.fileUrl = fileUrl;
//		}
//
//		@Override
//		public void run() {
//			byte[] bytes = Base64.decode(fileUrl, Base64.DEFAULT); // 将字符串转换为byte数组
//			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
//			byte[] buffer = new byte[1024];
//			FileOutputStream fos;
//			try {
//				fos = openFileOutput("hyxt.apk", Context.MODE_WORLD_READABLE
//						+ Context.MODE_WORLD_WRITEABLE);
//				int bytesum = 0;
//				int byteread = 0;
//				int y = 0;
//				while ((byteread = in.read(buffer)) != -1) {
//					bytesum += byteread;
//					// out.write(buffer, 0, byteread); // 文件写操作
//					fos.write(buffer, 0, byteread);
//
//				}
//				// Log.e("xxxxxxxxx", file.getId() + "下载完成");
//				fos.close();
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				File file = new File(getApplicationContext().getFilesDir()
//						.getAbsolutePath() + "/hyxt.apk");
//				intent.setDataAndType(Uri.fromFile(file),
//						"application/vnd.android.package-archive");
//				startActivity(intent);
//				android.os.Process.killProcess(android.os.Process.myPid());
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				// e.printStackTrace();
//				MyLog.e("更新apk出问题了", e.getMessage());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				MyLog.e("更新apk出问题了", e.getMessage());
//			} catch (Exception e) {
//				MyLog.e("更新apk出问题了", e.getMessage());
//			}
//		}
//	}
}
