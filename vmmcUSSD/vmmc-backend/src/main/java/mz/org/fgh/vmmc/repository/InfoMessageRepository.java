package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.InfoMessage;

public interface InfoMessageRepository extends JpaRepository<InfoMessage, Long> {

       public List<InfoMessage> findByCode(String code);

}
