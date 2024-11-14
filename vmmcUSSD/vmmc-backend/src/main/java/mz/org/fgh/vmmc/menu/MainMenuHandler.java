package mz.org.fgh.vmmc.menu;

import java.time.LocalDateTime;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class MainMenuHandler implements MenuHandler {

	private static MainMenuHandler instance = new MainMenuHandler();

	private MainMenuHandler() {
	}

	public static MainMenuHandler getInstance() {
		return instance;
	}

	@Override
	public UssdOut handleMenu(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) {
		if (currentState !=null) {
			//actualiza 
			currentState.setActive(false);
			menuService.saveCurrentState(currentState);
			
			currentState = new CurrentState(ussdIn.getSession(), 1, true, LocationType.MENU_PRINCIPAL.getCode(),
					ussdIn.getFrom(), LocalDateTime.now());
			long stateId = menuService.saveCurrentState(currentState);
			currentState.setId(stateId);
			
		} else {
			currentState = new CurrentState(ussdIn.getSession(), 1, true, LocationType.MENU_PRINCIPAL.getCode(),
					ussdIn.getFrom(), LocalDateTime.now());
			long stateId = menuService.saveCurrentState(currentState);
			currentState.setId(stateId);
		}
	 
		Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdIn.getSession(), true); 
		return getNextMenuText(ussdIn, currentState, currentMenu, menuService);

	}

	@Override
	public UssdOut recoverSession(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService, OperationMetadataService serviceMeta) {
		Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);
		if (currentState != null) {
			currentState.setActive(false);
			currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
			currentState.setSessionId(ussdIn.getSession());
			menuService.saveCurrentState(currentState);
		}
		return getNextMenuText(ussdIn, currentState, currentMenu, menuService);
		// return ConstantUtils.MENU_PRINCIPAL_DESCRIPTION;
	}

	private UssdOut getNextMenuText(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			MenuService menuService) {
		// Passa para o proximo menu se a opcao != 0
		// se tiver apenas a opcao (0. Voltar) e o utilizador introduzir o nome por
		// exemplo, passa para o proximo menu
		currentState.setIdMenu(currentMenu.getNextMenuId());
		menuService.saveCurrentState(currentState);
		// pega o proximo menu
		Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
		UssdOut out = new UssdOut(ussdIn);
		out.setAction(nextMenu.getAction());
		out.setContent(MessageUtils.getMenuText(nextMenu));
		return out;
	}
}
