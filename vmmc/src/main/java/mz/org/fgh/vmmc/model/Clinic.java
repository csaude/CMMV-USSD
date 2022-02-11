package mz.org.fgh.vmmc.model;

import java.io.Serializable;

public class Clinic implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public long id;
    private String code;
    private String name;
    private String type;
    private long districtId;
    private double latitude;
    private double longitude;

    public Clinic() {
	super();
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public long getDistrictId() {
	return districtId;
    }

    public void setDistrictId(long districtId) {
	this.districtId = districtId;
    }

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(double longitude) {
	this.longitude = longitude;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    @Override
    public String toString() {
	return "ClinicsResponse [code=" + code + ", name=" + name + ", type=" + type + ", districtId=" + districtId + ", latitude=" + latitude + ", longitude=" + longitude + "]";
    }

}
