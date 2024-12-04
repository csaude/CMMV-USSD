package mz.org.fgh.vmmc.inout;

import java.io.Serializable;

public class UssdResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String content;
	private String from;
	private String to;
	private String session;
	private String transaction;
	private String action;
	private String dateTime;

	public UssdResponse(UssdOut out) {
		this.content = out.getContent();
		this.from = out.getFrom();
		this.to = out.getTo();
		this.session = out.getSessionId();
		this.transaction = out.getTransactionId();
		this.action = out.getAction();
	 this.dateTime = out.getDateTime();

	}

	public UssdResponse() {

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

	public void setSession(String session) {
		this.session = session;
	}

	public String getSession() {
		return session;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transactionId) {
		this.transaction = transactionId;
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

	public UssdResponse(String content, String from, String to, String session, String transaction, String action,
			String dateTime) {
		super();
		this.content = content;
		this.from = from;
		this.to = to;
		this.session = session;
		this.transaction = transaction;
		this.action = action;
		this.dateTime = dateTime;
	}
	
	

}
