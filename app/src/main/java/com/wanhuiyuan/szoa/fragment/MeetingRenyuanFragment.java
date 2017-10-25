package com.wanhuiyuan.szoa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;

/**
 * 参会人员界面
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class MeetingRenyuanFragment extends Fragment {
	public static List<Dept> deptList;
	// static ExpandableListView userListView;//更改展现方式以后这个暂时不用了
	// ListViewAdapter treeViewAdapter;//更改展现方式以后这个暂时不用了
	TextView yingdao, shidao, tihui;// 应到、实到、替会统计的textview
	ListResult<Dept> result;
	int ydNum = 0;// 应到人员数量
	int sdNum = 0;// 实到人员数量
	int thNum = 0;// 替会人员数量
	Meeting meeting;
	Handler handler = new Handler();
	// List<ListViewAdapter.TreeNode> treeNode;//更改展现方式以后这个暂时不用了
	ProgressBar load_progressBar;
	FinalDb db;
	TextView ydryTv, thryTv, wdryTv;// 应到、替会、未到人员列表的textview
	StringBuffer ydsb = new StringBuffer();// 应到人员字符串
	StringBuffer thsb = new StringBuffer();// 替会人员字符串
	StringBuffer wdsb = new StringBuffer();// 未到人员字符串
	List<List<Login>> ydmap = new ArrayList<List<Login>>();// 应到人员集合
	List<List<Login>> wdmap = new ArrayList<List<Login>>();// 未到人员集合
	List<List<Login>> thmap = new ArrayList<List<Login>>();// 替会人员集合

	public MeetingRenyuanFragment() {
	}

	public MeetingRenyuanFragment(Meeting meeting) {
		this.meeting = meeting;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		db = FinalDb.create(getActivity(), "szoa.db");
		View view = inflater.inflate(R.layout.fragment_meeting_renyuan,
				container, false);
		load_progressBar = (ProgressBar) view
				.findViewById(R.id.load_progressBar);
		yingdao = (TextView) view.findViewById(R.id.persion_max_num);
		shidao = (TextView) view.findViewById(R.id.persion_in_num);
		tihui = (TextView) view.findViewById(R.id.persion_ti_num);
		ydryTv = (TextView) view.findViewById(R.id.ydryTv);
		thryTv = (TextView) view.findViewById(R.id.thryTv);
		wdryTv = (TextView) view.findViewById(R.id.wdryTv);
		// treeViewAdapter = new ListViewAdapter(getActivity(),
		// ListViewAdapter.PaddingLeft >> 1, meeting.getStatus());
		// userListView = (ExpandableListView) view.findViewById(R.id.list);
		//
		// treeNode = treeViewAdapter.GetTreeNode();
		deptList = new ArrayList<Dept>();

		return view;
	}

	class MyGridView extends GridView {
		public MyGridView(android.content.Context context,
				android.util.AttributeSet attrs) {
			super(context, attrs);
		}

		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}

	private void initDate() {
		ydsb = new StringBuffer();
		thsb = new StringBuffer();
		wdsb = new StringBuffer();
		ydNum = 0;
		sdNum = 0;
		thNum = 0;
		ydmap.clear();
		thmap.clear();
		wdmap.clear();
		// treeNode.clear();
		for (int i = 0; i < deptList.size(); i++) {
			List ydpeopleList = new ArrayList<Login>();// 应到人员集合
			List wdpeopleList = new ArrayList<Login>();// 未到人员集合
			List thpeopleList = new ArrayList<Login>();// 替会人员集合

			for (int ii = 0; ii < deptList.get(i).getUserList().size(); ii++) {
				if (deptList.get(i).getUserList().get(ii).getLoginType()
						.equals("1")) {
					sdNum += 1;
					ydpeopleList.add(deptList.get(i).getUserList().get(ii));
				} else if (deptList.get(i).getUserList().get(ii).getLoginType()
						.equals("2")) {
					sdNum += 1;
					thNum += 1;
					thpeopleList.add(deptList.get(i).getUserList().get(ii));
				} else if (deptList.get(i).getUserList().get(ii).getLoginType()
						.equals("0")) {
					wdpeopleList.add(deptList.get(i).getUserList().get(ii));
				}

			}
			ydNum += deptList.get(i).getUserList().size();
			// treeNode.add(node);
			if (ydpeopleList.size() > 0)
				ydmap.add(ydpeopleList);
			if (wdpeopleList.size() > 0)
				wdmap.add(wdpeopleList);
			if (thpeopleList.size() > 0)
				thmap.add(thpeopleList);
		}

		for (int i = 0; i < ydmap.size(); i++) {
			for (int j = 0; j < ydmap.get(i).size(); j++) {
				if (j == 0) {
					ydsb.append(Html.fromHtml("<font color=\"#A8A8A8\">["
							+ ydmap.get(i).get(j).getDeptName() + "]</font>"));
					ydsb.append("  <font color=\"#494949\">"
							+ ydmap.get(i).get(j).getTruename() + " </font>  ");
				} else {
					ydsb.append(" |  <font color=\"#494949\">"
							+ ydmap.get(i).get(j).getTruename() + " </font>  ");
				}
			}
		}

		for (int i = 0; i < wdmap.size(); i++) {
			for (int j = 0; j < wdmap.get(i).size(); j++) {
				if (j == 0) {
					wdsb.append(Html.fromHtml("<font color=\"#A8A8A8\">["
							+ wdmap.get(i).get(j).getDeptName() + "]</font>"));
					wdsb.append("  <font color=\"#494949\">"
							+ wdmap.get(i).get(j).getTruename() + " </font>  ");
				} else {
					wdsb.append(" |  <font color=\"#494949\">"
							+ wdmap.get(i).get(j).getTruename() + " </font>  ");
				}
			}
		}

		for (int i = 0; i < thmap.size(); i++) {
			for (int j = 0; j < thmap.get(i).size(); j++) {
				if (j == 0) {
					thsb.append("<font color=\"#A8A8A8\">["
							+ thmap.get(i).get(j).getDeptName() + "]</font>");
					thsb.append(" <font color=\"#494949\">"
							+ thmap.get(i).get(j).getTruename() + "</font> ");
				} else {
					thsb.append("| <font color=\"#494949\">"
							+ thmap.get(i).get(j).getTruename() + "</font> ");
				}
			}
		}

		String sdnums = "<font color=\"#ff0000\">" + sdNum + "</font>";
		String thnums = "<font color=\"#ff0000\">" + thNum + "</font>";
		yingdao.setText("应到：" + ydNum + "人   | ");
		shidao.setText(Html.fromHtml("实到：" + sdnums + "人  | "));
		tihui.setText(Html.fromHtml("替会：" + thnums + "人  "));
		if (meeting.getStatus() == 1) {
			((View) yingdao.getParent()).setVisibility(View.GONE);
		}
		ydryTv.setText(Html.fromHtml(ydsb.toString()));
		thryTv.setText(Html.fromHtml(thsb.toString()));
		wdryTv.setText(Html.fromHtml(wdsb.toString()));
		load_progressBar.setVisibility(View.GONE);
	}

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
				result = AppDataControl.getInstance().getPeoplelist(context,
						params);
				if (null != result) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								load_progressBar.setVisibility(View.GONE);
								if (result.getState() == 0) {
									if (result.getData().size() > 0) {
										deptList = result.getData();
										db.deleteAll(Dept.class);
										db.deleteAll(Login.class);
										for (int i = 0; i < deptList.size(); i++) {
											deptList.get(i).setMeetingId(
													meeting.getId());
											try {
												db.save(deptList.get(i));
											} catch (Exception e) {
												db.update(deptList.get(i));
											}
											// 缓存人员
											for (int i1 = 0; i1 < deptList
													.get(i).getUserList()
													.size(); i1++) {
												deptList.get(i)
														.getUserList()
														.get(i1)
														.setMeetingId(
																meeting.getId());
												try {
													db.save(deptList.get(i)
															.getUserList()
															.get(i1));
												} catch (Exception e) {
													db.update(deptList.get(i)
															.getUserList()
															.get(i1));
												}
											}
										}

										initDate();
									} else {
										// no_data.setVisibility(View.VISIBLE);
									}
								} else {
									MyToast.show(context, result.getError());
								}
							} catch (Exception e) {
								System.out.println(e);
								// no_data.setVisibility(View.VISIBLE);
							} finally {

							}
						}
					});
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							deptList = db.findAllByWhere(Dept.class,
									" meetingId='" + meeting.getId()
											+ "' order by deptOrder");
							load_progressBar.setVisibility(View.GONE);
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
																	+ meeting
																			.getId()
																	+ "' order by showorder"));
								}
								initDate();

							} else {
								MyToast.show(context, "未请求到数据！");
							}
						}
					});
				}
			} finally {
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// 相当于Fragment的onResume
			if (load_progressBar != null)
				load_progressBar.setVisibility(View.VISIBLE);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("meetingId", meeting.getId());
			new UserListThread(getActivity(), params).start();
		} else {
			// 相当于Fragment的onPause
		}
	}

}
