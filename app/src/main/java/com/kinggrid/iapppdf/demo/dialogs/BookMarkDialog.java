package com.kinggrid.iapppdf.demo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kinggrid.iapppdf.emdev.utils.LayoutUtils;
import com.kinggrid.iapppdf.ui.viewer.IAppPDFActivity;
import com.cec.szoa.R;

public class BookMarkDialog extends Dialog implements OnItemClickListener {
	ListView bookMarkListView;
	private ImageView load_img;
	private RelativeLayout loading;
	private TextView no_list;
	private Integer[] bookMarkList;
	IAppPDFActivity activity;
	TableRow notelist_title;

	public BookMarkDialog(Context context, final IAppPDFActivity activity,
			Integer[] list) {
		super(context, R.style.MyDialog);
		this.activity = activity;
		this.bookMarkList = list;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutUtils.maximizeWindow(getWindow());

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.bookmark_layout);
		notelist_title = (TableRow) findViewById(R.id.title);
		final Button btn_outline_close = (Button) findViewById(R.id.title_btn_close);
		btn_outline_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				dismiss();
			}
		});
		final TextView tv_outline_title = (TextView) findViewById(R.id.list_title);
		tv_outline_title.setText("书签目录");
		bookMarkListView = (ListView) findViewById(R.id.bookmark_list);
		no_list = (TextView) findViewById(R.id.no_data_prompt);

		loading = (RelativeLayout) findViewById(R.id.loading);
		load_img = (ImageView) findViewById(R.id.progress_img);
		Animation loadAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.load_animation);
		load_img.setAnimation(loadAnimation);
		if (bookMarkList != null && bookMarkList.length > 0) {
			// loading.setVisibility(View.GONE);
			notelist_title.setVisibility(View.VISIBLE);
			bookMarkListView.setAdapter(new ListViewAdapter(bookMarkList));
			bookMarkListView.setOnItemClickListener(this);
		} else {
			loading.setVisibility(View.GONE);
			no_list.setVisibility(View.VISIBLE);
			notelist_title.setVisibility(View.GONE);
		}
	}

	public void updateView(Integer[] list) {
		bookMarkList = list;
		if (bookMarkList != null && bookMarkList.length > 0) {
			loading.setVisibility(View.GONE);
			notelist_title.setVisibility(View.VISIBLE);
			bookMarkListView.setAdapter(new ListViewAdapter(bookMarkList));
			bookMarkListView.setOnItemClickListener(this);
		} else {
			loading.setVisibility(View.GONE);
			no_list.setVisibility(View.VISIBLE);
			notelist_title.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		this.dismiss();
		final int pageNo = bookMarkList[position];
		Log.d("Kevin", "pageNo : " + pageNo);
		final int maxPage = activity.getPageCount() - 1;
		Log.d("Kevin", "maxPage : " + maxPage);
		if (pageNo <= maxPage) {
			activity.jumpToPage(pageNo-1);
		}
	}

	private class ListViewAdapter extends BaseAdapter {

		/**
		 * 批注列表
		 */
		private final Integer[] mBookmarkList;

		private ListViewAdapter(final Integer[] mBookmarkList) {
			this.mBookmarkList = mBookmarkList;
		}

		@Override
		public int getCount() {
			return mBookmarkList.length;
		}

		@Override
		public Object getItem(final int i) {
			return mBookmarkList[i];
		}

		@Override
		public long getItemId(final int id) {
			return id;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.bookmark_listview_layout, null);
				holder = new Holder();
				holder.pageNum_textView = (TextView) convertView
						.findViewById(R.id.page_num);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			((TextView) convertView.findViewById(R.id.page_index))
			.setText((position + 1) + "");
			holder.pageNum_textView.setText("第"
					+( mBookmarkList[position])
					+ "页");
			return convertView;
		}
	}

	class Holder {

		TextView pageNum_textView;
	}
}
