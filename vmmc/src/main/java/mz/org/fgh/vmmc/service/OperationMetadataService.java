package mz.org.fgh.vmmc.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.commons.AppointmentStatus;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.Appointment;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Utente;
import mz.org.fgh.vmmc.repository.OperationMetadataRepository;
import mz.org.fgh.vmmc.utils.DateUtils;

@Service
public class OperationMetadataService {

    @Autowired
    OperationMetadataRepository operationMetadataRepository;

    public OperationMetadata saveOperationMetadata(OperationMetadata operationMetadata) {
	OperationMetadata metadata = operationMetadataRepository.findBySessionIdAndMenuIdAndOperationType(operationMetadata.getSessionId(), operationMetadata.getMenu().getId(),
		operationMetadata.getOperationType());

	if (metadata == null) {
	    return operationMetadataRepository.save(operationMetadata);
	} else {
	    metadata.setAttrName(operationMetadata.getAttrName());
	    metadata.setAttrValue(operationMetadata.getAttrValue());
	    return operationMetadataRepository.save(metadata);
	}
    }

    public Map<String, OperationMetadata> getMetadatasByOperationTypeAndSessionId(String sessionId, String operationType) {

	List<OperationMetadata> metadataList = operationMetadataRepository.findBySessionIdAndOperationType(sessionId, operationType);
	Map<String, OperationMetadata> map = metadataList.stream().collect(Collectors.toMap(OperationMetadata::getAttrName, Function.identity()));
	return map;
    }

    public Utente createUtenteByMetadatas(UssdRequest request, String operationType) {

	Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(request.getSessionId(), operationType);

	boolean hasPartner = metadatas.get("hasPartner").getAttrValue().equals("1") ? true : false;
	Utente utente = new Utente(metadatas.get("firstNames").getAttrValue(), metadatas.get("lastNames").getAttrValue(), "", // TODO: birth date
		metadatas.get("cellNumber") == null ? request.getPhoneNumber() : metadatas.get("cellNumber").getAttrValue(), hasPartner, metadatas.get("age").getAttrValue(),
		metadatas.get("provinceId").getAttrValue(), metadatas.get("districtId").getAttrValue());

	return utente;

    }

    public AppointmentRequest createAppointmentRequestByMetadatas(UssdRequest ussdRequest, String operationType) {

	Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(ussdRequest.getSessionId(), operationType);

	Appointment appointment = new Appointment(metadatas.get("appointmentDay").getAttrValue(), metadatas.get("appointmentMonth").getAttrValue(),
		Long.valueOf(metadatas.get("clinicId").getAttrValue()));

	long utenteId = 26; // TODO: Rever
	long clinicId = appointment.getClinicId();// 1
	String appointmentDate = DateUtils.formatDateByMonthAndDay(Integer.parseInt(appointment.getAppointmentDay()), Integer.parseInt(appointment.getAppointmentMonth()));
	AppointmentRequest request = new AppointmentRequest();
	request.setClinic(clinicId);
	request.setUtente(utenteId);
	request.setAppointmentDate(appointmentDate);
	request.setOrderNumer(1);
	request.setStatus(AppointmentStatus.APPOINTMENT_PENDING.getCode());
	request.setTime("0:0"); // TODO: Rever
	request.setHasHappened(false);
	return request;

    }

    public String getAppointmentConfirmationData(AppointmentRequest request) {
	// TODO: acrescentar outros dados
	LocalDateTime ldti = LocalDateTime.ofInstant(Instant.parse(request.getAppointmentDate()), ZoneOffset.UTC);
	String data = ldti.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	StringBuilder sb = new StringBuilder();
	return sb.append(data).append("\n").toString();

    }

    public void updateOperationMetadataSessionId(String sessionId, String phoneNumber) {
	operationMetadataRepository.updateOperationMetadataSessionId(sessionId, phoneNumber);
    }
}
