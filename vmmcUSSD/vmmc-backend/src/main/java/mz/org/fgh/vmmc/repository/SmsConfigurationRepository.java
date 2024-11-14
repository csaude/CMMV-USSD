package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.SmsConfiguration;

public interface SmsConfigurationRepository extends JpaRepository<SmsConfiguration, Long> {

    public List<SmsConfiguration> findByCode(String code);
}
