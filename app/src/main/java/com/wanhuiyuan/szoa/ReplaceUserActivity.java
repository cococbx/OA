package com.wanhuiyuan.szoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.ReplaceUserGridViewAdapter;
import com.wanhuiyuan.szoa.adapter.TiHuiUserGridViewAdapter;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.MeetPerson;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.gridview.MyGridView;
import com.wanhuiyuan.szoa.gridview.ReplaceUserListViewAdapter;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;

/**
 * 登录界面点击用户名输入框打开用户选择界面。
 * 
 * @author Administrator
 * 
 */
public class ReplaceUserActivity extends BaseActivity {

	// public List<Dept> deptList;// 部门集合
	public List<MeetPerson> userList;// 人员集合
	ExpandableListView userListView;// 用户ExpandableListView
	ReplaceUserListViewAdapter treeViewAdapter;// 用户适配器
	// ListResult<Dept> result;// 查询结果集合
	ListResult<MeetPerson> result;// 查询结果集合
	List<ReplaceUserListViewAdapter.TreeNode> treeNode;// 多列展示
	String meetingId;// 会议ID
	ProgressBar load_progressBar;// 加载进度条
	FinalDb db;

	private MyGridView userlistGrid;
	private TiHuiUserGridViewAdapter userlistAapter;
	private Meeting mMeeting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replace_user_layout);
		db = FinalDb.create(this, "szoa.db");
		TextView title = (TextView) findViewById(R.id.sys_title_txt);
		// 左边返回按钮
		leftBtn = (ImageButton) findViewById(R.id.btn_back);
		leftBtn.setVisibility(View.VISIBLE);
		title.setText("选择参会人员");
		load_progressBar = (ProgressBar) findViewById(R.id.load_progressBar);
		// meetingId = getIntent().getStringExtra("meetingId");
		mMeeting = (Meeting) getIntent().getSerializableExtra("meeting");
		meetingId = mMeeting.getId();

		// deptList = new ArrayList<Dept>();
		userList = new ArrayList<MeetPerson>();
		
		userList = db.findAll(MeetPerson.class);

		// initDate();
		// treeViewAdapter = new SelectUserListViewAdapter(this,
		// SelectUserListViewAdapter.PaddingLeft >> 1, mMeeting);
		// userListView = (ExpandableListView) findViewById(R.id.userList);
		// treeNode = treeViewAdapter.GetTreeNode();

		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("meetingId", meetingId);
		// 11.15新接口
		params.put("MeeId", meetingId);
		params.put("PerId", MyApplication.USERID);
		// 获取用户
		load_progressBar.setVisibility(View.VISIBLE);
		new UserListThread(this, params).start();
		// initDate();
		findViewById(R.id.imgbtn_right).setVisibility(View.GONE);
		// // 展开按钮
		// final TextView open = (TextView) findViewById(R.id.open);
		// // 收起按钮
		// final TextView close = (TextView) findViewById(R.id.close);
		// close.setVisibility(View.VISIBLE);
		// open.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// for (int i = 0; i < userList.size(); i++) {
		// userListView.expandGroup(i);
		// }
		// open.setVisibility(View.GONE);
		// close.setVisibility(View.VISIBLE);
		// }
		// });
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// close.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// for (int i = 0; i < userList.size(); i++) {
		// userListView.collapseGroup(i);
		// }
		// open.setVisibility(View.VISIBLE);
		// close.setVisibility(View.GONE);
		// }
		// });

		userlistGrid = (MyGridView) findViewById(R.id.GridView_toolbar);
		userlistGrid.setNumColumns(4);// ����ÿ������
		userlistGrid.setGravity(Gravity.CENTER);// λ�þ���
		userlistGrid.setHorizontalSpacing(10);// ˮƽ���

	}

	private void initData() {
		userlistAapter = new TiHuiUserGridViewAdapter(this, userList, mMeeting);
		userlistGrid.setAdapter(userlistAapter);// ���ò˵�Adapter
		userlistGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", userList.get(position));
				bundle.putSerializable("meeting", mMeeting);
				intent.putExtras(bundle);
				setResult(11, intent);
				finish();
			}

		});
	}

	/**
	 * 获取用户列表，在线获取不到的话就查询本地数据库，根据会议id查出该会议出席人员
	 * 
	 * @author Administrator
	 * 
	 */
	class UserListThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public UserListThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			try {
				result = AppDataControl.getInstance().getMeetPer(
						context, params);
				if (null != result) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (result.getState() == 0) {
									if (result.getData().size() > 0) {
										userList = result.getData();
										// db.deleteAll(MeetPerson.class);
										db.deleteByWhere(MeetPerson.class,
												" meetingId='" + meetingId
														+ "' and userid='"
														+ MyApplication.USERID
														+ "'");
										for (int i = 0; i < userList.size(); i++) {
											userList.get(i).setUserid(
													MyApplication.USERID);
											userList.get(i).setMeetingId(
													meetingId);
											try {
												db.save(userList.get(i));
											} catch (Exception e) {
												db.update(userList.get(i));
											}
										}
										initData();
									} else {
										MyToast.show(context, "未请求到参会人员数据！");
									}
								} else {
									MyToast.show(context, result.getError());
								}
							} catch (Exception e) {
								// no_data.setVisibility(View.VISIBLE);
							} finally {

							}
							load_progressBar.setVisibility(View.GONE);
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								// 加载离线数据
								load_progressBar.setVisibility(View.GONE);
//								userList = db.findAllByWhere(MeetPerson.class,
//										" meetingId='" + meetingId
//												+ "' and userid='"
//												+ MyApplication.USERID + "'");
								//当前主键为被替换人的id，上述保存方法不能保存到会议，所以离线时显示该用户的所有可替会人员
								userList = db.findAllByWhere(MeetPerson.class,
										" userid='"
												+ MyApplication.USERID + "'");
								if (userList.size() > 0) {
									initData();
								} else {
									MyToast.show(context, "未请求到数据！");
								}
							} catch (Exception e) {
								MyToast.show(context, "未请求到数据！");
							}
						}
					});
				}
			} finally {
			}
		}
	}
}
