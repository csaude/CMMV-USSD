package mz.org.fgh.vmmc.commons;

public enum AppointmentStatus {

    APPOINTMENT_CONFIRMED("CONFIRMADO"), APPOINTMENT_PENDING("PENDENTE");

    private String code;

    AppointmentStatus(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }

    public AppointmentStatus getFromCode(String code) {
	for (AppointmentStatus operationType : AppointmentStatus.values()) {
	    if (operationType.code.equalsIgnoreCase(code)) {
		return operationType;
	    }
	}
	return null;
    }

    public boolean equals(String code) {
	AppointmentStatus operationType = getFromCode(code);
	if (operationType != null) {
	    return this.equals(operationType);
	}
	return false;
    }

}
