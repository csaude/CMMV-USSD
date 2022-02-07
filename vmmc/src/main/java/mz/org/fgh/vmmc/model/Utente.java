package mz.org.fgh.vmmc.model;

import java.io.Serializable;
import java.util.Date;

public class Utente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String firstNames;
	private String lastNames;
	private String birthDate;
	private String cellNumber;
	private String haspartner;
	private String age;
	private String address;
	private String provinceId;
	private String districtId;
 

	public Utente() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Utente(String firstNames, String lastNames, String birthDate, String cellNumber, String haspartner,
			String age, String provinceId, String districtId ) {
		super();
		this.firstNames = firstNames;
		this.lastNames = lastNames;
		this.birthDate = birthDate;
		this.cellNumber = cellNumber;
		this.haspartner = haspartner;
		this.age = age;
		this.provinceId = provinceId;
		this.districtId = districtId;
		 
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

	@Override
	public String toString() {
	    return "Utente [firstNames=" + firstNames + ", lastNames=" + lastNames + ", birthDate=" + birthDate + ", cellNumber=" + cellNumber + ", haspartner=" + haspartner
		    + ", age=" + age + ", address=" + address + ", provinceId=" + provinceId + ", districtId=" + districtId + "]";
	}



}
