package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;

//
public interface MenuRepository extends JpaRepository<Menu, Long> {
	
	@Query(value = "select u from CurrentState u where u.sessionId  = :sessionId and u.isActive = :isActive") 
	public List<CurrentState> findCurrentStateBySessionId(@Param("sessionId") String sessionId, @Param("isActive") boolean isActive);
	
	
	@Query(value = "select u from Menu u where parent_menu_id  = :id and u.option = :option") 
	public List<Menu> findByMenuIdAndOption(@Param("id") Long id , @Param("option") String option);
	

}
