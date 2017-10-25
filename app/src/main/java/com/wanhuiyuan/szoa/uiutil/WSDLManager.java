package com.wanhuiyuan.szoa.uiutil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;

/**
 * 网络请求管理类
 *
 * @author Administrator
 */
public class WSDLManager {


	// WSDL命名空间
	public static final String WSDL_NAME_SPACE = "http://service.sdjxd.com";
	// 默认URL
	//http://172.16.100.248:8089/N9
	//http://58.18.32.38:8089/N9

	private String strWSDLURL;

	private static WSDLManager wsdlManager;

	private WSDLManager() {

	}

	public static synchronized WSDLManager shareManager(Context context) {
		if (wsdlManager == null) {
			wsdlManager = new WSDLManager();
		}
		return wsdlManager;
	}


	/**
	 * 访问有参数的接口<br>
	 * Map 键为参数名称, 值为参数值
	 */
	public String getDataXML(String WSDL, String methodName,
			LinkedHashMap<String, Object> args) throws IOException,
			XmlPullParserException {

		SoapObject soapObject = new SoapObject(WSDL_NAME_SPACE, methodName);

		if (args != null && args.size() > 0) {
			Set<String> keys = args.keySet();
			for (String key : keys) {
				soapObject.addProperty(key, args.get(key));
			}
		}

		HttpTransportSE httpTranstation = new HttpTransportSE(WSDL, 5000);
		try {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
//			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);
			envelope.encodingStyle = "UTF-8";
			httpTranstation.debug = true;
			httpTranstation.call(WSDL_NAME_SPACE + methodName, envelope);
			if (envelope.getResponse() != null) {
				Object object = envelope.bodyIn;
				Object rusult = envelope.getResponse();
				return rusult.toString();
			}
		} catch (SoapFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
