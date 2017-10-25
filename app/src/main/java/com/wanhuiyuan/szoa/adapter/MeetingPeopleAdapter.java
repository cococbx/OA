package com.wanhuiyuan.szoa.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.MeetingPersonInfo;
import com.wanhuiyuan.szoa.myview.ExpandListView;
import com.wanhuiyuan.szoa.uiutil.MeetPeopleShowInfo;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeetingPeopleAdapter extends BaseAdapter {
	
	private static class ViewHolder {
		TextView labelTxt;
		TextView attendInfo;
		TextView replaceInfo;
		TextView absenceInfo;
		TextView leaveInfo;
		LinearLayout attendLayout;
		LinearLayout replaceLayout;
		LinearLayout absenceLayout;
		LinearLayout leaveLayout;

		ExpandListView leaderList;
		LinearLayout normalListLayout;
	}
	
	ViewHolder viewHolder;
	Context context;
	
	public List<MeetPeopleShowInfo> infos = new ArrayList<MeetPeopleShowInfo>();
	
	public MeetingPeopleAdapter(List<MeetPeopleShowInfo> infos, Context context){
		this.infos = infos;
		this.context = context;
	}
	
	public void setMeetingPeople(List<MeetPeopleShowInfo> infos){
		this.infos = infos;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.meeting_person_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.labelTxt = (TextView) convertView.findViewById(R.id.label_txt);
            viewHolder.attendInfo = (TextView) convertView.findViewById(R.id.attend_info);
            viewHolder.replaceInfo = (TextView) convertView.findViewById(R.id.replace_info);
            viewHolder.absenceInfo = (TextView) convertView.findViewById(R.id.absence_info);
            viewHolder.leaveInfo = (TextView) convertView.findViewById(R.id.leave_info);
            viewHolder.attendLayout = (LinearLayout) convertView.findViewById(R.id.attend_layout);
            viewHolder.replaceLayout = (LinearLayout) convertView.findViewById(R.id.replace_layout);
            viewHolder.absenceLayout = (LinearLayout) convertView.findViewById(R.id.absence_layout);
            viewHolder.leaveLayout = (LinearLayout) convertView.findViewById(R.id.leave_layout);

			viewHolder.leaderList = (ExpandListView) convertView.findViewById(R.id.leader_list);
			viewHolder.normalListLayout = (LinearLayout) convertView.findViewById(R.id.normal_list_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MeetPeopleShowInfo info = infos.get(position);
        if (info != null) {
			if(info.isNewLine()){
				viewHolder.leaderList.setVisibility(View.VISIBLE);
				viewHolder.normalListLayout.setVisibility(View.GONE);
				viewHolder.labelTxt.setText(info.getLabel());

				MeetingLeaderAdapter leaderAdapter =
						new MeetingLeaderAdapter(context, info.getLeaderInfo());

				viewHolder.leaderList.setAdapter(leaderAdapter);
				leaderAdapter.notifyDataSetChanged();

				viewHolder.leaderList.setListViewHeightBasedOnChildren();

			}else {
				viewHolder.leaderList.setVisibility(View.GONE);
				viewHolder.normalListLayout.setVisibility(View.VISIBLE);
				viewHolder.labelTxt.setText(info.getLabel());
				if (info.getReplaceNum() == 0) {
					viewHolder.replaceLayout.setVisibility(View.GONE);
				} else {
					viewHolder.replaceLayout.setVisibility(View.VISIBLE);
					Spanned replaceTxt = info.getMeetingStrByType(MeetingPersonInfo.REPLACE_MEETING);
					viewHolder.replaceInfo.setText(replaceTxt);
				}

				if (info.getLeaveNum() == 0) {
					viewHolder.leaveLayout.setVisibility(View.GONE);
				} else {
					viewHolder.leaveLayout.setVisibility(View.VISIBLE);
					Spanned leaveTxt = info.getMeetingStrByType(MeetingPersonInfo.LEAVE_MEETING);
					viewHolder.leaveInfo.setText(leaveTxt);
				}

				Spanned attendTxt = info.getMeetingStrByType(MeetingPersonInfo.ATTEND_MEETING);
				viewHolder.attendInfo.setText(attendTxt);

				Spanned absenceTxt = info.getMeetingStrByType(MeetingPersonInfo.ABSENCE_MEETING);
				viewHolder.absenceInfo.setText(absenceTxt);
			}
        	
        	
        }

        return convertView;
	}
}
