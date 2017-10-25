package com.wanhuiyuan.szoa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.MeetingLeaderInfo;

/**
 * Created by victory on 2016/11/26.
 * 领导姓名
 */
public class MeetingLeaderAdapter extends BaseAdapter {

    Context context;
    MeetingLeaderInfo meetLeaderInfo;

    private class ViewHolder {
        private TextView leaderTxt;
    }

    ViewHolder viewHolder;

    public MeetingLeaderAdapter(Context context, MeetingLeaderInfo meetLeaderInfo) {
        this.context = context;
        this.meetLeaderInfo = meetLeaderInfo;
    }

    @Override
    public int getCount() {
        return meetLeaderInfo.getLeadInfosCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_meeting_leader,null);
            viewHolder = new ViewHolder();
            viewHolder.leaderTxt = (TextView) convertView.findViewById(R.id.leader_txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(viewHolder != null){
            viewHolder.leaderTxt.setText(meetLeaderInfo.getLeaderText(position, context));
        }
        return convertView;
    }
}