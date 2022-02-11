package mz.org.fgh.vmmc.menu;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.AppointmentResponse;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class LoginMenuHandler implements MenuHandler {

    Logger LOG = Logger.getLogger(LoginMenuHandler.class);
    private Map<String, Clinic> mapClinics;
    private static AppointmentRequest appointmentRequest;
    private static LoginMenuHandler instance = new LoginMenuHandler();

    private LoginMenuHandler() {
    }

    public static LoginMenuHandler getInstance() {
	return instance;
    }

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) throws Throwable {

	Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdRequest.getSessionId(), true);
	if (currentMenu != null) {
	    // Invoca o servico de consulta
	    if (currentMenu.getMenuField().equalsIgnoreCase("isConfirmed") && ussdRequest.getText().equalsIgnoreCase("1")) {
		appointmentRequest = operationMetadataService.createAppointmentRequestByMetadatas(ussdRequest, currentState.getLocation());
		AppointmentResponse response = RestClient.getInstance().makeAppointment(appointmentRequest);
		if ((response.getStatusCode() != 200 && response.getStatusCode() != 201)) {
		    currentState.setIdMenu(1);
		    currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		    menuService.saveCurrentState(currentState);
		    return ConstantUtils.MESSAGE_APPOINTMENT_FAILED;
		} else {
		    currentState.setActive(false);
		    menuService.saveCurrentState(currentState);
		}

	    }

	    if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {

		// Seta o ID da clinica correspondente a opcao escolhida
		if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE)) {
		    if (mapClinics.containsKey(ussdRequest.getText())) {
			ussdRequest.setText(mapClinics.get(ussdRequest.getText()).getId() + "");
		    } else {
			// Erro introduziu uma opcao que nao existe TODO:
			return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		    }
		}
		// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
		OperationMetadata metadata = new OperationMetadata(ussdRequest.getSessionId(), ussdRequest.getPhoneNumber(), currentState.getLocation(), currentMenu,
			currentMenu.getMenuField(), ussdRequest.getText());
		operationMetadataService.saveOperationMetadata(metadata);

	    }

	    String menu = navegate(currentMenu, ussdRequest, currentState, menuService, operationMetadataService);
	    return menu;

	} else {

	    return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService, operationMetadataService);

	}

    }

    // ==============================PRIVATE
    // BEHAVIOUR======================================

    private String navegate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	// Passa para o proximo menu, associado a opcao
	//
	// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
	// tiver apenas a opcao (0. Voltar), pega a opcao
	// introduzida pelo user para saber o proximo menu;
	if (currentMenu.getMenuItems().size() > 1 || (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(request.getText()).equals("0"))) {

	    // pega o menu pelo opcao introduzida
	    Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), request.getText());
	    currentState.setIdMenu(menu.get().getNextMenuId());
	    menuService.saveCurrentState(currentState);
	    Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());

	    if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE) || nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_PATIENT_CODE)) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getClinicsByDistrictId("1"));
	    }

	    return MessageUtils.getMenuText(nextMenu);

	} else {

	    Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
	    if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_AUTHENTICATION_CODE)) {
		// Invocar o servico de autenticacao
		if (request.getText().equalsIgnoreCase("123")) {
		    currentState.setIdMenu(currentMenu.getNextMenuId());
		    menuService.saveCurrentState(currentState);
		    return getNextMenuText(currentState, currentMenu, menuService);
		} else {
		    return MessageFormat.format(ConstantUtils.MESSAGE_LOGIN_FAILED, MessageUtils.getMenuText(currentMenu));
		}
	    } else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_ENROLLMENT_CONFIRMATION_CODE)) {

		appointmentRequest = operationMetadataService.createAppointmentRequestByMetadatas(request, currentState.getLocation());
		String appointmentDetails = operationMetadataService.getAppointmentConfirmationData(appointmentRequest);
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), appointmentDetails);

	    } else {
		return getNextMenuText(currentState, currentMenu, menuService);
	    }

	}

    }

    @Override
    public String recoverSession(CurrentState currentState, MenuService menuService) {
	Menu menu = menuService.findMenuById(currentState.getIdMenu());

	if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE)) {

	    return MessageFormat.format(MessageUtils.getMenuText(menu), getClinicsByDistrictId("1"));
	}
	return MessageUtils.getMenuText(menu);
    }

    // ==================================PRIVATE
    // BEHAVIOUR=============================

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

    private String getClinicsByDistrictId(String districtId) {
	String clinics = StringUtils.EMPTY;
	mapClinics = new HashMap<String, Clinic>();
	int key = 1;

	try {
	    List<Clinic> clinicsList = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics();
	    LOG.error(clinicsList.toString());
	    for (Clinic item : clinicsList) {
		clinics += key + ". " + item.getName() + "\n";
		mapClinics.put(String.valueOf(key), item);
		key++;
	    }
	} catch (Throwable e) {
	    LOG.error("ocorreu erro ao listar provincias: " + e);
	}
	return clinics;
    }

}
