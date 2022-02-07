package mz.org.fgh.vmmc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.CurrentState;

public interface CurrentStateRepository  extends JpaRepository<CurrentState, Long> {
	
	public CurrentState findBySessionId (String sessionId);
	
	public CurrentState findByPhoneNumber (String phoneNumber);

}
