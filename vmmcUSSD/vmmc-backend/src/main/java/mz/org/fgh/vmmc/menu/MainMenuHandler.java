package mz.org.fgh.vmmc.menu;

import java.time.LocalDateTime;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.service.FrontlineSmsConfigService;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class MainMenuHandler implements MenuHandler {

       private static MainMenuHandler instance = new MainMenuHandler();

       private MainMenuHandler() {
       }

       public static MainMenuHandler getInstance() {
	     return instance;
       }

       @Override
       public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, InfoMessageService infoMessageService, FrontlineSmsConfigService frontlineCSmsConfigService) {

	     if (currentState == null) {
	    	 currentState = new CurrentState(ussdRequest.getSessionId(), 1, true, LocationType.MENU_PRINCIPAL.getCode(), ussdRequest.getPhoneNumber(), LocalDateTime.now());
		  long stateId= menuService.saveCurrentState(currentState
			        );
		   currentState.setId(stateId);
	     } else {
		   currentState.setActive(true);
		   currentState.setIdMenu(1);
		   currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		   currentState.setSessionId(ussdRequest.getSessionId());
		   menuService.saveCurrentState(currentState);

	     }    
    	   Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdRequest.getSessionId(), true); 
	     return getNextMenuText(currentState, currentMenu, menuService);
	     
       }

       @Override
       public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService, SessionDataService sessionDataService, OperationMetadataService serviceMeta) {
    	   Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);
	     if (currentState != null) {
		   currentState.setActive(false);
		   currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		   currentState.setSessionId(request.getSessionId());
		   menuService.saveCurrentState(currentState);
	     }
	     return getNextMenuText(currentState, currentMenu, menuService);
	   //  return ConstantUtils.MENU_PRINCIPAL_DESCRIPTION;
       }

       
   	private String getNextMenuText(CurrentState currentState, Menu currentMenu, MenuService menuService) {
		// Passa para o proximo menu se a opcao != 0
		// se tiver apenas a opcao (0. Voltar) e o utilizador introduzir o nome por
		// exemplo, passa para o proximo menu
		currentState.setIdMenu(currentMenu.getNextMenuId());
		menuService.saveCurrentState(currentState);
		// pega o proximo menu
		Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
		return MessageUtils.getMenuText(nextMenu);
	}
}
