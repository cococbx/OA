package com.wanhuiyuan.szoa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.MeetingPeopleAdapter;
import com.wanhuiyuan.szoa.bean.Dept;
import com.wanhuiyuan.szoa.bean.ListResult;
import com.wanhuiyuan.szoa.bean.MeetingPersonLabel;
import com.wanhuiyuan.szoa.fragment.MeetingRenyuanFragment.UserListThread;
import com.wanhuiyuan.szoa.myview.PullRefreshListView;
import com.wanhuiyuan.szoa.myview.PullRefreshListView.OnRefreshListener;
import com.wanhuiyuan.szoa.uiutil.AppDataControl;
import com.wanhuiyuan.szoa.uiutil.MeetPeopleShowInfo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.tsz.afinal.FinalDb;

public class MeetingPersonFragment extends Fragment {
    String meetingId = "";

    TextView attendTxt;
    PullRefreshListView meetingInfoListView;
    List<MeetPeopleShowInfo> infos = new ArrayList<MeetPeopleShowInfo>();
    MeetingPeopleAdapter meetingPeopleAdapter;
    boolean init = false;

    public MeetingPersonFragment() {
    }

    @SuppressLint("ValidFragment")
    public MeetingPersonFragment(String meetingId) {
        this.meetingId = meetingId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting_person,
                container, false);
        attendTxt = (TextView) view.findViewById(R.id.attend_txt);
        meetingInfoListView = (PullRefreshListView) view.findViewById(R.id.meeting_info_list);
        meetingPeopleAdapter = new MeetingPeopleAdapter(infos, getActivity());
        meetingInfoListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                loadData();
            }

            @Override
            public void onNext() {
                // TODO Auto-generated method stub
                meetingInfoListView.onRefreshComplete();
            }

        });
        //loadData();
        init = true;
        return view;
    }

    void loadData() {
        new fetchDataTask().execute();
    }

    Spanned getAttendInfo(int shouldAttend, int allAttend, int replace) {
        String shouldAttendText = getResources().
                getString(R.string.meeting_should_attend, String.valueOf(shouldAttend));
        shouldAttendText = MeetPeopleShowInfo.
                colorFormatString(shouldAttendText, MeetPeopleShowInfo.blueColor);

        String redNowNum = MeetPeopleShowInfo.
                colorFormatString(String.valueOf(allAttend), MeetPeopleShowInfo.redColor);
        String nowAttendText = getResources().
                getString(R.string.meeting_now_attend, redNowNum);

        String redReplaceNum = MeetPeopleShowInfo.
                colorFormatString(String.valueOf(replace), MeetPeopleShowInfo.redColor);
        String replaceAttendText = getResources().
                getString(R.string.meeting_replace_attend, redReplaceNum);


        String ret = shouldAttendText + " | " + nowAttendText + " | " + replaceAttendText;

        return Html.fromHtml(ret);
    }


    class fetchDataTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String[] params) {
            ListResult<MeetingPersonLabel> result = AppDataControl.getInstance()
                    .getMeetingPeopleList(getActivity(), meetingId);
            if (result == null) {
                return -1;
            }
            List<MeetingPersonLabel> labels = result.getData();
            if (labels == null) {
                return -1;
            }
            infos.clear();
            for (MeetingPersonLabel label : labels) {
                MeetPeopleShowInfo showInfo = new MeetPeopleShowInfo(label);
                infos.add(showInfo);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer ret) {
            meetingInfoListView.onRefreshComplete();
            if (ret.intValue() == 0) {
                meetingPeopleAdapter.setMeetingPeople(infos);
                meetingInfoListView.setAdapter(meetingPeopleAdapter);
                meetingPeopleAdapter.notifyDataSetChanged();
                int shouldAttend = 0;
                int allAttend = 0;
                int replace = 0;
                for (MeetPeopleShowInfo info : infos) {
                    shouldAttend += info.getShouldAttendNum();
                    allAttend += info.getAllAttendNum();
                    replace += info.getReplaceNum();
                }
                Spanned attendInfo = getAttendInfo(shouldAttend, allAttend, replace);
                attendTxt.setText(attendInfo);
            }
        }
    }


    public void pullFresh() {
        if (meetingInfoListView == null || !init) {
            return;
        }
        meetingInfoListView.refresh();
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于Fragment的onResume
            pullFresh();
        } else {
            // 相当于Fragment的onPause
        }
    }


}
