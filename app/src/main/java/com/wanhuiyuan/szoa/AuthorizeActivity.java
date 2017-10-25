package com.wanhuiyuan.szoa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cec.szoa.R;
import com.horizon.util.encrypt.DESEDE;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingVisit;
import com.wanhuiyuan.szoa.bean.ResultString;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.JSONHelper;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AuthorizeActivity extends BaseActivity {

	ImageButton btn_back, imgbtn_right;// 左侧返回按钮，右侧更多按钮
	public TextView title;
	
	EditText userEdit;
	EditText passEdit;
	EditText meetingNumEdit;
	
	Button submitBtn;
	
	String user = "";
	String pass = "";
	String meetNum = "";
	
	String meetingId = "";
	
	MeetingVisit visitRet = null;

	ProgressDialog loadingDialog;// 等待框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorize);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage("登陆中...");
		loadingDialog.setCancelable(false);

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		title = (TextView) findViewById(R.id.sys_title_txt);
		imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);

		userEdit = (EditText) findViewById(R.id.user_edit);
		passEdit = (EditText) findViewById(R.id.pass_edit);
		meetingNumEdit = (EditText) findViewById(R.id.meeting_num_edit);
		
		String userName = SharePreferences.getInstance(getApplicationContext()).getAuthName();
		String meetingCode = SharePreferences.getInstance(getApplicationContext()).getMeetingCode();
		userEdit.setText(userName);
		meetingNumEdit.setText(meetingCode);
		
		submitBtn = (Button) findViewById(R.id.submit_btn);
		
		if(false){
			userEdit.setText("suzhongzhu");
			passEdit.setText("qwer1234");
			meetingNumEdit.setText("1df8");
		}
		
		btn_back.setVisibility(View.VISIBLE);
		imgbtn_right.setVisibility(View.GONE);
		title.setText("授权登录");
		btn_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		submitBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkInput()){
					submitMeetingInfo();
				}
			}
			
		});
	}
	
	static boolean isEmpty(String s){
		if(s == null){
			return true;
		}
		if(s.equals("")){
			return true;
		}
		return false;
	}
	
	boolean checkInput(){
		meetNum = meetingNumEdit.getText().toString();
		user = userEdit.getText().toString();
		pass = passEdit.getText().toString();
		
		if(isEmpty(meetNum)){
			MyToast.show(this, "授权码不能为空");
			return false;
		}
		if(meetNum.length() != 4){
			MyToast.show(this, "授权码必须为4位");
			return false;
		}
		if(isEmpty(user)){
			MyToast.show(this, "用户名不能为空");
			return false;
		}
		if(isEmpty(pass)){
			MyToast.show(this, "密码不能为空");
			return false;
		}
		return true;
	}

	
	void submitMeetingInfo(){
		loadingDialog.show();
		new AsyncTask<String, Integer, String>(){
			
			String errMsg = "";

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				user = userEdit.getText().toString();
				pass = passEdit.getText().toString();
				meetNum = meetingNumEdit.getText().toString();
				SharePreferences.getInstance(getApplicationContext()).putAuthorizeInfo(user, meetNum);
				String decodePass = DESEDE.encryptIt(pass).toUpperCase();
				ResultString ret = AppDataControl.getInstance().authorizeMeeting(AuthorizeActivity.this, user, decodePass, meetNum);
				if(ret == null)
				{
					return "";
				}
	        	if(ret.getData() == null){
	        		errMsg = ret.getErrMsg();
	        		//MyToast.show(getBaseContext(), ret.getErrMsg());
	        		return "";
	        	}
	        	if(ret.getData().equalsIgnoreCase("")){
	        		return "";
	        	}
	        	visitRet = (MeetingVisit) JSONHelper.convertToObject(ret.getData(),MeetingVisit.class);
				if(visitRet == null)
				{
					return "";
				}
	        	if(visitRet.getPerId().equals("") || visitRet.getMeetingId().equals("") ){
	        		errMsg = "返回信息错误";
	        		return "";
	        	}
	        	MyApplication.AUTHORIZE_USERID = visitRet.getPerId();
	        	MyApplication.setAuthorizeMode(true);
	        	meetingId = visitRet.getMeetingId();
	        	
				return meetingId;
			}
			


	        @Override
	        protected void onPostExecute(String ret) {
	        	loadingDialog.dismiss();
	        	if(ret == null){
	        		return;
	        	}
	        	if(ret.equals("")){
	        		if(errMsg != null && !errMsg.equals("")){
	        			MyToast.show(getBaseContext(), errMsg);
	        		}else{
	        			MyToast.show(getBaseContext(), "获取会议信息失败");
	        		}
	        		return;
	        	}
	        	if(isEmpty(meetingId)){
        			MyToast.show(getBaseContext(), "无法获取对应会议信息");
	        		return;
	        	}
				Intent intent = new Intent(AuthorizeActivity.this,
						SelectUserActivity.class);
				intent.putExtra("meetingId", meetingId);
				startActivityForResult(intent, 10);
				finish();
	        }
			
		}.execute();
	}
}
;