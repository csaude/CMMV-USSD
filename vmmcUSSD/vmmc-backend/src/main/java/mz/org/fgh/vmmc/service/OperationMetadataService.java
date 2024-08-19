package mz.org.fgh.vmmc.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.commons.AppointmentStatus;
import mz.org.fgh.vmmc.commons.RegisterStatus;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.model.Address;
import mz.org.fgh.vmmc.model.Appointment;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.repository.OperationMetadataRepository;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.DateUtils;

@Service
public class OperationMetadataService {

       @Autowired
       OperationMetadataRepository operationMetadataRepository;
       
       @Autowired
       SessionDataService sessionDataService;

       public OperationMetadata saveOperationMetadata(OperationMetadata operationMetadata) {
	     OperationMetadata metadata = operationMetadataRepository.findByCurrentStateIdAndMenuIdAndOperationType(operationMetadata.getCurrentState().getId(),
			 operationMetadata.getMenu().getId(), operationMetadata.getOperationType());

	     if (metadata == null) {
		   return operationMetadataRepository.save(operationMetadata);
	     } else {
		   metadata.setAttrName(operationMetadata.getAttrName());
		   metadata.setAttrValue(operationMetadata.getAttrValue());
		   return operationMetadataRepository.save(metadata);
	     }
       }

       public Map<String, OperationMetadata> getMetadatasByOperationTypeAndSessionId(long currentStateId, String operationType) {

	     List<OperationMetadata> metadataList = operationMetadataRepository.findByCurrentStateIdAndOperationType(currentStateId, operationType);
	     Map<String, OperationMetadata> map = metadataList.stream().collect(Collectors.toMap(OperationMetadata::getAttrName, Function.identity()));
	     return map;
       }

