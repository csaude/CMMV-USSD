package mz.org.fgh.vmmc.inout;

import java.io.Serializable;

public class UssdOut implements Serializable {

	private static final long serialVersionUID = 1L;
	private String content;
	private String from;
	private String to;
	private String sessionId;
	private String transactionId;
	private String action;
	private String dateTime;

	public UssdOut() {
		super();
	}

	public UssdOut(UssdIn in) {
		this.setSessionId(in.getSession());
		this.setDateTime(in.getDateTime());
		this.setFrom(in.getTo());
		this.setTo(in.getFrom());
		this.setTransactionId(in.getTransaction());
		this.setAction("true");
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}
