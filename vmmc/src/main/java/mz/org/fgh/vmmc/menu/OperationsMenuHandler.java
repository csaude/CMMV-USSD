package mz.org.fgh.vmmc.menu;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.AppointmentResponse;
import mz.org.fgh.vmmc.inout.AppointmentSearchResponse;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.DateUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class OperationsMenuHandler implements MenuHandler {

       Logger LOG = Logger.getLogger(OperationsMenuHandler.class);
       private Map<String, Clinic> mapClinics;
       private List<Clinic> clinicsList = new ArrayList<Clinic>();
       private AppointmentRequest appointmentRequest;
       private int lastIndex;
       private int startIndex;
       private final int pagingSize = 4;
       private static OperationsMenuHandler instance = new OperationsMenuHandler();

       private OperationsMenuHandler() {
       }

       public static OperationsMenuHandler getInstance() {
	     return instance;
       }

       @Override
       public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, HttpSession session) throws Throwable {

	     Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);

	     if (currentMenu != null) {
		   // Invoca o servico de consulta
		   if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE) && ussdRequest.getText().equalsIgnoreCase("1")) {

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

		   } // Se nao for paginacao/ opcao para voltar
		   else if (!ussdRequest.getText().equals("0") && !ussdRequest.getText().equals("#")) {

			 if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_AUTHENTICATION_CODE)) {
			        return autenticate(currentMenu, ussdRequest, currentState, menuService, sessionDataService);
			 } else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE)) {
			        // Seta o ID da clinica correspondente a opcao escolhida
			        if (mapClinics.containsKey(ussdRequest.getText())) {
				      ussdRequest.setText(mapClinics.get(ussdRequest.getText()).getId() + "");
			        } else {
				      currentState.setIdMenu(1);
				      currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
				      return ConstantUtils.MESSAGE_OPCAO_INVALIDA_TERMINAR;
			        }
			 } else if (!MessageUtils.isValidOption(currentMenu, ussdRequest.getText())) {
			        // Valida as opcoes para os menus com opcoes predifinidas na Base
			        return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, MessageUtils.getMenuText(currentMenu));
			 }
			 // Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
			 if (StringUtils.isNotBlank(currentMenu.getMenuField())) {
			        OperationMetadata metadata = new OperationMetadata(ussdRequest.getSessionId(), ussdRequest.getPhoneNumber(), currentState.getLocation(),
					    currentMenu, currentMenu.getMenuField(), ussdRequest.getText());
			        operationMetadataService.saveOperationMetadata(metadata);
			 }

		   }

		   return navegate(currentMenu, ussdRequest, currentState, menuService, operationMetadataService, sessionDataService);

	     } else {

		   return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService, operationMetadataService, sessionDataService, session);

	     }

       }

       @Override
       public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {
	     return MessageUtils.getMenuText(menuService.findMenuById(currentState.getIdMenu()));
       }

       // ==============================PRIVATE
       // BEHAVIOUR======================================

       private String navegate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService) {

	     // UtenteSearchResponse utente = (UtenteSearchResponse)
	     // httpSession.getAttribute("utenteSession");

	     // Passa para o proximo menu, associado a opcao
	     //
	     // se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
	     // tiver apenas a opcao (0. Voltar), pega a opcao
	     // introduzida pelo user para saber o proximo menu;
	     if ((currentMenu.getMenuItems().size() > 1) || (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(request.getText()).equals("0"))) {
		   // caso particular, se for distrito passa para a proxima tela
		   if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE) && !request.getText().equalsIgnoreCase("#")
			        && !request.getText().equalsIgnoreCase("0")) {
			 Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
			 currentState.setIdMenu(nextMenu.getId());
			 menuService.saveCurrentState(currentState);
			 return MessageUtils.getMenuText(nextMenu);
		   }
		   // pega o menu pelo opcao introduzida
		   Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), request.getText());
		   currentState.setIdMenu(menu.get().getNextMenuId());
		   menuService.saveCurrentState(currentState);
		   Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());

		   if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_CODE) || nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_PATIENT_CODE)) {
			 long districtId = Long.parseLong(sessionDataService.findBySessionIdAndAttrName(request.getSessionId(), "districtId").getAttrValue());// utente.getAddresses().get(0).getDistrict().getId();
			 return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getClinicsByDistrictId(districtId, request));
		   } else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE)) {
			 currentState.setIdMenu(1);
			 menuService.saveCurrentState(currentState);
			 AppointmentSearchResponse app = RestClient.getInstance()
				      .getAppointmentByUtenteId(sessionDataService.findBySessionIdAndAttrName(request.getSessionId(), "utenteId").getAttrValue());
			 if (app != null) {

			        String details = "\n Data: " + DateUtils.getSimpleDateFormat(app.getAppointmentDate()) + "\n Hora:" + app.getTime() + "\n US: "
					    + app.getClinic().getName();
			        return MessageFormat.format(MessageUtils.getMenuText(nextMenu), details);

			 } else {
			        return ConstantUtils.MESSAGE_APPOINTMENT_NOT_FOUND;
			 }

		   }

		   return MessageUtils.getMenuText(nextMenu);

	     } else {

		   Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
		   if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE)) {
			 // apresenta dados na tela de confirmacao
			 currentState.setIdMenu(nextMenu.getId());
			 menuService.saveCurrentState(currentState);
			 appointmentRequest = operationMetadataService.createAppointmentRequestByMetadatas(request, currentState.getLocation());
			 appointmentRequest.setClinic(Long.parseLong(sessionDataService.findBySessionIdAndAttrName(request.getSessionId(), "clinicId").getAttrValue()));
			 appointmentRequest.setUtente(Long.parseLong(sessionDataService.findBySessionIdAndAttrName(request.getSessionId(), "utenteId").getAttrValue()));
			 String appointmentDetails = operationMetadataService.getAppointmentConfirmationData(appointmentRequest);
			 return MessageFormat.format(MessageUtils.getMenuText(nextMenu), appointmentDetails);

		   } else {
			 return getNextMenuText(currentState, currentMenu, menuService);
		   }

	     }

       }

       private String autenticate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService, SessionDataService sessionDataService) {
	     // Invocar o servico de autenticacao
	     UtenteSearchResponse response = RestClient.getInstance().getUtenteBySystemNumber(request.getText());
	     String phoneNumber = StringUtils.remove(request.getPhoneNumber(), "+258");
	     if (response.getStatusCode() == 200 && response.getCellNumber().equalsIgnoreCase(phoneNumber)) {
		   currentState.setIdMenu(currentMenu.getNextMenuId());
		   menuService.saveCurrentState(currentState);
		   sessionDataService.saveSessionData(response, request.getSessionId());
		   // session.setAttribute("utenteSession", response);

		   return getNextMenuText(currentState, currentMenu, menuService);
	     } else {
		   return MessageFormat.format(ConstantUtils.MESSAGE_LOGIN_FAILED, MessageUtils.getMenuText(currentMenu));
	     }
       }

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

       // Devolve a lista de clinicas, sobre uma paginacao definida
       private String getClinicsByDistrictId(long districtId, UssdRequest ussdRequest) {

	     if (ussdRequest == null || !ussdRequest.getText().equalsIgnoreCase("#")) {
		   clinicsList = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics();
		   String menuText = getClinicsMenu(clinicsList.subList(0, pagingSize));
		   startIndex = lastIndex;
		   lastIndex = pagingSize;
		   return menuText;

	     } else {
		   if (lastIndex > clinicsList.size()) {
			 return getClinicsMenu(clinicsList.subList(startIndex, clinicsList.size()));
		   }
		   String menuText = getClinicsMenu(clinicsList.subList(startIndex, lastIndex));
		   startIndex = lastIndex;
		   lastIndex = startIndex + pagingSize;
		   return menuText;
	     }
       }

       private String getClinicsMenu(List<Clinic> list) {
	     mapClinics = new HashMap<String, Clinic>();
	     String menuClinics = StringUtils.EMPTY;
	     int key = 1;
	     for (Clinic item : list) {
		   menuClinics += key + ". " + item.getName() + "\n";
		   System.out.println(menuClinics);
		   mapClinics.put(String.valueOf(key), item);
		   key++;
	     }

	     return menuClinics;
       }

}
