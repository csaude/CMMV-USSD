package mz.org.fgh.vmmc.inout;

import java.io.Serializable;

public class UssdRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sessionId;
	private String serviceCode;
	private String phoneNumber;
	private String text;

	public UssdRequest(String sessionId, String serviceCode, String phoneNumber, String text) {
		super();
		this.sessionId = sessionId;
		this.serviceCode = serviceCode;
		this.phoneNumber = phoneNumber;
		this.text = text;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
