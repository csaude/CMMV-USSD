package mz.org.fgh.vmmc.inout;

public class SendSmsRequest {
       private String apiKey;
       private PayloadSms payload;

       public String getApiKey() {
	     return apiKey;
       }

       public void setApiKey(String apiKey) {
	     this.apiKey = apiKey;
       }

       public PayloadSms getPayload() {
	     return payload;
       }

       public void setPayload(PayloadSms payload) {
	     this.payload = payload;
       }

}
