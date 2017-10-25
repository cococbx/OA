package com.wanhuiyuan.szoa.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Meeting;

public class WaiteMeetingListAdapter extends BaseAdapter {

	private List<Meeting> meetingList;
	private LayoutInflater inflater;
	private Holder holder;
	private DisplayImageOptions options;
	private Activity context;
	DecimalFormat df = new DecimalFormat("######0.00");

	public WaiteMeetingListAdapter(Activity context, List<Meeting> meetingList) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.meetingList = meetingList;
		initImageLoaderOption();
	}

	private void initImageLoaderOption() {
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
	}

	private static class Holder {
		TextView type;
		TextView time;
		TextView title;
		TextView status;
	}

	@Override
	public int getCount() {
		return meetingList.size();
	}

	@Override
	public Object getItem(int position) {
		return meetingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// if(null == convertView) {
		holder = new Holder();
		convertView = inflater.inflate(R.layout.waite_meeting_item, null);

		holder.time = (TextView) convertView
				.findViewById(R.id.item_text_time);
		holder.type = (TextView) convertView
				.findViewById(R.id.item_type);
		holder.title = (TextView) convertView.findViewById(R.id.item_text);
		holder.title.setText("("+(position+1)+")  "+meetingList.get(position).getName());
//		if(meetingList.get(position).getMeetingType()==1){
//			holder.type.setText("[常务会议]");
//		}else if(meetingList.get(position).getMeetingType()==2){
//			holder.type.setText("[厅务会议]");
//		}else if(meetingList.get(position).getMeetingType()==3){
//			holder.type.setText("[内部会议]");
//		} 
		holder.status= (TextView) convertView
				.findViewById(R.id.item_status);
		if(meetingList.get(position).getStatus()==1){
			holder.status.setText("[材料下发]");
		}else if(meetingList.get(position).getStatus()==2){
			holder.status.setText("[开始]");
		}else if(meetingList.get(position).getStatus()==3){
			holder.status.setText("[暂停]");
		}else if(meetingList.get(position).getStatus()==4){
			holder.status.setText("[结束]");
		}
		holder.time.setText(meetingList.get(position).getTime());
		return convertView;
	}

}
