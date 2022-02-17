package mz.org.fgh.vmmc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.repository.SessionDataRepository;

@Service
public class SessionDataService {

       @Autowired
       SessionDataRepository sessionRepo;

       public List<SessionData> getAllSessionDataBySessionId(String sessionId) {
	     return sessionRepo.findBySessionId(sessionId);
       }

       public SessionData findBySessionIdAndAttrName(String sessionId, String attrName) {

	     return sessionRepo.findBySessionIdAndAttrName(sessionId, attrName);
       }

       public Long saveSessionMetadata(SessionData sessionData) {

	     SessionData sd = sessionRepo.findBySessionIdAndAttrName(sessionData.getSessionId(), sessionData.getAttrName());
	     if (sd != null) {
		   sd.setAttrValue(sessionData.getAttrValue());
		   return sessionRepo.save(sd).getId();
	     } else {
		   return sessionRepo.save(sessionData).getId();
	     }

       }

       public long deleteAllBySessionId(String sessionId) {

	     return sessionRepo.deleteBySessionId(sessionId);
       }

       public void saveSessionData(UtenteSearchResponse response, String sessionId) {

	     SessionData utenteIdSd = new SessionData(sessionId, "utenteId", String.valueOf(response.getId()));
	     SessionData clinicIdSd = new SessionData(sessionId, "clinicId", String.valueOf(response.getClinicId()));
	     SessionData districtIdSd = new SessionData(sessionId, "districtId", String.valueOf(response.getAddresses().get(0).getDistrict().getId()));

	     List<SessionData> sessionDataList = new ArrayList<SessionData>();
	     sessionDataList.add(utenteIdSd);
	     sessionDataList.add(clinicIdSd);
	     sessionDataList.add(districtIdSd);
	     sessionRepo.saveAll(sessionDataList);

       }

}
