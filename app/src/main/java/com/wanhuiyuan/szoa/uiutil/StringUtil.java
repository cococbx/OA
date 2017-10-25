package com.wanhuiyuan.szoa.uiutil;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import org.bouncycastle.util.encoders.Base64Encoder;

import android.annotation.SuppressLint;
import android.util.Base64;

public class StringUtil {
	/**
	 * 
	 * @param str
	 * @return true为空, fasle不为空
	 */
	public static boolean isNull(String str) {
		if (null != str && str.length() > 0 && !"".equals(str)
				&& !"null".equals(str))
			return false;
		return true;
	}

	public static int[] convertString2Array(String str) {
		int[] array = new int[2];
		String[] strs = str.split(",");
		array[0] = Integer.parseInt(strs[0].substring(1, strs[0].length()));
		array[1] = Integer.parseInt(strs[1].substring(0, strs[1].length() - 1));
		return array;
	}

	public static String getHotel(String str) {
		return null;
	}

	/**
	 * 截断只保留二两小数
	 * 
	 * @param money
	 */
	@SuppressLint("NewApi")
	public static String moneyTruncation(String money) {
		try {
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			decimalFormat.setMaximumFractionDigits(2);
			decimalFormat.setGroupingSize(0);
			decimalFormat.setRoundingMode(RoundingMode.FLOOR);
			return decimalFormat.format(Double.parseDouble(money));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return money;
	}

	// public static String relationship2String(Context context, String
	// relationship) {
	// String[] relations =
	// context.getResources().getStringArray(R.array.bless_relations);
	// for(int i=0; i<relations.length; i++) {
	// if(relations[i].equals(relationship)) {
	// return i + 1 + "";
	// }
	// }
	// return "";
	// }

	// public static String from2String(Context context, String from) {
	// if(context.getResources().getString(R.string.man).equals(from)) {
	// return "1";
	// }else if(context.getResources().getString(R.string.woman).equals(from)) {
	// return "0";
	// } else {
	// return "2";
	// }
	// }

	// public static String number2From(Context context, String fromNumber) {
	// if("1".equals(fromNumber)) {
	// return context.getResources().getString(R.string.man);
	// }else if("0".equals(fromNumber)) {
	// return context.getResources().getString(R.string.woman);
	// } else {
	// return context.getResources().getString(R.string.both);
	// }
	// }

	// public static String number2relation(Context context, String number) {
	// String[] relations =
	// context.getResources().getStringArray(R.array.bless_relations);
	// int num = Integer.parseInt(number);
	// switch (num) {
	// case 1:
	// return relations[0];
	// case 2:
	// return relations[1];
	// case 3:
	// return relations[2];
	// case 4:
	// return relations[3];
	// case 5:
	// return relations[4];
	// case 6:
	// return relations[5];
	// default:
	// break;
	// }
	// return "";
	// }

	public static String md5(String string) {

		byte[] hash;

		try {

			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException("Huh, MD5 should be supported?", e);

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("Huh, UTF-8 should be supported?", e);

		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {

			if ((b & 0xFF) < 0x10)
				hex.append("0");

			hex.append(Integer.toHexString(b & 0xFF));

		}

		return hex.toString();

	}
	
}
