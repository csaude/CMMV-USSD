package mz.org.fgh.vmmc.menu;

import java.util.HashMap;

import mz.org.fgh.vmmc.commons.LocationType;

/**
 * para gerir as instancias de cada um dos fluxos de menus
 * 
 * @author erciliofrancisco
 *
 */
public class MenuHandlerType {
    private static final HashMap<String, MenuHandler> menuHandlerTypes = new HashMap<>();
    static {
	menuHandlerTypes.put(LocationType.MENU_PRINCIPAL.getCode(), MainMenuHandler.getInstance());
	menuHandlerTypes.put(LocationType.MENU_CADASTRO.getCode(), RegisterMenuHandler.getInstance());
	menuHandlerTypes.put(LocationType.MENU_LOGIN.getCode(), LoginMenuHandler.getInstance());
    }

    public static HashMap<String, MenuHandler> getMenuHandlerTypes() {
	return menuHandlerTypes;
    }
}
