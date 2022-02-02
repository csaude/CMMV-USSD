package mz.org.fgh.vmmc.menu;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class LoginMenuHandler implements MenuHandler {

    private static LoginMenuHandler instance = new LoginMenuHandler();

    private LoginMenuHandler() {
    }

    public static LoginMenuHandler getInstance() {
	return instance;
    }

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdRequest.getSessionId());
	if (currentMenu != null) {

	    if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {
		// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
		OperationMetadata metadata = new OperationMetadata(ussdRequest.getSessionId(), ussdRequest.getPhoneNumber(), currentState.getLocation(), currentMenu,
			currentMenu.getMenuField(), ussdRequest.getText());
		operationMetadataService.saveOperationMetadata(metadata);

	    }

	    return navegate(currentMenu, ussdRequest, currentState, menuService);

	} else {

	    return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService, operationMetadataService);

	}

    }

    // ==============================PRIVATE
    // BEHAVIOUR======================================

    private String navegate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService) {

	// Passa para o proximo menu, associado a opcao
	//
	// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
	// tiver apenas a opcao (0. Voltar), pega a opcao
	// introduzida pelo user para saber o proximo menu;
	if (currentMenu.getMenuItems().size() > 1 || (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(request.getText()).equals("0"))) {

	    // pega o menu pelo opcao introduzida
	    Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), request.getText());
	    currentState.setIdMenu(menu.get().getNextMenuId());
	    // actualiza o menu corrente
	    menuService.saveCurrentState(currentState);
	    // Pega o proximo menu
	    Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());
	    return MessageUtils.getMenuText(nextMenu);

	} else {
	    // Passa para o proximo menu se a opcao != 0
	    //
	    // se tiver apenas a opcao (0. Voltar) e o utilizador introduzir o nome por
	    // exemplo, passa para o proximo menu
	    currentState.setIdMenu(currentMenu.getNextMenuId());
	    menuService.saveCurrentState(currentState);
	    // pega o proximo menu
	    Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());

	    return MessageUtils.getMenuText(nextMenu);
	}

    }

    @Override
    public String recoverSession(CurrentState currentState, MenuService menuService) {
	// TODO Auto-generated method stub
	return null;
    }

}
