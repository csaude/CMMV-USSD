package mz.org.fgh.vmmc.model;

public class Address {
    private long id;
    private String city;
    private String residence;
    private String latitude;
    private String longitude;
    private District district;

    public Address() {
	super();
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getResidence() {
	return residence;
    }

    public void setResidence(String residence) {
	this.residence = residence;
    }

    public String getLatitude() {
	return latitude;
    }

    public void setLatitude(String latitude) {
	this.latitude = latitude;
    }

    public String getLongitude() {
	return longitude;
    }

    public void setLongitude(String longitude) {
	this.longitude = longitude;
    }

    public District getDistrict() {
	return district;
    }

    public void setDistrict(District district) {
	this.district = district;
    }

}
