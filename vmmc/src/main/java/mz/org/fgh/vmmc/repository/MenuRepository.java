package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;

//
public interface MenuRepository extends JpaRepository<Menu, Long> {
	
	@Query(value = "select u from CurrentState u where u.sessionId  = :sessionId") 
	public List<CurrentState> findCurrentStateBySessionIdAndId(@Param("sessionId") String sessionId);
	
	
	@Query(value = "select u from Menu u where parent_menu_id  = :id and u.code = :code") 
	public List<Menu> findByMenuIdAndCode(@Param("id") Long id , @Param("code") String code);
	

}
