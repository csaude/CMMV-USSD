package mz.org.fgh.vmmc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SessionData")
public class SessionData implements Serializable {

       /**
        * 
        */
       private static final long serialVersionUID = 1L;

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private long id;

       @Column(name = "currentStateId")
       private long currentStateId;

       @Column(name = "attrName")
       private String attrName;

       @Column(name = "attrValue")
       private String attrValue;

       public SessionData() {
	     super();
       }

       public SessionData(long currentStateId, String attrName, String attrValue) {
	     super();
	     this.currentStateId = currentStateId;
	     this.attrName = attrName;
	     this.attrValue = attrValue;
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
	     this.id = id;
       }

       public String getAttrName() {
	     return attrName;
       }

       public void setAttrName(String attrName) {
	     this.attrName = attrName;
       }

       public String getAttrValue() {
	     return attrValue;
       }

       public void setAttrValue(String attrValue) {
	     this.attrValue = attrValue;
       }

       public long getCurrentStateId() {
	     return currentStateId;
       }

       public void setCurrentStateId(long currentStateId) {
	     this.currentStateId = currentStateId;
       }

       @Override
       public String toString() {
	     return "SessionData [id=" + id + ", currentStateId=" + currentStateId + ", attrName=" + attrName + ", attrValue=" + attrValue + "]";
       }

}
