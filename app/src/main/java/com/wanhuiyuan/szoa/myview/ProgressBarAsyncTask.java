package com.wanhuiyuan.szoa.myview;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wanhuiyuan.szoa.bean.YitiFile;

public class ProgressBarAsyncTask extends AsyncTask<String, Integer, String> {
	MyProgressBar progressBar;
	LinearLayout progressLayout;
	String fileUrl;
	YitiFile file;
	Context context;

	public ProgressBarAsyncTask(Context context, String fileUrl, YitiFile file,
			MyProgressBar progressBar, LinearLayout progressLayout) {
		super();
		this.context = context;
		this.progressBar = progressBar;
		this.fileUrl = fileUrl;
		this.file = file;
		this.progressLayout = progressLayout;
	}

	/**
	 * 这里的Intege参数对应AsyncTask中的第二个参数
	 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
	 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		int vlaue = values[0];
		progressBar.setProgress(vlaue);
	}

	@Override
	protected String doInBackground(String... arg0) {
		byte[] bytes = Base64.decode(fileUrl, Base64.DEFAULT); // 将字符串转换为byte数组
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		byte[] buffer = new byte[1024];
		// 把传入的String写入文件中
		FileOutputStream fos;

		try {
			fos = context.openFileOutput(file.getFileName(),
					Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);

			int bytesum = 0;
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1) {
				bytesum += byteread;
				fos.write(buffer, 0, byteread);
				publishProgress(bytesum * 100 / bytes.length);
			}
			fos.close();
		} catch (FileNotFoundException e) {
			return e.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (Exception e) {
			return e.toString();
		}
		return "ok";
	}

	@Override
	protected void onPostExecute(String result) {
		if (result.equals("ok"))
			progressLayout.setVisibility(View.GONE);
		else
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		super.onPostExecute(result);
	}

}
