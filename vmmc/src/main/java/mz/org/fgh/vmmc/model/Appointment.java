package mz.org.fgh.vmmc.model;

public class Appointment {

    private Long id;
    private String appointmentDay;
    private String appointmentMonth;
    private Long clinicId;

    public Appointment(String appointmentDay, String appointmentMonth, Long clinicId) {
	super();
	this.appointmentDay = appointmentDay;
	this.appointmentMonth = appointmentMonth;
	this.clinicId = clinicId;
    }

    public Appointment() {
	super();
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getAppointmentDay() {
	return appointmentDay;
    }

    public void setAppointmentDay(String appointmentDay) {
	this.appointmentDay = appointmentDay;
    }

    public String getAppointmentMonth() {
	return appointmentMonth;
    }

    public void setAppointmentMonth(String appointmentMonth) {
	this.appointmentMonth = appointmentMonth;
    }

    public Long getClinicId() {
	return clinicId;
    }

    public void setClinicId(Long clinicId) {
	this.clinicId = clinicId;
    }

    @Override
    public String toString() {
	return "Appointment [id=" + id + ", appointmentDay=" + appointmentDay + ", appointmentMonth=" + appointmentMonth + ", clinicId=" + clinicId + "]";
    }

}
