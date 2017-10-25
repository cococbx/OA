/*
 * BookShower.java
 * classes : com.kinggrid.iapppdf.demo.BookShower
 * @author 涂博之
 * V 1.0.0
 * Create at 2014年5月20日 下午4:35:00
 */
package com.wanhuiyuan.szoa.jingge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.tsz.afinal.FinalDb;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kinggrid.iapppdf.core.codec.OutlineLink;
import com.kinggrid.iapppdf.demo.dialogs.NoteDialog;
import com.kinggrid.iapppdf.demo.dialogs.OutlineDialog;
import com.kinggrid.iapppdf.ui.viewer.IAppPDFActivity;
import com.kinggrid.iapppdf.ui.viewer.PDFHandWriteView;
import com.kinggrid.iapppdf.ui.viewer.ViewerActivityController.LoadPageFinishedListener;
import com.kinggrid.pdfservice.Annotation;
import com.kinggrid.pdfservice.PageViewMode;
import com.kinggrid.signature.commen.FileUtils;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Postil;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.myview.RoundProgressBar;
import com.wanhuiyuan.szoa.service.UITimeReceiver;
import com.wanhuiyuan.szoa.service.UITimeReceiver.TimeBackCall;
import com.wanhuiyuan.szoa.uiutil.AnimationUtil;
import com.wanhuiyuan.szoa.uiutil.SharePreferences;
import com.wanhuiyuan.szoa.uiutil.WSDLManager;

/**
 * com.kinggrid.iapppdf.demo.BookShower
 * 
 * @author 涂博之 <br/>
 *         create at 2014年5月20日 下午4:35:00
 */
@SuppressLint("NewApi")
public class BookShower extends IAppPDFActivity implements ConstantValue,
		TimeBackCall, OnClickListener{

	private static final String TAG = "BookShower";

	private FrameLayout frameLayout;
	private LinearLayout  pizhuLayout;//左边菜单按钮布局
	private Context context;

	private boolean hasLoaded = false;//是否第一次加载

	private final String SAVESIGNFINISH_ACTION = "com.kinggrid.iapppdf.broadcast.savesignfinish";
	
	/**
	 * 全文批注按钮
	 */
	public ImageButton btnClose, btnClear, btnRedo, btnSave, btnPen,btnUndo;

	private TextView file_titile;
	private PDFHandWriteView full_handWriteView;
	private View handwriteView_layout;//手写批注工具栏布局
	private NoteDialog note_dlg;
	private AnnotUtil annotUtil;
	String fileName;// 文件名
	String fileRealName;// 文件真实名
	String fileId;// 文件id
	String meetingId;//会议id
	Button wdml, qpml, pzml, sqml;// 文档目录、签批目录、批注目录、书签目录（书签又不用了）
	FinalDb db;
	RoundProgressBar roundProgressBar;//倒计时进度条
	SeekBar gopageSeekBar;// 滑到进度条
	EditText gopageEditText, jumppageEv;// 第一个是进度条旁边的跳转页输入框，第二个是黑色背景的页面跳转输入框
	TextView gopageTextView, nowpage, nowpageTv, sumpageTv, jumppageTv;// nowpageTv是黑色背景的当前页，sumpageTv黑色背景总页数，jumppageTv黑色背景的跳转按钮
	LinearLayout tab_rdogroup, pageLayout;//底部菜单，页数布局
	boolean isPressWithMe = false;//是否是主动滑动翻页
	UITimeReceiver timeReceiver;
	boolean isFullScreen = false;
	int postilVersion = 0;//批注版本号
	ObjectAnimator translationUp;
	boolean isToolsHide = true;//底部菜单是否隐藏
	int maxPage = 0;//总页数
	CheckBox fl_pen,img_Float;//左边悬浮手写批注图标,更多按钮图标;
	CheckBox fl_annotation, back_imgbtn, locked, lockopen;//左边悬浮文字批注图标、返回、锁定方向
	public static int currpage=0; //外部调用当前页的变量
	boolean penOpen = false;//全文批注是否打开

	// 滑动条进度值获取
	private int SeekBararg1;
	
	private long firClick;
	private long secClick;
	
	//是否在页码输入状态下,如果在输入状态下按回车键后将直接跳转
	private boolean IsPageNumsInputIng = false;

	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		context = this;
		setContentView(R.layout.book);
		//去掉虚拟按键全屏显示  
		// 调用日志调试组件
		LogPrinter.getInstance(this).start();

		// imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		frameLayout = (FrameLayout) findViewById(R.id.book_frame);
		this.initPDFParams();
		super.initPDFView(frameLayout);
		this.initUI();
		this.initParentListener();
		super.setLoadingText(R.string.msg_loading_tip);
		annotUtil = new AnnotUtil(this, userName);
		timeReceiver = UITimeReceiver.newInstance(this, this);
		
	}

	private void initUI() {
		pizhuLayout = (LinearLayout) findViewById(R.id.pizhuLayout);
		roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
		tab_rdogroup = (LinearLayout) findViewById(R.id.tab_rdogroup);
		tab_rdogroup.setVisibility(View.GONE);

		pageLayout = (LinearLayout) findViewById(R.id.pageLayout);
		pageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				jumppageTv.setVisibility(View.VISIBLE);
				jumppageEv.setVisibility(View.VISIBLE);
				nowpageTv.setVisibility(View.GONE);
				
				//编辑框自动获得焦点，自动弹出输入框
				jumppageEv.requestFocus();
				InputMethodManager imm = ( InputMethodManager ) jumppageEv.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
		        imm.showSoftInput(jumppageEv,InputMethodManager.SHOW_FORCED); 
				
				//进入键盘输入状态
				IsPageNumsInputIng = true;
			}
		});

		gopageSeekBar = (SeekBar) findViewById(R.id.gopageSeekBar);
		gopageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				isPressWithMe = false;
