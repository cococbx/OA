package com.wanhuiyuan.szoa.bean;

/**
 * 呼叫服务显示对象
 * @author Administrator
 *
 */
public class ServiceBean {
	private String serviceId;//会议ID
	private String serviceName;//服务名称
	private String logoUrl;//图标

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

}
