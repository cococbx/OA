package com.wanhuiyuan.szoa.service;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.wanhuiyuan.szoa.LoginActivity;

public class UITimeReceiver extends BroadcastReceiver {
	public static final String TimeAction = "com.wanhuiyuan.szoa.service.timeReceiver";
	private TimeBackCall backCall;
	Context context;

	public UITimeReceiver() {

	}

	public UITimeReceiver(Context context, TimeBackCall backCall) {
		this.context = context;
		this.backCall = backCall;
	}

	public static UITimeReceiver newInstance(Context context,
			TimeBackCall backCall) {
		UITimeReceiver timeReceiver = new UITimeReceiver(context, backCall);
		return timeReceiver;
	}

	/* 注册 */
	public void registerReceiver() {
//		if (isWorked("UITimeReceiver")) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(TimeAction);
			context.registerReceiver(this, filter);
//		}
	}

	/* 取消注册 */
	public void unregisterReceiver() {
		context.unregisterReceiver(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		backCall.refushTime(context, intent);
		// 此处实现不够优雅，为了在UITimeReceiver中使用DynamicUIActivity中的TextView组件time，而将其设置为public类型，
		// 更好的实现是将UITimeReceiver作为DynamicUIActivity的内部类
	}

	public static interface TimeBackCall {
		public void refushTime(Context context, Intent intent);
	}

	private boolean isWorked(String className) {
		ActivityManager myManager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals(className)) {
				return true;
			}
		}
		return false;
	}
}
