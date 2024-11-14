package mz.org.fgh.vmmc.menu;

import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;

@Service
public interface MenuHandler {

       public UssdOut handleMenu(UssdIn ussdIn, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) throws Throwable;

       public UssdOut recoverSession(UssdIn request, CurrentState currentState, MenuService menuService, SessionDataService sessionDataservice, OperationMetadataService operationMetadataService) throws Throwable;

}
