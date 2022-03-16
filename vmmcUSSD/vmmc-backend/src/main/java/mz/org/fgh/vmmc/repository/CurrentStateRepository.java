package mz.org.fgh.vmmc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.CurrentState;

public interface CurrentStateRepository  extends JpaRepository<CurrentState, Long> {
	
	public CurrentState findBySessionIdAndIsActive (String sessionId, boolean isActive);
	
	public CurrentState findByPhoneNumberAndIsActive (String phoneNumber, boolean isActive);

}
