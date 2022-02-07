package mz.org.fgh.vmmc.menu;

import java.time.LocalDateTime;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;

public class MainMenuHandler implements MenuHandler {

    private static MainMenuHandler instance = new MainMenuHandler();

    private MainMenuHandler() {
    }

    public static MainMenuHandler getInstance() {
	return instance;
    }

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	if (currentState == null) {
	    menuService.saveCurrentState(
		    new CurrentState(ussdRequest.getSessionId(), 1, true, LocationType.MENU_PRINCIPAL.getCode(), ussdRequest.getPhoneNumber(), LocalDateTime.now()));
	}
	return "CON Bem Vindo ao CMMV \n \n  1. Entrar \n  2. Registar-se";
    }

    @Override
    public String recoverSession(CurrentState currentState, MenuService menuService) {
	// TODO Auto-generated method stub
	return null;
    }

}
