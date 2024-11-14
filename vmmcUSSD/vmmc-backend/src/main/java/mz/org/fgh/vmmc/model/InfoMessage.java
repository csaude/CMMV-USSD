package mz.org.fgh.vmmc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "InfoMessage")
public class InfoMessage implements Serializable {
       /**
        * 
        */
       private static final long serialVersionUID = 1L;

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private long id;

       @Column(name = "code")
       private String code;

       @Column(name = "description")
       private String description;

       public InfoMessage() {
	     super();
       }

       public String getCode() {
	     return code;
       }

       public void setCode(String code) {
	     this.code = code;
       }

       public String getDescription() {
	     return description;
       }

       public void setDescription(String description) {
	     this.description = description;
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
	     this.id = id;
       }

}
