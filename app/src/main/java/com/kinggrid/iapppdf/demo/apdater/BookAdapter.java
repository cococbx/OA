package com.kinggrid.iapppdf.demo.apdater;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cec.szoa.R;

/**
 * 自定义文档列表适配器
 * com.kinggrid.iapppdf.demo.BookAdapter
 * @author wmm
 * create at 2015年8月14日 上午8:51:08
 */
public class BookAdapter extends BaseAdapter {
	private static final String TAG = "BookAdapter";

	private ViewHolder holder;
	protected List<String> fileData;
	private LayoutInflater inflater;

	protected class ViewHolder {
		protected TextView fileName;
	}

	public BookAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	public void setFileData(List<String> fileData) {
		this.fileData = fileData;
	}

	@Override
	public int getCount() {
		return fileData.size();
	}

	@Override
	public Object getItem(int position) {
		return fileData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item, null);

			holder.fileName = (TextView) convertView
					.findViewById(R.id.file_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.fileName.setText(fileData.get(position));

		return convertView;
	}

}
