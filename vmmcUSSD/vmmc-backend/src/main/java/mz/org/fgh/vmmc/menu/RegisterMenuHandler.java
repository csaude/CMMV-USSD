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
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;
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

	@Override
	public UssdOut handleMenu(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) {

		Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);
		if (currentMenu != null) {
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {
				return handleRegisterConfirmation(ussdIn, currentState, menuService, smsConfigurationService);
			}
			if (!ussdIn.getContent().equals(ConstantUtils.OPTION_VOLTAR)
					&& StringUtils.isNotBlank(currentMenu.getMenuField())) {

				// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
				OperationMetadata metadata = new OperationMetadata(currentState, currentState.getSessionId(),
						currentState.getLocation(), currentMenu, currentMenu.getMenuField(), ussdIn.getContent());
				operationMetadataService.saveOperationMetadata(metadata);

				if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
					return handleProvincesMenu(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
					return handleDistrictsMenu(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_AGE)) {
					return handleRegisterAge(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_NAME)
						|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_SURNAME)) {
					return handleRegisterNameOrSurname(ussdIn, currentState, currentMenu, sessionDataService,
							menuService, operationMetadataService);
				} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE
						.equalsIgnoreCase(currentMenu.getCode())) {
					return handleClinicsMenu(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				} else if (ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH
						.equalsIgnoreCase(currentMenu.getCode())) {
					return handleAppointmentMonth(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				} else if (ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_DAY.equalsIgnoreCase(currentMenu.getCode())) {
					return handleAppointmentDay(ussdIn, currentState, currentMenu, sessionDataService, menuService,
							operationMetadataService);
				}

			}
			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService);
		} else {
			return MainMenuHandler.getInstance().handleMenu(ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService, infoMessageService, smsConfigurationService);
		}

	}

	// ==============================PRIVATE
	// BEHAVIOUR======================================

	private UssdOut navegate(Menu currentMenu, UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService) {
		UssdOut out = new UssdOut(ussdIn);
		// Passa para o proximo menu, associado a opcao
		//
		// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
		// tiver apenas a opcao (0. Voltar), pega a opcao
		// introduzida pelo user para saber o proximo menu;
		if (currentMenu.getMenuItems().size() > 1 || (currentMenu.getMenuItems().size() == 1
				&& StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VOLTAR))) {

			// caso particular, se for clinica passa para a proxima tela
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VOLTAR)) {

				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);
				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {
					out.setContent(
							MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth()));
					out.setAction(nextMenu.getAction());
					return out;
				}
				out.setContent(MessageUtils.getMenuText(nextMenu));
				out.setAction(nextMenu.getAction());
				return out;
			}

			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)
					&& !StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VOLTAR)
					&& !StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VER_MAIS)) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);

				if (nextMenu.getCode()
						.equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)) {
					out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, nextMenu));
					out.setAction(nextMenu.getAction());
					return out;
				}
				out.setContent(MessageUtils.getMenuText(nextMenu));
				out.setAction(nextMenu.getAction());
				return out;

			}
			// pega o menu pelo opcao introduzida
			Optional<Menu> menu = menuService.getCurrentMenuBySessionId(currentMenu.getId(), ussdIn.getContent());
			if (!menu.isPresent()) {

				out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
						StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
				out.setAction(currentMenu.getAction());
				return out;

			}
			currentState.setIdMenu(menu.get().getNextMenuId());
			// Pega o proximo menu
			Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());
			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PRINCIPAL_CODE)) {
				currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
			}
			// actualiza o menu corrente
			menuService.saveCurrentState(currentState);

			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {

				out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu()));
				out.setAction(nextMenu.getAction());
				return out;
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {

				out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu),
						getDistrictsMenu(Integer.parseInt(ussdIn.getContent()), allProvinces, ussdIn)));
				out.setAction(nextMenu.getAction());
				return out;
			} else if (nextMenu.getCode()
					.equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE)) {
				out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, nextMenu));
				out.setAction(currentMenu.getAction());
				return out;
			}
			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), ussdIn.getFrom()));
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {
				out.setContent(
						MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth()));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {

				utenteRequest = operationMetadataService.createUtenteByMetadatas(ussdIn, currentState.getLocation(),
						currentState);
				String details = operationMetadataService.getRegisterConfirmationData(utenteRequest);
				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), details));

			}

			out.setContent(MessageUtils.getMenuText(nextMenu));
			out.setAction(nextMenu.getAction());
			return out;

		} else {

			if (MessageUtils.isValidInput(ussdIn.getContent())) {
				// Passa para o proximo menu se a opcao != 0
				//
				// se tiver apenas a opcao (0. Voltar) e o utilizador introduzir o nome por
				// exemplo, passa para o proximo menu
				currentState.setIdMenu(currentMenu.getNextMenuId());
				menuService.saveCurrentState(currentState);
				// pega o proximo menu
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());

				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {

					out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu()));
					out.setAction(nextMenu.getAction());
					return out;
				} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
					int selectedProvinceId = Integer.parseInt(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());

					out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu),
							getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn)));
					out.setAction(nextMenu.getAction());
					return out;
				} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {
					// apresenta dados na tela de confirmacao
					currentState.setIdMenu(nextMenu.getId());
					menuService.saveCurrentState(currentState);

					utenteRequest = operationMetadataService.createUtenteByMetadatas(ussdIn, currentState.getLocation(),
							currentState);
					String appointmentDetails = operationMetadataService
							.getAppointmentConfirmationDataOnRegistration(utenteRequest, currentState);

					out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu), appointmentDetails));
					out.setAction(nextMenu.getAction());
					return out;

				}
				out.setContent(MessageUtils.getMenuText(nextMenu));
				out.setAction(nextMenu.getAction());
				return out;
			} else {

				out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
						StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
				out.setAction(currentMenu.getAction());
				return out;
			}

		}

	}

	@Override
	public UssdOut recoverSession(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService, OperationMetadataService operationMetadataService) {

		UssdOut out = new UssdOut(ussdIn);
		currentState.setSessionId(ussdIn.getSession());
		menuService.saveCurrentState(currentState);
		Menu menu = menuService.findMenuById(currentState.getIdMenu());
		if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {
			menu.setDescription(MessageFormat.format(menu.getDescription(), currentState.getPhoneNumber()));
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
			out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu()));
			out.setAction(menu.getAction());
			return out;
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH)) {

			out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu), DateUtils.getAppointmentsMonth()));
			out.setAction(menu.getAction());
			return out;
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
			int selectedProvinceId = Integer.parseInt(sessionDataService
					.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
			allProvinces = RestClient.getInstance().getAllProvinces();

			out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu),
					getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn)));
			out.setAction(menu.getAction());
			return out;

		} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_ON_REGISTRATION_CODE.equalsIgnoreCase(menu.getCode())) {

			out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, menu));
			out.setAction(menu.getAction());
			return out;
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {
			return handleMenuConfirmationPage(ussdIn, currentState, menu, operationMetadataService);
		}

		out.setContent(MessageUtils.getMenuText(menu));
		out.setAction(menu.getAction());
		return out;
	}

	private String getProvincesMenu() {
		String provinces = StringUtils.EMPTY;
		mapProvinces = new HashMap<>();
		allProvinces = RestClient.getInstance().getAllProvinces();
		int key = 1;
		for (Province province : allProvinces) {
			provinces += key + ". " + MessageUtils.removeAccent(province.getDescription()) + "\r\n";
			mapProvinces.put(String.valueOf(key), province);
			key++;
		}
		return provinces;
	}

	private String getDistrictsMenu(int idProvince, List<Province> allProvinces, UssdIn ussdIn) {

		if (!ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)
				&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VOLTAR)) {
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
			menuDistricts += item.getOption() + ". " + MessageUtils.removeAccent(item.getDescription()) + "\r\n";
			mapDistricts.put(String.valueOf(item.getOption()), item);
		}

		return menuDistricts;
	}

	private String getClinicsByDistrictMenu(UssdIn request, CurrentState currentState,
			SessionDataService sessionDataService, Menu menu) {
		long districtId = Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
		return MessageFormat.format(MessageUtils.getMenuText(menu), getClinicsByDistrictId(districtId, request));
	}

	// Devolve a lista de clinicas, sobre uma paginacao definida
	private String getClinicsByDistrictId(long districtId, UssdIn ussdIn) {

		if (ussdIn == null || !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)) {

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
			menuClinics += item.getOption() + ". " + MessageUtils.removeAccent(item.getName()) + "\r\n";
			mapClinics.put(String.valueOf(item.getOption()), item);
		}

		return menuClinics;
	}

	// =======================MENUS
	// HANDLERS============================================================
	private UssdOut handleRegisterConfirmation(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			SmsConfigurationService smsConfigurationService) {
		UssdOut out = new UssdOut(ussdIn);
		if (ussdIn.getContent().equalsIgnoreCase("1")) {

			return handleRegisterConfirmationOption1(currentState, menuService, smsConfigurationService, ussdIn);
		} else if (ussdIn.getContent().equalsIgnoreCase("2")) {
			return handleRegisterConfirmationOption2(currentState, menuService, ussdIn);
		}

		out.setContent(ConstantUtils.MESSAGE_OPCAO_INVALIDA);
		out.setAction("end");
		return out;
	}

	private UssdOut handleRegisterConfirmationOption1(CurrentState currentState, MenuService menuService,
			SmsConfigurationService smsConfigurationService, UssdIn ussdIn) {
		UtenteRegisterResponse response = RestClient.getInstance().registerUtente(utenteRequest);
		UssdOut out = new UssdOut(ussdIn);
		if ((response.getStatusCode() != 200 && response.getStatusCode() != 201)
				|| response.getSystemNumber() == null) {
			MenuUtils.resetSession(currentState, menuService);
			out.setContent(ConstantUtils.MESSAGE_REGISTER_ERROR);
			out.setAction("end");
			return out;
		} else {
			MenuUtils.resetSession(currentState, menuService);
			out.setContent(ConstantUtils.MENU_REGISTER_SUCCESS);
			out.setAction("end");

			/*
			 * try { Map<String, SmsConfiguration> configsSms = smsConfigurationService
			 * .findSmsConfigurationByCode("SMS_GATEWAY_CONFIG"); String smsBody =
			 * MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_PASSWORD_SMS,
			 * response.getSystemNumber()); MenuUtils.resetSession(currentState,
			 * menuService);
			 * 
			 * SendSmsRequest smsRequest = new SendSmsRequest(); smsRequest.setDestination(
			 * ussdIn.getFrom()); smsRequest.setText(smsBody); try {
			 * RestClient.getInstance().sendSms(smsRequest, configsSms); } catch (Exception
			 * e) { LOG.error(
			 * "[RegisterMenuHandler.handleMenu] Ocorreu um erro ao enviar SMS, apos o registo do utente: "
			 * + ussdIn.getFrom(), e); }
			 * out.setContent(ConstantUtils.MENU_REGISTER_SUCCESS); out.setAction("end");
			 * return out; } catch (Exception e) { // nao onsguiu // enviar o sms com a
			 * confirmacao de registo, entao mostra na tela return out.setContent(
			 * MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_PASSWORD_SMS,
			 * response.getSystemNumber())); out.setAction("end");
			 * 
			 * }
			 */
			return out;
		}
	}

	private UssdOut handleRegisterConfirmationOption2(CurrentState currentState, MenuService menuService,
			UssdIn ussdIn) {
		MenuUtils.resetSession(currentState, menuService);
		UssdOut out = new UssdOut(ussdIn);
		out.setContent(ConstantUtils.MESSAGE_REGISTER_NOT_CONFIRMED);
		out.setAction("end");
		return out;
	}

	private UssdOut handleMenuConfirmationPage(UssdIn ussdIn, CurrentState currentState, Menu menu,
			OperationMetadataService operationMetadataService) {

		utenteRequest = operationMetadataService.createUtenteByMetadatas(ussdIn, currentState.getLocation(),
				currentState);
		String details = operationMetadataService.getRegisterConfirmationData(utenteRequest);
		menu.setDescription(MessageFormat.format(menu.getDescription(), details));

		UssdOut out = new UssdOut(ussdIn);
		out.setContent(MessageUtils.getMenuText(menu));
		out.setAction(menu.getAction());
		return out;
	}

	private UssdOut handleProvincesMenu(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {

		UssdOut out = new UssdOut(ussdIn);
		if (mapProvinces.containsKey(ussdIn.getContent())) {
			SessionData sd = new SessionData(currentState.getId(), "provinceId",
					mapProvinces.get(ussdIn.getContent()).getId() + "");
			sessionDataService.saveSessionData(sd);
			ussdIn.setContent(mapProvinces.get(ussdIn.getContent()).getId() + "");
			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService);
		} else {

			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils
					.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu), getProvincesMenu()), "CON ")));
			out.setAction(currentMenu.getAction());
			return out;

		}

	}

	private UssdOut handleDistrictsMenu(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {
		UssdOut out = new UssdOut(ussdIn);
		int selectedProvinceId = Integer.parseInt(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());

		if (ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)) {

			out.setContent(MessageFormat.format(MessageUtils.getMenuText(currentMenu),
					getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn)));
			out.setAction(currentMenu.getAction());
			return out;

		} else if (mapDistricts.containsKey(ussdIn.getContent())) {
			// ver AQUIIIII
			SessionData sd = new SessionData(currentState.getId(), "districtId",
					mapDistricts.get(ussdIn.getContent()).getId() + "");
			sessionDataService.saveSessionData(sd);
			ussdIn.setContent(mapDistricts.get(ussdIn.getContent()).getId() + "");
			lastIndex = pagingSize;
			startIndex = 0;

			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService);

		} else {

			out.setContent(
					MessageFormat
							.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
									StringUtils.remove(
											MessageFormat.format(MessageUtils.getMenuText(currentMenu),
													getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn)),
											"CON ")));
			out.setAction(currentMenu.getAction());
			return out;

		}

	}

	private UssdOut handleRegisterAge(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {
		UssdOut out = new UssdOut(ussdIn);
		if (!MessageUtils.isValidAge(ussdIn.getContent())) {

			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_AGE_INVALID,
					StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
			out.setAction(currentMenu.getAction());
			return out;

		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService);
	}

	private UssdOut handleRegisterNameOrSurname(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {
		UssdOut out = new UssdOut(ussdIn);
		if (!MessageUtils.isValidStringField(ussdIn.getContent())) {

			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_REGISTER_VALUE_INVALID,
					StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
			out.setAction(currentMenu.getAction());
			return out;
		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService);
	}

	private UssdOut handleClinicsMenu(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {

		UssdOut out = new UssdOut(ussdIn);
		if (mapClinics.containsKey(ussdIn.getContent())) {
			// Seta o ID da clinica correspondente a opcao escolhida
			Clinic clinica = mapClinics.get(ussdIn.getContent());
			sessionDataService.saveClinicOnSessionData(clinica, currentState.getId());
			ussdIn.setContent(clinica.getId() + "");
			lastIndex = pagingSize;
			startIndex = 0;
			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService);

		} else {

			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils
					.remove(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, currentMenu), "CON ")));
			out.setAction(currentMenu.getAction());
			return out;

		}
	}

	private UssdOut handleAppointmentMonth(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {

		UssdOut out = new UssdOut(ussdIn);
		String month = ussdIn.getContent();
		if (!DateUtils.isValidMonth(month)) {
			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils.remove(
					MessageFormat.format(MessageUtils.getMenuText(currentMenu), DateUtils.getAppointmentsMonth()),
					"CON")));
			out.setAction(currentMenu.getAction());
			return out;
		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService);
	}

	private UssdOut handleAppointmentDay(UssdIn ussdIn, CurrentState currentState, Menu currentMenu,
			SessionDataService sessionDataService, MenuService menuService,
			OperationMetadataService operationMetadataService) {
		UssdOut out = new UssdOut(ussdIn);
		String month = operationMetadataService
				.getMetadatasByOperationTypeAndSessionId(currentState.getId(), currentState.getLocation())
				.get("monthRegister").getAttrValue();

		if (!DateUtils.isValidDay(ussdIn.getContent(), month)) {

			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DAY_INVALID,
					StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
			out.setAction(currentMenu.getAction());
			return out;
		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService);
	}

}
