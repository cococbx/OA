package com.wanhuiyuan.szoa.adapter;

import java.util.List;

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
import com.wanhuiyuan.szoa.bean.ServiceBean;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;

public class ServiceGridViewAdapter extends BaseAdapter {

	List<ServiceBean> serviceList;
	private LayoutInflater inflater;
	private Holder holder;
	private DisplayImageOptions options;
	private Context context;

	public ServiceGridViewAdapter(Context context, List<ServiceBean> serviceList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.serviceList = serviceList;
		initImageLoaderOption();
	}

	private void initImageLoaderOption() {
		options = new DisplayImageOptions.Builder()
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
		return serviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return serviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// if(null == convertView) {
		holder = new Holder();
		convertView = inflater.inflate(R.layout.service_item, null);
		holder.pic = (ImageView) convertView.findViewById(R.id.item_image);
		if (serviceList.get(position).getLogoUrl() != null
				&& serviceList.get(position).getLogoUrl().length() > 0) {
			Log.e("imgUrl->>", SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
			+ serviceList.get(position).getLogoUrl());
			ImageLoader.getInstance().displayImage(
					SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST)
							+ serviceList.get(position).getLogoUrl(), holder.pic,
					options);
		}
		holder.title = (TextView) convertView.findViewById(R.id.item_text);
		holder.title.setText(serviceList.get(position).getServiceName());
		return convertView;
	}


}
