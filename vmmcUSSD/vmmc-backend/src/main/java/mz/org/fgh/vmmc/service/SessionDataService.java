package mz.org.fgh.vmmc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.repository.SessionDataRepository;

@Service
public class SessionDataService {

       @Autowired
       SessionDataRepository sessionRepo;

       public List<SessionData> getAllSessionDataBySessionId(long sessionId) {
	     return sessionRepo.findByCurrentStateId(sessionId);
       }

       public SessionData findByCurrentStateIdAndAttrName(long currentStateId, String attrName) {

	     List<SessionData> list = sessionRepo.findByCurrentStateIdAndAttrName(currentStateId, attrName);
	     return list.isEmpty() ? null : list.get(list.size() - 1);
       }

       public Long saveSessionData(SessionData sessionData) {

	     SessionData sd = findByCurrentStateIdAndAttrName(sessionData.getCurrentStateId(), sessionData.getAttrName());
	     if (sd != null) {
		   sd.setAttrValue(sessionData.getAttrValue());
		   return sessionRepo.save(sd).getId();
	     } else {
		   return sessionRepo.save(sessionData).getId();
	     }

       }

       public long deleteAllByCurrentStateId(long sessionId) {

	     return sessionRepo.deleteByCurrentStateId(sessionId);
       }

       public void saveSessionData(UtenteSearchResponse response, long currentStateId) {
	 //    Address address = response.getAddresses().get(0);
	     SessionData utenteIdSd = new SessionData(currentStateId, "utenteId", String.valueOf(response.getId()));
	     SessionData appointmentId = new SessionData(currentStateId,"appointmentId", String.valueOf(response.getAppointments().get(0).getId()));
	    // SessionData districtIdSd = new SessionData(currentStateId, "districtId", String.valueOf(address != null ? address.getDistrict().getId() : ""));
	     saveSessionData(utenteIdSd);
	     //saveSessionData(districtIdSd);
	     saveSessionData(appointmentId);
       }

       public void saveClinicOnSessionData(Clinic clinic, long currentStateId) {
	     SessionData clinicIdSd = new SessionData(currentStateId, "clinicId", clinic.getId() + "");
	     SessionData clinicNameSd = new SessionData(currentStateId, "clinicName", clinic.getName());
	     saveSessionData(clinicIdSd);
	     saveSessionData(clinicNameSd);
       }

}
