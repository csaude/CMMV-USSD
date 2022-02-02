package mz.org.fgh.vmmc.model;

import java.io.Serializable;

public class District implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String code;
    private String description;
    private String id;
    private String provinceId;

    public District() {
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

    public String getId() {
	return id;
    }

    public String getProvinceId() {
	return provinceId;
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public void setId(String id) {
	this.id = id;
    }

    public void setProvinceId(String provinceId) {
	this.provinceId = provinceId;
    }

}
