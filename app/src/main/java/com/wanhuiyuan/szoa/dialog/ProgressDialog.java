package com.wanhuiyuan.szoa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cec.szoa.R;

public class ProgressDialog {
	private static Dialog loadingDialog = null;

	public static Dialog show(Context context) {
		return show(context, "");
	}

	public static Dialog show(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progreses_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.loading_animation);
		((ImageView) v.findViewById(R.id.img)).startAnimation(animation);
		if ("".equals(msg))
			tipTextView.setText("加载中,请稍后");
		else
			tipTextView.setText(msg);
		loadingDialog = new Dialog(context, R.style.progress_style_dialog);
		loadingDialog.setCancelable(false);
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		loadingDialog.show();
		return loadingDialog;
	}

	public static void dismiss() {
		if (null != loadingDialog) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
}
