package mz.org.fgh.vmmc.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.menu.MenuHandler;
import mz.org.fgh.vmmc.menu.MenuHandlerType;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.FrontlineSmsConfigService;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

@Service
public class VmmcService {

       Logger logger = Logger.getLogger(VmmcService.class.getName());
       private static HashMap<String, MenuHandler> menuTypes = MenuHandlerType.getMenuHandlerTypes();

       @Autowired
       MenuService menuService;

       @Autowired
       OperationMetadataService operationMetadataService;

       @Autowired
       SessionDataService sessionDataService;

       @Autowired
       InfoMessageService infoMessageService;

       @Autowired
       FrontlineSmsConfigService frontlineSmsConfigService;

       public String invokeVmmcUssdService(String sessionId, String serviceCode, String phoneNumber, String text) throws Throwable {

	     UssdRequest ussdRequest = new UssdRequest(sessionId, serviceCode, phoneNumber, MessageUtils.formatInputText(text));
	     long sessionRecoverTime = Long.parseLong(System.getProperty("sessionRecoverTime", "30"));

	     CurrentState currentState = menuService.findCurrentStateByPhoneNumber(phoneNumber, true);

	     if (currentState != null) {
		   long seconds = Duration.between(currentState.getCreatedDate(), LocalDateTime.now()).getSeconds();
		   if (isSessionExpired(ussdRequest, sessionRecoverTime, currentState, seconds)) {

			 switch (ussdRequest.getText()) {
			 case "":
			        return ConstantUtils.MENU_SESSION_RECOVER_DESCRIPTION;
			 case "1":
			        return menuTypes.get(currentState.getLocation()).recoverSession(ussdRequest, currentState, menuService, sessionDataService);
			 case "2":
			        return menuTypes.get(LocationType.MENU_PRINCIPAL.getCode()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService,
					    sessionDataService, infoMessageService, frontlineSmsConfigService);
			 default:
			        return ConstantUtils.MESSAGE_OPCAO_INVALIDA_TERMINAR;
			 }

		   } else if (currentState.getLocation().equalsIgnoreCase(LocationType.MENU_PRINCIPAL.getCode())) {
			 switch (ussdRequest.getText()) {

			 case "1":
			        currentState.setLocation(LocationType.MENU_OPERACOES.getCode());
			        break;
			 case "2":
			        currentState.setLocation(LocationType.MENU_CADASTRO.getCode());
			        break;
			 case "":
			        currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
			        break;
			 default:
			        return ConstantUtils.MESSAGE_OPCAO_INVALIDA_TERMINAR;

			 }
			 return menuTypes.get(currentState.getLocation()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService, sessionDataService,
				      infoMessageService, frontlineSmsConfigService);
		   } else {
			 return menuTypes.get(currentState.getLocation()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService, sessionDataService,
				      infoMessageService, frontlineSmsConfigService);
		   }

	     }

	     else {
		   return menuTypes.get(LocationType.MENU_PRINCIPAL.getCode()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService, sessionDataService,
			        infoMessageService, frontlineSmsConfigService);
	     }

       }

       private boolean isSessionExpired(UssdRequest ussdRequest, long sessionRecoverTime, CurrentState currentState, long seconds) {
	     return !currentState.getSessionId().equalsIgnoreCase(ussdRequest.getSessionId()) && seconds > sessionRecoverTime
			 && !currentState.getLocation().equalsIgnoreCase(LocationType.MENU_PRINCIPAL.getCode());
       }

       public String testService() {
	     return "Its working just fine cdc";
       }
}
