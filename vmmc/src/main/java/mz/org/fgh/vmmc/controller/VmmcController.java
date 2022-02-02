package mz.org.fgh.vmmc.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.menu.MenuHandler;
import mz.org.fgh.vmmc.menu.MenuHandlerType;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.utils.MessageUtils;

@RestController
public class VmmcController {

    Logger logger = Logger.getLogger(VmmcController.class.getName());
    private static HashMap<String, MenuHandler> menuTypes = MenuHandlerType.getMenuHandlerTypes();

    @Autowired
    MenuService menuService;

    @Autowired
    OperationMetadataService operationMetadataService;

    @PostMapping(path = "vmmcUssd")
    public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode, @RequestParam String phoneNumber, @RequestParam String text) throws IOException {

	UssdRequest ussdRequest = new UssdRequest(sessionId, serviceCode, phoneNumber, MessageUtils.formatInputText(text));
	long sessionRecoverTime = Long.parseLong(System.getProperty("sessionRecoverTime", "30"));

	CurrentState currentState = menuService.findCurrentStateByPhoneNumber(phoneNumber);

	if (currentState != null) {

	    long seconds = Duration.between(LocalDateTime.now(), currentState.getCreatedDate()).getSeconds();

	    // Recuperacao da sessão
	    if (seconds <= sessionRecoverTime && !currentState.getLocation().equalsIgnoreCase(LocationType.MENU_PRINCIPAL.getCode())
		    && StringUtils.isBlank(ussdRequest.getText())) {
		currentState.setSessionId(sessionId);
		menuService.saveCurrentState(currentState);
		operationMetadataService.updateOperationMetadataSessionId(currentState.getSessionId(), phoneNumber);
		return menuTypes.get(currentState.getLocation()).recoverSession(currentState, menuService);

	    } else if (currentState.getIdMenu() == 1) {

		switch (text) {
		case "0":
		    currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		    break;
		case "1":
		    currentState.setLocation(LocationType.MENU_LOGIN.getCode());
		    break;
		case "2":
		    currentState.setLocation(LocationType.MENU_CADASTRO.getCode());
		    break;

		}
	    }
	    return menuTypes.get(currentState.getLocation()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService);

	} else {

	    return menuTypes.get(LocationType.MENU_PRINCIPAL.getCode()).handleMenu(ussdRequest, currentState, menuService, operationMetadataService);
	}

    }

}
