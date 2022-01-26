package mz.org.fgh.vmmc.model;

import java.io.Serializable;
import java.util.List;

public class Province implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String description;
	private int id;
	private List<District> districts;
	

	public Province() {
		super();
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public List<District> getDistricts() {
		return districts;
	}

}
