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

}
