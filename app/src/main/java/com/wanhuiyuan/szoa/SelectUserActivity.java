package com.wanhuiyuan.szoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.SelectUserGridViewAdapter;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingVisit;
import com.wanhuiyuan.szoa.bean.ResultString;
import com.wanhuiyuan.szoa.gridview.ReplaceUserListViewAdapter;
import com.wanhuiyuan.szoa.gridview.SelectUserListViewAdapter;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.JSONHelper;

/**
 * 登录界面点击用户名输入框打开用户选择界面。
 * 
 * @author Administrator
 * 
 */
public class SelectUserActivity extends BaseActivity {

	public List<Dept> deptList;// 部门集合
	ExpandableListView userListView;// 用户ExpandableListView
	SelectUserListViewAdapter treeViewAdapter;// 用户适配器
	ListResult<Dept> result;// 查询结果集合
	List<SelectUserListViewAdapter.TreeNode> treeNode;// 多列展示
	String meetingId;// 会议ID
	Meeting meeting = null;
	ProgressBar load_progressBar;// 加载进度条
	FinalDb db;

	ProgressDialog loadingDialog;// 等待框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_user_layout);
		db = FinalDb.create(this, "szoa.db");
		TextView title = (TextView) findViewById(R.id.sys_title_txt);
		// 左边返回按钮
		leftBtn = (ImageButton) findViewById(R.id.btn_back);
		leftBtn.setVisibility(View.VISIBLE);
		title.setText("选择参会人员");
		load_progressBar = (ProgressBar) findViewById(R.id.load_progressBar);
		meetingId = getIntent().getStringExtra("meetingId");
		deptList = new ArrayList<Dept>();
		// initDate();
		treeViewAdapter = new SelectUserListViewAdapter(this,
				SelectUserListViewAdapter.PaddingLeft >> 1);
		userListView = (ExpandableListView) findViewById(R.id.userList);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage("获取会议信息中...");
		loadingDialog.setCancelable(false);

		treeNode = treeViewAdapter.GetTreeNode();
		deptList = new ArrayList<Dept>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("meetingId", meetingId);
		// 获取用户
		new UserListThread(this, params).start();
		// initDate();
		findViewById(R.id.imgbtn_right).setVisibility(View.GONE);
		// 展开按钮
		final TextView open = (TextView) findViewById(R.id.open);
		// 收起按钮
		final TextView close = (TextView) findViewById(R.id.close);
		close.setVisibility(View.VISIBLE);
		open.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < deptList.size(); i++) {
					userListView.expandGroup(i);
				}
				open.setVisibility(View.GONE);
				close.setVisibility(View.VISIBLE);
			}
		});
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				backMethod();
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < deptList.size(); i++) {
					userListView.collapseGroup(i);
				}
				open.setVisibility(View.VISIBLE);
				close.setVisibility(View.GONE);
			}
		});
	}
	
	public void handlerUserClick(Login login){
		MyApplication.USERID = login.getId();
		MyApplication.USERXINGMING = login.getTruename();
		loadingDialog.show();
		new AsyncTask<String, Integer, String>(){
			
			String errMsg = "";

			@Override
			protected String doInBackground(String... params) {
				
	    		Map<String, Object> params1 = new HashMap<String, Object>();
	    		params1.put("persionId", MyApplication.USERID);
	    		params1.put("timestamp", "0");

	    		ListResult<Meeting> result = AppDataControl.getInstance().getMeetingList(
	    				SelectUserActivity.this, params1);
	    		meeting = null;
	    		if(result != null){
	    			List<Meeting> meetings = result.getData();
	    			if(meetings == null){
	    				return "";
	    			}
	    			for(Meeting curMeeting:meetings){
	    				if(meetingId.equals(curMeeting.getId())){
	    					meeting = curMeeting;
	    				}
	    			}
	    		}
	    		
	        	if(meeting != null){
	        		AppDataControl.getInstance().authorizeLog(SelectUserActivity.this, 
	        				MyApplication.USERID, MyApplication.AUTHORIZE_USERID, meetingId);
	        	}
	    		
	    		return "";
			}
			


	        @Override
	        protected void onPostExecute(String ret) {
	        	loadingDialog.dismiss();
	        	if(meeting == null){
	        		return;
	        	}
				MyApplication.setVistorMode(false);
				MyApplication.setAuthorizeMode(true);
	        	
				Intent intent = null;
				intent = new Intent(SelectUserActivity.this,
						MainActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("meeting", meeting);
				b.putBoolean("isOnline", true);
				intent.putExtras(b);
				startActivity(intent);
				finish();
	        }
			
		}.execute();
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
				result = AppDataControl.getInstance().geDeptAndUserList(
						context, params);
				if (null != result) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (result.getState() == 0) {
									if (result.getData().size() > 0) {
										deptList = result.getData();
										for (int i = 0; i < deptList.size(); i++) {
											SelectUserListViewAdapter.TreeNode node = new SelectUserListViewAdapter.TreeNode();
											node.parent = deptList.get(i)
													.getDetpName();
											for (int ii = 0; ii < deptList
													.get(i).getUserList()
													.size(); ii++) {
												node.childs.add(deptList.get(i)
														.getUserList().get(ii));
											}
											treeNode.add(node);
										}

										treeViewAdapter
												.UpdateTreeNode(treeNode);
										userListView
												.setAdapter(treeViewAdapter);
										userListView.setGroupIndicator(null);
										for (int i = 0; i < treeViewAdapter
												.getGroupCount(); i++) {
											userListView.expandGroup(i);
										}
										load_progressBar
												.setVisibility(View.GONE);
									} else {
										MyToast.show(context, "未请求到参会人员数据！");
										load_progressBar
												.setVisibility(View.GONE);
									}
								} else {
									MyToast.show(context, result.getError());
								}
							} catch (Exception e) {
								// no_data.setVisibility(View.VISIBLE);
							} finally {

							}
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								// 加载离线数据
								load_progressBar.setVisibility(View.GONE);
								deptList = db.findAllByWhere(Dept.class,
										" meetingId='" + meetingId
												+ "' order by deptOrder");
								if (deptList.size() > 0) {
									for (int i = 0; i < deptList.size(); i++) {
										deptList.get(i)
												.setUserList(
														db.findAllByWhere(
																Login.class,
																" deptId='"
																		+ deptList
																				.get(i)
																				.getDetpId()
																		+ "' and meetingId='"
																		+ meetingId
																		+ "' order by showorder"));
									}
									for (int i = 0; i < deptList.size(); i++) {
										SelectUserListViewAdapter.TreeNode node = new SelectUserListViewAdapter.TreeNode();
										node.parent = deptList.get(i)
												.getDetpName();
										for (int ii = 0; ii < deptList.get(i)
												.getUserList().size(); ii++) {
											node.childs.add(deptList.get(i)
													.getUserList().get(ii));
										}
										treeNode.add(node);
									}

									treeViewAdapter.UpdateTreeNode(treeNode);
									userListView.setAdapter(treeViewAdapter);
									userListView.setGroupIndicator(null);
									// 展开列表
									for (int i = 0; i < treeViewAdapter
											.getGroupCount(); i++) {
										userListView.expandGroup(i);
									}

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
	

    // -----------------------------------
    // 退出
    // -----------------------------------
    private long exitTime = 0;

    @Override
    public void onBackPressed() {
		backMethod();
    }
    
    void backMethod(){
        if ((System.currentTimeMillis() - exitTime) > 2000) {
			MyToast.show(getApplicationContext(), "再按一次返回键将退出选人！");
            exitTime = System.currentTimeMillis();
        } else {
        	finish();
        }
    }
}
