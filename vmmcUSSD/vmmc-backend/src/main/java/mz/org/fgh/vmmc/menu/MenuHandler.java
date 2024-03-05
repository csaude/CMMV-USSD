package mz.org.fgh.vmmc.menu;

import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.FrontlineSmsConfigService;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;

@Service
public interface MenuHandler {

       public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) throws Throwable;

       public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService, SessionDataService sessionDataservice, OperationMetadataService operationMetadataService) throws Throwable;

}
