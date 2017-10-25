package com.wanhuiyuan.szoa;

import java.util.List;

import com.cec.szoa.R;
import com.horizon.util.encrypt.DESEDE;
import com.wanhuiyuan.szoa.adapter.LoginAdapter;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.newListResult;
import com.wanhuiyuan.szoa.uiutil.JSONHelper;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import net.tsz.afinal.FinalDb;

public class ChooseDepartmentActivity extends BaseActivity {

	ImageButton btn_back, imgbtn_right;// 左侧返回按钮，右侧更多按钮
	public TextView title;
	List<Login> loginList;
	LoginAdapter loginAdapter;
	ListView loginListView;
	String mima;
	String mimaDec;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);

		String json = getIntent().getStringExtra("json");
		mima = getIntent().getStringExtra("mima");
		mimaDec = getIntent().getStringExtra("mimaDec");

		newListResult<Login> result = (newListResult<Login>) JSONHelper.convertToObject(json,
				newListResult.class);
		loginList = result.getData();
		if (result.getData() != null) {
			loginList = (List<Login>) JSONHelper
					.convertToList(result.getData().toString(),
							Login.class);
		}

		btn_back = (ImageButton) findViewById(R.id.btn_back);
		title = (TextView) findViewById(R.id.sys_title_txt);
		imgbtn_right = (ImageButton) findViewById(R.id.imgbtn_right);
		
		btn_back.setVisibility(View.VISIBLE);
		imgbtn_right.setVisibility(View.GONE);
		title.setText("请选择登录用户");
		
		btn_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		loginListView = (ListView) findViewById(R.id.login_list);
		
		loginAdapter = new LoginAdapter(loginList, this);
		
		loginListView.setAdapter(loginAdapter);
		
		
		loginListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Login login = loginList.get(arg2);
				saveUserInfo(login);
				Intent intent = new Intent(ChooseDepartmentActivity.this,
						MeetingListActivity.class);
				startActivity(intent);
				finish();
			}
			
		});
	}


	
	void saveUserInfo(Login login){
		MyApplication.USERID = login.getId();
		MyApplication.USERXINGMING = login.getTruename();
		MyApplication.setVistorMode(false);
		MyApplication.setAuthorizeMode(true);
		saveInformation(login);
	}
	

	public void saveInformation(Login login) {
		SharePreferences sharePreferences = SharePreferences.getInstance(this);
		sharePreferences.putRealName(login.getTruename());
		sharePreferences.putDeptId(login.getDeptId());
		sharePreferences.putDeptName(login.getDeptName());
		sharePreferences.putUserName(login.getTruename());
		sharePreferences.putUserId(login.getId());
		sharePreferences.putPassword(mima);

		// 用户身份验证本地化
		OffLineUser offlineuser = new OffLineUser();
		offlineuser.setId(login.getId());
		offlineuser.setPassword(DESEDE.encryptIt(mima.toUpperCase()));
		offlineuser.setUsername(login.getTruename());
		offlineuser.setXingming(login.getUsername());
		FinalDb db = FinalDb.create(this, "szoa.db");
		try {
			db.save(offlineuser);
		} catch (Exception e) {
			db.update(offlineuser);
		}
	}
}
