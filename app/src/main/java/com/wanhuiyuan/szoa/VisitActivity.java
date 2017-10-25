package com.wanhuiyuan.szoa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.bean.MeetingVisit;
import com.wanhuiyuan.szoa.bean.ResultString;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.JSONHelper;

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

public class VisitActivity extends BaseActivity {

	ImageButton btn_back, imgbtn_right;// 左侧返回按钮，右侧更多按钮
	public TextView title;
	
	EditText meetingNumEdit;
	EditText nameEdit;
	EditText unitEdit;
	EditText jobEdit;
	EditText telephoneEdit;
	Button submitBtn;
	Meeting meeting;
	

	MeetingVisit visitRet = null;
	// TODO Auto-generated method stub
	String meetingNum = "";
	String name = "";
	String unit = "";
	String job = "";
	String tel = "";
	String meetingId = "";
	

	ProgressDialog loadingDialog;// 等待框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit);
		
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setMessage("获取会议信息中...");
		loadingDialog.setCancelable(false);

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		title = (TextView) findViewById(R.id.sys_title_txt);
		imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		
		btn_back.setVisibility(View.VISIBLE);
		imgbtn_right.setVisibility(View.GONE);
		title.setText("访客登录");
		btn_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		meetingNumEdit = (EditText) findViewById(R.id.meeting_num_edit);
		nameEdit = (EditText) findViewById(R.id.name_edit);
		unitEdit = (EditText) findViewById(R.id.unit_edit);
		jobEdit = (EditText) findViewById(R.id.job_edit);
		telephoneEdit = (EditText) findViewById(R.id.telephone_edit);
		
		if(false){
			meetingNumEdit.setText("1df8");
			nameEdit.setText("李立果");
			unitEdit.setText("中软");
			jobEdit.setText("android开发工程师");
			telephoneEdit.setText("13612121212");
		}
		
		submitBtn = (Button) findViewById(R.id.submit_btn);
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
	
	/**  
	 * 验证手机格式  
	 */    
	public static boolean isMobileNO(String mobiles) {    
	    /*  
			    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188  
			    联通：130、131、132、152、155、156、185、186  
			    电信：133、153、180、189、（1349卫通）  
			    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9  
			 
			------------------------------------------------ 
			13(老)号段：130、131、132、133、134、135、136、137、138、139 
			14(新)号段：145、147 
			15(新)号段：150、151、152、153、154、155、156、157、158、159 
			17(新)号段：170、171、173、175、176、177、178 
			18(3G)号段：180、181、182、183、184、185、186、187、188、189 
	    */    
	    String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。    
	    if (isEmpty(mobiles)) return false;    
	    else return mobiles.matches(telRegex);    
	}    
	
	boolean checkInput(){
		meetingNum = meetingNumEdit.getText().toString();
		name = nameEdit.getText().toString();
		unit = unitEdit.getText().toString();
		job = jobEdit.getText().toString();
		tel = telephoneEdit.getText().toString();
		
		if(isEmpty(meetingNum)){
			MyToast.show(this, "授权码不能为空");
			return false;
		}
		if(meetingNum.length() != 4){
			MyToast.show(this, "授权码必须为4位");
			return false;
		}
		if(isEmpty(name)){
			MyToast.show(this, "姓名不能为空");
			return false;
		}
		if(isEmpty(unit)){
			MyToast.show(this, "单位不能为空");
			return false;
		}
		if(isEmpty(job)){
			MyToast.show(this, "职称不能为空");
			return false;
		}
		if(isEmpty(tel)){
			MyToast.show(this, "手机号不能为空");
			return false;
		}
		if(!isMobileNO(tel)){
			MyToast.show(this, "手机号格式不正确");
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
				meetingNum = meetingNumEdit.getText().toString();
				name = nameEdit.getText().toString();
				unit = unitEdit.getText().toString();
				job = jobEdit.getText().toString();
				tel = telephoneEdit.getText().toString();
				ResultString ret = AppDataControl.getInstance().visitMeeting(VisitActivity.this, meetingNum, name, unit, job, tel);
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
	        	
	        	meetingId = visitRet.getMeetingId();
	        	MyApplication.USERID = visitRet.getPerId();
				MyApplication.USERXINGMING = name;
				MyApplication.setVistorMode(true);
				MyApplication.setAuthorizeMode(false);

	    		Map<String, Object> params1 = new HashMap<String, Object>();
	    		params1.put("persionId", MyApplication.USERID);
	    		params1.put("timestamp", "0");

	    		ListResult<Meeting> result = AppDataControl.getInstance().getMeetingList(VisitActivity.this, params1);
	    		
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
	    		
	    		
				return ret.getData();
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
	        	if(meeting == null){
        			MyToast.show(getBaseContext(), "无法获取对应会议信息");
	        		return;
	        	}
	        	
				Intent intent = null;
				intent = new Intent(VisitActivity.this,
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
}
