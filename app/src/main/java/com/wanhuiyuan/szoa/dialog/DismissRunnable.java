package com.wanhuiyuan.szoa.dialog;

public class DismissRunnable implements Runnable {
	@Override
	public void run() {
		ProgressDialog.dismiss();
	}
	
}
