package mz.org.fgh.vmmc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "OperationMetadata")
public class OperationMetadata {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private long id;

       @ManyToOne(fetch = FetchType.LAZY, optional = false)
       @JoinColumn(name = "CurrentStateId", nullable = false)
       private CurrentState currentState;

       @Column(name = "operationType")
       private String operationType;

       @ManyToOne(fetch = FetchType.LAZY, optional = false)
       @JoinColumn(name = "menuId", nullable = false)
       private Menu menu;

       @Column(name = "attrName")
       private String attrName;

       @Column(name = "attrValue")
       private String attrValue;

       public OperationMetadata() {
	     super();
       }

       public OperationMetadata(CurrentState currentState, String currentStateId, String operationType, Menu menu, String attrName, String attrValue) {
	     super();
	     this.currentState = currentState;
	     this.operationType = operationType;
	     this.menu = menu;
	     this.attrName = attrName;
	     this.attrValue = attrValue;
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
	     this.id = id;
       }

       public String getOperationType() {
	     return operationType;
       }

       public void setOperationType(String operationType) {
	     this.operationType = operationType;
       }

       public Menu getMenu() {
	     return menu;
       }

       public void setMenu(Menu menu) {
	     this.menu = menu;
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

       public CurrentState getCurrentState() {
	     return currentState;
       }

       public void setCurrentState(CurrentState currentState) {
	     this.currentState = currentState;
       }

}
