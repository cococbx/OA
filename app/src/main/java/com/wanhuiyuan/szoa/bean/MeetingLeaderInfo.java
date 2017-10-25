package com.wanhuiyuan.szoa.bean;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.List;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.uiutil.MeetPeopleShowInfo;

/**
 * Created by victory on 2016/11/21.
 */

public class MeetingLeaderInfo {
    private List<List<MeetingPersonInfo>> leaderInfosList;

    public MeetingLeaderInfo(MeetingPersonLabel personLabel){
        leaderInfosList = new ArrayList<List<MeetingPersonInfo>>();

        if(personLabel.getUserList() == null || personLabel.getUserList().isEmpty()){
            return;
        }
        String curDeptId = personLabel.getUserList().get(0).getDeptId();
        List<MeetingPersonInfo> leaderInfos = new ArrayList<MeetingPersonInfo>();

        for(MeetingPersonInfo personInfo:personLabel.getUserList()){
            if(personInfo.getDeptId().equals(curDeptId)) {
                leaderInfos.add(personInfo);
            }else {
                leaderInfosList.add(leaderInfos);
                curDeptId = personInfo.getDeptId();
                leaderInfos = new ArrayList<MeetingPersonInfo>();
                leaderInfos.add(personInfo);
            }
        }
        leaderInfosList.add(leaderInfos);
    }

    public int getLeadInfosCount(){
        return leaderInfosList.size();
    }

    public Spanned getLeaderText(int position, Context context){
        if(position >= leaderInfosList.size()){
            return Html.fromHtml("");
        }
        List<MeetingPersonInfo> infoList = leaderInfosList.get(position);
        StringBuffer attendTxt = new StringBuffer("");
        StringBuffer absenceTxt = new StringBuffer("");
        StringBuffer leaveTxt = new StringBuffer("");
        for(MeetingPersonInfo personInfo:infoList){
            int loginType = Integer.valueOf(personInfo.getLoginType()).intValue();
            switch (loginType){
                case MeetingPersonInfo.ATTEND_MEETING:
                case MeetingPersonInfo.REPLACE_MEETING:
                    if(attendTxt.length() != 0){
                        attendTxt.append(MeetPeopleShowInfo.colorFormatString("|", MeetPeopleShowInfo.deptColor));
                    }
                    attendTxt.append(personInfo.getTruename());
                    break;
                case MeetingPersonInfo.ABSENCE_MEETING:
                    if(absenceTxt.length() != 0){
                        absenceTxt.append(MeetPeopleShowInfo.colorFormatString("|", MeetPeopleShowInfo.deptColor));
                    }
                    absenceTxt.append(personInfo.getTruename());
                    break;
                case MeetingPersonInfo.LEAVE_MEETING:
                    if(leaveTxt.length() != 0){
                        leaveTxt.append(MeetPeopleShowInfo.colorFormatString("|", MeetPeopleShowInfo.deptColor));
                    }
                    leaveTxt.append(personInfo.getTruename());
                    break;
                default:
                    break;
            }
        }

        String outStr = "";
        if(attendTxt.length() != 0){
            String attendHead = "[" + context.getResources().getString(R.string.meeting_type_attend) + "]";
            outStr += MeetPeopleShowInfo.colorFormatString(attendHead, MeetPeopleShowInfo.deptColor);
            outStr += attendTxt;
        }

        if(absenceTxt.length() != 0){
            String absenceHead = "[" + context.getResources().getString(R.string.meeting_type_absence) + "]";
            outStr += MeetPeopleShowInfo.colorFormatString(absenceHead, MeetPeopleShowInfo.deptColor);
            outStr += absenceTxt;
        }

        if(leaveTxt.length() != 0){
            String leaveHead = "[" + context.getResources().getString(R.string.meeting_type_leave) + "]";
            outStr += MeetPeopleShowInfo.colorFormatString(leaveHead, MeetPeopleShowInfo.deptColor);
            outStr += leaveTxt;
        }

        return Html.fromHtml(outStr);
    }
}
