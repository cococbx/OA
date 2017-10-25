package com.wanhuiyuan.szoa.uiutil;


import android.content.Context;
import android.content.Intent;

public class ServiceUtil {
	public static void onStartService(Context context,Class<?> cls) {
		Intent intent=new Intent(context,cls);
		context.startService(intent);
	}
	public static void onStopService(Context context,Class<?> cls) {
		Intent intent=new Intent(context,cls);
		if (intent!=null) {
			context.stopService(intent);
		}
	}
}
