/*
 * MainActivity.java
 * classes : com.kinggrid.iapppdf.demo.MainActivity
 * @author tubozhi
 * V 1.0.0
 * Create at 2014年5月19日  下午2:46:46
 */
package com.wanhuiyuan.szoa.jingge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.kinggrid.iapppdf.demo.apdater.BookAdapter;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;

/**
 * com.kinggrid.iapppdf.demo.MainActivity
 * 
 * @author 涂博之<br/>
 *         create at 2014年5月19日 下午2:46:46
 */
public class MainActivity extends Activity implements ConstantValue {
	private static final String TAG = "MainActivity";

	private EditText userName;
	/**
	 * 阅读模式：连续、单页横向或单页纵向
	 */
	private	RadioGroup viewMode_RadioGroup;
	/**
	 * 设备模式：普通类或E人E本类
	 */
	private	RadioGroup deviceMode_RadioGroup;
	/**
	 * 签批保存模式：图片或矢量
	 */
	private	RadioGroup signMode_RadioGroup;
	/**
	 * 导入导出批注类型：全文批注、签名、文字注释或语音注释
	 */
	private RadioGroup annotType_RadioGroup;
	
	/**
	 * 可选功能CheckBox
	 */
	private CheckBox isFiledEdit,isAnnotProtect,isFillTemplate,isSave_history;
	/**
	 * 是否保存矢量信息到PDF文档CheckBox
	 */
	private CheckBox isSaveVector_box;
	/**
	 * PDF文件列表
	 */
	private ListView bookList;
	private BookAdapter adapter;
	private List<String> fileData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		copyFiles();//拷贝数据，正常使用中可忽略
		initView(); //初始化界面
		initData(); //初始化监听及数据等
		
