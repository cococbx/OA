package com.wanhuiyuan.szoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.cec.szoa.R;
import com.wanhuiyuan.down.ThreadInfo;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.OffLineYiti;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.service.DownloadService;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.DataCleanManager;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

/**
 * 配置管理界面
 * 
 * @author Administrator
 * 
 */
public class ConfigIpActivity extends Activity {
	EditText ipEditText, portEditText, projectEditText;// ip输入框，端口输入框，项目名输入框
	Button testBtn, clearBtn, downMsgBtn;// 链接按钮，清楚缓存按钮，下载离线输入按钮。
	RadioButton myRadioButton1, myRadioButton2, myRadioButton3;// 翻页效果选择按钮
	private ProgressDialog progressDialog = null;// 下载进度等待框
	SharePreferences share;
	Handler handler = new Handler();
	static FinalDb db;
	Map<String, Object> params = new HashMap<String, Object>();
	ProgressDialog downDialog;// 下载进度等待框
	int downSize = 0;// 已下载完成离线数据的数量来控制进度条对话框到时隐藏。

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_ip);
		db = FinalDb.create(this, "szoa.db");
		share = SharePreferences.getInstance(this);
		downDialog = new ProgressDialog(this);
		downDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		downDialog.setMessage("正在同步离线数据...");
		downDialog.setCancelable(false);
		myRadioButton1 = (RadioButton) findViewById(R.id.myRadioButton1);
		myRadioButton2 = (RadioButton) findViewById(R.id.myRadioButton2);
		myRadioButton3 = (RadioButton) findViewById(R.id.myRadioButton3);
		myRadioButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				share.putChangePage("vscroll");
			}
		});
		myRadioButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				share.putChangePage("horizontal");

			}
		});
		myRadioButton3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				share.putChangePage("vertical");
			}
		});
		if (share.getChangePage().equals("vscroll"))
			myRadioButton1.setChecked(true);
		if (share.getChangePage().equals("horizontal"))
			myRadioButton2.setChecked(true);
		if (share.getChangePage().equals("vertical"))
			myRadioButton3.setChecked(true);
		ipEditText = (EditText) findViewById(R.id.ipEditText);
		portEditText = (EditText) findViewById(R.id.portEditText);
		/**
		 * 增加IP格式判断方法，判断IP格式是否正确 victory
		 */
		ipEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (!isIp(ipEditText.getText().toString())) {
					MyToast.show(ConfigIpActivity.this, "IP格式不正确");
					return;
				}
			}
		});
		// 调用限制端口号输入方法
		portNumAstrict();
		projectEditText = (EditText) findViewById(R.id.projectEditText);
		testBtn = (Button) findViewById(R.id.testBtn);
		if (SharePreferences.getInstance(this).getIP() != null
				&& !SharePreferences.getInstance(this).getIP().equals("")) {
			ipEditText.setText(SharePreferences.getInstance(this).getIP());
			portEditText.setText(SharePreferences.getInstance(this).getPort());
			projectEditText.setText(SharePreferences.getInstance(this)
					.getProject());
		}
		ImageButton imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		imgbtn_right.setVisibility(View.GONE);
		ImageButton back = (ImageButton) findViewById(R.id.btn_back);
		clearBtn = (Button) findViewById(R.id.clearBtn);
		downMsgBtn = (Button) findViewById(R.id.downMsgBtn);
		downMsgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				downMsgBtn.setClickable(false);// 防止连续点击
				downMgs();// 下载离线数据
			}
		});
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// if (MyApplication.downedFilesNum ==
				// MyApplication.downFilesNum) {
				// daming 11.26修改，判断是否有下载任务从下载表中判断
				List<YitiFile> dbfiles = db.findAllByWhere(YitiFile.class,
						" isDown<>1 ");
				final int listCount = dbfiles.size();
				if (listCount == 0) {
					clear(getApplicationContext());
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							ConfigIpActivity.this);
					AlertDialog dialog;
					builder.setCancelable(false);
					builder.setTitle("提示"); // 设置标题
					builder.setMessage("下载中清除缓存需要退出程序，是否继续？"); // 设置内容
					builder.setPositiveButton("继续清除",
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									clear(getApplicationContext());
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					dialog = builder.create();
					dialog.show();

				}
				// MyApplication.files.clear();
				// if (MyApplication.downedFilesNum ==
				// MyApplication.downFilesNum) {
				// startService(regIntent);
				// }

			}
		});
		testBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ipEditText.getText().toString().length() < 1) {
					MyToast.show(ConfigIpActivity.this, "IP地址不能为空!");
					return;
				}
				if (portEditText.getText().toString().length() < 1) {
					MyToast.show(ConfigIpActivity.this, "端口号不能为空!");
					return;
				}
				if (projectEditText.getText().toString().length() < 1) {
					MyToast.show(ConfigIpActivity.this, "项目名称不能为空!");
					return;
				}
				/**
				 * 增加点击判断IP地址格式是否正确 victory
				 */
				if (!isIp(ipEditText.getText().toString())) {
					MyToast.show(ConfigIpActivity.this, "IP格式不正确");
					return;
				}
				progressDialog = ProgressDialog.show(ConfigIpActivity.this, "",
						"尝试连接服务,请稍候！");
				progressDialog.setProgressStyle(R.style.progress_style_dialog);
				progressDialog.show();
				// 链接测试，测试配置的ip\端口是否正确。
				FinalHttp fh = new FinalHttp();
				String fileUrl = "http://" + ipEditText.getText().toString()
						+ ":" + portEditText.getText().toString() + "/"
						+ projectEditText.getText().toString()
						+ "/service.do?action=testInterface";

				fh.get(fileUrl, new AjaxCallBack() {

					@Override
					public void onSuccess(Object t) {
						if (t.toString().contains("OK")) {
							progressDialog.dismiss();
							MyToast.show(ConfigIpActivity.this, "连接成功！");
							// clear(getApplicationContext());
							SharePreferences.getInstance(ConfigIpActivity.this)
									.putIP(ipEditText.getText().toString());
							SharePreferences.getInstance(ConfigIpActivity.this)
									.putPort(portEditText.getText().toString());
							SharePreferences.getInstance(ConfigIpActivity.this)
									.putProject(
											projectEditText.getText()
													.toString());
							SharePreferences.getInstance(ConfigIpActivity.this)
									.putServerUrl(
											"http://"
													+ ipEditText.getText()
															.toString()
													+ ":"
													+ portEditText.getText()
															.toString()
													+ "/"
													+ projectEditText.getText()
															.toString());
							finish();
						}
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						progressDialog.dismiss();
						MyToast.show(ConfigIpActivity.this, "连接失败！");
					}

				});
			}
		});
	}

	/**
	 * 清楚缓存，版本更新时清空本地所有的数据表，防止字段变化导致程序失败
	 * 
	 * @param context
	 */
	public static void clearAll(Context context) {
		DataCleanManager dcm = new DataCleanManager();
		dcm.delDatabases(context);
		dcm.cleanFiles(context);
	}

	/**
	 * 清楚缓存
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		DataCleanManager dcm = new DataCleanManager();
		dcm.clearDatabases(context);
		dcm.cleanFiles(context);
		// 保留用户的ip地址和用户信息
		// dcm.cleanSharedPreference(context);
		db = FinalDb.create(context, "szoa.db");
		db.deleteAll(ThreadInfo.class);
		MyToast.show(context, "缓存数据清除成功！");
	}

	// public static void firstSetupclear(Context context) {
	// DataCleanManager dcm = new DataCleanManager();
	// dcm.cleanDatabases(context);
	// dcm.cleanFiles(context);
	// //保留用户的ip地址和用户信息
	// // dcm.cleanSharedPreference(context);
	// MyToast.show(context, "缓存数据清除成功！");
	// }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {

		// init();
		super.onResume();
	}

	/**
	 * 下载离线数据
	 */

	public void downMgs() {
		downSize = 0;
		// 下载离线人员数据
		new OffLineUserTask().execute(1);
		// 获取会议和参会人对应关系
		new OffLineMeetingTask().execute(1);
		// 获取人员和议题的对应关系
		new OffLineYitiTask().execute(1);

	}

	/**
	 * 获取人员和议题的对应关系.
	 */
	private class OffLineYitiTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				downMsgBtn.setClickable(true);
				downDialog.cancel();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params1) {
			downSize++;
			final ListResult<OffLineYiti> oresult = AppDataControl
					.getInstance().getOffLineYitiList(ConfigIpActivity.this,
							params);
			final List<OffLineYiti> yitiList = new ArrayList<OffLineYiti>();
			if (null != oresult) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (oresult.getState() == 0) {
								if (oresult.getData().size() > 0) {
									yitiList.addAll(oresult.getData());
									try {
										db.deleteAll(OffLineYiti.class);
									} catch (Exception e) {
									}
									for (int i = 0; i < yitiList.size(); i++) {
										try {
											db.save(yitiList.get(i));
										} catch (Exception e) {
											db.update(yitiList.get(i));
										}
									}
								} else {
								}
							} else {
							}
						} catch (Exception e) {
						} finally {
						}
					}
				});
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						downMsgBtn.setClickable(true);
						downDialog.cancel();
						MyToast.show(ConfigIpActivity.this, "网络异常");
					}
				});
			}

			return null;
		}

	}

	/**
	 * 获取会议和参会人对应关系.
	 * 
	 * @author Administrator
	 * 
	 */
	private class OffLineMeetingTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				downMsgBtn.setClickable(true);
				downDialog.cancel();
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			downDialog.setMessage("正在下载离线会议数据");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Integer... params1) {

			downSize++;
			final ListResult<Meeting> oresult = AppDataControl.getInstance()
					.getOffLineUserSelectList(ConfigIpActivity.this, params);
			final List<Meeting> meetingList = new ArrayList<Meeting>();
			if (null != oresult) {
				try {
					if (oresult.getState() == 0) {
						if (oresult.getData().size() > 0) {
							meetingList.addAll(oresult.getData());
							try {
								db.deleteAll(Dept.class);
								db.deleteAll(Login.class);
							} catch (Exception e) {
								try {
									db.deleteAll(Login.class);
								} catch (Exception e1) {

								}
							}
							for (int i = 0; i < meetingList.size(); i++) {

								List<Dept> deptData = meetingList.get(i)
										.getDept();
								meetingList.get(i).setDept(deptData);
								if (meetingList.get(i).getDept() != null)
									for (int j = 0; j < meetingList.get(i)
											.getDept().size(); j++) {
										meetingList
												.get(i)
												.getDept()
												.get(j)
												.setMeetingId(
														meetingList.get(i)
																.getId());
										try {
											if (db.findAllByWhere(
													Dept.class,
													" detpId='"
															+ meetingList
																	.get(i)
																	.getDept()
																	.get(j)
																	.getDetpId()
															+ "' and meetingId='"
															+ meetingList
																	.get(i)
																	.getId()
															+ "'").size() == 0
													&& meetingList.get(i)
															.getDept().get(j)
															.getUserList()
															.size() > 0)
												// 保存参会人员部门信息
												db.save(meetingList.get(i)
														.getDept().get(j));
											else
												db.update(meetingList.get(i)
														.getDept().get(j));
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (meetingList.get(i).getDept().get(j)
												.getUserList() != null) {
											for (int k = 0; k < meetingList
													.get(i).getDept().get(j)
													.getUserList().size(); k++) {
												// 设置参会人员部门ID
												meetingList
														.get(i)
														.getDept()
														.get(j)
														.getUserList()
														.get(k)
														.setDeptId(
																meetingList
																		.get(i)
																		.getDept()
																		.get(j)
																		.getDetpId());
												// 设置参会人员会议ID
												meetingList
														.get(i)
														.getDept()
														.get(j)
														.getUserList()
														.get(k)
														.setMeetingId(
																meetingList
																		.get(i)
																		.getId());
												try {
													if (db.findAllByWhere(
															Login.class,
															" id='"
																	+ meetingList
																			.get(i)
																			.getDept()
																			.get(j)
																			.getUserList()
																			.get(k)
																			.getId()
																	+ "' and meetingId='"
																	+ meetingList
																			.get(i)
																			.getId()
																	+ "'")
															.size() < 1)
														// 将参会人员保存到数据库
														db.save(meetingList
																.get(i)
																.getDept()
																.get(j)
																.getUserList()
																.get(k));
													else
														db.update(meetingList
																.get(i)
																.getDept()
																.get(j)
																.getUserList()
																.get(k));
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
									}

							}
						} else {
						}
					} else {
					}

				} catch (Exception e) {
					System.out.println(e);
				} finally {

				}
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						downMsgBtn.setClickable(true);
						downDialog.cancel();
						MyToast.show(ConfigIpActivity.this, "网络异常");
					}
				});
			}

			return null;
		}

	}

	/**
	 * 获取所有人员信息
	 * 
	 * @author Administrator
	 * 
	 */
	private class OffLineUserTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPreExecute() {
			downDialog.show();
			downDialog.setMessage("正在下载离线人员数据");
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				downMsgBtn.setClickable(true);
				downDialog.cancel();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params1) {

			downSize++;

			final ListResult<OffLineUser> oresult = AppDataControl
					.getInstance().getOffLineUserList(ConfigIpActivity.this,
							params);
			final List<OffLineUser> userList = new ArrayList<OffLineUser>();
			if (null != oresult) {
				try {
					if (oresult.getState() == 0) {
						if (oresult.getData().size() > 0) {
							userList.addAll(oresult.getData());
							try {
								db.deleteAll(OffLineUser.class);
							} catch (Exception e) {

							}
							for (int i = 0; i < userList.size(); i++) {
								// 将系统所有用户信息保存到数据库，用来离线登录校验密码
								try {
									db.save(userList.get(i));
								} catch (Exception e) {
									db.update(userList.get(i));
								}
							}
						} else {
						}
					} else {
					}
				} catch (Exception e) {
				} finally {

				}
			} else {
				handler.post(new Runnable() {
					@Override
					public void run() {
						downMsgBtn.setClickable(true);
						downDialog.cancel();
						MyToast.show(ConfigIpActivity.this, "网络异常");
					}
				});
			}
			return null;
		}
	}

	/**
	 * 限定IP地址输入格式，只能为数字和.输入，限定端口号为0-65535，并设置只能输入数字，如果超出限制则输入为65535 修改 victory
	 * 增加方法 判定端口方法和IP格式验证方法
	 */
	public void portNumAstrict() {
		portEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int arg2,
					int arg3) {
				if (start > 1) {
					if (0 != -1 && 65535 != -1) {
						int num = Integer.parseInt(s.toString());
						if (num > 65535) {
							s = "65533";
						}
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s != null && !s.equals("")) {
					if (0 != -1 && 65535 != -1) {// 最大值和最小值自设
						int a = 0;
						try {
							a = Integer.parseInt(s.toString());
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							a = 0;
						}
						if (a > 65535)
							portEditText.setText("65535");
						return;
					}
				}
			}

		});
	}

	public String cc(String IP) {// 去掉IP字符串前后所有的空格
		while (IP.startsWith(" ")) {
			IP = IP.substring(1, IP.length()).trim();
		}
		while (IP.endsWith(" ")) {
			IP = IP.substring(0, IP.length() - 1).trim();
		}
		return IP;
	}

	public boolean isIp(String IP) {// 判断是否是一个IP
		boolean b = false;
		IP = this.cc(IP);
		if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			String s[] = IP.split("\\.");
			if (Integer.parseInt(s[0]) < 255)
				if (Integer.parseInt(s[1]) < 255)
					if (Integer.parseInt(s[2]) < 255)
						if (Integer.parseInt(s[3]) < 255)
							b = true;
		}
		return b;
	}
}
