package mz.org.fgh.vmmc.menu;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;

public class LoginMenuHandler implements MenuHandler {

    private static LoginMenuHandler instance = new LoginMenuHandler();

    private LoginMenuHandler() {
    }

    public static LoginMenuHandler getInstance() {
	return instance;
    }

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {
	// TODO Auto-generated method stub
		return null;
    }

    @Override
    public String recoverSession(CurrentState currentState, MenuService menuService) {
	// TODO Auto-generated method stub
	return null;
    }

}