       public UtenteRegisterRequest createUtenteByMetadatas(UssdIn ussdIn, String operationType, CurrentState currentState) {

	     Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(currentState.getId(), operationType);
	     int year = LocalDate.now().getYear() - Integer.parseInt(metadatas.get("age").getAttrValue());
	     int month = LocalDate.now().getMonthValue();
	     int day = LocalDate.now().getDayOfMonth();

	     UtenteRegisterRequest request = new UtenteRegisterRequest();
	     request.setFirstNames(metadatas.get("firstNames").getAttrValue());
	     request.setLastNames(metadatas.get("lastNames").getAttrValue());
	     request.setBirthDate(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
	     request.setRegisterDate(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
	     request.setStatus(RegisterStatus.REGISTER_PENDING.getCode());

	     District district = new District();
	     district.setId(Long.valueOf(metadatas.get("districtId").getAttrValue()));
	     district.setProvinceId(metadatas.get("provinceId").getAttrValue());

	     Address[] addresses = new Address[1];
	     Address address = new Address();
	     address.setResidence("N/A");
	     address.setDistrict(district);
	     address.setLatitude("0");
	     address.setLongitude("0");
	     addresses[0] = address;

	     request.setAddresses(addresses);
	     request.setCellNumber(metadatas.get("cellNumber") == null ? StringUtils.remove(ussdIn.getTo(), "+258")
			 : StringUtils.remove(metadatas.get("cellNumber").getAttrValue(), "+258"));
	     request.setWhatsappNumber(request.getCellNumber());
	   //  request.setHaspartner(metadatas.get("hasPartner").getAttrValue().equals("1") ? true : false);
	     request.setAge(metadatas.get("age").getAttrValue());
	     
	     Appointment appointment = new Appointment();   
	     
	      
	     int appointmentDay = Integer.parseInt(metadatas.get("dayRegister").getAttrValue());
	     int appointmentMonth = Integer.parseInt(metadatas.get("monthRegister").getAttrValue());
	     String appointmentDate = DateUtils.formatDateByMonthAndDay(appointmentDay, appointmentMonth);
	     
	     appointment.setAppointmentDate(operationType);
	     appointment.setAppointmentDate(appointmentDate);
	     appointment.setOrderNumer(1);
	     appointment.setStatus(AppointmentStatus.APPOINTMENT_PENDING.getCode());
	     appointment.setTime("0:0"); // TODO: Rever
	     appointment.setHasHappened(false);
	     
	     Clinic clinic = new Clinic();
	     clinic.setId(Long.parseLong(sessionDataService
					.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicId").getAttrValue()));
	     appointment.setClinic(clinic);
	     
	     
	     List<Appointment> appointments = new ArrayList<>();
	     appointments.add(appointment);
	     request.setAppointments(appointments);
	     
	     
	     return request;

       }

       public AppointmentRequest createAppointmentRequestByMetadatas(UssdIn ussdIn, String operationType, CurrentState currentState, Menu menu) {

	     Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(currentState.getId(), operationType);

	     int appointmentDay = Integer.parseInt(metadatas.get(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE.equalsIgnoreCase(menu.getCode())? "appointmentDay":"dayReschedule").getAttrValue());
	     int appointmentMonth = Integer.parseInt(metadatas.get(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE.equalsIgnoreCase(menu.getCode())? "appointmentMonth":"monthReschedule" ).getAttrValue());
	     
	     long clinicId = Long.parseLong(sessionDataService
					.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicId").getAttrValue());
	     
	     if (ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_RESCHEDULE_CODE.equalsIgnoreCase(menu.getCode())) {
	    	 clinicId = Integer.parseInt(metadatas.get("clinicReschedule").getAttrValue());
	     }
	     Long appointmentId =Long.parseLong(sessionDataService
		.findByCurrentStateIdAndAttrName(currentState.getId(), "appointmentId").getAttrValue());
		  String appointmentDate = DateUtils.formatDateByMonthAndDay(appointmentDay, appointmentMonth);
	     AppointmentRequest request = new AppointmentRequest();
	     request.setAppointmentDate(appointmentDate);
	     request.setOrderNumer(1);
	     request.setStatus(AppointmentStatus.APPOINTMENT_PENDING.getCode());
	     request.setTime("0:0"); // TODO: Rever
	     request.setHasHappened(false);
	     request.setId(appointmentId);
	     request.setClinic(clinicId);
	     return request;

       }

       
       public AppointmentRequest createAppointmentRequestByMetadatasOnRegistration(UssdIn ussdIn, String operationType, CurrentState currentState) {

  	     Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(currentState.getId(), operationType);

  	     int appointmentDay = Integer.parseInt(metadatas.get("dayRegister").getAttrValue());
  	     int appointmentMonth = Integer.parseInt(metadatas.get("monthRegister").getAttrValue());

  	     String appointmentDate = DateUtils.formatDateByMonthAndDay(appointmentDay, appointmentMonth);

  	     AppointmentRequest request = new AppointmentRequest();

  	     request.setAppointmentDate(appointmentDate);
  	     request.setOrderNumer(1);
  	     request.setStatus(AppointmentStatus.APPOINTMENT_PENDING.getCode());
  	     request.setTime("0:0"); // TODO: Rever
  	     request.setHasHappened(false);
  	     return request;

         }
       
       public String getAppointmentConfirmationDataOnRegistration( UtenteRegisterRequest utenteRequest, CurrentState currentState) {
    	     // TODO: acrescentar outros dados
    	   
    	   SessionData sessionData = sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicName");
    	    Appointment appointment =  utenteRequest.getAppointments().get(0);
    	     

    	     String appointmentDate = DateUtils.getSimpleDateFormat(appointment != null ? appointment.getAppointmentDate(): " ");
    	     StringBuilder sb = new StringBuilder();
    	     return sb.append("\n Nome:").append(utenteRequest.getFirstNames()).append(" ").append(utenteRequest.getLastNames()).append("\n Idade:").append(utenteRequest.getAge()).append("\n Data:").append(appointmentDate).append("\n Unidade Sanitaria: ").append(sessionData.getAttrValue()).toString();

           }
       
       
       public String getAppointmentConfirmationData(AppointmentRequest data) {
	     // TODO: acrescentar outros dados

	     String appointmentDate = DateUtils.getSimpleDateFormat(data.getAppointmentDate());
	     StringBuilder sb = new StringBuilder();
	     return sb.append("\n Data:").append(appointmentDate).append("\n Unidade Sanitaria: ").append(data.getClinicName()).toString();

       }

       


       public String getRegisterConfirmationData(UtenteRegisterRequest data) {

	     StringBuilder sb = new StringBuilder();
	     return sb.append("\n Nome:").append(data.getFirstNames()).append(" ").append(data.getLastNames()).append("\n Idade:").append(data.getAge()).append("\n Telefone:")
			 .append(data.getCellNumber()).toString();
       }

       /*
        * public void updateOperationMetadataSessionId(String sessionId, String
        * phoneNumber) {
        * operationMetadataRepository.updateOperationMetadataSessionId(sessionId,
        * phoneNumber); }
        */
}
