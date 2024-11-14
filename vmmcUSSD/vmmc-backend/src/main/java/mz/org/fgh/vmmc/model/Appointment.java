package mz.org.fgh.vmmc.model;

public class Appointment {

    private Long id;

    private Clinic clinic;
    private String appointmentDate;
    private String time;
    private long orderNumer;
    private boolean hasHappened;
    private String visitDate;
    private String status;
    

    public Appointment() {
	super();
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Clinic getClinic() {
	return clinic;
    }

    public void setClinic(Clinic clinicId) {
	this.clinic = clinicId;
    }

    public String getAppointmentDate() {
	return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
	this.appointmentDate = appointmentDate;
    }

    public String getTime() {
	return time;
    }

    public void setTime(String time) {
	this.time = time;
    }

    public long getOrderNumer() {
	return orderNumer;
    }

    public void setOrderNumer(long orderNumer) {
	this.orderNumer = orderNumer;
    }

    public boolean isHasHappened() {
	return hasHappened;
    }

    public void setHasHappened(boolean hasHappened) {
	this.hasHappened = hasHappened;
    }

    public String getVisitDate() {
	return visitDate;
    }

    public void setVisitDate(String visitDate) {
	this.visitDate = visitDate;
    }
    
    public String getStatus() {
		return status;
	}
    
    public void setStatus(String status) {
		this.status = status;
	}

}
