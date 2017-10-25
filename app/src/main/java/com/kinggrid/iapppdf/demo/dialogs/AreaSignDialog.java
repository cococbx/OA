
package com.kinggrid.iapppdf.demo.dialogs;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kinggrid.iapppdf.ui.viewer.PDFHandWriteView;
import com.kinggrid.iapppdf.ui.viewer.viewers.GLView;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.jingge.BookShower;
import com.wanhuiyuan.szoa.myview.MyToast;
/**
 * 区域签批窗口
 * com.kinggrid.iapppdf.demo.AreaSignWindow
 * @author wmm
 * create at 2015年9月1日 下午5:37:27
 */
public class AreaSignDialog implements OnClickListener,OnTouchListener{
	
	private Context context;
	private Activity activity;
	private Button btn_move,btn_edit_switch,btn_pen,btn_close,btn_clear,btn_save,btn_redo,btn_undo;
	private EditText editText;
	private PDFHandWriteView area_sign_view;
	private String copy_right;
	private OnAreaSignatureListener signatureListener;
	private View area_layout;
	private boolean isShowEdit;
	/**
	 * 签名设置框
	 */
	private SignConfigDialog signConfigDialog;
	private DisplayMetrics dm = new DisplayMetrics(); 
	
	private GLView view;
	private int widgetHeight;
	private RelativeLayout btnsContainer;
	
	public AreaSignDialog(Activity activity,String copyRight,boolean isShowEdit){
		this.context = activity;
		this.copy_right = copyRight;
		this.activity = activity;
		this.isShowEdit = isShowEdit;
		dm = activity.getResources().getDisplayMetrics();
		view = (GLView)((BookShower)activity).getController().getView();
	}

	public View get(){
		area_layout  = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.area_annot_layout, null);
		initSignInfo(area_layout);
		return area_layout;
	}
	
	private void initSignInfo(View layout){
		area_sign_view = (PDFHandWriteView) layout.findViewById(R.id.handwrite_kgview);
		area_sign_view.setCopyRight(activity, copy_right);
		signConfigDialog = new SignConfigDialog(context, area_sign_view,"area_Size","area_Color","area_Type",50);
		//设置默认画笔
//		area_sign_view.setPenInfo(4f, Color.BLACK, PDFHandWriteView.TYPE_BALLPEN);
		area_sign_view.setPenInfo(signConfigDialog.getPenMaxSizeFromXML(), signConfigDialog.getPenColorFromXML(), signConfigDialog.getPenTypeFromXML());
		
		btnsContainer = (RelativeLayout) layout.findViewById(R.id.btns_container);
		//移动
		btn_move = (Button) layout.findViewById(R.id.port_move);
		btn_move.setOnTouchListener(this);
		// 文字编辑
		editText = (EditText) layout.findViewById(R.id.port_edit);
		// 开启文字功能
		btn_edit_switch = (Button) layout.findViewById(R.id.port_edit_switch);
		btn_edit_switch.setOnClickListener(this);
		if(isShowEdit){
			editText.setVisibility(View.VISIBLE);
			btn_edit_switch.setBackgroundResource(R.drawable.kinggrid_outline_list_expand);
		}else{
			editText.setVisibility(View.GONE);
			btn_edit_switch.setBackgroundResource(R.drawable.kinggrid_outline_list_collapse);
		}
		// 笔型
		btn_pen = (Button) layout.findViewById(R.id.port_pen);
		btn_pen.setOnClickListener(this);
		// 关闭
		btn_close = (Button) layout.findViewById(R.id.port_close);
		btn_close.setOnClickListener(this);
		// 清屏
		btn_clear = (Button) layout.findViewById(R.id.port_clear);
		btn_clear.setOnClickListener(this);
		// 保存
		btn_save = (Button) layout.findViewById(R.id.port_save);
		btn_save.setOnClickListener(this);
		// 重做
		btn_redo = (Button) layout.findViewById(R.id.port_redo);
		btn_redo.setOnClickListener(this);
		// 撤销
		btn_undo = (Button) layout.findViewById(R.id.port_undo);
		btn_undo.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.port_pen:
			// 设置画笔相关属性
//			new SignConfigDialog(context, area_sign_view,"area_Size","area_Color","area_Type",RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,50);
			signConfigDialog.showSettingWindow(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			break;
		case R.id.port_save:
			if (!area_sign_view.canSave()) {
				MyToast.show(context, "没有签批内容，无需保存！");
				return;
			}
			// 保存
			if(signatureListener != null){
				signatureListener.onSignatureSave(area_sign_view,isShowEdit,editText);
				isShowEdit = false;
			}
			break;
		case R.id.port_redo:
			// 重做
			area_sign_view.doRedoHandwriteInfo();
			break;
		case R.id.port_undo:
			// 撤销
			area_sign_view.doUndoHandwriteInfo();
			break;
		case R.id.port_close:
			// 清屏
			area_sign_view.doClearHandwriteInfo();
			//关闭
			if(signatureListener != null){
				signatureListener.onSignatureClose();
				isShowEdit = false;
			}
			break;
		case R.id.port_clear:
			// 清屏
			area_sign_view.doClearHandwriteInfo();
			break;
		case R.id.port_edit_switch:
			// 文字编辑功能
			switchEditText();
			break;
		}
		
	}
	/**
	 * 切换文字编辑功能
	 */
	private void switchEditText(){
		if(isShowEdit){
			isShowEdit = false;
			editText.setVisibility(View.GONE);
			btn_edit_switch.setBackgroundResource(R.drawable.kinggrid_outline_list_collapse);
		}else{
			isShowEdit = true;
			editText.setVisibility(View.VISIBLE);
			btn_edit_switch.setBackgroundResource(R.drawable.kinggrid_outline_list_expand);
		}
	}
	/** 
	 * 设置签名监听
	 */
	public void setSignatureListener(OnAreaSignatureListener signatureListener) {
		this.signatureListener = signatureListener;
	}
	
	public interface OnAreaSignatureListener{
		public void onSignatureSave(PDFHandWriteView pdfHandWriteView,boolean isShowEdit,EditText editText);
		public void onSignatureClose();
	}
	
	float lastY;
	int top, bottom;
	private boolean isMoving;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if(R.id.port_move == v.getId()){
			widgetHeight = area_sign_view.getHeight() + editText.getHeight() + btnsContainer.getHeight();
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					lastY = event.getRawY();
					isMoving = true;
					break;
				case MotionEvent.ACTION_MOVE:
					if(isMoving){
						int dy = (int) (event.getRawY() - lastY);
						top = area_layout.getTop() + dy;
						bottom = area_layout.getBottom() + dy;
						//控制不超出范围
						if(top < widgetHeight - view.getHeight()){
							top = widgetHeight - view.getHeight();
							bottom = top + area_layout.getHeight();
						} else if(bottom > area_layout.getHeight()){
							bottom = area_layout.getHeight();
							top = 0;
						}
						area_layout.layout(area_layout.getLeft(), top, 
								area_layout.getRight(), bottom);
						lastY = event.getRawY();
					}
					break;
				case MotionEvent.ACTION_UP:
					isMoving = false;
					break;
				
			}
			return true;
		}
		return false;
	}
}