		//注册保存文档后的广播，在实际使用中可根据需要选择是否注册
		IntentFilter filter = new IntentFilter(
				"com.kinggrid.iapppdf.broadcast.savepdf");
		registerReceiver(receiver, filter);
	}
	/**
	 * 拷贝PDF到设备本地，方便演示功能
	 * @param fileName
	 * @param toFilePath
	 * @throws IOException
	 */
	private void copyAssetsFileToSDCard(String fileName, String toFilePath)
			throws IOException {
		File file = new File(toFilePath);
		if (file.exists()) {
			return;
		}
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(toFilePath);
		myInput = getAssets().open(fileName);
		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}
		myOutput.flush();
		myInput.close();
		myOutput.close();
	}
	/**
	 * 为了方便使用，拷贝一些文件到设备本地，在实际使用中可忽略
	 */
	private void copyFiles(){
		try {
			copyAssetsFileToSDCard("testsignature.jpg", SDCARD_PATH + "/testSignature.jpg");
			copyAssetsFileToSDCard("dx.pdf", SDCARD_PATH + "/电信受理单.pdf");
			copyAssetsFileToSDCard("yd.pdf", SDCARD_PATH + "/移动受理单.pdf");
			copyAssetsFileToSDCard("dx_fill.pdf", SDCARD_PATH + "/电信模板(带回执单).pdf");
			copyAssetsFileToSDCard("ga_fill.PDF", SDCARD_PATH + "/公安模板.pdf");
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void initView() {
		
		userName = (EditText) findViewById(R.id.user_name);
		bookList = (ListView) findViewById(R.id.book_directory);
		viewMode_RadioGroup = (RadioGroup) findViewById(R.id.viewmode);
		deviceMode_RadioGroup = (RadioGroup) findViewById(R.id.modes);
		signMode_RadioGroup = (RadioGroup) findViewById(R.id.sign_mode);
		annotType_RadioGroup = (RadioGroup) findViewById(R.id.annot_type);
		
		isFiledEdit = (CheckBox) findViewById(R.id.can_field_edit);
		isAnnotProtect = (CheckBox) findViewById(R.id.annot_protect);
		isFillTemplate = (CheckBox) findViewById(R.id.fill_template);
		isSave_history = (CheckBox) findViewById(R.id.save_history);
		isSaveVector_box = (CheckBox) findViewById(R.id.isSaveVector);
	}
	/**
	 * 初始化数据、监听等
	 */
	private void initData(){
		adapter = new BookAdapter(this);
		fileData = getData(SDCARD_PATH);
		adapter.setFileData(fileData);
		bookList.setAdapter(adapter);
		bookList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String s = fileData.get(position);
				String fileName = SDCARD_PATH + "/"  + s;
				doOpenFile(fileName);
			}
		});
		viewMode_RadioGroup.setTag(R.id.viewmode_vs);
		viewMode_RadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						viewMode_RadioGroup.setTag(group
								.getCheckedRadioButtonId());
					}
				});

		deviceMode_RadioGroup.setTag(R.id.common_mode);
		deviceMode_RadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if(group.getCheckedRadioButtonId() == R.id.common_mode){
							signMode_RadioGroup.setVisibility(View.VISIBLE);
						} else {
							signMode_RadioGroup.setVisibility(View.GONE);
						}
						if(group.getCheckedRadioButtonId() == R.id.eben_mode){
							isSaveVector_box.setVisibility(View.VISIBLE);
						} else {
							isSaveVector_box.setVisibility(View.GONE);
						}
						deviceMode_RadioGroup.setTag(group.getCheckedRadioButtonId());
					}
				});
		signMode_RadioGroup.setTag(R.id.vector_sign);
		signMode_RadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						signMode_RadioGroup.setTag(group.getCheckedRadioButtonId());
					}
				});
		
		annotType_RadioGroup.setTag(R.id.annot_stmap);
		annotType_RadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						annotType_RadioGroup.setTag(group.getCheckedRadioButtonId());
					}
					
				});
	}
	/**
	 * 获取指定路径的PDF文档列表
	 * @param filePath 需要扫描的路径
	 * @return 文档名称列表表
	 */
	private List<String> getData(String filePath) {
		List<String> filedata = new ArrayList<String>();
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] fileArray = file.listFiles(new FileNameSelector(
					"pdf||.pdf||.PDF"));
			if (null != fileArray && fileArray.length > 0) {
				for (int i = 0; i < fileArray.length; i++) {
					if (!fileArray[i].isDirectory()) {
						filedata.add(fileArray[i].getName());
					}
				}
			}
		}

		return filedata;
	}
	/**
	 * 打开继承金格控件的Activity，并传递需要的参数
	 * @param filepath 要打开的文件路径
	 */
	private void doOpenFile(String filepath) {
		File file = new File(filepath);
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent("android.intent.action.VIEW", uri);
		intent.setClassName(this,"com.kinggrid.iapppdf.demo.BookShower");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//传递用户名，默认admin
		intent.putExtra(NAME, userName.getText().toString());
		//传递授权码(必传)
		intent.putExtra(LIC, MyApplication.copyRight);
		//是否支持域编辑功能，在表单PDF文件中可体现此功能，默认为false
		intent.putExtra(CANFIELDEIDT, isFiledEdit.isChecked());//可选值为布尔值
		//文档保存之后批注是否只读，默认为false,不需要修改请忽略此参
		intent.putExtra(ANNOTPROTECTNAME, isAnnotProtect.isChecked());
		//是否为E人E本模式，默认为false
		if((Integer)deviceMode_RadioGroup.getTag() == R.id.eben_mode){
			
			intent.putExtra(T7MODENAME, true);
			intent.putExtra(VECTORNAME, true);//可选值为布尔值
			//是否保存矢量信息到PDF文档中，默认为true(支持单笔删除，但较慢)，为false时删除一次的手写内容,仅适用于E本模式
			intent.putExtra(SAVEVECTORNAME, isSaveVector_box.isChecked());
			
		} else if((Integer)deviceMode_RadioGroup.getTag() == R.id.common_mode){
			
			intent.putExtra(T7MODENAME, false);
			
			//是否选用矢量方式，保存签批时通过此参判断是矢量方式还是图片方式保存
			if((Integer)signMode_RadioGroup.getTag() == R.id.vector_sign){
				intent.putExtra(VECTORNAME, true);
			} else {
				intent.putExtra(VECTORNAME, false);
			}
		} /*else if((Integer)deviceMode_RadioGroup.getTag() == R.id.eben_sdk_mode){
			//是否使用E人E本手写SDK，需要另外引用Eben SDK，默认为false
			intent.putExtra(EBENSDKNAME, true);
		}*/
		intent.putExtra(ANNOT_TYPE, sureAnnotType());
		//是否保留PDF上次阅读位置，默认为true,为false时每次都从第一页开始阅读
		intent.putExtra(LOADCACHENAME, isSave_history.isChecked());
		//是否填充模板(选用)
		intent.putExtra(FILLTEMPLATE, isFillTemplate.isChecked());
		//阅读模式，默认PageViewMode.VSCROLL，竖向连续翻页,不需要重设可忽略
		intent.putExtra(VIEWMODENAME,surePageViewMode());
		startActivity(intent);
	}

	/**
	 * 接收保存文档后的广播，在实际使用中可根据需要选择是否接收
	 */
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.kinggrid.iapppdf.broadcast.savepdf")) {
				String isSuccessed = intent.getStringExtra("success");
				if (isSuccessed != null && isSuccessed.equals("TRUE")) {
					Toast.makeText(getApplicationContext(), "保存成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "保存失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private int sureAnnotType(){
		int type = 0;
		switch((Integer)annotType_RadioGroup.getTag()){
		case R.id.annot_stmap:
			type = TYPE_ANNOT_STAMP;
			break;
		case R.id.annot_text:
			type = TYPE_ANNOT_TEXT;
			break;
		case R.id.annot_sound:
			type = TYPE_ANNOT_SOUND;
			break;
		}
		
		return type;
	}
	/**
	 * 授权码
	 * @return
	 */
	private String sureCopyRight() {
		//金格科技iApp试用许可A02    过期时间=2016-3-22
		//String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU368sLBQG57FhM8Bkq7aPAVrvKeTdxzwi2Wk0Yn+WSxoXx6aD2yiupr6ji7hzsE6/QRx89Izb7etgW5cXVl5PwISjX+xEgRoNggvmgA8zkJXOVWEbHWAH22+t7LdPt+jENUl53rvJabZGBUtMVMHP2J32poSbszHQQyNDZrHtqZuuSgCNRP4FpYjl8hG/IVrYXSo6k2pEkUqzd5hh5kngQSOW8fXpxdRHfEuWC1PB9ruQ=";
		//金格科技iApp试用许可A02    过期时间=2016-5-31
		String copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU368sLBQG57FhM8Bkq7aPAVhuziP3rfQCP2RudLVf7IBbx6aD2yiupr6ji7hzsE6/QRx89Izb7etgW5cXVl5PwIaPZfsrCNYHp6aNc1JCEMBGVWEbHWAH22+t7LdPt+jENP768Cf/WDYfN4yHYvhvNM6cd2k72+1SjzDw0qWe3PQeSgCNRP4FpYjl8hG/IVrYX0L1mZ1SqnOWw8XrnxNWUAeW8fXpxdRHfEuWC1PB9ruQ=";
		return copyRight;
	}
	/**
	 * PDF文档显示模式
	 * @return
	 */
	private int surePageViewMode() {
		int mode = VIEWMODE_VSCROLL;
		switch ((Integer) viewMode_RadioGroup.getTag()) {
		case R.id.viewmode_vs:
			mode = VIEWMODE_VSCROLL;
			break;
		case R.id.viewmode_singleh:
			mode = VIEWMODE_SINGLEH;
			break;
		case R.id.viewmode_singlev:
			mode = VIEWMODE_SINGLEV;
			break;
		}
		return mode;
	}

	/**
	 * 文件过滤器
	 * com.kinggrid.iapppdf.demo.FileNameSelector
	 * @author wmm
	 * create at 2015年8月14日 上午9:40:37
	 */
	public class FileNameSelector implements FilenameFilter {
		String extension = ".";

		public FileNameSelector(String fileExtensionNoDot) {
			extension += fileExtensionNoDot;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith("pdf") || name.endsWith("PDF");
		}
	}
}
