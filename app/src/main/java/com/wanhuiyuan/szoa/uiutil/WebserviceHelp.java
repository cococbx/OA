package com.wanhuiyuan.szoa.uiutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.ksoap2.serialization.SoapObject;

import android.util.Log;

public class WebserviceHelp {
	private static final String tag = "jj";
	// webservice 命名空间
	private static final String NAMESPACE = "http://WebXml.com.cn/";

	// WebService地址
	private static String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	// webservice 调用方法
	private static final String METHOD_NAME = "getWeatherbyCityName";
	private static final int TimeoutBySecond = 1;
	// webservice 的SOAP_ACTION
	private static String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";

	public static SoapObject soapObject;

	/***
	 * 拼接请求webservice
	 * 
	 * @return
	 */
	private static StringEntity getEntity(String thecity) {
		StringEntity entity = null;

		String str = String
				.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
						+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						+ "<soap:Body>"
						+ "<getWeatherbyCityName xmlns=\"http://WebXml.com.cn/\">"
						+ "<theCityName>%1$s</theCityName>"
						+ "</getWeatherbyCityName>" + "</soap:Body>"
						+ "</soap:Envelope>", thecity);
		try {
			entity = new StringEntity(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	/***
	 * webservice 请求
	 */
	public static HttpPost httpPost(StringEntity entity, String SOAP_ACTION) {

		HttpPost request = new HttpPost(URL);
		request.addHeader("SOAPAction", SOAP_ACTION);
		request.addHeader("Host", "www.webxml.com.cn");
		request.addHeader("Content-Type", "text/xml; charset=utf-8");
		request.setEntity(entity);
		return request;
	}

	/***
	 * 获取响应webservice HttpResponse
	 */
	public static HttpResponse getHttpResponse(HttpPost httpPost) {
		HttpClient client = new DefaultHttpClient();
		client.getParams()
				.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
						TimeoutBySecond * 1000);
		HttpResponse httpResponse = null;

		try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return httpResponse;
	}

	/***
	 * 获取内容 解析httpresponse
	 */
	public static String getContent(HttpResponse httpResponse) {

		InputStream inputStream;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			inputStream = httpResponse.getEntity().getContent();

			in = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
				sb.append("\n");// 换行
			}
			in.close();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String getWeather(String cityName) {
		StringEntity entity = getEntity(cityName);
		HttpPost httpPost = httpPost(entity, SOAP_ACTION);
		HttpResponse httpResponse = getHttpResponse(httpPost);
		String weather = getContent(httpResponse);
		Log.v(tag, weather);
		return weather;
	}

}