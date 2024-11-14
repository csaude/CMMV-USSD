package mz.org.fgh.vmmc.inout;

import java.io.Serializable;

public class RecipientSms implements Serializable {
       /**
        * 
        */
       private static final long serialVersionUID = 1L;
       private String type;
       private String value;

       public RecipientSms() {
	     super();
       }

       
       public RecipientSms(String type, String value) {
	     super();
	     this.type = type;
	     this.value = value;
       }


       public String getType() {
	     return type;
       }

       public void setType(String type) {
	     this.type = type;
       }

       public String getValue() {
	     return value;
       }

       public void setValue(String value) {
	     this.value = value;
       }

}
