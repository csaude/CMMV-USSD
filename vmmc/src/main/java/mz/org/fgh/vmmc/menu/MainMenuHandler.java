package mz.org.fgh.vmmc.menu;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;

public class MainMenuHandler implements MenuHandler {

       private static MainMenuHandler instance = new MainMenuHandler();

       private MainMenuHandler() {
       }

       public static MainMenuHandler getInstance() {
	     return instance;
       }

       @Override
       public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, HttpSession session) {

	     if (currentState == null) {
		   menuService.saveCurrentState(
			        new CurrentState(ussdRequest.getSessionId(), 1, true, LocationType.MENU_PRINCIPAL.getCode(), ussdRequest.getPhoneNumber(), LocalDateTime.now()));
	     } else {
		   currentState.setActive(false);
		   currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		   menuService.saveCurrentState(currentState);

	     }
	     return ConstantUtils.MENU_PRINCIPAL_DESCRIPTION;
       }

       @Override
       public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	     if (currentState != null) {
		   currentState.setActive(false);
		   currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
	     }
	     return ConstantUtils.MENU_PRINCIPAL_DESCRIPTION;
       }

}
