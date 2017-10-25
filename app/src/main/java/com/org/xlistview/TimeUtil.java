package com.org.xlistview;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ʱ�乤����
 * 
 * @author way
 * 
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public static String converTime(long time) {
		long currentSeconds = System.currentTimeMillis() / 1000;
		long timeGap = currentSeconds - time / 1000;// ������ʱ���������
		String timeStr = null;
		if(timeGap > 3*24 *60*60){
			timeStr = getDayTime(time) + " " + getMinTime(time);
		}
		else if (timeGap > 24 * 2 * 60 * 60) {// 2�����Ͼͷ��ر�׼ʱ��
			timeStr = "ǰ�� " + getMinTime(time);
		} else if (timeGap > 24 * 60 * 60) {// 1��-2��
			timeStr = timeGap / (24 * 60 * 60) + "���� " + getMinTime(time);
		} else if (timeGap > 60 * 60) {// 1Сʱ-24Сʱ
			timeStr = timeGap / (60 * 60) + "���� " + getMinTime(time);
		} else if (timeGap > 60) {// 1����-59����
			timeStr = timeGap / 60 + "���� " + getMinTime(time);
		} else {// 1����-59����
			timeStr = "���� " + getMinTime(time);
		}
		return timeStr;
	}

	public static String getChatTime(long time) {
		return getMinTime(time);
	}

	public static String getPrefix(long time) {
		long currentSeconds = System.currentTimeMillis();
		long timeGap = currentSeconds - time;// ������ʱ���
		String timeStr = null;
		if (timeGap > 24 * 3 * 60 * 60 * 1000) {
			timeStr = getDayTime(time) + " " + getMinTime(time);
		} else if (timeGap > 24 * 2 * 60 * 60 * 1000) {
			timeStr = "ǰ�� " + getMinTime(time);
		} else if (timeGap > 24 * 60 * 60 * 1000) {
			timeStr = "���� " + getMinTime(time);
		} else {
			timeStr = "���� " + getMinTime(time);
		}
		return timeStr;
	}

	public static String getDayTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
		return format.format(new Date(time));
	}

	public static String getMinTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}
}
