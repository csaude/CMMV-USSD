package mz.org.fgh.vmmc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.model.FrontlineSmsConfig;
import mz.org.fgh.vmmc.repository.FrontlineSmsConfigRepository;

@Service
public class FrontlineSmsConfigService {       

       @Autowired
       FrontlineSmsConfigRepository frontlineSmsConfigRepo;

       public List<FrontlineSmsConfig> findFrontlineSmsConfigByCode(String code) {
	     return frontlineSmsConfigRepo.findByCode(code);
       }

     
}
