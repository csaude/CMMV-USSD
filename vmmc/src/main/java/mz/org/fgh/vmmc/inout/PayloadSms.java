package mz.org.fgh.vmmc.inout;

public class PayloadSms {
       private String message;
       private RecipientSms[] recipients;

       public PayloadSms() {
	     super();
       }

       public PayloadSms(String message, RecipientSms[] recipients) {
	     super();
	     this.message = message;
	     this.recipients = recipients;
       }

       public String getMessage() {
	     return message;
       }

       public void setMessage(String message) {
	     this.message = message;
       }

       public RecipientSms[] getRecipients() {
	     return recipients;
       }

       public void setRecipients(RecipientSms[] recipients) {
	     this.recipients = recipients;
       }

}
