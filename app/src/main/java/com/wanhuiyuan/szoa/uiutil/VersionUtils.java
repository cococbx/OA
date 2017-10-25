package com.wanhuiyuan.szoa.uiutil;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionUtils {

	Activity context;
	String newVerName;
	int newVerCode;

	public VersionUtils(Activity context) {
		this.context = context;
	}

	public void getServerVer(boolean isAutoCheck) {
//			String versionUrl = (String) PreferenceUitls.getParam(
//				PreferenceUitls.PREFER_URL_NAME, WSDLManager.DEF_WSDLURL)
//					+ "/wwwroot/app/n9iver.json";
//		   new XHttpUtils(context).getServiceInfo(versionUrl,isAutoCheck);
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
			"com.cec.szoa", 0).versionCode;

		} catch (NameNotFoundException e) {
		}
		return verCode;
	}
	
	public static String getVerName(Context context) {
		String verCode = "";
		try {
			verCode = context.getPackageManager().getPackageInfo(
			"com.cec.szoa", 0).versionName;

		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

}
