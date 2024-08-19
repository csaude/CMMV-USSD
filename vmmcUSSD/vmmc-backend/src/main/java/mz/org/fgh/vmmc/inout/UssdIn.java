package mz.org.fgh.vmmc.inout;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UssdIn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String content;
	private String from;
	private String to;
	private String session;
	private String transaction;
	private String action;
	private String dateTime  ;

	public UssdIn() {

	}



	public UssdIn(String content, String from, String to, String session, String transaction, String action,
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



	 public String getContent() {
		return content;
	}
	 public void setContent(String text) {
		this.content = text;
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

	public String getSession() {
		return session;
	}

	public void setSession(String sessionId) {
		this.session = sessionId;
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



	@Override
	public String toString() {
		return "UssdIn [content=" + content + ", from=" + from + ", to=" + to + ", session=" + session
				+ ", transaction=" + transaction + ", action=" + action + ", dateTime=" + dateTime + "]";
	}
}
