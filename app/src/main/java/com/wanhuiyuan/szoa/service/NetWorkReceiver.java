package com.wanhuiyuan.szoa.service;

import com.wanhuiyuan.szoa.MyApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import de.greenrobot.event.EventBus;

public class NetWorkReceiver extends BroadcastReceiver {
	Intent regIntent ;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// Toast.makeText(context, intent.getAction(), 1).show();
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		regIntent = new Intent(context, DownloadService.class);
			
		// Toast.makeText(context,
		// "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
		// +"\n"+"active:"+activeInfo.getTypeName(), 1).show();
		if (activeInfo != null) {
			EventBus.getDefault().post("connect");
			if (MyApplication.downedFilesNum == MyApplication.downFilesNum) {
				context.startService(regIntent);
			}
		}else{
//			context.stopService(regIntent);
		}
	} // 如果无网络连接activeInfo为null

}
