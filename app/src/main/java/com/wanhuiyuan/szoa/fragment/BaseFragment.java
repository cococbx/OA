package com.wanhuiyuan.szoa.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.uiutil.NetWorkState;

public class BaseFragment extends Fragment {
//	private OnErrorListener onErrorListener;
	private OnRefreshListener onRefreshListener;
	private ImageView loadingError;
	LayoutInflater inflater;
	private TextView hint;
	Handler handler = new Handler();
	public BaseFragment() {
		
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		State wifiState = null;
		State mobileState = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiState = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			mobileState = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState();
			if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED == mobileState) {
				hint.setVisibility(View.GONE);
				NetWorkState.NETWORK_STATE = NetWorkState.MOBILE_STATE;
				if(null != onRefreshListener)
					onRefreshListener.onRefresh();
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED == wifiState
					&& State.CONNECTED != mobileState) {
				hint.setVisibility(View.GONE);
				NetWorkState.NETWORK_STATE = NetWorkState.WIFI_STATE;
				if(null != onRefreshListener)
					onRefreshListener.onRefresh();
			} else if (wifiState != null && mobileState != null
					&& State.CONNECTED != wifiState
					&& State.CONNECTED != mobileState) {
				hint.setVisibility(View.VISIBLE);
				NetWorkState.NETWORK_STATE = NetWorkState.NO_STATE;
//				control();
			}
		}

	};
	private ViewGroup root;
	private LinearLayout contentLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentLayout.addView((View)container ,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		return root;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		if(null == inflater) {
			inflater = LayoutInflater.from(getActivity());
		}
		if(null == root) {
			root = (ViewGroup)inflater.inflate(R.layout.base_fragment, null);
			contentLayout = (LinearLayout) root.findViewById(R.id.content_fragment_layout);
			contentLayout.removeAllViews();
		}
		hint = (TextView) root.findViewById(R.id.hint_fragment);
//		loadingError = (ImageView) root.findViewById(R.id.loading_error);
//		loadingError.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(NetWorkState.NETWORK_STATE != NetWorkState.NO_STATE) {
//					if(null != onErrorListener) {
//						onErrorListener.reLoad();
//						showContentView(true);
//					}
//				} else {
//					//提示没有网络
//				}
//			}
//		});
//		getActivity().registerReceiver(receiver, filter);
	}
	
	public void showContentView(boolean visible) {
		if(visible) {
			contentLayout.setVisibility(View.VISIBLE);
		} else {
			contentLayout.setVisibility(View.GONE);
		}
	}
	
	protected void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}
	
//	protected void setOnErrorListener(OnErrorListener onErrorListener) {
//		this.onErrorListener = onErrorListener;
//		if(NetWorkState.NETWORK_STATE == NetWorkState.NO_STATE)
//			showContentView(false);
//		else
//			showContentView(true);
//	}
	
	interface OnRefreshListener {
		void onRefresh();
	}
	
	
	
}
