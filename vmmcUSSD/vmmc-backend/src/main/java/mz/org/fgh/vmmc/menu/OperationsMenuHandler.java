package mz.org.fgh.vmmc.menu;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.AppointmentResponse;
import mz.org.fgh.vmmc.inout.AppointmentSearchResponse;
import mz.org.fgh.vmmc.inout.PayloadSms;
import mz.org.fgh.vmmc.inout.RecipientSms;
import mz.org.fgh.vmmc.inout.SendSmsRequest;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.InfoMessage;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.DateUtils;
import mz.org.fgh.vmmc.utils.MenuUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

public class OperationsMenuHandler implements MenuHandler {

	Logger LOG = Logger.getLogger(OperationsMenuHandler.class);
	private Map<String, Clinic> mapClinics;
	private List<Clinic> clinicsList = new ArrayList<Clinic>();
	private AppointmentRequest appointmentRequest;
	private int lastIndex;
	private int startIndex;
	private final int pagingSize = 3;
	private static OperationsMenuHandler instance = new OperationsMenuHandler();

	private OperationsMenuHandler() {
	}

	public static OperationsMenuHandler getInstance() {
		return instance;
	}

	@Override
	public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService) throws Throwable {

		Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);

		if (currentMenu != null) {
			// Invoca o servico de consulta
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE)) {

				if (ussdRequest.getText().equalsIgnoreCase("1")) {
					AppointmentResponse response = RestClient.getInstance().makeAppointment(appointmentRequest);
					if ((response.getStatusCode() != 200 && response.getStatusCode() != 201)) {
						currentState.setIdMenu(1);
						currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
						menuService.saveCurrentState(currentState);
						return ConstantUtils.MESSAGE_APPOINTMENT_FAILED;
					} else {
						currentState.setActive(false);
						menuService.saveCurrentState(currentState);
						return ConstantUtils.MESSAGE_APPOINTMENT_SUCCESS;
					}
				} else if (ussdRequest.getText().equalsIgnoreCase("2")) {

					MenuUtils.resetSession(currentState, menuService);
					return ConstantUtils.MESSAGE_APPOINTMENT_NOT_CONFIRMED;

				}

			}

			else if (!ussdRequest.getText().equals("0") && !ussdRequest.getText().equals("#")) {

				if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_AUTHENTICATION_CODE)) {
					return autenticate(currentMenu, ussdRequest, currentState, menuService, sessionDataService);
				} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE.equalsIgnoreCase(currentMenu.getCode())
						|| ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(currentMenu.getCode())) {

					if (mapClinics.containsKey(ussdRequest.getText())) {
						// Seta o ID da clinica correspondente a opcao escolhida
						Clinic clinica = mapClinics.get(ussdRequest.getText());
						sessionDataService.saveClinicOnSessionData(clinica, currentState.getId());
						ussdRequest.setText(clinica.getId() + "");
						lastIndex = pagingSize;
						startIndex = 0;

					} else {
						return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils.remove(
								getClinicsByDistrictMenu(ussdRequest, currentState, sessionDataService, currentMenu),
								"CON "));
					}
				} else if (ussdRequest.getText().equalsIgnoreCase("1")
						&& ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE
								.equalsIgnoreCase(currentMenu.getCode())) {
					// TODO: Enviar sms com a lista de clinicas
					long districtId = Long.parseLong(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
					List<Clinic> clinics = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics()
							.stream().sorted(Comparator.comparing(Clinic::getName)).collect(Collectors.toList());

					currentState.setActive(false);
					menuService.saveCurrentState(currentState);
					RecipientSms[] recipients = new RecipientSms[1];
					recipients[0] = new RecipientSms(ConstantUtils.TYPE_RECIEPIENT_MOBILE,
							ussdRequest.getPhoneNumber());
					PayloadSms payload = new PayloadSms(getClinicsForSms(clinics), recipients);
					SendSmsRequest smsRequest = new SendSmsRequest();
					smsRequest.setPayload(payload);
					RestClient.getInstance().sendSms(smsRequest);

					return ConstantUtils.MESSAGE_SEND_SMS_CLINIC_LIST;

				} else if (ConstantUtils.MENU_APPOINTMENT_MONTH.equalsIgnoreCase(currentMenu.getCode())) {
					int day = Integer.parseInt(operationMetadataService
							.getMetadatasByOperationTypeAndSessionId(currentState.getId(), currentState.getLocation())
							.get("appointmentDay").getAttrValue());
					int month = StringUtils.isNotBlank(ussdRequest.getText()) ? Integer.parseInt(ussdRequest.getText())
							: Integer
									.parseInt(
											operationMetadataService
													.getMetadatasByOperationTypeAndSessionId(currentState.getId(),
															currentState.getLocation())
													.get("appointmentMonth").getAttrValue());
					if (!DateUtils.isValidDate(day, month)) {
						return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu),
										DateUtils.getAppointmentsMonth()), "CON"));
					}

				} else if (ConstantUtils.MENU_APPOINTMENT_DAY.equalsIgnoreCase(currentMenu.getCode())) {
					if (!DateUtils.isValidDay(ussdRequest.getText())) {
						return MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DAY_INVALID,
								StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
					}
				}

				else if (!MessageUtils.isValidOption(currentMenu, ussdRequest.getText())) {
					// Valida as opcoes para os menus com opcoes predifinidas na Base

					return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
							StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
				}

				// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
				if (StringUtils.isNotBlank(currentMenu.getMenuField())) {
					OperationMetadata metadata = new OperationMetadata(currentState, currentState.getSessionId(),
							currentState.getLocation(), currentMenu, currentMenu.getMenuField(), ussdRequest.getText());

					operationMetadataService.saveOperationMetadata(metadata);
				}

			}

			return navegate(currentMenu, ussdRequest, currentState, menuService, operationMetadataService,
					sessionDataService, infoMessageService);

		} else {

			return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService);

		}

	}

	@Override
	public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService) {
		currentState.setSessionId(request.getSessionId());
		menuService.saveCurrentState(currentState);
		Menu menu = menuService.findMenuById(currentState.getIdMenu());
		if (ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(menu.getCode())
				|| ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE.equalsIgnoreCase(menu.getCode())) {
			return getClinicsByDistrictMenu(request, currentState, sessionDataService, menu);
		} else if (ConstantUtils.MENU_APPOINTMENT_MONTH.equalsIgnoreCase(menu.getCode())) {

			return MessageFormat.format(MessageUtils.getMenuText(menu), DateUtils.getAppointmentsMonth());

		} else if (ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE.equalsIgnoreCase(menu.getCode())) {
			return getAppointmentDetails(currentState, menuService, sessionDataService, menu);
		}
		return MessageUtils.getMenuText(menu);

	}

	private String getClinicsByDistrictMenu(UssdRequest request, CurrentState currentState,
			SessionDataService sessionDataService, Menu menu) {
		long districtId = Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
		return MessageFormat.format(MessageUtils.getMenuText(menu), getClinicsByDistrictId(districtId, request));
	}

	// ==============================PRIVATE
	// BEHAVIOUR======================================

	// Responsavel por passar para o proximo menu
	private String navegate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService) {

		// UtenteSearchResponse utente = (UtenteSearchResponse)
		// httpSession.getAttribute("utenteSession");

		// Passa para o proximo menu, associado a opcao
		//
		// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
		// tiver apenas a opcao (0. Voltar), pega a opcao
		// introduzida pelo user para saber o proximo menu;
		if ((currentMenu.getMenuItems().size() > 1)
				|| (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(request.getText()).equals("0"))) {
			// caso particular, se for clinica passa para a proxima tela
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE)
					&& !request.getText().equalsIgnoreCase("#") && !request.getText().equalsIgnoreCase("0")) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);
				return MessageUtils.getMenuText(nextMenu);
			}

			// pega o menu pelo opcao introduzida
			Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), request.getText());
			if (!menu.isPresent()) {
				return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
						StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
			}
			currentState.setIdMenu(menu.get().getNextMenuId());
			menuService.saveCurrentState(currentState);
			Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());

			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_CODE)) {
				return getClinicsByDistrictMenu(request, currentState, sessionDataService, nextMenu);
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE)) {

				return getAppointmentDetails(currentState, menuService, sessionDataService, nextMenu);

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_MONTH)) {

				return MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth());

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE)) {

				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), request.getPhoneNumber()));
			} else if (ConstantUtils.MENU_INFORMATIVE_MESSAGES.equalsIgnoreCase(currentMenu.getCode())) {

				List<InfoMessage> listMessage = infoMessageService.findMessagesByCode(menu.get().getCode());
				for (InfoMessage message : listMessage) {
					RecipientSms[] recipients = new RecipientSms[1];
					recipients[0] = new RecipientSms(ConstantUtils.TYPE_RECIEPIENT_MOBILE, request.getPhoneNumber());
					PayloadSms payload = new PayloadSms(message.getDescription(), recipients);
					SendSmsRequest smsRequest = new SendSmsRequest();
					smsRequest.setPayload(payload);
					RestClient.getInstance().sendSms(smsRequest);
				}
				MenuUtils.resetSession(currentState, menuService);
				return MessageFormat.format(ConstantUtils.MESSAGE_INFORMATIVE_MESSAGES, request.getPhoneNumber());
			}

			return MessageUtils.getMenuText(nextMenu);

		} else {

			Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE)) {
				// apresenta dados na tela de confirmacao
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);
				appointmentRequest = operationMetadataService.createAppointmentRequestByMetadatas(request,
						currentState.getLocation(), currentState);
				appointmentRequest.setClinic(Long.parseLong(sessionDataService
						.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicId").getAttrValue()));
				appointmentRequest.setUtente(Long.parseLong(sessionDataService
						.findByCurrentStateIdAndAttrName(currentState.getId(), "utenteId").getAttrValue()));
				appointmentRequest.setClinicName(sessionDataService
						.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicName").getAttrValue());

				String appointmentDetails = operationMetadataService.getAppointmentConfirmationData(appointmentRequest);
				return MessageFormat.format(MessageUtils.getMenuText(nextMenu), appointmentDetails);

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_MONTH)) {

				return MessageFormat.format(getNextMenuText(currentState, currentMenu, menuService),
						DateUtils.getAppointmentsMonth());

			}

			return getNextMenuText(currentState, currentMenu, menuService);

		}

	}

	private String getAppointmentDetails(CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService, Menu nextMenu) {

		String utenteId = sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "utenteId")
				.getAttrValue();
		AppointmentSearchResponse app = RestClient.getInstance().getAppointmentByUtenteId(utenteId);
		if (app.getStatusCode() == 200) {

			String details = MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DETAILS,
					DateUtils.getSimpleDateFormat(app.getAppointmentDate()), app.getClinic().getName(),
					app.getStatus());
			return MessageFormat.format(MessageUtils.getMenuText(nextMenu), details);

		} else if (app.getStatusCode() == 404) {
			MenuUtils.resetSession(currentState, menuService);
			return ConstantUtils.MESSAGE_APPOINTMENT_NOT_FOUND;
		} else {
			MenuUtils.resetSession(currentState, menuService);
			return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		}
	}

	private String autenticate(Menu currentMenu, UssdRequest request, CurrentState currentState,
			MenuService menuService, SessionDataService sessionDataService) {
		// Invocar o servico de autenticacao
		UtenteSearchResponse response = RestClient.getInstance().getUtenteBySystemNumber(request.getText());
		String phoneNumber = StringUtils.remove(request.getPhoneNumber(), "+258");
		if (response.getStatusCode() == 200 && phoneNumber.equalsIgnoreCase(response.getCellNumber())) {
			currentState.setIdMenu(currentMenu.getNextMenuId());
			menuService.saveCurrentState(currentState);
			sessionDataService.saveSessionData(response, currentState.getId());
			// session.setAttribute("utenteSession", response);
			return getNextMenuText(currentState, currentMenu, menuService);
		} else {
			return MessageFormat.format(ConstantUtils.MESSAGE_LOGIN_FAILED,
					StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
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

			clinicsList = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics().stream()
					.sorted(Comparator.comparing(Clinic::getName)).collect(Collectors.toList());
			int key = 1;
			for (Clinic dis : clinicsList) {
				dis.setOption(key + "");
				key++;
			}
			String menuText = getClinicsMenu(
					clinicsList.subList(0, pagingSize > clinicsList.size() ? clinicsList.size() : pagingSize));
			startIndex = pagingSize;
			lastIndex = startIndex + pagingSize;
			return menuText;

		} else {
			if (lastIndex > clinicsList.size()) {
				return getClinicsMenu(clinicsList.subList(startIndex, clinicsList.size()));
			}
			String menuText = getClinicsMenu(clinicsList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = lastIndex + pagingSize;
			return menuText;
		}
	}

	private String getClinicsMenu(List<Clinic> list) {
		mapClinics = new HashMap<String, Clinic>();
		String menuClinics = StringUtils.EMPTY;

		for (Clinic item : list) {
			menuClinics += item.getOption() + ". " + item.getName() + "\n";
			mapClinics.put(String.valueOf(item.getOption()), item);
		}

		return menuClinics;
	}

	private String getClinicsForSms(List<Clinic> list) {
		String menuClinics = StringUtils.EMPTY;
		int i = 1;
		for (Clinic item : list) {
			menuClinics += i + "." + item.getName() + ",";
			i++;
		}

		return menuClinics;
	}
}
