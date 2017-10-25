package com.wanhuiyuan.szoa.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Meeting;

/*
 * 改为pdf的通知以后，这个不再使用。
 */
@SuppressLint("ValidFragment")
public class MeetingInfoFragment extends BaseFragment {

	Meeting meeting;
	TextView meeting_title;
	WebView meeting_content;

	public MeetingInfoFragment() {
	}

	public MeetingInfoFragment(Meeting meeting) {
		this.meeting = meeting;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meeting_info, container,
				false);
		meeting_title = (TextView) view.findViewById(R.id.meeting_title);
		meeting_content = (WebView) view.findViewById(R.id.meeting_content);
		meeting_content.getSettings().setDefaultTextEncodingName("UTF-8");
		if (meeting != null) {
			meeting_title.setText(meeting.getName());
			if(meeting.getContent()!=null)
			meeting_content.loadData(meeting.getContent(), "text/html; charset=UTF-8", null);
		}
		return view;
	}

}
