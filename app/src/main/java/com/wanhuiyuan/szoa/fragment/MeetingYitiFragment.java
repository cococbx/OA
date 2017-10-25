package com.wanhuiyuan.szoa.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ProgressBar;

import com.org.xlistview.XExpandableListView;
import com.org.xlistview.XExpandableListView.IXListViewListener;
import com.wanhuiyuan.down.DownloadService2;
import com.wanhuiyuan.szoa.MeetingListActivity;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.YItiExpandableListAdapter;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.OffLineYiti;
import com.wanhuiyuan.szoa.bean.ProgressItem;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.jingge.ConstantValue;
import com.wanhuiyuan.szoa.myview.MyProgressBar;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.service.DownloadService;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.HttpClientHolder;
import com.wanhuiyuan.szoa.uiutil.MyLog;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;
import com.wanhuiyuan.szoa.uiutil.SDHandler;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

import de.greenrobot.event.EventBus;

/**
 * 会议议题列表界面
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class MeetingYitiFragment extends BaseFragment implements ConstantValue {
	public int scrollStates;
	FinalDb db;
	List<MeetingYiti> yitiList;// 议题对象列表
	// ExpandableListView listivew;
	private XExpandableListView listivew; // 自定义控件
	ListResult<MeetingYiti> result;
	String meetingId = "";// 会议id
	YItiExpandableListAdapter adapter;
	Map<String, Object> params;
	Map<String, Object> params1 = new HashMap<String, Object>();
	int progress;
	MyProgressBar progressBar = null;
	ProgressBar load_progressBar;// 加载进度条
	String fileId;// 文件id
	int index = 0;// 下载进度条view的索引
	long clickTime = 0;// 记录点击时间，防止连续点击
	int downSize = 0;// 已下载完成离线数据的数量来控制进度条对话框到时隐藏。
	Intent regIntent;
	final static String SDCARD_PATH = Environment.getExternalStorageDirectory()
			.getPath().toString();
	
	//被替会人ID
	private String oTherUserid = "";
	boolean bReplace = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public MeetingYitiFragment() {

	}

	public MeetingYitiFragment(String meetingId, String moTherUserid) {
		this.meetingId = meetingId;
		oTherUserid = moTherUserid;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		// Intent regIntent = new Intent(getActivity(), DownloadService.class);
		// getActivity().startService(regIntent);
		View view = inflater.inflate(R.layout.fragment_meeting_yiti, container,
				false);
		load_progressBar = (ProgressBar) view
				.findViewById(R.id.load_progressBar);// 加载进度条
		load_progressBar.setVisibility(View.GONE);
		params = new HashMap<String, Object>();
		db = FinalDb.create(getActivity(), "szoa.db");
		regIntent = new Intent(getActivity(), DownloadService.class);
		// 下拉刷新 11.18
		// listivew = (ExpandableListView) view.findViewById(R.id.list);
		listivew = (XExpandableListView) view.findViewById(R.id.list);
		listivew.setGroupIndicator(null);
		listivew.setPullLoadEnable(false);// 禁止下拉加载更多
		listivew.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// 设置显示刷新的提示

				listivew.setRefreshTime(System.currentTimeMillis());
				// 获取议题列表数据
				new YitiListThread(getActivity(), params).start();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						downloadFiles();
					}
				}).start();;
			}

			@Override
			public void onLoadMore() {
			}
		});
		params.put("meetingId", meetingId);
		// params.put("userId", SharePreferences.getInstance(getActivity())
		// .getUserName());
		//如果有被替会人，就请求该用户的议题信息
		if (null == oTherUserid || "".equals(oTherUserid)){
			params.put("userId", MyApplication.USERID);
		}
		else{
			bReplace = true;
			params.put("userId", oTherUserid);
		}
		params.put("stamp", "0");

		// 获取议题列表数据
		new YitiListThread(getActivity(), params).start();
		if(MyApplication.isOtherLoginMode() || bReplace){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					downloadFiles();
				}
			}).start();;
		}
		return view;
	}

	private void initDate() {
		if (getActivity() == null)
			return;
		adapter = new YItiExpandableListAdapter(getActivity(), yitiList);
		listivew.setAdapter(adapter);
		for (int i = 0; i < yitiList.size(); i++) {
			listivew.expandGroup(i);
		}
		listivew.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (yitiList.get(groupPosition).getFiles().size() == 1) {
					// 防止连续点击
					if ((System.currentTimeMillis() - clickTime) > 3000) {
						Log.e("========================", "clickTime:" + clickTime);
						clickTime = System.currentTimeMillis();
						List<YitiFile> dbfiles = db.findAllByWhere(
								YitiFile.class,
								"id='"
										+ yitiList.get(groupPosition)
												.getFiles().get(0).getId()
										+ "' and isDown=1 ");
						// 判断文件是否下载完成
						if (dbfiles.size() != 0)
							doOpenFile(yitiList.get(groupPosition).getFiles()
									.get(0));
						else
							MyToast.show(getActivity(), "文件尚未下载完成");
					}
					return true;
				} else if (yitiList.get(groupPosition).getFiles().size() == 0) {
					MyToast.show(getActivity(), "该议题无材料可看");
					return true;
				}
				return false;
			}
		});
		listivew.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 防止连续点击
				if ((System.currentTimeMillis() - clickTime) > 3000) {
					Log.e("========================", "clickTime:" + clickTime);
					clickTime = System.currentTimeMillis();
					List<YitiFile> dbfiles = db.findAllByWhere(YitiFile.class,
							"id='"
									+ yitiList.get(groupPosition).getFiles()
											.get(childPosition).getId()
									+ "' and isDown=1 ");
					// 判断文件是否下载完成
					if (dbfiles.size() != 0)
						doOpenFile(yitiList.get(groupPosition).getFiles()
								.get(childPosition));
					else
						MyToast.show(getActivity(), "文件尚未下载完成");
				}
				return false;
			}
		});

	}

	// 获取异步状态信息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				listivew.setVisibility(View.GONE);
				load_progressBar.setVisibility(View.VISIBLE);
			} else if (msg.what == 1) {
				listivew.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
				listivew.stopRefresh();
			}
		}
	};


	/*
	 * 获取议题列表，在线获取不到则加载本地数据
	 */

	public class YitiListThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public YitiListThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			try {
				Message msg1 = new Message();
				msg1.what = 0; // 开始刷新
				handler.sendMessage(msg1);

				result = AppDataControl.getInstance().getMettingSubjectlist(
						context, params);

				if (null != result) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (result.getState() == 0
										&& result.getData() != null) {
									if (result.getData().size() > 0) {
										yitiList = result.getData();
										db.deleteByWhere(MeetingYiti.class,
												" meetingId='" + meetingId
														+ "' and userid='"
														+ MyApplication.USERID
														+ "'");

										for (int i = 0; i < yitiList.size(); i++) {
											yitiList.get(i).setMeetingId(
													meetingId);
											yitiList.get(i).setUserid(
													MyApplication.USERID);
											try {
												db.save(yitiList.get(i));
											} catch (Exception e) {
												db.update(yitiList.get(i));
											}

											for (int j = 0; j < yitiList.get(i).getFiles().size(); j++) {
												YitiFile file = yitiList.get(i).getFiles().get(j);
												List<YitiFile> dbfiles = db.findAllByWhere(
														YitiFile.class, "id='" + file.getId()
																+ "' ");
												if(MyApplication.isOtherLoginMode()){
													// 判断该文件在数据库是否存在
													if (dbfiles.size() == 0) {
														file.setYitiId(yitiList.get(i).getId());// 设置议题ID
														file.setMeetingId(yitiList.get(i).getMeetingId());
														file.setIsDown(0);// 设置状态为未下载
														try {
															db.save(file);// 文件信息保存到数据库
														} catch (Exception e) {
															db.update(file);
														}
													}
												}
											}
										}
										//如果是替他人开会重新请求下载任务
										if ("".equals(oTherUserid) || MyApplication.isOtherLoginMode()){
											Intent msgIntent = new Intent(context,
													DownloadService.class);
											msgIntent.putExtra("Command",
													"REDOWNLOAD");
											context.startService(msgIntent);
										}
										
										initDate();
										
										
									} else {
										// no_data.setVisibility(View.VISIBLE);
									}
								} else {
									// MyToast.show(getActivity(),
									// result.getError());
									offline();
								}
								// 数据请求完毕
								Message msg1 = new Message();
								msg1.what = 1;
								handler.sendMessage(msg1);
							} catch (Exception e) {
								MyLog.e("议题列表报错了：", e.getMessage());
								offline();
							} finally {
								// 数据请求完毕
								Message msg1 = new Message();
								msg1.what = 1;
								handler.sendMessage(msg1);
							}
							
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							MyLog.e("议题列表没有请求到数据：", "议题列表没有请求到数据");
							offline();
						}
					});
				}
			} finally {
			}
		}
	}

	// 加载离线数据
	private void offline() {
		// yitiList = db.findAllByWhere(MeetingYiti.class, "meetingId='"
		// + meetingId + "' order by showOrder");
		yitiList = db.findAllByWhere(MeetingYiti.class, "meetingId='"
				+ meetingId + "' and userid = '" + MyApplication.USERID
				+ "' order by showOrder");

		// 只离线本人的议题，无须再重新加载其他的关联信息
		// List<MeetingYiti> yittListNew = new ArrayList<MeetingYiti>();
		if (yitiList != null && yitiList.size() > 0) {
			// for (int i = 0; i < yitiList.size(); i++) {
			// String sql = "yitiId='"
			// + yitiList.get(i).getId().toUpperCase()
			// + "' and userId='"
			// + MyApplication.USERID.toUpperCase() + "'";
			// List<OffLineYiti> yitis = db.findAllByWhere(OffLineYiti.class,
			// sql);
			// if (yitis.size() > 0)
			// yittListNew.add(yitiList.get(i));
			// }
			// for (int i = 0; i < yittListNew.size(); i++) {
			// String yitifileSql = "yitiId='" + yittListNew.get(i).getId()
			// + "' order by showOrder desc ";
			// List<YitiFile> files = db.findAllByWhere(YitiFile.class,
			// yitifileSql);
			// yittListNew.get(i).setFiles(files);
			// }
			// yitiList.clear();
			// yitiList.addAll(yittListNew);
			// initDate();

			for (int i = 0; i < yitiList.size(); i++) {
				String yitifileSql = "yitiId='" + yitiList.get(i).getId()
						+ "' order by showOrder desc ";
				List<YitiFile> files = db.findAllByWhere(YitiFile.class,
						yitifileSql);
				yitiList.get(i).setFiles(files);
			}
		}
		// 数据请求完毕
		Message msg1 = new Message();
		msg1.what = 1;
		handler.sendMessage(msg1);

		initDate();
	}

	private void doOpenFile(YitiFile ytFile) {
		File file = new File(getActivity().getApplicationContext()
				.getFilesDir().getAbsolutePath()
				+ "/" + ytFile.getRealName());
		if (file == null) {
			MyToast.show(getActivity(), "文件未找到！");
		} else if (!file.exists()) {
			MyToast.show(getActivity(), "文件尚未下载！");
		} else {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent("android.intent.action.VIEW", uri);
			intent.setClassName(getActivity(),
					"com.wanhuiyuan.szoa.jingge.BookShower");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 传递用户名，默认admin
			intent.putExtra(NAME, MyApplication.USERID);
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
			if (SharePreferences.getInstance(getActivity()).getChangePage()
					.equals("vertical")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEV);
			} else if (SharePreferences.getInstance(getActivity())
					.getChangePage().equals("horizontal")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEH);
			} else
				intent.putExtra(VIEWMODENAME, VIEWMODE_VSCROLL);
			intent.putExtra("fileName", ytFile.getRealName());
			intent.putExtra("fileRealName", ytFile.getFileName());
			intent.putExtra("fileId", ytFile.getId());
			intent.putExtra("meetingId", meetingId);
			startActivity(intent);
		}
	}

	// 接收进度条更新消息
	public void onEventMainThread(ProgressItem item) {

		progress = item.getProgress();
		fileId = item.getFileId();

		// Log.v("进度", fileId + "】】】" + progress);
		index = 0;
		if (yitiList == null) {
			return;
		}
		for (int i = 1; i <= yitiList.size(); i++) {
			for (int j = 0; j < yitiList.get(i - 1).getFiles().size(); j++) {
				if (yitiList.get(i - 1).getFiles().get(j).getId()
						.equals(fileId)) {
					if (listivew != null) {
						index += i;
						if (i > 1) {
							for (int z = 0; z < i - 1; z++) {
								index += yitiList.get(z).getFiles().size();
							}
						}

						index += j;
						try {
							// 更换可下拉组件后可视控件加1 11.18 tom
							progressBar = (MyProgressBar) listivew.getChildAt(
									index - listivew.getFirstVisiblePosition()
											+ 1)
									.findViewById(R.id.progressBar1);
							progressBar.setProgress(progress);
						} catch (Exception e) {
//							e.printStackTrace();
						}
						if (progress == 100) {
							if (progressBar != null)
								((View) progressBar.getParent())
										.setVisibility(View.GONE);
							yitiList.get(i - 1).getFiles().get(j).setIsDown(1);
							adapter.notifyDataSetChanged();
						}
					}
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	/*
	 * 获取人员和议题的对应关系
	 */
	private class OffLineYitiTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPreExecute() {
			listivew.setVisibility(View.GONE);
			load_progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				listivew.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
				new YitiListThread(getActivity(), params).start();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params5) {
			downSize++;

			final ListResult<OffLineYiti> oresult = AppDataControl
					.getInstance().getOffLineYitiList(getActivity(), params1);
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
							MyLog.e("议题列表报错了：", e.getMessage());
						} finally {
						}
					}
				});
			}

			return null;
		}

	}

	// 获取会议和参会人对应关系
	private class OffLineMeetingTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPreExecute() {
			listivew.setVisibility(View.GONE);
			load_progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				listivew.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
				new YitiListThread(getActivity(), params).start();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params5) {

			downSize++;
			final ListResult<Meeting> oresult = AppDataControl.getInstance()
					.getOffLineUserSelectList(getActivity(), params1);
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
												db.save(meetingList.get(i)
														.getDept().get(j));
											else
												db.update(meetingList.get(i)
														.getDept().get(j));
										} catch (Exception e) {
											MyLog.e("议题列表报错了：", e.getMessage());
											// db.save(meetingList
											// .get(i)
											// .getDept()
											// .get(j));
										}

										if (meetingList.get(i).getDept().get(j)
												.getUserList() != null) {
											for (int k = 0; k < meetingList
													.get(i).getDept().get(j)
													.getUserList().size(); k++) {
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
													MyLog.e("议题列表报错了：", e.getMessage());
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
					MyLog.e("议题列表报错了：", e.getMessage());
				} finally {

				}
			}

			return null;
		}

	}

	/*
	 * 获取所有人员信息
	 */
	private class OffLineUserTask extends AsyncTask<Integer, Float, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			listivew.setVisibility(View.GONE);
			load_progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (downSize == 3) {
				listivew.setVisibility(View.VISIBLE);
				load_progressBar.setVisibility(View.GONE);
				new YitiListThread(getActivity(), params).start();
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Integer... params5) {

			downSize++;
			final ListResult<OffLineUser> oresult = AppDataControl
					.getInstance().getOffLineUserList(getActivity(), params1);
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
					MyLog.e("议题列表报错了：", e.getMessage());
				} finally {

				}
			}
			return null;
		}
	}

	// 实现数据传递
	public void getRefush(Callback callback) {
		downSize = 0;
		// 获取所有人员信息
		// new OffLineUserTask().execute(1);

		// 获取会议和参会人对应关系
		// new OffLineMeetingTask().execute(1);

		// 获取人员和议题的对应关系
		// new OffLineYitiTask().execute(1);

		// 获取议题列表数据,单离线会议之前没有该内容，包含在以上三个之中
		new YitiListThread(getActivity(), params).start();

	}

	// 创建刷新回调接口
	public interface Callback {
		public void getRefush(String msg);
	}
	
	public void downloadFiles() {
		List<MeetingYiti> yitiList;
		Map<String, Object> params = new HashMap<String, Object>();
		if(bReplace){
			params.put("PerId", oTherUserid);
		}else{
			params.put("PerId", MyApplication.USERID);
		}
		// 获取所有文件
		ListResult<MeetingYiti> result = AppDataControl.getInstance()
				.getAllFilelist(getActivity(), params);
		if (null != result) {
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
							Intent intent = new Intent(getActivity(),
									DownloadService2.class);
							intent.setAction(DownloadService2.ACTION_START);
							intent.putExtra("fileinfo", file);
							getActivity().startService(intent);

						}

					}
				}
			}
		}
	}
}
