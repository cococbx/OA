package com.wanhuiyuan.szoa.uiutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utility {

	public static boolean verifyInstallPackage(String packagePath, String crc) {
		InputStream signedData = null;
		MessageDigest sig;
		File packageFile;
		try {
			String digestStr = checkMd5(new File(packagePath));
			crc = crc.toLowerCase();
			if (digestStr.equals(crc)) {// 比较两个文件的MD5值，如果一样则返回true
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		} finally {
			try {
				if (signedData != null)
					signedData.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private static String checkMd5(File f) {

		FileInputStream fis = null;
		byte[] rb = null;
		DigestInputStream digestInputStream = null;
		try {
			fis = new FileInputStream(f);
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digestInputStream = new DigestInputStream(fis, md5);
			byte[] buffer = new byte[4096];
			while (digestInputStream.read(buffer) > 0)
				;
			md5 = digestInputStream.getMessageDigest();
			rb = md5.digest();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rb.length; i++) {
			String a = Integer.toHexString(0XFF & rb[i]);
			if (a.length() < 2) {
				a = '0' + a;
			}
			sb.append(a);
		}
		return sb.toString();
	}

}
