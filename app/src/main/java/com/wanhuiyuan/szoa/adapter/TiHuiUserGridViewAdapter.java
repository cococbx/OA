package com.wanhuiyuan.szoa.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.MeetPerson;
import com.wanhuiyuan.szoa.bean.Meeting;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

public class TiHuiUserGridViewAdapter extends BaseAdapter {

	private List<MeetPerson> userList;
	private LayoutInflater inflater;
	private Holder holder;
	private DisplayImageOptions options;
	private Activity context;
	DecimalFormat df = new DecimalFormat("######0.00");
	private Meeting mMeeting;

	public TiHuiUserGridViewAdapter(Activity context, List<MeetPerson> userList, Meeting meeting) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.userList = userList;
		this.mMeeting = meeting;
		initImageLoaderOption();
	}

	private void initImageLoaderOption() {
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.default_avatar) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.default_avatar) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.default_avatar) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
	}

	private static class Holder {
		ImageView pic;
		TextView tihui;
		TextView title;
	}

	@Override
	public int getCount() {
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// if(null == convertView) {
		holder = new Holder();
		convertView = inflater.inflate(R.layout.select_tihuiuser_item, null);
//		holder.pic = (ImageView) convertView.findViewById(R.id.item_image);
//		if (userList.get(position).getImgUrl() != null
//				&& userList.get(position).getImgUrl().length() > 0) {
//			Log.e("imgUrl->>", SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
//			+ userList.get(position).getImgUrl());
//			ImageLoader.getInstance().displayImage(
//					SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
//							+ userList.get(position).getImgUrl(), holder.pic,
//					options);
//		}
		holder.title = (TextView) convertView.findViewById(R.id.item_text);
		holder.title.setText(userList.get(position).getUsername());
		holder.tihui = (TextView) convertView
				.findViewById(R.id.item_text_tihui);
//		holder.tihui.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("user", userList.get(position));
//				bundle.putSerializable("meeting", mMeeting);
//				intent.putExtras(bundle);
//				context.setResult(11, intent);
//				context.finish();
//			}
//		});
//		holder.title.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("user", userList.get(position));
//				bundle.putSerializable("meeting", mMeeting);
//				intent.putExtras(bundle);
//				context.setResult(11, intent);
//				context.finish();
//			}
//		});
//		holder.pic.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("user", userList.get(position));
//				bundle.putSerializable("meeting", mMeeting);
//				intent.putExtras(bundle);
//				context.setResult(11, intent);
//				context.finish();
//			}
//		});
		
//		convertView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				Bundle bundle = new Bundle();
//				bundle.putSerializable("user", userList.get(position));
//				bundle.putSerializable("meeting", mMeeting);
//				intent.putExtras(bundle);
//				context.setResult(11, intent);
//				context.finish();	
//			}
//		});
		return convertView;
	}

	public List<MeetPerson> getCompanyList() {
		return userList;
	}

	public void setCompanyList(List<MeetPerson> userList) {
		this.userList = userList;
	}
}
