package mz.org.fgh.vmmc.inout;

import java.util.List;

import mz.org.fgh.vmmc.model.Address;
import mz.org.fgh.vmmc.model.Appointment;
import mz.org.fgh.vmmc.model.Clinic;

public class UtenteSearchResponse extends GenericResponse {
       private long id;
       private long clinicId;
       private String firstNames;
       private String lastNames;
       private String status;
       private String systemNumber;
       private String cellNumber;
       private List<Address> addresses;
       private List<Appointment> appointments;
       private Clinic clinic;

       public UtenteSearchResponse() {
	     super();
       }

       public long getId() {
	     return id;
       }

       public void setId(long id) {
	     this.id = id;
       }

       public Long getClinicId() {
	     return clinicId;
       }

       public void setClinicId(long clinicId) {
	     this.clinicId = clinicId;
       }

       public String getFirstNames() {
	     return firstNames;
       }

       public void setFirstNames(String firstNames) {
	     this.firstNames = firstNames;
       }

       public String getLastNames() {
	     return lastNames;
       }

       public void setLastNames(String lastNames) {
	     this.lastNames = lastNames;
       }

       public String getStatus() {
	     return status;
       }

       public void setStatus(String status) {
	     this.status = status;
       }

       public String getSystemNumber() {
	     return systemNumber;
       }

       public void setSystemNumber(String systemNumber) {
	     this.systemNumber = systemNumber;
       }

       public String getCellNumber() {
	     return cellNumber;
       }

       public void setCellNumber(String cellNumber) {
	     this.cellNumber = cellNumber;
       }

       public List<Address> getAddresses() {
	     return addresses;
       }

       public void setAddresses(List<Address> addresses) {
	     this.addresses = addresses;
       }

       public List<Appointment> getAppointments() {
	     return appointments;
       }

       public void setAppointments(List<Appointment> appointments) {
	     this.appointments = appointments;
       }

       public Clinic getClinic() {
	     return clinic;
       }

       public void setClinic(Clinic clinic) {
	     this.clinic = clinic;
       }
}
