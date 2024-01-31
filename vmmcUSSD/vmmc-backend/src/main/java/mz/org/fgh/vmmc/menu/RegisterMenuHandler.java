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
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.inout.AppointmentRequest;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.service.FrontlineSmsConfigService;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.DateUtils;
import mz.org.fgh.vmmc.utils.MenuUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

@Service
public class RegisterMenuHandler implements MenuHandler {
	Logger LOG = Logger.getLogger(RegisterMenuHandler.class);

	private RegisterMenuHandler() {
	}

	public static RegisterMenuHandler getInstance() {
		return instance;
	}

	private List<District> districtList;
	private static RegisterMenuHandler instance = new RegisterMenuHandler();
	private int lastIndex;
	private int startIndex;
	private Map<String, Clinic> mapClinics;
	private List<Clinic> clinicsList = new ArrayList<Clinic>();
	private final int pagingSize = 4;
	private Map<String, Province> mapProvinces;
	private Map<String, District> mapDistricts;
	private List<Province> allProvinces;
	private UtenteRegisterRequest utenteRequest;
	private AppointmentRequest appointmentRequest;

	@Override
	public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService, FrontlineSmsConfigService frontlineSmsConfigService) {

		Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);
		if (currentMenu != null) {

			//
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {

				return handleRegisterConfirmation(ussdRequest, currentState, menuService);
			}

			if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {
				if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
					if (mapProvinces.containsKey(ussdRequest.getText())) {
						SessionData sd = new SessionData(currentState.getId(), "provinceId",
								mapProvinces.get(ussdRequest.getText()).getId() + "");
						sessionDataService.saveSessionData(sd);
						ussdRequest.setText(mapProvinces.get(ussdRequest.getText()).getId() + "");
					} else {
						return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(
										MessageFormat.format(MessageUtils.getMenuText(currentMenu), getProvincesMenu()),
										"CON "));

					}
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {

					int selectedProvinceId = Integer.parseInt(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());

					if (ussdRequest.getText().equalsIgnoreCase("#")) {

						return MessageFormat.format(MessageUtils.getMenuText(currentMenu),
								getDistrictsMenu(selectedProvinceId, allProvinces, ussdRequest));
					} else if (mapDistricts.containsKey(ussdRequest.getText())) {
						// ver AQUIIIII
						SessionData sd = new SessionData(currentState.getId(), "districtId",
								mapDistricts.get(ussdRequest.getText()).getId() + "");
						sessionDataService.saveSessionData(sd);
						ussdRequest.setText(mapDistricts.get(ussdRequest.getText()).getId() + "");
						lastIndex = pagingSize;
						startIndex = 0;

					} else {
						return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(
										MessageFormat.format(MessageUtils.getMenuText(currentMenu),
												getDistrictsMenu(selectedProvinceId, allProvinces, ussdRequest)),
										"CON "));

					}
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_AGE)) {
					if (!MessageUtils.isValidAge(ussdRequest.getText())) {
						return MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_AGE_INVALID,
								StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
					}

				}

				else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_NAME)
						|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_SURNAME)) {
					if (!MessageUtils.isValidStringField(ussdRequest.getText())) {
						return MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_VALUE_INVALID,
								StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));

					}

				} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE
						.equalsIgnoreCase(currentMenu.getCode())) {

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
				}
				if (ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH.equalsIgnoreCase(currentMenu.getCode())) {

					String month = ussdRequest.getText();
					if (!DateUtils.isValidMonth(month)) {
						return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu),
										DateUtils.getAppointmentsMonth()), "CON"));
					}

				} else if (ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_DAY.equalsIgnoreCase(currentMenu.getCode())) {

					String month = operationMetadataService
							.getMetadatasByOperationTypeAndSessionId(currentState.getId(), currentState.getLocation())
							.get("monthRegister").getAttrValue();

					if (!DateUtils.isValidDay(ussdRequest.getText(), month)) {
						return MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DAY_INVALID,
								StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
					}
				}

				// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
				OperationMetadata metadata = new OperationMetadata(currentState, currentState.getSessionId(),
						currentState.getLocation(), currentMenu, currentMenu.getMenuField(), ussdRequest.getText());

				operationMetadataService.saveOperationMetadata(metadata);

			}

			return navegate(currentMenu, ussdRequest, currentState, menuService, operationMetadataService,
					sessionDataService);

		} else {
			return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService,
					operationMetadataService, sessionDataService, infoMessageService, frontlineSmsConfigService);
		}

	}

	// ==============================PRIVATE
	// BEHAVIOUR======================================

	private String navegate(Menu currentMenu, UssdRequest request, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService) {

		// Passa para o proximo menu, associado a opcao
		//
		// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
		// tiver apenas a opcao (0. Voltar), pega a opcao
		// introduzida pelo user para saber o proximo menu;
		if (currentMenu.getMenuItems().size() > 1
				|| (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(request.getText()).equals("0"))) {

			// caso particular, se for clinica passa para a proxima tela
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)
					&& !request.getText().equalsIgnoreCase("#") && !request.getText().equalsIgnoreCase("0")) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);
				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {
					return MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth());
				}
				return MessageUtils.getMenuText(nextMenu);
			}

			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)
					&& !StringUtils.trim(request.getText()).equals("0")
					&& !StringUtils.trim(request.getText()).equals("#")) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);

				if (nextMenu.getCode()
						.equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)) {
					return getClinicsByDistrictMenu(request, currentState, sessionDataService, nextMenu);
				}
				return MessageUtils.getMenuText(nextMenu);

			}
			// pega o menu pelo opcao introduzida
			Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), request.getText());
			if (!menu.isPresent()) {
				return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
						StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
			}
			currentState.setIdMenu(menu.get().getNextMenuId());
			// actualiza o menu corrente
			menuService.saveCurrentState(currentState);
			// Pega o proximo menu
			Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());

			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
				return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
				return MessageFormat.format(MessageUtils.getMenuText(nextMenu),
						getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces, request));
			} else if (nextMenu.getCode()
					.equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)) {
				return getClinicsByDistrictMenu(request, currentState, sessionDataService, nextMenu);
			}
			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), request.getPhoneNumber()));
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {
				return MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth());
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {

				utenteRequest = operationMetadataService.createUtenteByMetadatas(request, currentState.getLocation(),
						currentState);
				String details = operationMetadataService.getRegisterConfirmationData(utenteRequest);
				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), details));

			}
			return MessageUtils.getMenuText(nextMenu);

		} else {

			if (MessageUtils.isValidInput(request.getText())) {
				// Passa para o proximo menu se a opcao != 0
				//
				// se tiver apenas a opcao (0. Voltar) e o utilizador introduzir o nome por
				// exemplo, passa para o proximo menu
				currentState.setIdMenu(currentMenu.getNextMenuId());
				menuService.saveCurrentState(currentState);
				// pega o proximo menu
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());

				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
					return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
				} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
					int selectedProvinceId = Integer.parseInt(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
					return MessageFormat.format(MessageUtils.getMenuText(nextMenu),
							getDistrictsMenu(selectedProvinceId, allProvinces, request));
				} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {
					// apresenta dados na tela de confirmacao
					currentState.setIdMenu(nextMenu.getId());
					menuService.saveCurrentState(currentState);

					utenteRequest = operationMetadataService.createUtenteByMetadatas(request,
							currentState.getLocation(), currentState);
					// String details =
					// operationMetadataService.getRegisterConfirmationData(utenteRequest);
					// nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(),
					// details));

					String appointmentDetails = operationMetadataService
							.getAppointmentConfirmationDataOnRegistration(utenteRequest, currentState);

					return MessageFormat.format(MessageUtils.getMenuText(nextMenu), appointmentDetails);

				}

				return MessageUtils.getMenuText(nextMenu);
			} else {
				return MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
						StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON "));
			}

		}

	}

	private String getProvincesMenu() {
		String provinces = StringUtils.EMPTY;
		mapProvinces = new HashMap<>();
		allProvinces = RestClient.getInstance().getAllProvinces();
		int key = 1;
		for (Province province : allProvinces) {
			provinces += key + ". " + province.getDescription() + "\n";
			mapProvinces.put(String.valueOf(key), province);
			key++;
		}
		return provinces;
	}

	private String getDistrictsMenu(int idProvince, List<Province> allProvinces, UssdRequest ussdRequest) {

		if (!ussdRequest.getText().equalsIgnoreCase("#") && !ussdRequest.getText().equalsIgnoreCase("0")) {
			startIndex = 0;
			lastIndex = pagingSize;
			districtList = new ArrayList<District>();
			districtList = allProvinces.stream().filter(p -> p.getId() == (idProvince)).findFirst().get().getDistricts()
					.stream().sorted(Comparator.comparing(District::getDescription)).collect(Collectors.toList());
			int key = 1;
			for (District dis : districtList) {
				dis.setOption(key + "");
				key++;
			}

			String menuText = getFormatedDistrictByList(districtList.subList(startIndex,
					pagingSize > districtList.size() ? districtList.size() : pagingSize));

			startIndex = lastIndex;
			lastIndex = startIndex + pagingSize;
			return menuText;

		} else {
			if (lastIndex > districtList.size()) {
				return getFormatedDistrictByList(districtList.subList(startIndex, districtList.size()));
			}
			String menuText = getFormatedDistrictByList(districtList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = startIndex + pagingSize;
			return menuText;
		}
	}

	private String getFormatedDistrictByList(List<District> list) {
		mapDistricts = new HashMap<String, District>();
		String menuDistricts = StringUtils.EMPTY;

		for (District item : list) {
			menuDistricts += item.getOption() + ". " + item.getDescription() + "\n";
			mapDistricts.put(String.valueOf(item.getOption()), item);
		}

		return menuDistricts;
	}

	private String getClinicsByDistrictMenu(UssdRequest request, CurrentState currentState,
			SessionDataService sessionDataService, Menu menu) {
		long districtId = Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
		return MessageFormat.format(MessageUtils.getMenuText(menu), getClinicsByDistrictId(districtId, request));
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
			Integer lastElementIndex = pagingSize > clinicsList.size() ? clinicsList.size() : pagingSize;
			String menuText = getClinicsMenu(clinicsList.subList(0, lastElementIndex));

			startIndex = lastElementIndex;
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

	@Override
	public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService) {
		currentState.setSessionId(request.getSessionId());
		menuService.saveCurrentState(currentState);
		Menu menu = menuService.findMenuById(currentState.getIdMenu());

		if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

			menu.setDescription(MessageFormat.format(menu.getDescription(), currentState.getPhoneNumber()));
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
			return MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu());
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {
			return MessageFormat.format(MessageUtils.getMenuText(menu), DateUtils.getAppointmentsMonth());
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
			int selectedProvinceId = Integer.parseInt(sessionDataService
					.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
			allProvinces = RestClient.getInstance().getAllProvinces();
			return MessageFormat.format(MessageUtils.getMenuText(menu),
					getDistrictsMenu(selectedProvinceId, allProvinces, request));

		} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE.equalsIgnoreCase(menu.getCode())) {
			return getClinicsByDistrictMenu(request, currentState, sessionDataService, menu);
		}

		return MessageUtils.getMenuText(menu);
	}

	private String handleRegisterConfirmation(UssdRequest ussdRequest, CurrentState currentState,
			MenuService menuService) {
		if (ussdRequest.getText().equalsIgnoreCase("1")) {
			return handleRegisterConfirmationOption1(currentState, menuService);
		} else if (ussdRequest.getText().equalsIgnoreCase("2")) {
			return handleRegisterConfirmationOption2(currentState, menuService);
		}
		return ConstantUtils.MESSAGE_OPCAO_INVALIDA;
	}

	private String handleRegisterConfirmationOption1(CurrentState currentState, MenuService menuService) {
		UtenteRegisterResponse response = RestClient.getInstance().registerUtente(utenteRequest);

		if ((response.getStatusCode() != 200 && response.getStatusCode() != 201)
				|| response.getSystemNumber() == null) {
			MenuUtils.resetSession(currentState, menuService);
			return ConstantUtils.MESSAGE_REGISTER_ERROR;
		} else {
			MenuUtils.resetSession(currentState, menuService);
			/*
			 * try { FrontlineSmsConfig configsSms =
			 * frontlineSmsConfigService.findFrontlineSmsConfigByCode(ConstantUtils.
			 * FRONTLINE_SMS_CONFIG).get(0); String smsBody =
			 * MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_PASSWORD_SMS,
			 * response.getSystemNumber()); MenuUtils.resetSession(currentState,
			 * menuService); RecipientSms[] recipients = new RecipientSms[1]; recipients[0]
			 * = new RecipientSms(ConstantUtils.TYPE_RECIEPIENT_MOBILE,
			 * ussdRequest.getPhoneNumber()); PayloadSms payload = new PayloadSms(smsBody,
			 * recipients); SendSmsRequest smsRequest = new SendSmsRequest();
			 * smsRequest.setPayload(payload); try {
			 * RestClient.getInstance().sendSms(smsRequest, configsSms); } catch (Exception
			 * e) { LOG.
			 * error("[RegisterMenuHandler.handleMenu] Ocorreu um erro ao enviar SMS, apos o registo do utente: "
			 * + ussdRequest.getPhoneNumber(), e); } return
			 * ConstantUtils.MENU_REGISTER_SUCCESS; } catch (Exception e) { //nao onsguiu
			 * enviar o sms com a confirmacao de registo, entao mostra na tela return
			 * MessageFormat.format( "END" +ConstantUtils.MESSAGE_REGISTER_PASSWORD_SMS,
			 * response.getSystemNumber());
			 * 
			 * }
			 */
			return MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_SUCESS_SMS, response.getSystemNumber());
		}
	}

	private String handleRegisterConfirmationOption2(CurrentState currentState, MenuService menuService) {
		MenuUtils.resetSession(currentState, menuService);
		return ConstantUtils.MESSAGE_REGISTER_NOT_CONFIRMED;
	}

}
