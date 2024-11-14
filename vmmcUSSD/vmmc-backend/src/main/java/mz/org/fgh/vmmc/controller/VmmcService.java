package mz.org.fgh.vmmc.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.menu.MenuHandler;
import mz.org.fgh.vmmc.menu.MenuHandlerType;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;
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
	SmsConfigurationService smsConfigurationService;

	public UssdOut invokeVmmcUssdService(String sessionId, String transactionId, String from, String to, String content,
			String action) throws Throwable {
		long sessionRecoverTime = getSessionRecoverTime();
		OffsetDateTime now = OffsetDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		String formattedDate = now.format(formatter);

		CurrentState currentState = menuService.findCurrentStateByPhoneNumber(from, true);
		UssdIn ussdIn = new UssdIn(MessageUtils.formatInputText(content), from, to, sessionId, transactionId, action,
				formattedDate);

		UssdOut out = new UssdOut();
		if (ussdIn.getAction().equalsIgnoreCase("false") || currentState == null) {

			out = menuTypes.get(LocationType.MENU_PRINCIPAL.getCode()).handleMenu(ussdIn, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService, smsConfigurationService);

			return out;
		}

		long seconds = Duration.between(currentState.getCreatedDate(), LocalDateTime.now()).getSeconds();
		if (isSessionExpired(ussdIn, sessionRecoverTime, currentState, seconds)) {
			out = handleExpiredSession(ussdIn, currentState, operationMetadataService);
		} else {
			out = handleCurrentState(ussdIn, currentState);
		}

		return out;
	}

	private UssdOut handleCurrentState(UssdIn ussdIn, CurrentState currentState) throws Throwable {
		UssdOut out = new UssdOut(ussdIn);
		if (currentState.getLocation().equalsIgnoreCase(LocationType.MENU_PRINCIPAL.getCode())) { 
			switch (ussdIn.getContent()) {
			case "1":
			case "3":
			case "4":
				currentState.setLocation(LocationType.MENU_OPERACOES.getCode());
				break;
			case "2":
				currentState.setLocation(LocationType.MENU_CADASTRO.getCode());
				break;
			case "":
				currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
				break;
			default:
				out.setContent(ConstantUtils.MESSAGE_OPCAO_INVALIDA_TERMINAR);
				out.setAction("End");
				return out;
			}
			return menuTypes.get(currentState.getLocation()).handleMenu(ussdIn, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService, smsConfigurationService);
		} else {

			return menuTypes.get(currentState.getLocation()).handleMenu(ussdIn, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService, smsConfigurationService);
		}
	}

	private UssdOut handleExpiredSession(UssdIn ussdIn, CurrentState currentState,
			OperationMetadataService operationMetadataService) throws Throwable {
		UssdOut out = new UssdOut(ussdIn);

		switch (ussdIn.getContent()) {
		case "":
			out.setContent(ConstantUtils.MENU_SESSION_RECOVER_DESCRIPTION);
			out.setAction("request");
			return out;
		case "1":
			return menuTypes.get(currentState.getLocation()).recoverSession(ussdIn, currentState, menuService,
					sessionDataService, operationMetadataService);
		case "2":
			return menuTypes.get(LocationType.MENU_PRINCIPAL.getCode()).handleMenu(ussdIn, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService, smsConfigurationService);
		default:

			out.setContent(ConstantUtils.MESSAGE_OPCAO_INVALIDA_TERMINAR);
			out.setAction("end");
			return out;
		}
	}

	private long getSessionRecoverTime() {
		return Long.parseLong(System.getProperty("sessionRecoverTime", "30"));
	}

	private boolean isSessionExpired(UssdIn ussdIn, long sessionRecoverTime, CurrentState currentState, long seconds) {
		return !currentState.getSessionId().equalsIgnoreCase(ussdIn.getSession()) && seconds > sessionRecoverTime
				&& !currentState.getLocation().equalsIgnoreCase(LocationType.MENU_PRINCIPAL.getCode());
	}

}
