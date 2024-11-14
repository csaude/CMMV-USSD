package mz.org.fgh.vmmc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FrontlineSmsConfig")
public class FrontlineSmsConfig implements Serializable {

       private static final long serialVersionUID = 1L;
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private long id;
       @Column(name = "code")
       private String code;
       @Column(name = "endpoint")
       private String endpoint;
       @Column(name = "apiKey")
       private String apiKey;

       public String getApiKey() {
	     return apiKey;
       }

       public void setApikey(String apiKey) {
	     this.apiKey = apiKey;
       }

       public String getEndpoint() {
	     return endpoint;
       }

       public void setEndpoint(String endpoint) {
	     this.endpoint = endpoint;
       }

       public String getCode() {
              return code;
       }

       public void setCode(String code) {
              this.code = code;
       }
       
       

}
