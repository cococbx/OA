package com.wanhuiyuan.szoa.uiutil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.ApkFile;
import com.wanhuiyuan.szoa.bean.Bookmark;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.MeetPerson;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingPersonLabel;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.OffLineYiti;
import com.wanhuiyuan.szoa.bean.Result;
import com.wanhuiyuan.szoa.bean.ResultString;
import com.wanhuiyuan.szoa.bean.ServiceBean;
import com.wanhuiyuan.szoa.bean.Version;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.bean.newListResult;
import com.wanhuiyuan.szoa.dialog.ProgressDialog;

public class AppDataControl {

	public static int errorCode = -1;
	public static String errorText = "未知错误";
	private static AppDataControl instance;

	// 单例模式获取唯一的MyApplication实例
	public static AppDataControl getInstance() {
		if (null == instance) {
			instance = new AppDataControl();
		}
		return instance;
	}

	/**
	 * 根据url地址获取xml内容，并返回解析的结果
	 * 
	 */
	public static String getRemoteObject(Context context, String url,
			Map<String, Object> params) throws Exception {
		if (!NetWorkUtil.isNetworkAvailable(context)) {
			errorText = "请检查网络";
			return null;
		}
		String retJson = ConnectionUtil.doPost(context, url, params);
		return retJson;

	}

	public static String getRemoteObject(Context context,
			Map<String, Object> params) throws Exception {
		if (!NetWorkUtil.isNetworkAvailable(context)) {
			errorText = "请检查网络";
			return null;
		}
		String retJson = ConnectionUtil.doPost(context, "", params);
		return retJson;
	}

	public static String getRemoteObject(Context context, String url)
			throws Exception {
		if (!NetWorkUtil.isNetworkAvailable(context)) {
			errorText = "请检查网络";
			return null;
		}
		String retJson = ConnectionUtil.doPost(context, url);
		return retJson;
	}

