package mz.org.fgh.vmmc.inout;

import mz.org.fgh.vmmc.model.Clinic;

public class AppointmentSearchResponse extends GenericResponse {

       private long id;
       private String appointmentDate;
       private String time;
       private String status;
       private int order;
       private Clinic clinic;

       public AppointmentSearchResponse() {
	     super();
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
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

       public String getStatus() {
	     return status;
       }

       public void setStatus(String status) {
	     this.status = status;
       }

       public int getOrder() {
	     return order;
       }

       public void setOrder(int order) {
	     this.order = order;
       }

       public Clinic getClinic() {
	     return clinic;
       }

       public void setClinic(Clinic clinic) {
	     this.clinic = clinic;
       }

}
