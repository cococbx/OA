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
import com.wanhuiyuan.szoa.SelectUserActivity;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

public class SelectUserGridViewAdapter extends BaseAdapter {

	private List<Login> userList;
	private LayoutInflater inflater;
	private Holder holder;
	private DisplayImageOptions options;
	private SelectUserActivity context;
	DecimalFormat df = new DecimalFormat("######0.00");

	public SelectUserGridViewAdapter(SelectUserActivity context, List<Login> userList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.userList = userList;
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
		convertView = inflater.inflate(R.layout.select_user_item, null);
		holder.pic = (ImageView) convertView.findViewById(R.id.item_image);
		if (userList.get(position).getImgUrl() != null
				&& userList.get(position).getImgUrl().length() > 0) {
			Log.e("imgUrl->>", SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
			+ userList.get(position).getImgUrl());
			ImageLoader.getInstance().displayImage(
					SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
							+ userList.get(position).getImgUrl(), holder.pic,
					options);
		}
		holder.title = (TextView) convertView.findViewById(R.id.item_text);
		holder.title.setText(userList.get(position).getTruename());
		holder.tihui = (TextView) convertView
				.findViewById(R.id.item_text_tihui);
		holder.tihui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickUser(position);	
			}
		});
		holder.title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickUser(position);	
			}
		});
		holder.pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickUser(position);	
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickUser(position);	
			}
		});
		return convertView;
	}
	
	void clickUser(int position){
		Login login = userList.get(position);
		context.handlerUserClick(login);
	}

	public List<Login> getCompanyList() {
		return userList;
	}

	public void setCompanyList(List<Login> userList) {
		this.userList = userList;
	}
}
