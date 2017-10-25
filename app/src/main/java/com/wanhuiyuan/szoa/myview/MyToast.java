package com.wanhuiyuan.szoa.myview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cec.szoa.R;

public class MyToast extends Toast {
	private static MyToast mToast;

	private static View v;

	public MyToast(Context context) {
		super(context);
	}

	// public static void show(Context context, String message, int duration)
	// {
	// initView(context, message, duration).show();
	// }

	// public static void show(Context context, int stringId, int duration)
	// {
	// initView(context, context.getResources().getString(stringId),
	// duration).show();
	// }

	public static void show(Context context, String message) {
		if (null == context || null == message || "".equals(message)) {
			return;
		}
		if (mToast == null) {
			mToast = initView(context, message, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
		}
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

	public void setText(CharSequence s) {
		if (v == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}
		TextView tv = (TextView) v.findViewById(R.id.tvForToast);
		if (tv == null) {
			throw new RuntimeException(
					"This Toast was not created with Toast.makeText()");
		}
		tv.setText(s);
	}

	public static MyToast initView(Context context, CharSequence text,
			int duration) {
		MyToast result = new MyToast(context);
		LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflate.inflate(R.layout.my_toast, null);
		TextView tv = (TextView) v.findViewById(R.id.tvForToast);
		tv.setText(text);
		result.setView(v);
		result.setDuration(duration);
		return result;
	}
}