//				if (progressBarStatus == 0) {
					int go = (maxPage * SeekBararg1) / 100;
 					if (go == 0)
						jumpToPage(0);
					else
						jumpToPage(go - 1);
					// isSeek = true;
//				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				isPressWithMe = true;
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				SeekBararg1 = arg1;
				int go = (maxPage * SeekBararg1) / 100;
 
				// 最后一页
				if (go == maxPage)
					go = go - 1;
				// nowpage.setText((go + 1) + "/" + maxPage);
				nowpageTv.setText((go + 1) + "");
				sumpageTv.setText("/" + maxPage);

			}
		});

		file_titile = (TextView) findViewById(R.id.file_titile);
		file_titile.setText(getIntent().getStringExtra("fileRealName"));

		gopageTextView = (TextView) findViewById(R.id.gopageTextView);
		nowpage = (TextView) findViewById(R.id.nowpage);
		jumppageEv = (EditText) findViewById(R.id.jumppageEv);
		nowpageTv = (TextView) findViewById(R.id.nowpageTv);
		sumpageTv = (TextView) findViewById(R.id.sumpageTv);
		jumppageTv = (TextView) findViewById(R.id.jumppageTv);
		gopageEditText = (EditText) findViewById(R.id.gopageEditText);
		jumppageTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//跳转
				JumpToPage();
			}
			
			
		});
		jumppageEv.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				4) });
		
		//对编辑框输入的内容进行捕获，如果输入发现了回车键直接进行调整
		jumppageEv.setOnKeyListener(onKeyListener); 
		
		
		gopageEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
		img_Float = (CheckBox) findViewById(R.id.img_Float);
		img_Float.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (!isToolsHide) {
					tab_rdogroup.setVisibility(View.GONE);
					tab_rdogroup.setAnimation(AnimationUtil.moveToViewBottom());

					isToolsHide = true;
				} else {
					tab_rdogroup.setVisibility(View.VISIBLE);
					tab_rdogroup.setAnimation(AnimationUtil
							.moveToViewLocation());
					isToolsHide = false;

				}

			}
		});


		wdml = (Button) findViewById(R.id.tab_jg_wdml);
		qpml = (Button) findViewById(R.id.tab_jg_qpml);
		pzml = (Button) findViewById(R.id.tab_jg_pzml);

		sqml = (Button) findViewById(R.id.tab_jg_sqml);
		sqml.setVisibility(View.GONE);

		fl_annotation = (CheckBox) findViewById(R.id.fl_annotation);
		fl_pen = (CheckBox) findViewById(R.id.fl_pen);
		back_imgbtn = (CheckBox) findViewById(R.id.back_imgbtn);
		locked = (CheckBox) findViewById(R.id.locked);
		lockopen = (CheckBox) findViewById(R.id.lockopen);
		
		/**
		 * 更改状态栏透明度，数值越高，透明度越小，字体颜色更改在布局文件内，有需要在改正
		 * cyt
		 */
		int alpha  = 95;
		fl_annotation.getBackground().setAlpha(alpha);
		back_imgbtn.getBackground().setAlpha(alpha);
		locked.getBackground().setAlpha(alpha);
		lockopen.getBackground().setAlpha(alpha);
		fl_pen.getBackground().setAlpha(alpha);
		img_Float.getBackground().setAlpha(alpha);
		lockopen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				lockScreen();
				lockopen.setVisibility(View.GONE);
				locked.setVisibility(View.VISIBLE);
			}
		});
		locked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				unLockScreen();
				lockopen.setVisibility(View.VISIBLE);
				locked.setVisibility(View.GONE);
			}
		});
		fl_annotation.setOnClickListener(this);
		fl_pen.setOnClickListener(this);
		back_imgbtn.setOnClickListener(this);

		wdml.setOnClickListener(this);
		qpml.setOnClickListener(this);
		pzml.setOnClickListener(this);
		sqml.setOnClickListener(this);

		gopageTextView.setOnClickListener(this);
		
	}
	//键盘监听
    private OnKeyListener onKeyListener = new OnKeyListener() {  
        
        @Override  
        public boolean onKey(View v, int keyCode, KeyEvent event) {  
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){ 
            	
            	JumpToPage();
                  
                return true;  
            }  
            return false;  
        }  
    };  
	
	//开始跳转
	private void JumpToPage(){

		// TODO Auto-generated method stub
		/*
		 * 判断输入页数和最大页数相比较，如果输入页数大于最大页数则提示输入有误请重新输入，如果不大于最大页数，则执行跳转操作
		 * cyt
		 */
		if (jumppageEv.getText().toString().length() > 0) {
			
			if(Integer.valueOf(jumppageEv.getText().toString())>maxPage){
				MyToast.show(context, "您输入的页码有误，请重新输入！");
				return;
			}
			
			
			int go = Integer.parseInt(jumppageEv.getText().toString());
			if (go > 0 && go < 10000) {
				if (go - 1 != getCurrentPageNo())
					jumpToPage(go - 1);
				//todo
				//Log.e(TAG, String.format("go:%d getCurrentPageNo:%d", go, getCurrentPageNo()));
				if(getCurrentPageNo() != go - 1){
					jumpToPage(go - 1);
					Log.e(TAG, "jump failed!jump again!");
				}
				nowpageTv.setText((getCurrentPageNo() + 1) + "");
			}
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(jumppageEv.getWindowToken(), 0);
		}
		jumppageTv.setVisibility(View.GONE);
		jumppageEv.setVisibility(View.GONE);
		nowpageTv.setVisibility(View.VISIBLE);
		
		//结束键盘输入状态，跳转后
		IsPageNumsInputIng = false;
		
		
	}

	@Override
	public void onClick(View v) {
		if (IAppPDFActivity.progressBarStatus == 1) {
			MyToast.show(context, "正在加载页面，请稍后尝试！");
			return;
		}
		switch (v.getId()) {
		case R.id.fl_annotation:
			if (penOpen) {
				if (full_handWriteView.canSave()) {
					final View dialog_view = getLayoutInflater().inflate(
							R.layout.insert_progress, null);
					Runnable runnable = new Runnable() {

						@Override
						public void run() {

							insertVertorFlag = 1;
							doSaveHandwriteInfo(true, false, full_handWriteView);
							Message msg = new Message();
							msg.what = MSG_WHAT_DISMISSDIALOG;
							msg.obj = dialog_view;
							myHandler.sendMessage(msg);
						}
					};
					Thread thread = new Thread(runnable);
					thread.start();
				} 
				/*
				 * else { Message msg = new Message(); msg.what =
				 * MSG_WHAT_DISMISSDIALOG; myHandler.sendMessage(msg); }
				 */
				gopageEditText.setEnabled(true);
				gopageSeekBar.setEnabled(true);

				penOpen = false;

			}
			openTextAnnotation();
			break;
		case R.id.fl_pen:
			if (!penOpen) {
				openHardWrite();
				penOpen = true;
			} else {

				if (full_handWriteView.canSave()) {
					// 添加等待交互
					final View dialog_view = getLayoutInflater().inflate(
							R.layout.insert_progress, null);
					showViewToPDF(dialog_view);
					Runnable runnable = new Runnable() {

						@Override
						public void run() {

							insertVertorFlag = 1;
							doSaveHandwriteInfo(true, false, full_handWriteView);
							// 取消等待交互
							Message msg = new Message();
							msg.what = MSG_WHAT_DISMISSDIALOG;
							msg.obj = dialog_view;
							myHandler.sendMessage(msg);
						}
					};
					Thread thread = new Thread(runnable);
					thread.start();
				} else {
					Message msg = new Message();
					msg.what = MSG_WHAT_DISMISSDIALOG;
					myHandler.sendMessage(msg);

				}
				gopageEditText.setEnabled(true);
				gopageSeekBar.setEnabled(true);
				penOpen = false;
			}
			break;
		case R.id.back_imgbtn:
			saveJson();
			break;
		case R.id.tab_jg_wdml:
			setAnnotOpeFalse();
			openOutlineList();
			break;
		case R.id.tab_jg_qpml:
			setAnnotOpeFalse();
			openAnnotationList(TYPE_ANNOT_STAMP);
			break;
		case R.id.tab_jg_pzml:
			setAnnotOpeFalse();
			openAnnotationList(TYPE_ANNOT_TEXT);
			break;
		case R.id.tab_jg_sqml:
			// openAnnotationList(TYPE_ANNOT_TEXT);
			// BookMarkDialog bmd = new BookMarkDialog(BookShower.this,
			// BookShower.this, getBookMark());
			// bmd.show();
			break;
		case R.id.gopageTextView:
			if (gopageEditText.getText().toString().length() > 0) {
				int go = Integer.parseInt(gopageEditText.getText().toString());
				if (go > 0 && go < 10000)
					if (go - 1 != getCurrentPageNo())
						jumpToPage(go - 1);
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(gopageTextView.getWindowToken(), 0);
			}
			break;
		}

	}

	/**
	 * 初始化金格控件的参数,根据各自需求重写
	 */
	private void initPDFParams() {
		Intent book_intent = getIntent();
		// 用户名，默认admin
		if (book_intent.hasExtra(NAME)) {
			userName = book_intent.getStringExtra(NAME); // 用户名，默认admin
		}
		// 授权码(必传)
		if (book_intent.hasExtra(LIC)) {
			copyRight = book_intent.getStringExtra(LIC); // 授权码，必传，字符串格式
		}
		// 是否支持域编辑功能，在表单PDF文件中可体现此功能，默认为false
		if (book_intent.hasExtra(CANFIELDEIDT)) {
			isFieldEidt = book_intent.getBooleanExtra(CANFIELDEIDT, false); // 是否支持域编辑功能，默认为false
		}
		// 是否为E人E本模式，默认为false
		if (book_intent.hasExtra(T7MODENAME)) {
			isSupportEbenT7Mode = book_intent
					.getBooleanExtra(T7MODENAME, false);
		}
		if (book_intent.hasExtra(EBENSDKNAME)) {
			isUseEbenSDK = book_intent.getBooleanExtra(EBENSDKNAME, false);
		}
		// 是否保存矢量信息到PDF文档中，默认为true(支持单笔删除，但较慢)，为false时删除一次的手写内容
		if (book_intent.hasExtra(SAVEVECTORNAME)) {
			isSaveVector = book_intent.getBooleanExtra(SAVEVECTORNAME, true);
		}
		// 是否选用矢量方式，保存签批时通过此参判断是矢量方式还是图片方式保存
		if (book_intent.hasExtra(VECTORNAME)) {
			isVectorSign = book_intent.getExtras().getBoolean(VECTORNAME);
		}
		// 阅读模式，默认PageViewMode.VSCROLL，竖向连续翻页,不需要重设可忽略
		if (book_intent.hasExtra(VIEWMODENAME)) {
			int mode = book_intent.getIntExtra(VIEWMODENAME, VIEWMODE_VSCROLL);
			switch (mode) {
			case VIEWMODE_VSCROLL:
				pageViewMode = PageViewMode.VSCROLL;
				break;
			case VIEWMODE_SINGLEV:
				pageViewMode = PageViewMode.SINGLEV;
				break;
			case VIEWMODE_SINGLEH:
				pageViewMode = PageViewMode.SINGLEH;
				break;
			}
		}
		// 是否保留PDF上次阅读位置，默认为true,为false时每次都从第一页开始阅读
		if (book_intent.hasExtra(LOADCACHENAME)) {
			loadPDFReaderCache = book_intent.getBooleanExtra(LOADCACHENAME,
					true);
		}
		fileName = book_intent.getStringExtra("fileName");
		fileRealName = book_intent.getStringExtra("fileRealName");
		fileId = book_intent.getStringExtra("fileId");
		meetingId = book_intent.getStringExtra("meetingId");
	}

	/**
	 * 监听金格控件
	 */
	private void initParentListener() {

		// 加载PDF文件结束监听，根据需求选择是否监听
		getController().setLoadPageFinishedListener(
				new LoadPageFinishedListener() {
					@Override
					public void onLoadPageFinished() {
						Log.i(TAG, "文档加载完成");
						// if (getHaveBookMark()) {
						// bookmark_Float.setChecked(true);
						// } else {
						// bookmark_Float.setChecked(false);
						// }
						if (!hasLoaded) {
							// Thread thread = new Thread(new
							// LoadAnnotations());
							// thread.start();
							maxPage = getPageCount();
							pizhuLayout.setVisibility(View.VISIBLE);
							pageLayout.setVisibility(View.VISIBLE);
							db = FinalDb.create(context, "szoa.db");
							downPizhu();
							hasLoaded = true;
							if (maxPage > 0) {
								gopageSeekBar
										.setProgress((getCurrentPageNo() + 1)
												* 100 / maxPage);

								// nowpage.setText(currentPage + "/" + maxPage);
								nowpageTv
										.setText((getCurrentPageNo() + 1) + "");
								sumpageTv.setText("/" + maxPage);
							}

						}
						/*
						 * if ((getCurrentPageNo() + 1) == maxPage) {
						 * gopageSeekBar.setProgress(100); } else { if (maxPage
						 * > 0) gopageSeekBar .setProgress((getCurrentPageNo() +
						 * 1)
						 * 
						 * 100 / maxPage); } if (maxPage > 0)
						 * nowpage.setText((getCurrentPageNo() + 1) + "/" +
						 * maxPage);
						 */

						// 填充模板，根据需求实现功能

					}
				});


		super.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageChange(String currentPage) {
				 currpage = getCurrentPageNo();
				// if (getHaveBookMark()) {
				// bookmark_Float.setChecked(true);
				// } else {
				// bookmark_Float.setChecked(false);
				// }
				if (maxPage > 0) {
					gopageSeekBar.setProgress((Integer.parseInt(currentPage))
							* 100 / maxPage);

					// nowpage.setText(currentPage + "/" + maxPage);
					nowpageTv.setText(currentPage + "");
					sumpageTv.setText("/" + maxPage);
				}
				// title.setText("("+currentPage+"/"+maxPage+")"+fileName);
			}
		});
		// 添加批注
		super.setOnViewTouchAddAnnotListener(new OnViewTouchAddAnnotListener() {

			@Override
			public void onTouch(float x, float y) {
				if (isAnnotation) {
					isAnnotation = false;
					// 文字批注
					Log.v("tbz", "textannot x = " + x + ", y = " + y);
					annotUtil.addTextAnnot(x, y);
				}
				if (isSound) {
					isSound = false;
					// 语音批注
					annotUtil.addSoundAnnot(x, y);
				}
				if (isFinger) {
					isFinger = false;
					// TODO 录入指纹
				}
			}
		});
		// 显示批注
		super.setOnViewTouchShowAnnotListener(new OnViewTouchShowAnnotListener() {

			@Override
			public void onTouchTextAnnot(Annotation annotation) {
				annotUtil.showTextAnnot(annotation);
			}

			@Override
			public void onTouchSoundAnnot(Annotation annotation) {
				annotUtil.showSoundAnnot(annotation);
			}
		});
	}

	@Override
	protected void onStartImpl() {
		timeReceiver.registerReceiver();
		super.onStartImpl();
	}

	@Override
	protected void onDestroyImpl(boolean finishing) {
		timeReceiver.unregisterReceiver();
		super.onDestroyImpl(finishing);
		// 关闭日志调试组件
		LogPrinter.getInstance(this).stop();
		System.exit(0);
	}

	public class LoadAnnotations implements Runnable {

		@Override
		public void run() {
			ArrayList<Annotation> list = new ArrayList<Annotation>();
			list = getAnnotsFromJson(readFromTxt(Environment
					.getExternalStorageDirectory() + "/" + fileName + ".txt"));
			if (list != null) {
				// list = getAnnotationWithMe(list);
				for (int i = 0; i < list.size(); i++) {
					Annotation annot = list.get(i);
					String type = annot.getStyleName();
					int pageNo = Integer.parseInt(annot.getPageNo()) - 1;
					float x = Float.parseFloat(annot.getX());
					float y = Float.parseFloat(annot.getY());
					String userName = annot.getAuthorName();
					String creatTime = annot.getCreateTime();
					if (type.equals("Stamp")) {
						if (annot.getAnnoContent().startsWith("q")) {
							insertVectorAnnotation(pageNo, x, y,
									Float.parseFloat(annot.getWidth()),
									Float.parseFloat(annot.getHeight()),
									annot.getAnnoContent(), userName,
									creatTime, 1);
						} else {
							insertHandWriteAnnotation(pageNo, x, y,
									Float.parseFloat(annot.getWidth()),
									Float.parseFloat(annot.getHeight()),
									annot.getAnnoContent(), userName,
									creatTime, 1);
						}
					} else if (type.equals("Text")) {
						Log.d("tbz", "insert text annotation");
						insertTextAnnotation(pageNo, x, y,
								annot.getAnnoContent(), userName, creatTime, "");
					} else if (type.equals("Sound")) {
						insertSoundAnnotation(annot.getUnType(), pageNo, x, y,
								userName, annot.getSoundData(),
								(int) annot.getSoundRate(),
								(int) annot.getSoundChannels(),
								(int) annot.getSoundBitspersample(), creatTime);
					}
				}
			}
			Message msg = new Message();
			msg.what = MSG_WHAT_REFRESHDOCUMENT;
			myHandler.sendMessage(msg);
		}

	}

	@Override
	protected void onPauseImpl(boolean finishing) {
		super.onPauseImpl(finishing);
	}

	/**
	 * 打开批注或注释的列表
	 * 
	 * @param subType
	 *            批注类型
	 */
	private void openAnnotationList(final int subType) {
		note_dlg = new NoteDialog(context, BookShower.this, subType);
		note_dlg.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final ArrayList<Annotation> newNoteList = getAnnotationList(subType);
				final Message msg = new Message();
				msg.what = MSG_WHAT_LOADANNOTCOMPLETE;
				final Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("annot_list", newNoteList);
				msg.setData(bundle);
				myHandler.sendMessage(msg);
			}

		}).start();
	}

	/**
	 * 打开大纲
	 */
	private void openOutlineList() {
		final List<OutlineLink> outline = getOutlineList();
		if (outline != null && outline.size() > 0) {
			final OutlineDialog dlg = new OutlineDialog(BookShower.this,
					outline);
			dlg.show();
		} else {
			MyToast.show(context, "大纲不可用");
		}
	}

	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_WHAT_DISMISSDIALOG:
				View rm_view = (View) msg.obj;
				hiddenViewFromPDF(rm_view);
				hiddenViewFromPDF(handwriteView_layout);
				closeHandWrite();
				fl_pen.setClickable(true);
				break;
			case MSG_WHAT_LOADANNOTCOMPLETE:
				// 获取批注之后，更新批注清单
				ArrayList<Annotation> list = msg.getData()
						.getParcelableArrayList("annot_list");
				// list = removeDuplicateWithOrder(list);
				note_dlg.updateView(list);
				break;
			case MSG_WHAT_REFRESHDOCUMENT:
				refreshDocument();
				break;
			case 204:
				MyToast.show(context, "开始同步云端批注！");
				break;
			case 205:
				MyToast.show(context, "正在保存文档！");
				break;
			}
		}
	};

	private void showSaveDialog(Context context) {
		View v = View.inflate(context, R.layout.save_dialog_layout, null);
		final Button cancel = (Button) v.findViewById(R.id.btn_cancel);
		final Button no_save = (Button) v.findViewById(R.id.btn_no_save);
		final Button save = (Button) v.findViewById(R.id.btn_save);

		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});

		no_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (loadPDFReaderCache) {
					savePDFReadSettings();
				}
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		});

		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				saveAndExit();
			}

		});

		dialog.setContentView(v);
		dialog.setCancelable(false);
		dialog.show();
		getController().setDialogWindowSize(dialog);
	}

	/**
	 * 保存并退出金格控件
	 */
	private void saveAndExit() {
		if (isUseEbenSDK) {

		} else {
			saveDocument();
			if (loadPDFReaderCache) {
				Log.v("tbz", "save reader setting on exit");
				savePDFReadSettings();
			}
			//根据金格要求，临时增加关闭方法
			closeDocument();
			
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());// 必需要杀掉进程
		}
	}

	@Override
	public void onBackPressed() {
		saveJson();// 做判断是否需要保存
		return;
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.kinggrid.pages.bmp.save")) {
				isLocked = false;
				MyToast.show(context, "保存页面图片完毕");
			}
			if (action.equals(SAVESIGNFINISH_ACTION)) {
				if (loadPDFReaderCache) {
					savePDFReadSettings();
				}
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
	};

	private void initBtnView(final View layout) {
		btnClose = (ImageButton) layout.findViewById(R.id.btn_close);
		btnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doCloseHandwriteInfo(layout, full_handWriteView);
			}
		});
		btnSave = (ImageButton) layout.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (full_handWriteView.canSave()) {
					Runnable runnable = new Runnable() {

						@Override
						public void run() {

							insertVertorFlag = 1;
							doSaveHandwriteInfo(true, false, full_handWriteView);
							// hiddenViewFromPDF(dialog_view);
							// 取消等待交互
							// Message msg = new Message();
							// msg.what = MSG_WHAT_DISMISSDIALOG;
							// msg.obj = dialog_view;
							// myHandler.sendMessage(msg);
						}
					};
					Thread thread = new Thread(runnable);
					thread.start();
				} else {
					MyToast.show(context, "没有保存内容");
				}
				gopageEditText.setEnabled(true);
				gopageSeekBar.setEnabled(true);
			}
		});
		btnUndo = (ImageButton) layout.findViewById(R.id.btn_undo);
		btnUndo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				full_handWriteView.doUndoHandwriteInfo();
			}
		});
		btnRedo = (ImageButton) layout.findViewById(R.id.btn_redo);
		btnRedo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				full_handWriteView.doRedoHandwriteInfo();
			}
		});
		btnClear = (ImageButton) layout.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				full_handWriteView.doClearHandwriteInfo();
			}
		});
		btnPen = (ImageButton) layout.findViewById(R.id.btn_pen);
		btnPen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				full_handWriteView.setPenSettingInfoName("demo_type",
						"demo_size", "demo_color");
				full_handWriteView.doSettingHandwriteInfo();
			}
		});


	}

	/**
	 * 批注信息写入文本
	 * @param fileString
	 */
	private void writeStringToTxt(String fileString) {
		File filePath = new File(Environment.getExternalStorageDirectory()
				+ "/pizhu/");
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/pizhu/" + fileName + userName + ".txt");
		if (!filePath.exists()) {
			filePath.mkdirs();
		}

		// 创建文件
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/pizhu/"
							+ fileName + userName + ".txt");
			outputStream.write(fileString.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//批注信息写成文件
	private void writeToFile(String path, byte[] data) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(path);
			outputStream.write(data);
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将数组转换为JSON格式的数据。
	 * 
	 * @param stoneList
	 *            数据源
	 * @return JSON格式的数据
	 */
	public String annotationToJson(List<Annotation> annotList) {
		try {
			JSONArray array = new JSONArray();
			// JSONObject object = new JSONObject();
			int length = annotList.size();
			for (int i = 0; i < length; i++) {
				Annotation annot = annotList.get(i);
				JSONObject jsonObject = new JSONObject();
				String authorId = annot.getAuthorName();
				String authorName = annot.getAuthorName();
				String pageNo = annot.getPageNo();
				String X = annot.getX();
				String Y = annot.getY();
				String width = annot.getWidth();
				String height = annot.getHeight();
				String styleName = annot.getStyleName();
				String createTime = annot.getCreateTime();
				String annotContent = annot.getAnnoContent();
				String unType = annot.getUnType();
				jsonObject.put("authorId", authorId);
				jsonObject.put("authorName", authorName);
				jsonObject.put("fileId", fileId);
				jsonObject.put("fileName", fileName);
				jsonObject.put("unType", unType);
				jsonObject.put("styleName", styleName);
				jsonObject.put("pageNo", pageNo);
				jsonObject.put("X", X);
				jsonObject.put("Y", Y);
				jsonObject.put("width", width);
				jsonObject.put("height", height);
				jsonObject.put("createTime", createTime);
				jsonObject.put("annotContent", annotContent);
				if (annot.getStyleName().equals("Stamp")) {
					jsonObject.put("styleId", "12");
					if (annotContent != null) {
						if (annotContent.startsWith("q")) {
							jsonObject.put("annotSignature", "");
						} else {
							jsonObject.put("annotSignature",
									bytesToHexString(getBytesFromFile(new File(
											annotContent))));
						}
					}
				} else if (annot.getStyleName().equals("Text")) {
					jsonObject.put("styleId", "0");
					jsonObject.put("annotSignature", "");
				} else if (annot.getStyleName().equals("Sound")) {
					jsonObject.put("styleId", "17");
					jsonObject.put("annotSignature", "");
					jsonObject.put("annotRate",
							String.valueOf(annot.getSoundRate()));
					jsonObject.put("annotChannels",
							String.valueOf(annot.getSoundChannels()));
					jsonObject.put("annotBitspersample",
							String.valueOf(annot.getSoundBitspersample()));
					jsonObject.put("soundData",
							bytesToHexString(annot.getSoundData()));
				}
				array.put(jsonObject);
			}
			// object.put("annots", array);
			Log.d("Kevin", "array.toString() : " + array.toString());
			return array.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] hexStr2Bytes(String hex) {
		if (hex == null || hex.equals("")) {
			return null;
		}
		hex = hex.toLowerCase();
		int len = hex.length() / 2;
		char[] hexChars = hex.toCharArray();
		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			b[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}

		return b;
	}

	private byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			Log.v("tbz", "out = " + out.toByteArray());
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<Annotation> getAnnotsFromJson(String json) {
		Log.d("Kevin", " getAnnotsFromJson ");
		ArrayList<Annotation> annotList = new ArrayList<Annotation>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			Log.d("Kevin", " jsonArray.length() : " + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				Annotation annotation = new Annotation();
				annotation.setPageNo(jsonObject2.getString("pageNo"));
				annotation.setX(jsonObject2.getString("X"));
				annotation.setY(jsonObject2.getString("Y"));
				annotation.setWidth(jsonObject2.getString("width"));
				annotation.setHeight(jsonObject2.getString("height"));
				annotation.setStyleName(jsonObject2.getString("styleName"));
				annotation.setCreateTime(jsonObject2.getString("createTime"));
				annotation.setAuthorName(jsonObject2.getString("authorName"));
				annotation.setAuthorId(jsonObject2.getString("authorId"));
				annotation.setUnType(jsonObject2.getString("unType"));
				String styleName = "";
				if (jsonObject2.has("styleName")) {
					styleName = jsonObject2.getString("styleName");
					// if(styleName.equals("Unknown")){
					// styleName = "";
					// }
				}
				annotation.setStyleName(styleName);

				String unType = "";
				if (jsonObject2.has("unType")) {
					unType = jsonObject2.getString("unType");
				}
				annotation.setUnType(unType);

				if (jsonObject2.getString("styleName").equals("Stamp")) {
					if (jsonObject2.has("annotContent")) {
						String annotContextpath = jsonObject2
								.getString("annotContent");
						// Log.v("zxg", "annotContextpath = " +
						// annotContextpath);
						String annotfilename = annotContextpath
								.substring(annotContextpath.lastIndexOf("/") + 1);
						annotContextpath = SDCARD_PATH
								+ "/kgpdffiles/tempsignatures/" + annotfilename;
						// String annotContextpath =
						// FileUtils.getPdfWriteImagePath();
						Log.v("zxg", "annotContextpath = " + annotContextpath);
						annotation.setAnnoContent(annotContextpath);
						if (jsonObject2.has("annotSignature")) {
							byte[] annotSignature = hexStr2Bytes(jsonObject2
									.getString("annotSignature"));
							writeToFile(annotContextpath, annotSignature);
						}
					} else {
						byte[] annotSignature = hexStr2Bytes(jsonObject2
								.getString("annotSignature"));
						String annotContent = FileUtils.getPdfWriteImagePath();
						annotation.setAnnoContent(annotContent);
						writeToFile(annotContent, annotSignature);
					}
				}

				if (jsonObject2.getString("styleName").equals("Text")) {
					annotation.setAnnoContent(jsonObject2
							.getString("annotContent"));
				}

				if (jsonObject2.getString("styleName").equals("Sound")) {
					annotation.setSoundRate(Integer.valueOf(jsonObject2
							.getString("annotRate")));
					annotation.setSoundChannels(Integer.valueOf(jsonObject2
							.getString("annotChannels")));
					annotation.setSoundBitspersample(Integer
							.valueOf(jsonObject2
									.getString("annotBitspersample")));
					annotation.setSoundData(hexStr2Bytes(jsonObject2
							.getString("soundData")));
				}

				annotList.add(annotation);
			}
			// annotList = removeDuplicateWithOrder(annotList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return annotList;
	}

	/**
	 * @param string
	 */
	private String readFromTxt(String fileName) {
		String content = "";
		Log.d("Kevin", fileName);
		try {
			FileInputStream inputStream = new FileInputStream(fileName);

			byte[] bytes = new byte[1024 * 4];
			Log.d("Kevin", "bytes");
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			while (inputStream.read(bytes) != -1) {
				arrayOutputStream.write(bytes, 0, bytes.length);
			}
			Log.d("Kevin", "inputStream");
			inputStream.close();
			arrayOutputStream.close();
			content = new String(arrayOutputStream.toByteArray());
			Log.d("Kevin", content);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	// 打开手写签批界面
	public void openHardWrite() {
		gopageEditText.setEnabled(false);
		gopageSeekBar.setEnabled(false);
		handwriteView_layout = View.inflate(context,
				R.layout.signature_kinggrid_full, null);
		full_handWriteView = (PDFHandWriteView) handwriteView_layout
				.findViewById(R.id.v_canvas);
		initBtnView(handwriteView_layout);
		openHandwriteAnnotation(handwriteView_layout, full_handWriteView);
		full_handWriteView.setPenInfo(12, Color.RED, 0);// 设置初始笔色为红色

	}

	public void closeHandWrite() {
		handwriteView_layout = View.inflate(context,
				R.layout.signature_kinggrid_full, null);
		full_handWriteView = (PDFHandWriteView) handwriteView_layout
				.findViewById(R.id.v_canvas);
		doCloseHandwriteInfo(handwriteView_layout, full_handWriteView);
	}

	// 手写回退
	public void returnHardWrite() {
		full_handWriteView.doUndoHandwriteInfo();
	}

	public void setHardWrite() {
		if (full_handWriteView == null) {
			handwriteView_layout = View.inflate(context,
					R.layout.signature_kinggrid_full, null);
			full_handWriteView = (PDFHandWriteView) handwriteView_layout
					.findViewById(R.id.v_canvas);
		}
		full_handWriteView.setPenSettingInfoName("demo_type", "demo_size",
				"demo_color");
		full_handWriteView.doSettingHandwriteInfo();
	}

	ArrayList<String> serviceAnnotList = new ArrayList<String>();

	boolean isListInServer(ArrayList<Annotation> annotList){
		for(String curId:serviceAnnotList){
			for(Annotation annotation:annotList){
				if(curId == null){
					continue;
				}
				if(curId.equals(annotation.getAnnotId())){
					//Log.e(TAG,"=============curId:" + curId + " annotation.getAnnotId()" + annotation.getAnnotId());
					return true;
				}
			}
		}
		return false;
	}

	/*
	 *下载批注 
	 */
	public void downPizhu() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// Message msg204 = new Message();
				// msg204.what = 204;
				// myHandler.sendMessage(msg204);
				LinkedHashMap args = new LinkedHashMap();
				args.put("userId", userName);
				args.put("fileId", fileId);
				//Log.e(TAG, "id='" + fileId + userName + "'");
				Log.e(TAG, "fileId:" + fileId + " userName:" + userName);
				String str = null;
				ArrayList<Annotation> list = new ArrayList<Annotation>();

				try {
					List<Postil> postils = db.findAllByWhere(Postil.class,
							" id='" + fileId + userName + "'");
					if (postils != null && postils.size() > 0) {
						postilVersion = postils.get(0).getVersion();
					}
					str = WSDLManager.shareManager(BookShower.this).getDataXML(
							SharePreferences.getInstance(context).getServerUrl(
									MyApplication.SERVICE_HOST)
									+ "/services/MeetingService",
							"getFilePostil", args);
					//Log.e(TAG, "getFilePostil:" + str);
					String jsonStr = "";
					if (str != null) {
						JSONObject object = new JSONObject(str);
						int sversion = object.getInt("version");
						try {
							jsonStr = object.getString("filePostil");
						} catch (JSONException e){
							jsonStr = "";
							e.printStackTrace();
						}
						if (jsonStr == null || jsonStr.equals("")) {
							postilVersion = sversion;
							String annots = readFromTxt(Environment
									.getExternalStorageDirectory()
									+ "/pizhu/"
									+ fileName + userName + ".txt");
							if(annots != null && !annots.isEmpty()) {
								list.addAll(getAnnotsFromJson(annots));
							}
						}
						if (jsonStr.length() > 4 && sversion > postilVersion) {
							postilVersion = sversion;
							list.addAll(getAnnotsFromJson(jsonStr));
							serviceAnnotList.clear();
							for(Annotation annotation:list){
								serviceAnnotList.add(annotation.getAnnotId());
							}
						} else {
							String annots = readFromTxt(Environment
									.getExternalStorageDirectory()
									+ "/pizhu/"
									+ fileName + userName + ".txt");
							if(annots != null && !annots.isEmpty()) {
								list.addAll(getAnnotsFromJson(annots));
							}
						}
					} else {
						list.addAll(getAnnotsFromJson(readFromTxt(Environment
								.getExternalStorageDirectory()
								+ "/pizhu/"
								+ fileName + userName + ".txt")));
					}
					if (list != null) {
						// list = removeDuplicateWithOrder(list);
						for (int i = 0; i < list.size(); i++) {
							Annotation annot = list.get(i);
							String type = annot.getStyleName();
							int pageNo = Integer.parseInt(annot.getPageNo()) - 1;
							float x = Float.parseFloat(annot.getX());
							float y = Float.parseFloat(annot.getY());
							String userName = annot.getAuthorName();
							String creatTime = annot.getCreateTime();

							if (type.equals("Stamp")) {
								if (annot.getAnnoContent().startsWith("q")) {
									insertVectorAnnotation(
											pageNo,
											x,
											y,
											Float.parseFloat(annot.getWidth()),
											Float.parseFloat(annot.getHeight()),
											annot.getAnnoContent(), userName,
											creatTime, 1);
								} else {
									insertHandWriteAnnotation(
											pageNo,
											x,
											y,
											Float.parseFloat(annot.getWidth()),
											Float.parseFloat(annot.getHeight()),
											annot.getAnnoContent(), userName,
											creatTime, 1);
								}
							} else if (type.equals("Text")) {
								Log.d("tbz", "insert text annotation");
								insertTextAnnotation(pageNo, x, y,
										annot.getAnnoContent(), userName,
										creatTime, "");
							} else if (type.equals("Sound")) {
								insertSoundAnnotation(annot.getUnType(),
										pageNo, x, y, userName,
										annot.getSoundData(),
										(int) annot.getSoundRate(),
										(int) annot.getSoundChannels(),
										(int) annot.getSoundBitspersample(),
										creatTime);
							}
						}
						// Message msg205 = new Message();
						// msg205.what = 205;
						// myHandler.sendMessage(msg205);
						Message msg = new Message();
						msg.what = MSG_WHAT_REFRESHDOCUMENT;
						myHandler.sendMessage(msg);
					}
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*
	 * 保存批注
	 */
	public void saveJson() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				ArrayList<Annotation> annotList = getAnnotationList(-1);// 获取文档内所有的批注
				if (annotList.size() > 0) {
					Message msg205 = new Message();
					msg205.what = 205;
					myHandler.sendMessage(msg205);
					String jsonStr = annotationToJson(annotList);
					Postil postil = new Postil();
					postil.setId(fileId + userName);
					postil.setVersion(postilVersion + 1);
					postil.setFileId(fileId);
					postil.setUserId(userName);
					postil.setMeetingId(meetingId);
					postil.setContent(jsonStr);
					if(isListInServer(annotList)){
						postil.setIsupload(true);
					}else {
						postil.setIsupload(false);
					}
					try {
						db.save(postil);
					} catch (Exception e) {
						db.update(postil);
					}
					writeStringToTxt(jsonStr);
				}
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());//
				// 必需要杀掉进程
			}
		}).start();
	}
	Handler handler = new Handler();

	@Override
	public void windowToFullScreen() {
		// titleAnimator(tab_rdogroup);
		// if (!isToolsHide) {
		// tab_rdogroup.setVisibility(View.GONE);
		// tab_rdogroup.setAnimation(AnimationUtil.moveToViewBottom());
		//
		// isToolsHide = true;
		// } else {
		// tab_rdogroup.setVisibility(View.VISIBLE);
		// tab_rdogroup.setAnimation(AnimationUtil.moveToViewLocation());
		// isToolsHide = false;
		//
		// }
		super.windowToFullScreen();
	}

	/*
	 * 刷新倒计时进度条
	 */
	@Override
	public void refushTime(Context context, Intent intent) {
		if (meetingId.equals(intent.getStringExtra("meetingId"))) {
			if (intent.getIntExtra("progress", 0) > 0
					&& intent.getIntExtra("progress", 0) <= 100)
				roundProgressBar.setVisibility(View.VISIBLE);
			roundProgressBar.setProgress(intent.getIntExtra("progress", 0),
					intent.getStringExtra("time"));
			if (intent.getIntExtra("progress", 0) == 0) {
				roundProgressBar.setVisibility(View.GONE);
			}
		}
	}
	
}
