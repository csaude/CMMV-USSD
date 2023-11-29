package mz.org.fgh.vmmc.commons;

public enum RegisterStatus {
    
    REGISTER_ASSOCIATED("ASSOCIADO"), REGISTER_PENDING("PENDENTE"), REGISTER_SENT("ENVIADO");

    private String code;

    RegisterStatus(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

    public RegisterStatus getFromCode(String code) {
	for (RegisterStatus operationType : RegisterStatus.values()) {
	    if (operationType.code.equalsIgnoreCase(code)) {
		return operationType;
	    }
	}
	return null;
    }

    public boolean equals(String code) {
	RegisterStatus operationType = getFromCode(code);
	if (operationType != null) {
	    return this.equals(operationType);
	}
	return false;
    }

}
