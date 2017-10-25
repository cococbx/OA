package com.wanhuiyuan.szoa.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

public class RenyuanGridViewAdapter extends BaseAdapter {

	private List<Login> userList;
	private LayoutInflater inflater;
	private Holder holder;
	private DisplayImageOptions options;
	private Context context;
	private int meetingState=0;

	public RenyuanGridViewAdapter(Context context, List<Login> userList,int meetingState) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.userList = userList;
		this.meetingState = meetingState;
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
		TextView type;
		TextView date;
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
		convertView = inflater.inflate(R.layout.item_menu, null);
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
		if(userList.get(position).getLoginType().equals("2")){
			holder.title.setTextColor(context.getResources().getColor(R.color.tihuiColor));
		}
		if(!userList.get(position).getLoginType().equals("0")){
			 convertView.findViewById(R.id.item_text_state_off).setVisibility(View.GONE);
			 convertView.findViewById(R.id.item_text_state_on).setVisibility(View.VISIBLE);
		}else{
			 convertView.findViewById(R.id.item_text_state_off).setVisibility(View.VISIBLE);
			 convertView.findViewById(R.id.item_text_state_on).setVisibility(View.GONE);
		}
		if(meetingState==1){
			 convertView.findViewById(R.id.item_text_state_off).setVisibility(View.GONE);
			 convertView.findViewById(R.id.item_text_state_on).setVisibility(View.GONE);
		}
		return convertView;
	}

	public List<Login> getCompanyList() {
		return userList;
	}

	public void setCompanyList(List<Login> userList) {
		this.userList = userList;
	}
}
