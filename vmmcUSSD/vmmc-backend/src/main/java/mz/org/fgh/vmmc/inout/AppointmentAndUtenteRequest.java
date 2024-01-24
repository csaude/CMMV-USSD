package mz.org.fgh.vmmc.inout;

public class AppointmentAndUtenteRequest {

       private Long id;

       private String appointmentDate;
       private String time = "0:0"; // TODO: ver
       private Long clinic;
       private Long utente;
       private String status;
       private long orderNumer;
       private boolean hasHappened;
       private String visitDate;
       private String clinicName;

       public AppointmentAndUtenteRequest() {
	     super();
       }

       public Long getId() {
	     return id;
       }

       public void setId(Long id) {
	     this.id = id;
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

       public Long getClinic() {
	     return clinic;
       }

       public void setClinic(Long clinic) {
	     this.clinic = clinic;
       }

       public String getStatus() {
	     return status;
       }

       public void setStatus(String status) {
	     this.status = status;
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

       public Long getUtente() {
	     return utente;
       }

       public void setUtente(Long utente) {
	     this.utente = utente;
       }

       public String getClinicName() {
	     return clinicName;
       }

       public void setClinicName(String clinicName) {
	     this.clinicName = clinicName;
       }

}
