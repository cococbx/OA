
package com.wanhuiyuan.szoa.jingge;

import android.os.Environment;

/**
 * 常量
 * com.kinggrid.iapppdf.demo.ConstantValue
 * @author wmm
 * create at 2015年8月13日 下午5:13:18
 */
public interface ConstantValue {
	final static int KEY_DOCUMENT_SAVE = 0;//退出保存
	final static int KEY_SINGER = 1;//签名
	final static int KEY_SINGER_DEL = 2;//删除签名
	final static int KEY_FULL_SINGER = 3;//全文批注
	final static int KEY_DEL_FULL_SINGER = 4;// 删除全文批注
	final static int KEY_TEXT_NOTE = 5;//文字注释
	final static int KEY_DEL_TEXT_NOTE = 6;// 删除文字注释
	final static int KEY_SOUND_NOTE = 7;// 语音批注
	final static int KEY_DEL_SOUND_NOTE = 8;// 删除语音批注
	final static int KEY_NOTE_LIST = 9; //批注列表
	final static int KEY_TEXT_LIST = 10;// 文字注释列表
	final static int KEY_SOUND_LIST = 11; //语音批注列表
	final static int KEY_BOOKMARK_LIST = 12;// 大纲
	final static int KEY_CAMERA = 13;// 证件拍照
	final static int KEY_DIGITAL_SIGNATURE = 14;//数字签名
	final static int KEY_VERIFY = 15;//验证
	final static int KEY_SAVEAS = 16; // 另存
	final static int KEY_SAVE_PAGES = 17; // 保存页面图片
	final static int KEY_FIELD_CONTENT = 18; // 获取全部域内容
	final static int KEY_AREA = 19; //区域签批
	final static int KEY_LOCAL_DIGITAL_SIGNATURE = 20;//数字签名
	//final static int KEY_DUPLEX = 21;//同步签批
	//final static int KEY_ABOUT = 22; // 关于界面
	
	
	

/*	final static int TYPE_ANNOT_HANDWRITE = 1;*/
	final static int TYPE_ANNOT_STAMP = 1;//全文批注
	final static int TYPE_ANNOT_TEXT = 2;//文字注释
	final static int TYPE_ANNOT_SIGNATURE = 3;//签名
	final static int TYPE_ANNOT_SOUND = 4;//语音注释
	
	final static String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath().toString();
	
	//intent传递名称,实际使用中根据需要自定义名称
	final String NAME = "demo_name";
	final String LIC = "demo_lic";
	final String CANFIELDEIDT = "demo_fieldEdit";
	final String T7MODENAME = "demo_T7Mode";
	final String EBENSDKNAME = "demo_ebenSDK";
	final String SAVEVECTORNAME = "demo_savevectortopdf";
	final String VECTORNAME = "demo_vectorsign";
	final String VIEWMODENAME = "demo_viewMode";
	final String LOADCACHENAME = "demo_loadCache";
	final String ANNOTPROTECTNAME = "demo_annotprotect";
	final String FILLTEMPLATE = "demo_filltemplate";
	final String ANNOT_TYPE = "demo_annottype";
	
	//阅读模式
	final int VIEWMODE_VSCROLL = 101;
	final int VIEWMODE_SINGLEH = 102;
	final int VIEWMODE_SINGLEV = 103;
	
	//Handler 
	final int MSG_WHAT_DISMISSDIALOG = 201;
	final int MSG_WHAT_LOADANNOTCOMPLETE = 202;
	final int MSG_WHAT_REFRESHDOCUMENT = 203;
	
	//拍照需要的参数
	final int REQUESTCODE_PHOTOS_TAKE = 100;
	final int REQUESTCODE_PHOTOS_CROP = 200;
	
	//签名方式：域定位、位置定位、文字定位、数字签名等
	final int SIGN_MODE_FIELDNAME = 301;
	final int SIGN_MODE_TEXT = 302;
	final int SIGN_MODE_POSITION = 303;
	final int SIGN_MODE_SERVER = 304;
	final int SIGN_MODE_KEY = 305;
	final int SIGN_MODE_BDE = 306;
}

