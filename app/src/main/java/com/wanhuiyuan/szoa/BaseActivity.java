package com.wanhuiyuan.szoa;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.uiutil.NetWorkState;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;



public class BaseActivity extends Activity{
	protected OnHeadClickListener headClickListener;
//	protected OnErrorListener onErrorListener;
	public Button  rightBtn;//最右侧按钮
	ImageButton secondRightBtn;//倒数第二个按钮。
	ImageButton leftBtn;//左侧返回按钮
	public TextView title , hint;//标题、网络状态文本框
	private Drawable rightDrawable , secondDrawable;
	protected RelativeLayout headLayout;//顶部布局
	private LinearLayout contentLayout;//内容布局
	private ImageView loadingError;//加载失败提示
	Handler handler = new Handler();
	protected SharePreferences sharePreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		IntentFilter filter = new IntentFilter();
		setContentView(R.layout.base);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);//注册网络监听
		init();
		headInit();
		
	}
	//显示屏幕中间布局
	protected void showContentView(boolean visible) {
		if(visible) {
			contentLayout.setVisibility(View.VISIBLE);
		} else {
			contentLayout.setVisibility(View.GONE);
		}
	}
	
	//添加内容到屏幕中间
	public void addContentView(int resource) {
		contentLayout = (LinearLayout) findViewById(R.id.content_layout);
		contentLayout.setBackgroundResource(R.color.layout_background);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(resource, null);
		contentLayout.addView(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}
	
	protected void setContentBackgroundResource(int resid) {
		//设置背景
		contentLayout.setBackgroundResource(resid);
	}
	
	//网络状态监听
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		State wifiState = null;
		State mobileState = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiState = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			try {
				mobileState = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
						.getState();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED == mobileState) {
				hint.setVisibility(View.GONE);
				NetWorkState.NETWORK_STATE = NetWorkState.MOBILE_STATE;
				
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED == wifiState
					&& State.CONNECTED != mobileState) {
				hint.setVisibility(View.GONE);
				NetWorkState.NETWORK_STATE = NetWorkState.WIFI_STATE;
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED != mobileState) {
				hint.setVisibility(View.VISIBLE);
				NetWorkState.NETWORK_STATE = NetWorkState.NO_STATE;
			}
		}

	};
	
	private void init()  {
		sharePreferences = SharePreferences.getInstance(this);
		hint = (TextView) findViewById(R.id.hint);
		hint.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_14));
		loadingError = (ImageView) findViewById(R.id.loading_error);
//		loadingError.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(NetWorkState.NETWORK_STATE != NetWorkState.NO_STATE) {
//					if(null != onErrorListener) {
//						onErrorListener.reLoad();
//						showContentView(true);
//					}
//				}
//			}
//		});
	}
	
	//设置最右边按钮图标
	public void setRightDrawable(Drawable rightDrawable) {
		rightBtn.setBackgroundDrawable(rightDrawable);
		rightBtn.setPadding(0, 0, 10, 0);
		if(null != rightDrawable) 
			setRightBtnVisible(true);
	}
	
	//获取最右边按钮图标
	public Drawable getRightDrawable() throws Exception {
		if(null != rightDrawable)
			return rightDrawable;
		else
			throw new Exception("请设置右边第一张的背景图片");
	}
	
	//设置最右边图标和文字
	public void setRightDrawableAndText(Drawable rightDrawable, String text) {
		this.rightDrawable = rightDrawable;
		try {
//			rightBtn.setBackgroundResource(R.drawable.back_selector);
			rightBtn.setBackgroundResource(R.color.transparent);
			rightBtn.setPadding(-5,0,0,0);
			rightBtn.setText(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null != rightDrawable) 
			setRightBtnVisible(true);
	}
	//设置左边文本框内容
	public void setLeftText(String content) {
		
//		leftBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, environmentSettings.getFontSize(EnvironmentSettings.FONT_SIZE_16));
	}
	
	//设置最右侧文本框内容
	public void setRightText(String content) {
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText(content);
		rightBtn.setPadding(10, 0, 0, 0);
		rightBtn.setBackgroundColor(00000000);
		rightBtn.setTextColor(getResources().getColor(R.color.white));
//		rightBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, environmentSettings.getFontSize(EnvironmentSettings.FONT_SIZE_16));
	}

	//获取右边倒数第二个图片
	public Drawable getSecondDrawable() throws Exception{
		if(null != secondDrawable)
			return secondDrawable;
		else
			throw new Exception("请设置右边第二张的背景图片");
	}

	//设置右边倒数第二个按钮图片
	public void setSecondDrawable(Drawable secondDrawable) throws Exception {
		this.secondDrawable = secondDrawable;
		showSecondRightBtn();
	}
	
	
	public void headInit() {
		//左边返回按钮
		leftBtn = (ImageButton) findViewById(R.id.head_back);
		//顶部布局
		headLayout = (RelativeLayout) findViewById(R.id.head_layout);
		//顶部布局参数
		LayoutParams headParams = (LayoutParams) headLayout.getLayoutParams();
		headParams.height = (int) this.getResources().getDimension(R.dimen.head_height);
		//标题
		title = (TextView) findViewById(R.id.head_title);
//		title.setTextSize(TypedValue.COMPLEX_UNIT_PX, environmentSettings.getFontSize(EnvironmentSettings.FONT_SIZE_24));
		//最右边按钮
		rightBtn = (Button) findViewById(R.id.head_other);
//		rightBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, environmentSettings.getFontSize(EnvironmentSettings.FONT_SIZE_24));
		//右边倒数第二个按钮
		secondRightBtn = (ImageButton) findViewById(R.id.imgbtn_right);
		leftBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				headClickListener.leftClick();
			}
		});
		rightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				headClickListener.rightClick();
			}
		});
