package com.wanhuiyuan.szoa.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wanhuiyuan.szoa.MeetingListActivity;
import com.cec.szoa.R;

public class AddPopWindowMeetingList extends PopupWindow {
	private View conentView;
	MeetingListActivity context;
	long clickTime = 0;

	@SuppressLint("InflateParams")
	public AddPopWindowMeetingList(final MeetingListActivity activity) {
		this.context = (MeetingListActivity) activity;
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.popupwindow_add, null);

		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);

		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);
		TextView refushYitiTv = (TextView) conentView
				.findViewById(R.id.refushYitiTv);
		refushYitiTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((System.currentTimeMillis() - clickTime) > 10000) {
					clickTime = System.currentTimeMillis();
					context.refush();
					AddPopWindowMeetingList.this.dismiss();
				}
			}
		});

	}
	

	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, 0, 0);
		} else {
			this.dismiss();
		}
	}
}
