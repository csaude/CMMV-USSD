package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.CurrentState;

public interface CurrentStateRepository  extends JpaRepository<CurrentState, Long> {
	
	public List<CurrentState>findBySessionIdAndIsActive (String sessionId, boolean isActive);
	
	public List<CurrentState> findByPhoneNumberAndIsActive (String phoneNumber, boolean isActive);

}
