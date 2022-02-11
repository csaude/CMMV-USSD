package mz.org.fgh.vmmc.inout;

import java.io.Serializable;

import mz.org.fgh.vmmc.model.Address;

 
public class UtenteRegisterRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String firstNames;
    private String lastNames;
    private String birthDate;
    private String cellNumber;
    private String whatsappNumber;
    private String preferedLanguage;
    private String documentType;
    private String documentNumber;
    private String systemNumber;
    private boolean haspartner;
    private String age;
    private String status;
    private Address address;
    private String registerDate;

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

    public String getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(String birthDate) {
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

    public boolean getHaspartner() {
	return haspartner;
    }

    public void setHaspartner(boolean haspartner) {
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

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }
    

}
