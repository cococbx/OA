package com.wanhuiyuan.szoa.jingge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class LogPrinter {

	private String mDir;
	//private Context mContext;
	private static LogPrinter instance;
	private LogDumper mLogDumper;
	private int myPid;
	
	
	
	public static LogPrinter getInstance(Context context){
		if(instance == null){
			instance = new LogPrinter(context);
		}
		return instance;
	}
	
	private LogPrinter(Context context){
		init(context);
		myPid = android.os.Process.myPid();
	}
	
	public void init(Context context){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			mDir = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ File.separator + "iappppdf";
		}else{
			mDir = context.getFilesDir().getAbsolutePath() + File.separator + "iappppdf";
		}
		
		File file = new File(mDir);
		if(!file.exists()){
			file.mkdirs();
		}
		
	}
	
	public void setFileDir(String dir){
		mDir = dir;
	}
	
	public void start(){
		if(mLogDumper == null){
			mLogDumper = new LogDumper(String.valueOf(myPid), mDir);
		}
		mLogDumper.start();
	}
	
	public void stop(){
		if(mLogDumper != null){
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
	}
	
	private class LogDumper extends Thread {
		private String mPid;
		private Process localProc;
		private boolean mRunning = true;
		private FileOutputStream out;
		private BufferedReader mReader;
		String cmds;
		
		public LogDumper(String pid, String dir){
			mPid = pid;
			try{
				File target = new File(dir, getFileName());
				if(target.exists()){
					target.delete();
					target.createNewFile();
				}
				out = new FileOutputStream(target);
			}catch(FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cmds = "logcat *:e *:i *:d *:v | grep \"(" + mPid + ")\"";
		}
		
		public void stopLogs(){
			mRunning = false;
		}
		
		private String getFileName(){
			StringBuffer name = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String date = format.format(new Date(System.currentTimeMillis()));
			name.append("Log");
			name.append(date);
			name.append(".txt");
			return name.toString();
		}
		
		private String getDateEN(){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = format.format(new Date(System.currentTimeMillis()));
			return date;
		}
		
		@Override
		public void run() {
			try{
				localProc = Runtime.getRuntime().exec(cmds);
				mReader = new BufferedReader(new InputStreamReader(
						localProc.getInputStream()), 1024);
				String line = null;
				while(mRunning && (line = mReader.readLine()) != null){
					if(!mRunning) break;
					
					if(line.length() == 0)continue;
					
					if(out != null && line.contains(mPid)){
						out.write((getDateEN() + "  " 
								+ line + "\n").getBytes());
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}finally {
				if(localProc != null){
					localProc.destroy();
					localProc = null;
				}
				
				if(mReader != null){
					try{
						mReader.close();
						mReader = null;
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				
				if(out != null){
					try{
						out.close();
					}catch(IOException e){
						e.printStackTrace();
					}
					
					out = null;
				}
			}
		}

	}
}
