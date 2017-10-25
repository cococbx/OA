package com.wanhuiyuan.szoa.uiutil;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * xml形式的文件存储
 * @author xiaoxiao
 *
 */
public class SharePreferences {
	private static final String PREFERENCE_NAME = "createRichFactory"; //文件名
	private static SharedPreferences sharedPreferences;
	private static SharePreferences createRichSharePreferences;
	private SharePreferences(Context context){
		if(sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE); 
		}
	}
	
	public static SharePreferences getInstance(Context context) {
		if(null == createRichSharePreferences) {
			createRichSharePreferences = new SharePreferences(context);
		}
		return createRichSharePreferences;
	}
	
	public void putUserName(String userName) {
		sharedPreferences.edit().putString("userName", userName).commit();
	}
	
	public String getUserName() {
		return sharedPreferences.getString("userName", "");
	}
	
	public void putUserId(String userId) {
		sharedPreferences.edit().putString("userId", userId).commit();
	}
	
	public String getuserId() {
		return sharedPreferences.getString("userId", "");
	}
	
	public void putRealName(String realName) {
		sharedPreferences.edit().putString("realName", realName).commit();
	}
	
	public String getRealName() {
		return sharedPreferences.getString("realName", "");
	}
	
	public void putReplaceName(String replaceName) {
		sharedPreferences.edit().putString("replaceName", replaceName).commit();
	}
	
	public String getReplaceName() {
		return sharedPreferences.getString("replaceName", "");
	}
	
	public void putPassword(String password) {
		sharedPreferences.edit().putString("password", password).commit();
	}
	
	public String getPassword() {
		return sharedPreferences.getString("password", "");
	}
	
	public void putIsLogin(boolean isLogin) {
		 sharedPreferences.edit().putBoolean("isLogin", isLogin).commit();
	}
	
	public boolean isLogin() {
		return sharedPreferences.getBoolean("isLogin", false);
	}
	
	public void putIsSavePassword(boolean isSavePassword) {
		 sharedPreferences.edit().putBoolean("isSavePassword", isSavePassword).commit();
	}
	
	public boolean getisSavePassword() {
		return sharedPreferences.getBoolean("isSavePassword", false);
	}
	
	public void putToken(String token) {
		sharedPreferences.edit().putString("token", token).commit();
	}
	
	public String getToken() {
		return sharedPreferences.getString("token", "");
	}
	
	public boolean isFirstOpen() {
		return sharedPreferences.getBoolean("firstOpen", true);
	}
	
	public void putIsFirstOpen(boolean isFirstOpen) {
		sharedPreferences.edit().putBoolean("firstOpen", isFirstOpen).commit();
	}
	
	public void putVerificationCodeTime(long time) {
		sharedPreferences.edit().putLong("verificationCodeTime", time).commit();
	}
	
	public long getVerificationCodeTime() {
		return sharedPreferences.getLong("verificationCodeTime", 0);
	}
	
	
	public void putIP(String IP) {
		sharedPreferences.edit().putString("IP", IP).commit();
	}
	
	public String getIP() {
		return sharedPreferences.getString("IP", "");
	}
	
	public void putPort(String port) {
		sharedPreferences.edit().putString("port", port).commit();
	}
	
	public String getPort() {
		return sharedPreferences.getString("port", "");
	}
	
	public void putProject(String project) {
		sharedPreferences.edit().putString("project", project).commit();
	}
	
	public String getProject() {
		return sharedPreferences.getString("project", "");
	}
	
	public void putServerUrl(String serverUrl) {
		sharedPreferences.edit().putString("serverUrl", serverUrl).commit();
	}
	
	public String getServerUrl(String url) {
		return sharedPreferences.getString("serverUrl", url);
	}
	
	public void putDeptId(String deptId) {
		sharedPreferences.edit().putString("deptId", deptId).commit();
	}
	
	public String getDeptId() {
		return sharedPreferences.getString("deptId", "");
	}
	
	public void putDeptName(String deptName) {
		sharedPreferences.edit().putString("deptName", deptName).commit();
	}
	
	public String getDeptName() {
		return sharedPreferences.getString("deptName", "");
	}
	
	public void putAuthorizeInfo(String authName, String meetingCode) {
		sharedPreferences.edit().putString("authName", authName).commit();
		sharedPreferences.edit().putString("meetingCode", meetingCode).commit();
		//sharedPreferences.edit().commit();
	}
	
	public String getAuthName() {
		return sharedPreferences.getString("authName", "");
	}
	
	public String getMeetingCode() {
		return sharedPreferences.getString("meetingCode", "");
	}
	
	public void putChangePage(String vorh) {
		sharedPreferences.edit().putString("vorh", vorh).commit();
	}
	
	public String getChangePage() {
		return sharedPreferences.getString("vorh", "");
	}
	
	//已经完成清除缓存的版本号，完成清除后更新版本号到配置文件
	public void putClearVersionCode(int value) {
		sharedPreferences.edit().putInt("clearversioncode", value).commit();
	}
	
	public int getClearVersionCode() {
		return sharedPreferences.getInt("clearversioncode", 0);
	}
	
	public void loginOut(){
		sharedPreferences.edit().putString("replaceName", "").commit();
		sharedPreferences.edit().putString("password", "").commit();
		sharedPreferences.edit().putString("userName", "").commit();
		sharedPreferences.edit().putString("realName", "").commit();
		sharedPreferences.edit().putBoolean("isLogin", false).commit();
		sharedPreferences.edit().putBoolean("isSavePassword", false).commit();
		sharedPreferences.edit().putString("userId", "").commit();
		sharedPreferences.edit().putString("token", "").commit();
		sharedPreferences.edit().putString("deptId", "").commit();
		sharedPreferences.edit().putString("deptName", "").commit();
	}
}
