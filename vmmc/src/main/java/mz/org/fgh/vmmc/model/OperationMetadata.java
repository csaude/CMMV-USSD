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

    @Column(name = "sessionId")
    private String sessionId;

    @Column(name = "phoneNumber")
    private String phoneNumber;

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

    public OperationMetadata(  String sessionId, String phoneNumber, String operationType, Menu menu, String attrName, String attrValue) {
	super();
	 
	this.sessionId = sessionId;
	this.phoneNumber = phoneNumber;
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

    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
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

}
