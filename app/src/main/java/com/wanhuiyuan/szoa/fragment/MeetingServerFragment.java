package com.wanhuiyuan.szoa.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.ServiceGridViewAdapter;
import com.wanhuiyuan.szoa.bean.CallService;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.ServiceBean;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

/**
 * 呼叫服务界面
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class MeetingServerFragment extends BaseFragment {

	Meeting meeting;
	GridView gridView;
	CallService service;// 呼叫服务内容对象
	Map<String, Object> params;
	ListResult<ServiceBean> serverResult;// 呼叫服务项查询结果集
	List<ServiceBean> serviceList;// 呼叫服务项对象List
	ServiceGridViewAdapter adapter;// 呼叫服务项适配器
	private View rootView = null;

	public MeetingServerFragment() {
	}

	public MeetingServerFragment(Meeting meeting) {
		this.meeting = meeting;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			service = new CallService();
			params = new HashMap<String, Object>();
			Map<String, Object> listParams = new HashMap<String, Object>();
			listParams.put("meetingId", meeting.getId());

			service.setMeetingId(meeting.getId());
			service.setMeetingName(meeting.getName());
			service.setUserId(MyApplication.USERID);
			service.setUserName(SharePreferences.getInstance(getActivity())
					.getRealName());
			service.setDeptName(SharePreferences.getInstance(getActivity())
					.getDeptName());
			rootView = inflater.inflate(R.layout.fragment_meeting_server,
					container, false);
			gridView = (GridView) rootView.findViewById(R.id.service_content);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// 发送呼叫服务请求
					service.setServiceContent(serviceList.get(arg2)
							.getServiceName());
					new ServiceThread().start();
				}

			});
			//获取呼叫服务项列表
			new ServiceListThread(getActivity(), listParams).start();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	private void initDate() {
		adapter = new ServiceGridViewAdapter(getActivity(), serviceList);
		gridView.setAdapter(adapter);
	}

	/**
	 * 发送呼叫服务内容。
	 * 
	 * @author Administrator
	 * 
	 */
	class ServiceThread extends Thread {

		public ServiceThread() {

		}

		@Override
		public void run() {
			params.put("meetingId", service.getMeetingId());
			try {
				params.put("meetingName",
						URLEncoder.encode(service.getMeetingName(), "UTF-8"));
				params.put("serviceContent",
						URLEncoder.encode(service.getServiceContent(), "UTF-8"));
				params.put("deptName",
						URLEncoder.encode(service.getDeptName(), "UTF-8"));
				params.put("userName",
						URLEncoder.encode(service.getUserName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			params.put("userId", service.getUserId());
			final String result = AppDataControl.getInstance().getService(
					getActivity(), params);
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (result != null) {
						if (result.contains("true")) {
							MyToast.show(getActivity(), "服务请求发送成功");
						}
					} else {
						MyToast.show(getActivity(), "服务请求发送失败");
					}
				}
			});
		}
	}

	/**
	 * 获取当前会议可用呼叫服务项
	 * 
	 * @author Administrator
	 * 
	 */
	class ServiceListThread extends Thread {
		private Context context;
		private Map<String, Object> params;

		public ServiceListThread(Context context, Map<String, Object> params) {
			this.context = context;
			this.params = params;
		}

		@Override
		public void run() {
			try {
				serverResult = AppDataControl.getInstance()
						.getServiceContentlist(context, params);
				if (null != serverResult) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							try {
								if (serverResult.getState() == 0) {
									if (serverResult.getData().size() > 0) {
										serviceList = serverResult.getData();
										initDate();
									} else {
										// no_data.setVisibility(View.VISIBLE);
									}
								} else {
									MyToast.show(context,
											serverResult.getError());
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
							MyToast.show(context, "未请求到数据！");
						}
					});
				}
			} finally {
			}
		}
	}

}
