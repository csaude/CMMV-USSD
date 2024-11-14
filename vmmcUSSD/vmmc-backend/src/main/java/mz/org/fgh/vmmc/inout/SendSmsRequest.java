package mz.org.fgh.vmmc.inout;

public class SendSmsRequest {
	private String service;
	private String destination;
	private String text;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
