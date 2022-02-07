package mz.org.fgh.vmmc.commons;

public enum LocationType {

	MENU_PRINCIPAL("MENU_PRINCIPAL"), MENU_CADASTRO("CADASTRO"), MENU_LOGIN("LOGIN");

	private String code;

	LocationType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public LocationType getFromCode(String code) {
		for (LocationType operationType : LocationType.values()) {
			if (operationType.code.equalsIgnoreCase(code)) {
				return operationType;
			}
		}
		return null;
	}

	public boolean equals(String code) {
		LocationType operationType = getFromCode(code);
		if (operationType != null) {
			return this.equals(operationType);
		}
		return false;
	}

}
