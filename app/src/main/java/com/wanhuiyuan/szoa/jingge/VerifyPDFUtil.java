
package com.wanhuiyuan.szoa.jingge;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kinggrid.iapppdf.ui.viewer.IAppPDFActivity.SignPosition;
import com.cec.szoa.R;

public class VerifyPDFUtil {

	private static final String TAG = "VerifyPDFUtil";
	private final static String signatruePdfUrl = "pdf/isignature/template.pdf";
	private final static String verifyPdfUrl = "pdf/verify/template.pdf";
	private Context context;
	private Handler handler;
	
	public VerifyPDFUtil(Context mContext,Handler mHandler){
		this.context = mContext;
		this.handler = mHandler;
	}
	
	public void upload(String url, String imagePath, int type, List<SignPosition> positions){
		HttpURLConnection conn = null;
		OutputStream out = null;
		try{
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            /*conn.setRequestProperty("enctype", "multipart/form-data");
            conn.setRequestProperty("contentType", "charset=UTF-8");*/
            conn.setRequestProperty("connection", "close");
            conn.setRequestMethod("POST");

            
            DataInputStream disImage = null;
            String imageOrder = null;
            String json = null;
            BitmapFactory.Options options = new  BitmapFactory.Options();
            if(type == 1){
            	File image = new File(imagePath);
            	long imageSize = image.length();
            	Log.v("tbz","imageSize = " + imageSize);
                disImage = new DataInputStream(new FileInputStream(image));
                imageOrder = "fileSize="+ imageSize + ",fileName=IMAGE";
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, options);
                options.inJustDecodeBounds = false;
                
                json = getJsonData(positions);
            }
            
            out = conn.getOutputStream();
            int bytesOut = 0;
            byte[] bufferOut = new byte[1024 * 4];
            Log.v("tbz","outputStream start transform");
            if(type == 1){
            	out.write("type=1".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("debug=0".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                /*out.write("pageno=1".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("x=100".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("y=100".getBytes("UTF-8"));*/
                out.write(("position=" + json).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("w="+options.outWidth).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("h="+options.outHeight).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("imagetype=jpg".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("pdfPath="+signatruePdfUrl).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(imageOrder.getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                while (((bytesOut = disImage.read(bufferOut, 0, 1024)) != -1)) {
                  out.write(bufferOut, 0, bytesOut);
                }

            }else if(type == 2){
            	out.write("type=2".getBytes("UTF-8"));
            	out.write("\r\n".getBytes("UTF-8"));
            	out.write("debug=0".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
            	out.write(("pdfPath="+verifyPdfUrl).getBytes("UTF-8"));
            	out.write("\r\n".getBytes("UTF-8"));
            }
            
            /*out.write("\r\n".getBytes("UTF-8"));
            out.write(pdfOrder.getBytes("UTF-8"));
            out.write("\r\n".getBytes("UTF-8"));*/
            /*while (((bytesOut = disPDF.read(bufferOut, 0, 1024)) != -1)) {
                out.write(bufferOut, 0, bytesOut);
            }*/
            
            out.flush();
            out.close();
            
            InputStream isResult = conn.getInputStream();
            String resultString = inputStream2String(isResult);
            if(type == 1){
            	sendMsgToHandler(resultString, false);
            }else if(type == 2){
            	/*if(resultString.length() == 1){
            		sendMsgToHandler(resultString, false);
            	}else{
            		sendMsgToHandler(resultString, true);
            	}*/
            	sendMsgToHandler(resultString, false);
            }
            
		}catch(Exception e){
			Log.v("tbz","exception found");
			sendMsgToHandler(context.getString(R.string.exception_tip), false);
			e.printStackTrace();
		}finally {
			if(conn != null){
				conn.disconnect();
				conn = null;
			}
		}
		
	}
	
	private String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesOut = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytesOut = is.read(bufferOut)) != -1) {
            baos.write(bufferOut, 0, bytesOut);
        }
        String resstrString = new String();
        resstrString = baos.toString("UTF-8");
        baos.flush();
        baos.close();
        return resstrString;
    }
	
	private void sendMsgToHandler(String message, boolean isShowVerify){
		Message msg = new Message();
		Bundle data = new Bundle();
		if(message.equals("-1")){
			data.putString("value",context.getString(R.string.result_no_exist_sign));
		}/*else if(message.equals("0")){
			data.putString("value",getString(R.string.result_invalid_sign));
		}else if(message.equals("1")){
			data.putString("value",getString(R.string.result_valid_sign));
		}else if(message.equals("2")){
			data.putString("value",getString(R.string.result_add_content));
		}*/else if(message.equals("3")){
			data.putString("value",context.getString(R.string.result_sign_unusual));
		}/*else if(isShowVerify){
			msg.obj = 99;
			data.putString("value", message);
		}*/else{
			data.putString("value", message);
		}
		
        msg.setData(data);
        handler.sendMessage(msg);
	}
	
	private String getJsonData(List<SignPosition> positions){
		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		try{
			for(int i=0; i<positions.size(); i++){
				JSONObject jo = new JSONObject();
				jo.put("pageno", positions.get(i).pageno);
				jo.put("x", (positions.get(i).rect[0] + positions.get(i).rect[2]) / 2);
				jo.put("y", positions.get(i).height - (positions.get(i).rect[1] + positions.get(i).rect[3]) / 2);
				array.put(jo);
			}
			
			data.put("positions", array);
		}catch(JSONException joe){
			joe.printStackTrace();
		}
		
		return data.toString();
	}
}

