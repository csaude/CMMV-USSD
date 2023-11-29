package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.SessionData;

public interface SessionDataRepository extends JpaRepository<SessionData, Long> {

       public List<SessionData> findByCurrentStateId(long currentStateId);

       public List<SessionData> findByCurrentStateIdAndAttrName(long sessionId, String attrName);

       public long deleteByCurrentStateId(long currentStateId);

}
