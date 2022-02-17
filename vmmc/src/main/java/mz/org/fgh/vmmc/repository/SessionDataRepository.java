package mz.org.fgh.vmmc.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mz.org.fgh.vmmc.model.SessionData;

public interface SessionDataRepository extends JpaRepository<SessionData, Long> {

       public List<SessionData> findBySessionId(String sessionId);

       public SessionData findBySessionIdAndAttrName(String sessionId, String attrName);

       public long deleteBySessionId(String sessionId);
       
       @Modifying
       @Transactional
       @Query(value = "update SessionData m set m.sessionId =:sessionId where m.phoneNumber=:phoneNumber")
       public void updateSessionDataByPhoneNumber(@Param("sessionId") String sessionId,@Param("phoneNumber")  String phoneNumber);

}
