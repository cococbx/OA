package com.wanhuiyuan.szoa.uiutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;

import android.content.Context;
/**
 * 
 * @author xiaoxiao
 *
 */
public class ConnectionUtil {
	public static final String TAG_POST = "post";
	public static String readJsonData(InputStream inSteam) throws IOException  {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = inSteam.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		return new String(outStream.toByteArray());
	}
	
	
	public static String getServerUrl(Context context) throws IOException {
		return SharePreferences.getInstance(context).getServerUrl(MyApplication.SERVICE_HOST);
	}
	
	public static String doPost(Context context, String requestUrl, Map<String, Object> params) throws IOException  {
		String url = getServerUrl(context);
		url += requestUrl;
		HttpPost httpRequest = new HttpPost(url);
//		httpRequest.setHeader("User-Agent","android");
//		params.put("apiVersion", "1.0");
		httpRequest.setHeader("Content-type","application/x-www-form-urlencoded;charset=utf-8");
		if(params != null && params.size() > 0 && !"".equals(params)) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, Object>> iter = params.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, Object> entrys = iter.next();
				String valueStr = "";
				if(entrys.getValue() != null){
					valueStr = entrys.getValue().toString();
				}
				parameters.add(new BasicNameValuePair(entrys.getKey(), valueStr));
			}
			httpRequest.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
		} 
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true); 
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 7000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 7000);
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		if(httpResponse.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} else {
			throw new IOException("statusCode:" + httpResponse.getStatusLine().getStatusCode());
		}
	}
	
	public static String doPost(Context context, String requestUrl) throws IOException  {
		String url = getServerUrl(context);
		url += requestUrl;
		HttpPost httpRequest = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		if(httpResponse.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} else {
			throw new IOException("statusCode:" + httpResponse.getStatusLine().getStatusCode());
		}
	}
	
	
	public static String doGet(Context context, String requestUrl) throws ParseException, IOException {
		HttpGet httpGet;
		requestUrl= requestUrl.replaceAll("\n", "");
		if(!requestUrl.contains("http://")) {
			String url = getServerUrl(context);
			url += requestUrl;
			httpGet = new HttpGet(url);
		} else {
			httpGet = new HttpGet(requestUrl);
		}
//		httpGet.setHeader("User-Agent","android");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		if(httpResponse.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		} else {
			throw new IOException();
		}
	}
}
