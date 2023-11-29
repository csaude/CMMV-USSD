package mz.org.fgh.vmmc.utils;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;

public class MenuUtils {

       public static void resetSession(CurrentState currentState, MenuService menuService) {
	     currentState.setIdMenu(1);
	     currentState.setActive(false);
	     currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
	     menuService.saveCurrentState(currentState);
       }

}