	public Result<Login> login(Context context, Map<String, Object> params) {
		Result<Login> result = null;
		try {
			// String json = AppDataControl.getRemoteObject(context,
			// "/service.do?action=login", params);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=loginBeforeList", params);
			if (json == null)
				return result;
			result = (Result<Login>) JSONHelper.convertToObject(json,
					Result.class);
			if (result.getData() != null) {
				Login resultData = (Login) JSONHelper.convertToObject(
						new JSONObject(json).getString("data"), Login.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ProgressDialog.dismiss();
		}
		return result;
	}

	public newListResult<Login> loginByRealName(Context context, String name, String password, String deptId, StringBuffer saveJson) {
		newListResult<Login> result = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("password", password);
		params.put("deptId", deptId);
		try {
			// String json = AppDataControl.getRemoteObject(context,
			// "/service.do?action=login", params);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=newloginBeforeList", params);
			if (json == null)
				return result;
			result = (newListResult<Login>) JSONHelper.convertToObject(json,
					newListResult.class);
			saveJson.append(json);
			if (result.getData() != null) {
				List<Login> resultData = (List<Login>) JSONHelper
						.convertToList(result.getData().toString(),
								Login.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ProgressDialog.dismiss();
		}
		return result;
	}

	/**
	 * 获取所有人员信息
	 * 
	 * @param context
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListResult<OffLineUser> getOffLineUserList(Context context,
			Map<String, Object> params) {
		ListResult<OffLineUser> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getAllUserList", params);
			if (json == null)
				return result;
			result = (ListResult<OffLineUser>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<OffLineUser> resultData = (List<OffLineUser>) JSONHelper
						.convertToList(result.getData().toString(),
								OffLineUser.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取会议和参会人对应关系
	 * 
	 * @param context
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListResult<Meeting> getOffLineUserSelectList(Context context,
			Map<String, Object> params) {
		ListResult<Meeting> result = new ListResult<Meeting>();
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getPersonMeetingRel", params);
			if (json == null)
				return result;
			result = (ListResult<Meeting>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<Meeting> resultData = (List<Meeting>) JSONHelper
						.convertToList(result.getData().toString(),
								Meeting.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取人员和议题的对应关系
	 * 
	 * @param context
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ListResult<OffLineYiti> getOffLineYitiList(Context context,
			Map<String, Object> params) {
		ListResult<OffLineYiti> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getYitiPeopleRel", params);
			if (json == null)
				return result;
			result = (ListResult<OffLineYiti>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<OffLineYiti> resultData = (List<OffLineYiti>) JSONHelper
						.convertToList(result.getData().toString(),
								OffLineYiti.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 根据会议id获取人员与议题的对应关系--暂未使用
	 */
	@SuppressWarnings("unchecked")
	public ListResult<OffLineYiti> getOffLineYitiListByMeetingId(
			Context context, Map<String, Object> params) {
		ListResult<OffLineYiti> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getYitiPeopleRelByMeetingId", params);
			if (json == null)
				return result;
			result = (ListResult<OffLineYiti>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<OffLineYiti> resultData = (List<OffLineYiti>) JSONHelper
						.convertToList(result.getData().toString(),
								OffLineYiti.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 材料分发、会议开始状态会议列表
	 */
	@SuppressWarnings("unchecked")
	public ListResult<Meeting> getMeetingList(Context context,
			Map<String, Object> params) {
		ListResult<Meeting> result = null;
		try {
			// String json = AppDataControl.getRemoteObject(context,
			// "/service.do?action=getAllMeetingList", params);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=meetingListForPerson", params);
			if (json == null)
				return result;
			result = (ListResult<Meeting>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<Meeting> resultData = (List<Meeting>) JSONHelper
						.convertToList(result.getData().toString(),
								Meeting.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 历史会议列表
	 */
	@SuppressWarnings("unchecked")
	public ListResult<Meeting> getPastMeetingList(Context context,
			Map<String, Object> params) {
		ListResult<Meeting> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getPastMeetingList", params);
			if (json == null)
				return result;
			result = (ListResult<Meeting>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<Meeting> resultData = (List<Meeting>) JSONHelper
						.convertToList(result.getData().toString(),
								Meeting.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取参会人员信息（包含出席情况）
	 */
	@SuppressWarnings("unchecked")
	public ListResult<Dept> getPeoplelist(Context context,
			Map<String, Object> params) {
		ListResult<Dept> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getPeoplelist", params);
			if (json == null)
				return result;
			result = (ListResult<Dept>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<Dept> resultData = (List<Dept>) JSONHelper.convertToList(
						result.getData().toString(), Dept.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取参会人员列表新接口
	 */
	public ListResult<MeetingPersonLabel> getMeetingPeopleList(Context context,String meetingId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("meetingId", meetingId);
		ListResult<MeetingPersonLabel> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=ttt", params);
			if (json == null)
				return result;
			result = (ListResult<MeetingPersonLabel>) 
					JSONHelper.convertToObject(json,ListResult.class);
			if (result.getData() != null) {
				List<MeetingPersonLabel> resultData = (List<MeetingPersonLabel>) 
						JSONHelper.convertToList(
						result.getData().toString(), MeetingPersonLabel.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取某个会议的参会人员列表
	 */
	public ListResult<Dept> geDeptAndUserList(Context context,
			Map<String, Object> params) {
		ListResult<Dept> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getParticipants", params);
			if (json == null)
				return result;
			result = (ListResult<Dept>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<Dept> resultData = (List<Dept>) JSONHelper.convertToList(
						result.getData().toString(), Dept.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取某个会议的参会人员列表
	 */
	@SuppressWarnings("unchecked")
	public ListResult<MeetPerson> getMeetPer(Context context,
			Map<String, Object> params) {
		ListResult<MeetPerson> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getMeetPerByMeeIdPerId", params);
			if (json == null)
				return result;
			result = (ListResult<MeetPerson>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<MeetPerson> resultData = (List<MeetPerson>) JSONHelper
						.convertToList(result.getData().toString(),
								MeetPerson.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取当前会议的议题列表
	 */
	public ListResult<MeetingYiti> getMettingSubjectlist(Context context,
			Map<String, Object> params) {
		ListResult<MeetingYiti> result = null;
		try {
			// String json = AppDataControl.getRemoteObject(context,
			// "/service.do?action=getMettingSubjectlist", params);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getIssueList", params);
			if (json == null)
				return result;
			result = (ListResult<MeetingYiti>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<MeetingYiti> resultData = (List<MeetingYiti>) JSONHelper
						.convertToList(result.getData().toString(),
								MeetingYiti.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 保存书签信息--已经不用
	 */
	public Result<String> saveBookMark(Context context,
			Map<String, Object> params) {
		Result<String> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=saveBookMark", params);
			if (json == null)
				return result;
			result = (Result<String>) JSONHelper.convertToObject(json,
					String.class);
			if (result.getData() != null) {
				String resultData = (String) JSONHelper.convertToObject(
						new JSONObject(json).getString("data"), String.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			// ProgressDialog.dismiss();
		}
		return result;
	}

	/*
	 * 获取书签信息--已经不用
	 */
	public Result<Bookmark> getBookMark(Context context,
			Map<String, Object> params) {
		Result<Bookmark> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getBookMark", params);
			if (json == null)
				return result;
			result = (Result<Bookmark>) JSONHelper.convertToObject(json,
					Result.class);
			if (result.getData() != null) {
				Bookmark resultData = (Bookmark) JSONHelper.convertToObject(
						new JSONObject(json).getString("data"), Bookmark.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// ProgressDialog.dismiss();
		}
		return result;
	}

	/*
	 * 获取服务器版本号
	 */
	public Result<Version> getVersion(Context context,
			Map<String, Object> params) {
		Result<Version> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getVerCode", params);
			if (json == null)
				return result;
			result = (Result<Version>) JSONHelper.convertToObject(json,
					Result.class);
			// if (result.getData() != null) {
			// Version resultData = (Version) JSONHelper.convertToObject(
			// result.getData().toString(), Version.class);
			// result.setData(resultData);
			// }
			if (result.getData() != null) {
				Version resultData = (Version) JSONHelper.convertToObject(
						new JSONObject(json).getString("data"), Version.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 获取文件信息，这是之前使用的获取字节流文件。--已经不用
	 */
	public String getFileMessage(Context context, Map<String, Object> params) {
		String returnStr = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getFileMessage", params);
			if (json == null)
				return returnStr;
			Result resultBena = JSON.parseObject(json, Result.class);
			returnStr = resultBena.getData().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	/*
	 * 获取所有下载文件
	 */
	@SuppressWarnings("unchecked")
	public ListResult<MeetingYiti> getAllFilelist(Context context,
			Map<String, Object> params) {
		ListResult<MeetingYiti> result = null;
		try {
			// String json = AppDataControl.getRemoteObject(context,
			// "/service.do?action=getMettingSubjectlistOffLine", params);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getMettingSubjectlistByPerId", params);
			if (json == null)
				return result;
			result = (ListResult<MeetingYiti>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<MeetingYiti> resultData = (List<MeetingYiti>) JSONHelper
						.convertToList(result.getData().toString(),
								MeetingYiti.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 发送呼叫服务
	 */
	public String getService(Context context, Map<String, Object> params) {
		String returnStr = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getService", params);
			if (json == null)
				return returnStr;
			returnStr = json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	/*
	 * 获取呼叫服务项列表
	 */
	public ListResult<ServiceBean> getServiceContentlist(Context context,
			Map<String, Object> params) {
		ListResult<ServiceBean> result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getServiceType", params);
			if (json == null)
				return result;
			result = (ListResult<ServiceBean>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<ServiceBean> resultData = (List<ServiceBean>) JSONHelper
						.convertToList(result.getData().toString(),
								ServiceBean.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 更改参会状态
	 */
	public ResultString attendMeeting(Context context,
			Map<String, Object> params) {

		ResultString result = null;
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=attendMeeting", params);
			if (json == null)
				return result;

			result = (ResultString) JSONHelper.convertToObject(json,
					ResultString.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	static final public int LOG_DOWN_START = 0;
	static final public int LOG_DOWN_FINISH = 1;
	static final public int LOG_DOWN_MD5_SUCCESS = 2;
	static final public int LOG_DOWN_MD5_FAILED = 3;

	static final public String LOG_FORMAT = "{\"fileId\":\"%s\",\"meetingId\":\"%s\",\"issueId\":\"%s\",\"userId\":\"%s\",\"deviceId\":\"%s\","
			+ "\"startTime\":\"%s\",\"operateTime\":\"%s\",\"content\":\"%s\",\"logType\":\"%d\"}";
	


	/*
	 * 发送日志
	 */
	public ResultString sendLogRun(Context context,
			String fileId, String meetingId, String issueId,String userId, String deviceId,
			String startTime, String operateTime, String content, int logType) {

		ResultString result = null;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		String requestJson = String.format(LOG_FORMAT, 
				fileId, meetingId, issueId, userId, deviceId,
				startTime, operateTime, content, logType);
 		params.put("json", requestJson);
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=appLogging", params);
			if (json == null)
				return result;

			result = (ResultString) JSONHelper.convertToObject(json,
					ResultString.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	// 线程池
	public static ExecutorService sExecutorService = Executors
			.newFixedThreadPool(2);
	
	class LogThread extends Thread {
		private Context context;
		private String fileId;
		private String meetingId;
		private String issueId;
		private String startTime;
		private String content;
		private int logType;
		
		public LogThread(Context context,
				String fileId, String meetingId, String issueId,
				String startTime, String content, int logType){
			this.context = context;
			this.fileId = fileId;
			this.meetingId = meetingId;
			this.issueId = issueId;
			this.startTime = startTime;
			this.content = content;
			this.logType = logType;
		}
		
		@SuppressWarnings("resource")
		@Override
		public void run() {
				// TODO Auto-generated method stub
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间 
			String dateStr = formatter.format(curDate); 
			WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
			String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
			sendLogRun(context, fileId, meetingId, issueId, MyApplication.USERID, 
					m_szWLANMAC, startTime, dateStr, content, logType);
		}
	}
	
	public void sendDownStartLog(Context context){
		LogThread logThread = new LogThread(context, "", "", "", "", "", LOG_DOWN_START);
		sExecutorService.execute(logThread);
	}
	
	public void sendDownFinishLog(Context context){
		LogThread logThread = new LogThread(context, "", "", "", "", "", LOG_DOWN_FINISH);
		sExecutorService.execute(logThread);
	}
	
	public void sendDownSuccessLog(Context context, String fileId, String meetingId,String yitiId, String startTime){
		LogThread logThread = 
				new LogThread(context, fileId, meetingId, yitiId, startTime, "", LOG_DOWN_MD5_SUCCESS);
		sExecutorService.execute(logThread);
	}
	
	public void sendDownFailedLog(Context context, String fileId, String meetingId, String yitiId, String startTime){
		Log.e("", "AppDataControl:sendDownFailedLog");
		LogThread logThread = 
				new LogThread(context, fileId, meetingId, yitiId, startTime, "", LOG_DOWN_MD5_FAILED);
		sExecutorService.execute(logThread);
	}
	


	static final public String VISIT_FORMAT = "{\"name\":\"%s\",\"unit\":\"%s\",\"job\":\"%s\""
			+ ",\"tel\":\"%s\",\"licesenKey\":\"%s\",\"id\":\"%s\"}";

	/*
	 * 访客登录模式
	 */
	public ResultString visitMeeting(Context context,
			String meetingNum, String name, String unit, String job, String tel) {

		ResultString result = null;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		String requestJson = String.format(VISIT_FORMAT, 
				name, unit, job, tel, meetingNum, "");
 		params.put("json", requestJson);
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=visitorLogin", params);
			if (json == null)
				return result;

			result = (ResultString) JSONHelper.convertToObject(json,
					ResultString.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	/*
	 * 授权登录模式
	 */
	public ResultString authorizeMeeting(Context context,
			String user, String pass, String meetNum) {

		ResultString result = null;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("operAccount", user);
		params.put("operPassword", pass);
		params.put("key", meetNum);
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=checkOperator", params);
			if (json == null)
				return result;

			result = (ResultString) JSONHelper.convertToObject(json,
					ResultString.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	/*
	 * 授权日志接口
	 */
	public ResultString authorizeLog(Context context,
			String user, String authorize, String meetId) {

		ResultString result = null;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", user);
		params.put("operId", authorize);
		params.put("meetId", meetId);
		try {
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=insertOperatorLog", params);
			if (json == null)
				return result;

			result = (ResultString) JSONHelper.convertToObject(json,
					ResultString.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	

	/*
	 * 获取所有下载文件
	 */
	@SuppressWarnings("unchecked")
	public ListResult<ApkFile> getApkFile(Context context, String fileId) {
		ListResult<ApkFile> result = null;
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("fileId", fileId);
			String json = AppDataControl.getRemoteObject(context,
					"/service.do?action=getFileMessage2", params);
			if (json == null)
				return result;
			result = (ListResult<ApkFile>) JSONHelper.convertToObject(json,
					ListResult.class);
			if (result.getData() != null) {
				List<ApkFile> resultData = (List<ApkFile>) JSONHelper
						.convertToList(result.getData().toString(),
								ApkFile.class);
				result.setData(resultData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
