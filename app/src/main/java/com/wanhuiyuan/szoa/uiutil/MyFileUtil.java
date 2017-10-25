package com.wanhuiyuan.szoa.uiutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import com.horizon.util.encrypt.DESEDE;
import com.wanhuiyuan.szoa.MyApplication;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.OffLineUser;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.jingge.ConstantValue;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MyFileUtil implements ConstantValue{
	MD5Utility util;
	File file;
	public MyFileUtil() {
		util = new MD5Utility();
	}

	/**
	 * 解密
	 * 
	 * @param fileUrl
	 *            源文件
	 * @param tempUrl
	 *            临时文件
	 * @param ketLength
	 *            密码长度
	 * @return
	 * @throws Exception
	 */
	public String decrypt(Context context, String fileUrl, String tempUrl,
			int keyLength, String md5String) throws Exception {
		file = new File(fileUrl);
		if (!file.exists()) {
			return null;
		}
		if (!util.verifyInstallPackage(fileUrl, md5String)){
			file.delete();
			return null;
		}
		InputStream is = new FileInputStream(fileUrl);
		OutputStream out = context.openFileOutput(tempUrl,
				Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);

		try {
			byte[] buffer = new byte[1024];
			byte[] buffer2 = new byte[1024];
			byte bMax = (byte) 255;
			long size = file.length() - keyLength;
			int mod = (int) (size % 1024);
			int div = (int) (size >> 10);
			int count = mod == 0 ? div : (div + 1);
			int k = 1, r;
			while ((k <= count && (r = is.read(buffer)) > 0)) {
				if (mod != 0 && k == count) {
					r = mod;
				}

				for (int i = 0; i < r; i++) {
					byte b = buffer[i];
					buffer2[i] = b == 0 ? bMax : --b;
				}
				out.write(buffer2, 0, r);
				k++;
			}
			out.close();
			is.close();
			file.delete();
		} catch (Exception e) {
			return null;
		}
		return tempUrl;
	}

	/**
	 * 判断文件是否加密
	 * 
	 * @param fileName
	 * @return
	 */
	public String readFileLastByte(String fileName, int keyLength) {
		File file = new File(fileName);
		if (!file.exists())
			return null;
		StringBuffer str = new StringBuffer();
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "r");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			for (int i = keyLength; i >= 1; i--) {
				randomFile.seek(fileLength - i);
				str.append((char) randomFile.read());
			}
			randomFile.close();
			return str.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void doOpenFile(Context mContext, YitiFile ytFile, String meetingId) {
		File file = new File(mContext.getApplicationContext()
				.getFilesDir().getAbsolutePath()
				+ "/" + ytFile.getRealName());
		if (file == null) {
			MyToast.show(mContext, "文件未找到！");
		} else if (!file.exists()) {
			MyToast.show(mContext, "文件尚未下载！");
		} else {
			Uri uri = Uri.fromFile(file);
			Intent intent = new Intent("android.intent.action.VIEW", uri);
			intent.setClassName(mContext,
					"com.wanhuiyuan.szoa.jingge.BookShower");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// 传递用户名，默认admin
			intent.putExtra(NAME, MyApplication.USERID);
			// 传递授权码(必传)
			//String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU3CQHU1ScyOebPLnpsDlQDzLvKeTdxzwi2Wk0Yn+WSxoXx6aD2yiupr6ji7hzsE6/QqGcC+eseQV1yrWJ/1FwxLCjX+xEgRoNggvmgA8zkJXOVWEbHWAH22+t7LdPt+jENwSCMsAYnhGWJ0gXIIaLjG32poSbszHQQyNDZrHtqZuuSgCNRP4FpYjl8hG/IVrYXo/POmAlKHHVOdR0F5DQjr+W8fXpxdRHfEuWC1PB9ruQ=";
			intent.putExtra(LIC, MyApplication.copyRight);
			// 是否支持域编辑功能，在表单PDF文件中可体现此功能，默认为false
			intent.putExtra(CANFIELDEIDT, false);// 可选值为布尔值
			// 文档保存之后批注是否只读，默认为false,不需要修改请忽略此参
			// intent.putExtra(ANNOTPROTECTNAME, isAnnotProtect.isChecked());
			// 是否为E人E本模式，默认为false

			// 是否保留PDF上次阅读位置，默认为true,为false时每次都从第一页开始阅读
			intent.putExtra(LOADCACHENAME, false);
			// 阅读模式，默认PageViewMode.VSCROLL，竖向连续翻页,不需要重设可忽略
			if (SharePreferences.getInstance(mContext).getChangePage()
					.equals("vertical")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEV);
			} else if (SharePreferences.getInstance(mContext)
					.getChangePage().equals("horizontal")) {
				intent.putExtra(VIEWMODENAME, VIEWMODE_SINGLEH);
			} else
				intent.putExtra(VIEWMODENAME, VIEWMODE_VSCROLL);
			intent.putExtra("fileName", ytFile.getRealName());
			intent.putExtra("fileRealName", ytFile.getFileName());
			intent.putExtra("fileId", ytFile.getId());
			intent.putExtra("meetingId", meetingId);
			mContext.startActivity(intent);
		}
	}
}
