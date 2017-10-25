package com.wanhuiyuan.szoa;

import java.util.List;

import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.uiutil.CrashHandler;

import android.app.Application;

public class MyApplication extends Application {
	public static int downFilesNum = 0;
	public static int downedFilesNum = 0;
	public static String SERVICE_HOST = "";
	public static String USERID = "";
	public static String USERXINGMING = "";
	public static String AUTHORIZE_USERID = "";
	public static String DOWNLOAD_PATH;
	public static boolean firstLoad = true;
	private static boolean VISITOR_MODE = false;
	private static boolean AUTHORIZE_MODE = false;
	//public static String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU3CQHU1ScyOebPLnpsDlQDzCjYgmFZo8sqFMkNKOgywo7x6aD2yiupr6ji7hzsE6/QqGcC+eseQV1yrWJ/1FwxLD4Y1YsZxXwh2w5W4lqa1RyVWEbHWAH22+t7LdPt+jENUp0yRGw03l8UY1ryrlWCQGj7ISWDgc3YLh0bz4sFvgWSgCNRP4FpYjl8hG/IVrYXKlEsCbCtDWPSC8ObydILqOW8fXpxdRHfEuWC1PB9ruQ=";
	public static String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLtTrj0K80eOa5SMscsukOl12Sj5OOgBIgJCAkNHOnxCI7FSulUdZrj1H3dmxTjkOaZd/7cf8nWq9BfLfl9E/wHb4rmL5kBOT7/bZE+tnPAPV093zJozRgNMZE3uwXKH12fP4sy4WL+ectYC0YJxAtkWF7KKeL4hJgLscmq65tlNT0toBg5rHR6dlWy3Onm3DIgkQLXJLGBuoP95zYF78d6Gg0R7Ceqbeo//nf1/XvPu/f3nVsMaX7nUVY3ymxGwb/EJivFSQD3PkXo2wvBkMqubSdtBMs6TpEWUs6Ao7HOXaOlfoc7z8DXi3sINF5oizRYDzqGYvyTde304FFjVDRU6BI6KYwFlcBYnKg15z7MpUzIAzd94GXLYwXQQyDXnjvb44+oWA6yuS47Uqtqvl76Q==";

	//是否调试状态
	public static boolean DEBUG = false;
	public static boolean isGetDownFileMes = false;
	//是否定时重启下载线程
	public static boolean RESTART_DOWNLOAD = false;
	
	@Override
	public void onCreate() {
		DOWNLOAD_PATH = getFilesDir().getPath();
		
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
	}
	
	public static void setVistorMode(boolean isVisit){
		VISITOR_MODE = isVisit;
	}
	
	public static void setAuthorizeMode(boolean isAuthorize){
		AUTHORIZE_MODE = isAuthorize;
	}
	
	public static boolean isOtherLoginMode(){
		return VISITOR_MODE || AUTHORIZE_MODE;
	}
	
	public static boolean isAuthorize(){
		return AUTHORIZE_MODE;
	}
	
	public static boolean isVisit(){
		return AUTHORIZE_MODE;
	}
}
