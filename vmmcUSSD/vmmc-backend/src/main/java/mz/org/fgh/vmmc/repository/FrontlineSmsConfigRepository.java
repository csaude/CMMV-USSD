package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.FrontlineSmsConfig;

public interface FrontlineSmsConfigRepository extends JpaRepository<FrontlineSmsConfig, Long> {

       public List<FrontlineSmsConfig> findByCode(String code);
}
