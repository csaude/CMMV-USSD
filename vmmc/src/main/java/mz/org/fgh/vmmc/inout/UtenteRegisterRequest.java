package mz.org.fgh.vmmc.inout;

import java.util.Date;

import mz.org.fgh.vmmc.model.Address;

public class UtenteRegisterRequest {
    private String firstNames;
    private String lastNames;
    private Date birthDate;
    private String cellNumber;
    private String whatsappNumber;
    private String preferedLanguage;
    private String documentType;
    private String documentNumber;
    private String systemNumber;
    private String haspartner;
    private String age;
    private String status;
    private Address address;

    public String getFirstNames() {
	return firstNames;
    }

    public void setFirstNames(String firstNames) {
	this.firstNames = firstNames;
    }

    public String getLastNames() {
	return lastNames;
    }

    public void setLastNames(String lastNames) {
	this.lastNames = lastNames;
    }

    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    public String getCellNumber() {
	return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
	this.cellNumber = cellNumber;
    }

    public String getWhatsappNumber() {
	return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
	this.whatsappNumber = whatsappNumber;
    }

    public String getPreferedLanguage() {
	return preferedLanguage;
    }

    public void setPreferedLanguage(String preferedLanguage) {
	this.preferedLanguage = preferedLanguage;
    }

    public String getDocumentType() {
	return documentType;
    }

    public void setDocumentType(String documentType) {
	this.documentType = documentType;
    }

    public String getDocumentNumber() {
	return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
	this.documentNumber = documentNumber;
    }

    public String getSystemNumber() {
	return systemNumber;
    }

    public void setSystemNumber(String systemNumber) {
	this.systemNumber = systemNumber;
    }

    public String getHaspartner() {
	return haspartner;
    }

    public void setHaspartner(String haspartner) {
	this.haspartner = haspartner;
    }

    public String getAge() {
	return age;
    }

    public void setAge(String age) {
	this.age = age;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

}
