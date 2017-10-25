package com.kinggrid.iapppdf.demo.dialogs;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kinggrid.iapppdf.company.signature.CertInfoController;
import com.longmai.mtoken.k5.sof.SOF_K5AppLib;
import com.cec.szoa.R;
/**
 * 证书选择对话框
 * com.kinggrid.iapppdf.demo.CertInfoDialog
 * @author wmm
 * create at 2015年9月10日 上午11:17:19
 */
public class CertInfoDialog implements OnClickListener{

	private static final String TAG = "CertInfoDialog";
	private Context context;
	private TextView tv_title;
	private ProgressBar progressBar;
	private TextView tv_alert;
	private AlertDialog alertDialog;
	private AlertDialog key_dialog;
	private AlertDialog input_dialog = null;
	private CertInfoController certInfoController;

	public CertInfoDialog(Context mContext) {
		this.context = mContext;
		certInfoController = new CertInfoController(mContext, mHandler);
		getKeyCertList();
	}
	private EditText auth_edittext;
	private EditText pwd_edittext;
	private Button ok_btn,cancel_btn;
	/**
	 * 获取蓝牙Key证书列表，要求设备在4.4以上
	 */
	private void getKeyCertList() {
		View input_view = LayoutInflater.from(context).inflate(R.layout.input_layout, null);
		auth_edittext = (EditText) input_view.findViewById(R.id.cert_user);
		pwd_edittext = (EditText) input_view.findViewById(R.id.cert_pwd);
		ok_btn = (Button) input_view.findViewById(R.id.ok_input);
		cancel_btn = (Button) input_view.findViewById(R.id.cancel_input);
		ok_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
//		ok_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String auth = auth_edittext.getText().toString();
//				String pwd = pwd_edittext.getText().toString();
//				input_dialog.dismiss();
//				
//				View load_view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
//				progressBar = (ProgressBar) load_view.findViewById(R.id.progressBar);
//				tv_title = (TextView) load_view.findViewById(R.id.tv_title);
//				tv_title.setText(R.string.choiseCertificate);
//				tv_alert = (TextView) load_view.findViewById(R.id.tv_alert);
//				
//				key_dialog = new  AlertDialog.Builder(context).create();
//				key_dialog.setView(load_view);
//				key_dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//				key_dialog.show();
//				key_dialog.setCancelable(false);
//				
//				certInfoController.startGetKeyCertList(auth, pwd);
//			}
//		});
		input_dialog = new AlertDialog.Builder(context).setTitle(R.string.enterUsernameAndPassword)
				.setView(input_view).create();
//		input_dialog.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		input_dialog.show();
		input_dialog.setCancelable(false);
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CertInfoController.LOADINGCERTLIST:
				tv_alert.setText(R.string.getCertificateList);
				break;

			case CertInfoController.LOADCERTFAILED:
				tv_alert.setText(R.string.getCertificateNameFailed);
				progressBar.setVisibility(View.GONE);
				key_dialog.setCancelable(true);
				break;

			case CertInfoController.LOADINGCERTINFO:
				tv_alert.setText(R.string.getCertificateInfo);
				break;

			case CertInfoController.LOADCERTINFOFAILED:
				tv_alert.setText(R.string.getCertificateInfoFailed);
				progressBar.setVisibility(View.GONE);
				key_dialog.setCancelable(true);
				break;

			case CertInfoController.LOADCERTINFOFINISH:
				
				List<Map<String, String>> list = certInfoController.getContainerInfoList();
				TextView tv_info;
				if (list.size() <= 0) {
					View view = LayoutInflater.from(context).inflate(
							R.layout.show_info, null);
					tv_info = (TextView) view.findViewById(R.id.tv_info);
					tv_info.setText(R.string.noCertificate);
					break;
				} else{
					show();
				}
				break;
			case CertInfoController.LOADKEYFAILED:
				tv_alert.setText(R.string.doNotfindBluetoothDevice);
				progressBar.setVisibility(View.GONE);
				key_dialog.setCancelable(true);
				break;
			case CertInfoController.CONNECTDEVICEFAILED:
				tv_alert.setText(R.string.connectDeviceFailed);
				progressBar.setVisibility(View.GONE);
				key_dialog.setCancelable(true);
				break;
			case CertInfoController.LOGINFAILED:
				tv_alert.setText(R.string.loginFailed);
				progressBar.setVisibility(View.GONE);
				key_dialog.setCancelable(true);
				break;
			}

		}
	};

	private void show() {
		if(key_dialog != null){
			key_dialog.dismiss();
		}
		View view = LayoutInflater.from(context).inflate(R.layout.diglog_layout, null);
	    final Spinner spinner = (Spinner) view.findViewById(R.id.cert_list);
	    final EditText pwd_edittext = (EditText) view.findViewById(R.id.input_pwd);
	    final TextView pwd_info = (TextView) view.findViewById(R.id.pwd_info);
	    Button ok_btn = (Button) view.findViewById(R.id.ok);
	    
	    pwd_info.setVisibility(View.GONE);
    	pwd_edittext.setVisibility(View.GONE);
    	List<String> containers = certInfoController.getContainerList();
    	String[] certs = new String[containers.size()];
    	for (int i = 0; i < containers.size(); i++) {
    		certs[i] = containers.get(i);
		}
	    
	    if(certs != null){
	    	final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,certs);                
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);                
		    spinner.setAdapter(adapter);
	    }
		ok_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				certItemClickInterface.onItemClick(certInfoController.getSOF_K5AppLib(),
						((String) spinner.getSelectedItem()).trim());
				
				alertDialog.dismiss();

			}
		});
	    
		alertDialog = new  AlertDialog.Builder(context).create();
		alertDialog.setTitle(R.string.choiseCertificate);
	    alertDialog.setView(view);
//		alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
		alertDialog.setCancelable(false);
	    
	}
	private CertItemClickInterface certItemClickInterface;
	public interface CertItemClickInterface{
		public void onItemClick(SOF_K5AppLib app,String containerName);
	}
	public void setOnItemClickListener(CertItemClickInterface certItemClickInterface){
		this.certItemClickInterface = certItemClickInterface;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_input:
			String auth = auth_edittext.getText().toString();
			String pwd = pwd_edittext.getText().toString();
			input_dialog.dismiss();
			
			View load_view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
			progressBar = (ProgressBar) load_view.findViewById(R.id.progressBar);
			tv_title = (TextView) load_view.findViewById(R.id.tv_title);
			tv_title.setText(R.string.choiseCertificate);
			tv_alert = (TextView) load_view.findViewById(R.id.tv_alert);
			
			key_dialog = new  AlertDialog.Builder(context).create();
			key_dialog.setView(load_view);
//			key_dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			key_dialog.show();
			key_dialog.setCancelable(false);
			
			certInfoController.startGetKeyCertList(auth, pwd);
			break;

		case R.id.cancel_input:
			if(input_dialog != null){
				input_dialog.dismiss();
			}
			break;
		}
	}
}