//		secondRightBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				headClickListener.secondRightClick();
//			}
//		});
		
//		setLeftText("返回");
	}
	
	//隐藏最右边按钮
	public void hiddenRightBtn(){
		secondRightBtn.setVisibility(View.GONE);
	}
	//修改左边按钮图标
	public void changeLeftDrawable(Drawable leftDrawable) {
		if(null != leftDrawable) {
			leftBtn.setBackgroundDrawable(leftDrawable);
		}
	}
	
	//修改右边倒数第二个按钮图标
	public void changeSecondDrawable(Drawable secondDrawable) {
		this.secondDrawable = secondDrawable;
		if(null != secondDrawable) {
			secondRightBtn.setBackgroundDrawable(secondDrawable);
		}
	}
	
	//显示右边倒数第二个按钮
	public void showSecondRightBtn() {
		try {
			secondRightBtn.setBackgroundDrawable(getSecondDrawable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//设置标题
	public void setTitle(String titleName) {
		title.setText(titleName);
	}
	
	//获取标题内容
	public String getTitleText() {
		return title.getText().toString();
	}
	
	//获取中间标题文本框
	public TextView getTitleView() {
		return title;
	}
	//隐藏返回按钮
	public void goneHeadBack() {
		leftBtn.setVisibility(View.GONE);
	}
	//设置顶部背景
	public void setHeadBackground(int color) {
		headLayout.setBackgroundResource(color);
	}
	
	//设置顶部背景
	public void setHeadBackgroundDrawable(Drawable drawable) {
		headLayout.setBackgroundDrawable(drawable);
	}
	
	//注册顶部按钮监听事件
	protected interface OnHeadClickListener{
		void leftClick();
		void rightClick();
		void secondRightClick();
	}
	
	
	//控制最右边按钮是否隐藏
	public void setRightBtnVisible(boolean visible) {
		if(visible) {
			rightBtn.setVisibility(View.VISIBLE);
		} else {
			rightBtn.setVisibility(View.GONE);
		}
	}
	
	//控制右边倒数第二个按钮是否隐藏
	public void setSecondRightBtnVisible(boolean visible){
		if(visible) {
			secondRightBtn.setVisibility(View.VISIBLE);
			showSecondRightBtn();
		} else {
			secondRightBtn.setVisibility(View.GONE);
		}
	}
	
	//控制左边按钮是否隐藏
	public void setLeftBtnVisible(boolean visible) {
		if(visible) {
			leftBtn.setVisibility(View.VISIBLE);
		} else {
			leftBtn.setVisibility(View.GONE);
		}
	}
	
	//没有网络
	public boolean isNoNetWork() {
		if(NetWorkState.NETWORK_STATE == NetWorkState.NO_STATE) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消监听
		unregisterReceiver(receiver);
	}
	
}
