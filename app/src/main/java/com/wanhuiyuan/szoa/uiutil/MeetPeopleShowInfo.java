package com.wanhuiyuan.szoa.uiutil;

import java.util.ArrayList;
import java.util.List;

import com.wanhuiyuan.szoa.bean.MeetingLeaderInfo;
import com.wanhuiyuan.szoa.bean.MeetingPersonInfo;
import com.wanhuiyuan.szoa.bean.MeetingPersonLabel;

import android.text.Html;
import android.text.Spanned;

public class MeetPeopleShowInfo {
    static final public int deptColor = 0xA8A8A8;
    static final public int blueColor = 0x186ae8;
    static final public int redColor = 0xEE2222;
    
    String label;

    MeetingPersonLabel personLabel;
    MeetingLeaderInfo leaderInfo;

    List<MeetingPersonInfo> attendList = new ArrayList<MeetingPersonInfo>();
    List<MeetingPersonInfo> replaceList = new ArrayList<MeetingPersonInfo>();
    List<MeetingPersonInfo> leaveList = new ArrayList<MeetingPersonInfo>();
    List<MeetingPersonInfo> absenceList = new ArrayList<MeetingPersonInfo>();
    
    public MeetPeopleShowInfo(MeetingPersonLabel label){
        personLabel = label;
        initPeopleList();
    }

    List<MeetingPersonInfo> getPeopleListByType(int loginType){
    	
        switch (loginType){
            case MeetingPersonInfo.ATTEND_MEETING:
                return attendList;
            case MeetingPersonInfo.REPLACE_MEETING:
                return replaceList;
            case MeetingPersonInfo.LEAVE_MEETING:
                return leaveList;
            case MeetingPersonInfo.ABSENCE_MEETING:
                return absenceList;
            default:
                return null;
        }

    }

    private void initPeopleList(){
        attendList.clear();
        replaceList.clear();
        leaveList.clear();
        absenceList.clear();
        leaderInfo = new MeetingLeaderInfo(personLabel);
        List<MeetingPersonInfo> userList = personLabel.getUserList();
        label = personLabel.getLabel();
        for(MeetingPersonInfo info: userList){
        	try{
        		int loginType = Integer.valueOf(info.getLoginType()).intValue();
	        	List<MeetingPersonInfo> peopleList = getPeopleListByType(loginType);
		        if(peopleList != null){
		            peopleList.add(info);
		        }
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
    }

    public boolean isNewLine(){
        return personLabel.isNewLine();
    }
    
    public String getLabel(){
    	return label;
    }

    String deptFormat(String dept){
        return String.format("[%s]", dept);
    }
    
    static public String colorFormatString(String text, int color){
        return String.format("<font color=\"#%x\">%s</font>", color, text);
    }

    public Spanned getMeetingStrByType(int loginType){
        List<MeetingPersonInfo> peopleList = getPeopleListByType(loginType);
        if(peopleList == null || peopleList.size() == 0){
            return Html.fromHtml((""));
        }
        String curDeptId = "";
        StringBuffer retBuf = new StringBuffer();

        for(MeetingPersonInfo userInfo:peopleList){
            if(!curDeptId.equals(userInfo.getDeptId())){
                String deptStr = colorFormatString(deptFormat(userInfo.getDeptName()), deptColor);
                retBuf.append(deptStr);
                curDeptId = userInfo.getDeptId();
            }else {
                retBuf.append(colorFormatString("|", deptColor));
            }
            retBuf.append(userInfo.getTruename());
            if(userInfo.getLoginType().equals("2")){
                retBuf.append(colorFormatString("(" + userInfo.getWillName() + ")", deptColor));
            }
        }

        return Html.fromHtml(retBuf.toString());
    }

    public MeetingLeaderInfo getLeaderInfo(){
        return leaderInfo;
    }

    public int getShouldAttendNum(){
        return attendList.size() + replaceList.size() + absenceList.size();
    }

    public int getAllAttendNum(){
        return attendList.size() + replaceList.size();
    }

    public int getReplaceNum(){
        return replaceList.size();
    }
    
    public int getLeaveNum(){
    	return leaveList.size();
    }
}

