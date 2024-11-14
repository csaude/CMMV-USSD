package mz.org.fgh.vmmc.model;

import java.io.Serializable;
import java.util.List;

public class Utente implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String firstNames;
    private String lastNames;
    private String birthDate;
    private String cellNumber;
    private boolean haspartner;
    private String age;
    private String address;
    private String provinceId;
    private String districtId;
    private List<Appointment> appointments;
    private Clinic clinic;

    public Utente() {
	super();
	// TODO Auto-generated constructor stub
    }

    public Utente(String firstNames, String lastNames, String birthDate, String cellNumber, boolean haspartner, String age, String provinceId, String districtId) {
	super();
	this.firstNames = firstNames;
	this.lastNames = lastNames;
	this.birthDate = birthDate;
	this.cellNumber = cellNumber;
	this.haspartner = haspartner;
	this.age = age;
	this.provinceId = provinceId;
	this.districtId = districtId;
	this.clinic = clinic;

    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

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

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    public String getProvinceId() {
	return provinceId;
    }

    public void setProvinceId(String provinceId) {
	this.provinceId = provinceId;
    }

    public String getDistrictId() {
	return districtId;
    }

    public void setDistrictId(String districtId) {
	this.districtId = districtId;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }
    
    public List<Appointment> getAppointments() {
		return appointments;
	}
    
    public Clinic getClinic() {
		return clinic;
	}
    public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}
   
    public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}

    @Override
    public String toString() {
	return "Utente [firstNames=" + firstNames + ", lastNames=" + lastNames + ", birthDate=" + birthDate + ", cellNumber=" + cellNumber + ", haspartner=" + haspartner + ", age="
		+ age + ", address=" + address + ", provinceId=" + provinceId + ", districtId=" + districtId + "]";
    }

}
