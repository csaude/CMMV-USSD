package mz.org.fgh.vmmc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.model.InfoMessage;
import mz.org.fgh.vmmc.repository.InfoMessageRepository;

@Service
public class InfoMessageService {

       @Autowired
       InfoMessageRepository infoMessageRepo;

       public List<InfoMessage> findMessagesByCode(String code) {
	     return infoMessageRepo.findByCode(code);
       }

     
}
