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
import mz.org.fgh.vmmc.inout.SendSmsRequest;
import mz.org.fgh.vmmc.inout.UssdIn;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.inout.UtenteSearchResponse;
import mz.org.fgh.vmmc.model.Clinic;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.InfoMessage;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.model.SessionData;
import mz.org.fgh.vmmc.model.SmsConfiguration;
import mz.org.fgh.vmmc.service.InfoMessageService;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.service.SmsConfigurationService;
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
	private Map<String, Province> mapProvinces;
	private Map<String, District> mapDistricts;
	private List<District> districtList;
	private List<Province> allProvinces;
	private static OperationsMenuHandler instance = new OperationsMenuHandler();

	private OperationsMenuHandler() {
	}

	public static OperationsMenuHandler getInstance() {
		return instance;
	}

	@Override
	public UssdOut handleMenu(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) throws Throwable {

		UssdOut out = new UssdOut(ussdIn);

		Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);

		if (currentMenu != null) {
			// Invoca o servico de consulta
			if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE) || currentMenu
					.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_RESCHEDULE_CODE)) {
				return handleRegisterConfirmation(currentMenu, ussdIn, currentState, menuService,
						operationMetadataService, sessionDataService, infoMessageService, smsConfigurationService);
			}

			else if (!ussdIn.getContent().equals(ConstantUtils.OPTION_VOLTAR)
					|| ussdIn.getContent().equals(ConstantUtils.OPTION_VER_MAIS)) {

				if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_RESCHEDULE_CODE)
						|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_US_LIST_CODE)) {
					return handleMenuProvince(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
							sessionDataService, infoMessageService, smsConfigurationService);
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_RESCHEDULE_CODE)
						|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_US_LIST_CODE)) {

					return handleMenuDistrict(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
							sessionDataService, infoMessageService, smsConfigurationService);
				} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_AUTHENTICATION_CODE)) {
					return autenticate(currentMenu, ussdIn, currentState, menuService, sessionDataService);
				} else if (ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE.equalsIgnoreCase(currentMenu.getCode())
						|| ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(currentMenu.getCode())
						|| ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE
								.equalsIgnoreCase(currentMenu.getCode())) {
					if (ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)) {

						out.setAction(currentMenu.getAction());
						out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, currentMenu));
						return out;

					} else if ("00".equalsIgnoreCase(ussdIn.getContent())
							&& ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(currentMenu.getCode())) {

						return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
								sessionDataService, infoMessageService, smsConfigurationService);

					} else if (mapClinics.containsKey(ussdIn.getContent())) {
						// Seta o ID da clinica correspondente a opcao escolhida
						Clinic clinica = mapClinics.get(ussdIn.getContent());
						sessionDataService.saveClinicOnSessionData(clinica, currentState.getId());
						ussdIn.setContent(clinica.getId() + "");
						lastIndex = pagingSize;
						startIndex = 0;

					} else {

						out.setAction(currentMenu.getAction());
						out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(
										getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, currentMenu),
										"CON ")));
						return out;

					}
				} else if (ussdIn.getContent().equalsIgnoreCase("1")
						&& ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE
								.equalsIgnoreCase(currentMenu.getCode())) {
					Map<String, SmsConfiguration> configsSms = smsConfigurationService
							.findSmsConfigurationByCode("SMS_GATEWAY_CONFIG");

					long districtId = Long.parseLong(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
					List<Clinic> clinics = RestClient.getInstance().getClinicsByDistrict(districtId).getClinics()
							.stream().sorted(Comparator.comparing(Clinic::getName)).collect(Collectors.toList());

					currentState.setActive(false);
					menuService.saveCurrentState(currentState);

					SendSmsRequest smsRequest = new SendSmsRequest();
					smsRequest.setDestination(ussdIn.getFrom());
					smsRequest.setText(getClinicsForSms(clinics));
					RestClient.getInstance().sendSms(smsRequest, configsSms);

					out.setAction("end");
					out.setContent(ConstantUtils.MESSAGE_SEND_SMS_CLINIC_LIST);
					return out;

				} else if (ConstantUtils.MENU_APPOINTMENT_MONTH.equalsIgnoreCase(currentMenu.getCode())
						|| ConstantUtils.MENU_APPOINTMENT_RESCHEDULE_MONTH.equalsIgnoreCase(currentMenu.getCode())) {

					String month = ussdIn.getContent();
					if (!DateUtils.isValidMonth(month)) {

						out.setAction(currentMenu.getAction());
						out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu),
										DateUtils.getAppointmentsMonth()), "CON")));
						return out;

					}

				} else if (ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH
						.equalsIgnoreCase(currentMenu.getCode())) {

					String month = ussdIn.getContent();
					if (!DateUtils.isValidMonth(month)) {

						out.setAction(currentMenu.getAction());
						out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
								StringUtils.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu),
										DateUtils.getAppointmentsMonth()), "CON")));
						return out;

					}

				} else if (ConstantUtils.MENU_APPOINTMENT_DAY.equalsIgnoreCase(currentMenu.getCode())
						|| ConstantUtils.MENU_APPOINTMENT_RESCHEDULE_DAY.equalsIgnoreCase(currentMenu.getCode())) {

					String keyMonth = ConstantUtils.MENU_APPOINTMENT_DAY.equalsIgnoreCase(currentMenu.getCode())
							? "appointmentMonth"
							: "monthReschedule";

					String month = operationMetadataService
							.getMetadatasByOperationTypeAndSessionId(currentState.getId(), currentState.getLocation())
							.get(keyMonth).getAttrValue();

					if (!DateUtils.isValidDay(ussdIn.getContent(), month)) {

						out.setAction(currentMenu.getAction());
						out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DAY_INVALID,
								StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
						return out;
					}

				} else if (ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE.equalsIgnoreCase(currentMenu.getCode())) {

					if ((ussdIn.getContent().equalsIgnoreCase("1") || ussdIn.getContent().equalsIgnoreCase("2"))) {
						String utenteId = sessionDataService
								.findByCurrentStateIdAndAttrName(currentState.getId(), "utenteId").getAttrValue();
						AppointmentSearchResponse appointment = RestClient.getInstance()
								.getAppointmentByUtenteId(utenteId);
						if (appointment.getStatus().equalsIgnoreCase("CONFIRMADO")) {
							MenuUtils.resetSession(currentState, menuService);

							out.setAction(currentMenu.getAction());
							out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_UPDATE_APPOINTMENT_ERROR,
									StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
							return out;

						}
					} else {
						out.setAction(currentMenu.getAction());
						out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils.remove(
								getAppointmentDetails(currentState, menuService, sessionDataService, currentMenu),
								"CON ")));
						return out;
					}
				}

				else if (!MessageUtils.isValidOption(currentMenu, ussdIn.getContent())) {
					// Valida as opcoes para os menus com opcoes predifinidas na Base
					out.setAction(currentMenu.getAction());
					out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA,
							StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
					return out;
				}

				// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
				if (StringUtils.isNotBlank(currentMenu.getMenuField())) {
					OperationMetadata metadata = new OperationMetadata(currentState, currentState.getSessionId(),
							currentState.getLocation(), currentMenu, currentMenu.getMenuField(), ussdIn.getContent());
					operationMetadataService.saveOperationMetadata(metadata);
				}

			}

			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService, infoMessageService, smsConfigurationService);

		} else {

			return MainMenuHandler.getInstance().handleMenu(ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService, infoMessageService, smsConfigurationService);

		}

	}

	// ==============================PRIVATE
	// BEHAVIOUR======================================

	// Responsavel por passar para o proximo menu
	private UssdOut navegate(Menu currentMenu, UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			OperationMetadataService operationMetadataService, SessionDataService sessionDataService,
			InfoMessageService infoMessageService, SmsConfigurationService smsConfigurationService) {

		// UtenteSearchResponse utente = (UtenteSearchResponse)
		// httpSession.getAttribute("utenteSession");

		// Passa para o proximo menu, associado a opcao
		//
		// se tiver mais opcoes para seleccionar e não somente a opcao (0. Voltar) OU se
		// tiver apenas a opcao (0. Voltar), pega a opcao
		// introduzida pelo user para saber o proximo menu;
		UssdOut out = new UssdOut(ussdIn);
		if ((currentMenu.getMenuItems().size() > 1) || (currentMenu.getMenuItems().size() == 1
				&& StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VOLTAR))) {
			// caso particular, se for clinica passa para a proxima tela
			if ((currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE) || currentMenu
					.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE))
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VOLTAR)) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);
				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_MONTH)
						|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_RESCHEDULE_MONTH)) {
					out.setAction(nextMenu.getAction());
					out.setContent(
							MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth()));
					return out;
				}
				out.setContent(MessageUtils.getMenuText(nextMenu));
				out.setAction(nextMenu.getAction());
				return out;
			} else if ((currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_RESCHEDULE_CODE)
					|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_US_LIST_CODE))
					&& !StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VOLTAR)
					&& !StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VER_MAIS)) {
				Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
				currentState.setIdMenu(nextMenu.getId());
				menuService.saveCurrentState(currentState);

				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE)
						|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_CODE)) {
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
				out.setAction("request");
				return out;

			}
			currentState.setIdMenu(menu.get().getNextMenuId());
			menuService.saveCurrentState(currentState);
			Menu nextMenu = menuService.findMenuById(menu.get().getNextMenuId());

			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_RESCHEDULE_CODE)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_US_LIST_CODE)) {

				out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu()));
				out.setAction(nextMenu.getAction());
				return out;
			} else if ((currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_RESCHEDULE_CODE)
					|| currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_US_LIST_CODE))
					&& !StringUtils.trim(ussdIn.getContent()).equals(ConstantUtils.OPTION_VOLTAR)) {

				if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE)
						|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_CODE)) {
					out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, nextMenu));
					out.setAction(nextMenu.getAction());
					return out;
				} else {
					int selectedProvinceId = Integer.parseInt(sessionDataService
							.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
					out.setContent(getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn, nextMenu));
					out.setAction(nextMenu.getAction());
					return out;
				}

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_CODE) || nextMenu.getCode()
							.equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE)) {
				out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, nextMenu));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE)) {
				out.setContent(getAppointmentDetails(currentState, menuService, sessionDataService, nextMenu));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_MONTH)
					|| ConstantUtils.MENU_APPOINTMENT_RESCHEDULE_MONTH.equalsIgnoreCase(nextMenu.getCode())) {

				out.setContent(
						MessageFormat.format(MessageUtils.getMenuText(nextMenu), DateUtils.getAppointmentsMonth()));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE)) {

				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), ussdIn.getFrom()));
			} else if (!ConstantUtils.OPTION_VOLTAR.equalsIgnoreCase(ussdIn.getContent())
					&& ConstantUtils.MENU_INFORMATIVE_MESSAGES.equalsIgnoreCase(currentMenu.getCode())) {

				Map<String, SmsConfiguration> configsSms = smsConfigurationService
						.findSmsConfigurationByCode("SMS_GATEWAY_CONFIG");

				List<InfoMessage> listMessage = infoMessageService.findMessagesByCode(menu.get().getCode());
				for (InfoMessage message : listMessage) {				 
					SendSmsRequest smsRequest = new SendSmsRequest();
					smsRequest.setDestination(ussdIn.getFrom());
					smsRequest.setText(message.getDescription());
					RestClient.getInstance().sendSms(smsRequest, configsSms);
				}
				MenuUtils.resetSession(currentState, menuService);
				out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_INFORMATIVE_MESSAGES, ussdIn.getFrom()));
				out.setAction("end");
				return out;

			}

			out.setContent(MessageUtils.getMenuText(nextMenu));
			out.setAction(nextMenu.getAction());
			return out;
		} else {

			currentState.setIdMenu(currentMenu.getNextMenuId());
			menuService.saveCurrentState(currentState);
			// pega o proximo menu
			Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
			if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE) || nextMenu
					.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_RESCHEDULE_CODE)) {
				return handleMenuConfirmationPage(ussdIn, currentState, nextMenu, operationMetadataService, menuService,
						sessionDataService);
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_RESCHEDULE_CODE)) {
				out.setContent(MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu()));
				out.setAction(nextMenu.getAction());
				return out;
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_RESCHEDULE_CODE)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_US_LIST_CODE)) {

				int selectedProvinceId = Integer.parseInt(sessionDataService
						.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
				out.setContent(getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn, nextMenu));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE)
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)
					&& !ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VOLTAR)) {
				out.setContent(MessageUtils.getMenuText(nextMenu));
				out.setAction(nextMenu.getAction());
				return out;
			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_MONTH)
					|| nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_RESCHEDULE_MONTH)) {

				out.setContent(MessageFormat.format(getNextMenuText(currentState, currentMenu, menuService),
						DateUtils.getAppointmentsMonth()));
				out.setAction(nextMenu.getAction());
				return out;

			} else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE)) {

				nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), ussdIn.getFrom()));

			}

			out.setContent(getNextMenuText(currentState, currentMenu, menuService));
			out.setAction(nextMenu.getAction());
			return out;

		}

	}

	@Override
	public UssdOut recoverSession(UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService, OperationMetadataService operationMetadataService) {
		UssdOut out = new UssdOut(ussdIn);
		currentState.setSessionId(ussdIn.getSession());
		menuService.saveCurrentState(currentState);
		Menu menu = menuService.findMenuById(currentState.getIdMenu());
		if (ConstantUtils.MENU_CLINICS_LIST_CODE.equalsIgnoreCase(menu.getCode())
				|| ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_CODE.equalsIgnoreCase(menu.getCode())
				|| ConstantUtils.MENU_CLINICS_LIST_APPOINTMENT_RESCHEDULE_CODE.equalsIgnoreCase(menu.getCode())) {

			out.setAction(menu.getAction());
			out.setContent(getClinicsByDistrictMenu(ussdIn, currentState, sessionDataService, menu));
			return out;
		} else if (ConstantUtils.MENU_APPOINTMENT_MONTH.equalsIgnoreCase(menu.getCode())
				|| ConstantUtils.MENU_APPOINTMENT_ON_REGISTRATION_MONTH.equalsIgnoreCase(menu.getCode())) {
			out.setAction(menu.getAction());
			out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu), DateUtils.getAppointmentsMonth()));
			return out;

		} else if (ConstantUtils.MENU_APPOINTMENT_DETAILS_CODE.equalsIgnoreCase(menu.getCode())) {
			out.setAction(menu.getAction());
			out.setContent(getAppointmentDetails(currentState, menuService, sessionDataService, menu));
			return out;

		} else if (ConstantUtils.MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE.equalsIgnoreCase(menu.getCode())) {
			menu.setDescription(MessageFormat.format(menu.getDescription(), ussdIn.getFrom()));
			out.setAction(menu.getAction());
			out.setContent(MessageUtils.getMenuText(menu));
			return out;
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_CODE)
				|| menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_APPOINTMENT_CONFIRMATION_RESCHEDULE_CODE)) {
			return handleMenuConfirmationPage(ussdIn, currentState, menu, operationMetadataService, menuService,
					sessionDataService);
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)
				|| menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_RESCHEDULE_CODE)
				|| menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_US_LIST_CODE)) {

			out.setAction(menu.getAction());
			out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu()));
			return out;
		} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_RESCHEDULE_CODE)
				|| menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_US_LIST_CODE)) {
			int selectedProvinceId = Integer.parseInt(sessionDataService
					.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());
			allProvinces = RestClient.getInstance().getAllProvinces();

			out.setAction(menu.getAction());
			out.setContent(getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn, menu));
			return out;
		}
		out.setAction(menu.getAction());
		out.setContent(MessageUtils.getMenuText(menu));
		return out;
	}

	// =================================MENU HANDLERS===========================

	private UssdOut handleRegisterConfirmation(Menu currentMenu, UssdIn ussdIn, CurrentState currentState,
			MenuService menuService, OperationMetadataService operationMetadataService,
			SessionDataService sessionDataService, InfoMessageService infoMessageService,
			SmsConfigurationService smsConfigurationService) throws Throwable {

		UssdOut out = new UssdOut(ussdIn);
		if (ussdIn.getContent().equalsIgnoreCase("1")) {

			AppointmentResponse response = RestClient.getInstance().updateAppointment(appointmentRequest);
			if ((response.getStatusCode() != 200 && response.getStatusCode() != 201)) {
				currentState.setIdMenu(1);
				currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
				menuService.saveCurrentState(currentState);
				out.setAction("end");
				out.setContent(ConstantUtils.MESSAGE_APPOINTMENT_FAILED);
				return out;
			} else {
				currentState.setActive(false);
				menuService.saveCurrentState(currentState);
				out.setAction("end");
				out.setContent(ConstantUtils.MESSAGE_APPOINTMENT_SUCCESS);
				return out;
			}
		} else if (ussdIn.getContent().equalsIgnoreCase("2")) {

			MenuUtils.resetSession(currentState, menuService);
			out.setContent(ConstantUtils.MESSAGE_APPOINTMENT_NOT_CONFIRMED);
			out.setAction("end");
			return out;

		} else if (ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VOLTAR)) {
			return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService,
					sessionDataService, infoMessageService, smsConfigurationService);
		} else {
			String appointmentDetails = operationMetadataService.getAppointmentConfirmationData(appointmentRequest);
			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils
					.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu), appointmentDetails), "CON")));
			out.setAction("end");
			return out;

		}
	}

	private UssdOut handleMenuDistrict(Menu currentMenu, UssdIn ussdIn, CurrentState currentState,
			MenuService menuService, OperationMetadataService operationMetadataService,
			SessionDataService sessionDataService, InfoMessageService infoMessageService,
			SmsConfigurationService smsConfigurationService) {
		UssdOut out = new UssdOut(ussdIn);
		int selectedProvinceId = Integer.parseInt(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "provinceId").getAttrValue());

		if (ussdIn.getContent().equalsIgnoreCase(ConstantUtils.OPTION_VER_MAIS)) {
			out.setContent(getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn, currentMenu));
			out.setAction("request");
			return out;

		} else if (mapDistricts.containsKey(ussdIn.getContent())) {
			// ver AQUIIIII
			SessionData sd = new SessionData(currentState.getId(), "districtId",
					mapDistricts.get(ussdIn.getContent()).getId() + "");
			sessionDataService.saveSessionData(sd);
			ussdIn.setContent(mapDistricts.get(ussdIn.getContent()).getId() + "");
			lastIndex = pagingSize;
			startIndex = 0;

		} else {
			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils
					.remove(getDistrictsMenu(selectedProvinceId, allProvinces, ussdIn, currentMenu), "CON ")));
			out.setAction("request");
			return out;
		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService,
				infoMessageService, smsConfigurationService);
	}

	private String getClinicsByDistrictMenu(UssdIn request, CurrentState currentState,
			SessionDataService sessionDataService, Menu menu) {
		long districtId = Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "districtId").getAttrValue());
		return getClinicsByDistrictId(districtId, request, menu);
	}

	private UssdOut handleMenuProvince(Menu currentMenu, UssdIn ussdIn, CurrentState currentState,
			MenuService menuService, OperationMetadataService operationMetadataService,
			SessionDataService sessionDataService, InfoMessageService infoMessageService,
			SmsConfigurationService smsConfigurationService) throws Throwable {
		UssdOut out = new UssdOut(ussdIn);
		if (mapProvinces.containsKey(ussdIn.getContent())) {
			SessionData sd = new SessionData(currentState.getId(), "provinceId",
					mapProvinces.get(ussdIn.getContent()).getId() + "");
			sessionDataService.saveSessionData(sd);
			ussdIn.setContent(mapProvinces.get(ussdIn.getContent()).getId() + "");
		} else {
			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_OPCAO_INVALIDA, StringUtils
					.remove(MessageFormat.format(MessageUtils.getMenuText(currentMenu), getProvincesMenu()), "CON ")));
			out.setAction("request");
			return out;

		}
		return navegate(currentMenu, ussdIn, currentState, menuService, operationMetadataService, sessionDataService,
				infoMessageService, smsConfigurationService);
	}

	// ================================== Private Behaviour ==============
	private UssdOut handleMenuConfirmationPage(UssdIn ussdIn, CurrentState currentState, Menu menu,
			OperationMetadataService operationMetadataService, MenuService menuService,
			SessionDataService sessionDataService) {
		// apresenta dados na tela de confirmacao
		currentState.setIdMenu(menu.getId());
		menuService.saveCurrentState(currentState);
		appointmentRequest = operationMetadataService.createAppointmentRequestByMetadatas(ussdIn,
				currentState.getLocation(), currentState, menu);

		appointmentRequest.setUtente(Long.parseLong(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "utenteId").getAttrValue()));
		appointmentRequest.setClinicName(
				sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "clinicName").getAttrValue());
		appointmentRequest.setId(Long.parseLong(sessionDataService
				.findByCurrentStateIdAndAttrName(currentState.getId(), "appointmentId").getAttrValue()));

		String appointmentDetails = operationMetadataService.getAppointmentConfirmationData(appointmentRequest);
		UssdOut out = new UssdOut(ussdIn);
		out.setAction(menu.getAction());
		out.setContent(MessageFormat.format(MessageUtils.getMenuText(menu), appointmentDetails));
		return out;
	}

	private String getAppointmentDetails(CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService, Menu nextMenu) {

		String utenteId = sessionDataService.findByCurrentStateIdAndAttrName(currentState.getId(), "utenteId")
				.getAttrValue();
		AppointmentSearchResponse app = RestClient.getInstance().getAppointmentByUtenteId(utenteId);
		if (app.getStatusCode() == 200 || app.getStatusCode() == 201) {

			sessionDataService.saveClinicOnSessionData(app.getClinic(), currentState.getId());
			String details = MessageFormat.format(ConstantUtils.MESSAGE_APPOINTMENT_DETAILS,
					DateUtils.getSimpleDateFormat(app.getAppointmentDate()), app.getClinic().getName(),
					app.getStatus());
			return MessageFormat.format(MessageUtils.getMenuText(nextMenu), details);

		} else {
			MenuUtils.resetSession(currentState, menuService);
			return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		}
	}

	private UssdOut autenticate(Menu currentMenu, UssdIn ussdIn, CurrentState currentState, MenuService menuService,
			SessionDataService sessionDataService) {
		// Invocar o servico de autenticacao
		UtenteSearchResponse response = RestClient.getInstance().getUtenteBySystemNumber(ussdIn.getContent());
		String phoneNumber = StringUtils.trim(StringUtils.remove(ussdIn.getFrom(), "+258"));
		UssdOut out = new UssdOut(ussdIn);

		if (response.getStatusCode() == 200 && phoneNumber.equalsIgnoreCase(response.getCellNumber())) {
			currentState.setIdMenu(currentMenu.getNextMenuId());
			menuService.saveCurrentState(currentState);
			sessionDataService.saveSessionData(response, currentState.getId());
			// session.setAttribute("utenteSession", response);
			// return getNextMenuText(currentState, currentMenu, menuService);
			Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
			out.setAction(nextMenu.getAction());
			out.setContent(getAppointmentDetails(currentState, menuService, sessionDataService, nextMenu));
			return out;
		} else {
			out.setContent(MessageFormat.format(ConstantUtils.MESSAGE_LOGIN_FAILED,
					StringUtils.remove(MessageUtils.getMenuText(currentMenu), "CON ")));
			out.setAction("request");
			return out;
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

	private String getDistrictsMenu(int idProvince, List<Province> allProvinces, UssdIn ussdIn, Menu currentMenu) {

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

			return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menuText);
			// return menuText;

		} else {
			if (lastIndex > districtList.size()) {
				String menu = getFormatedDistrictByList(districtList.subList(startIndex, districtList.size()));
				List<Menu> menus = currentMenu.getMenuItems();
				menus.removeIf(t -> t.getOption().equalsIgnoreCase("99"));
				return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menu);
			}
			String menuText = getFormatedDistrictByList(districtList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = startIndex + pagingSize;
			return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menuText);
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

	// Devolve a lista de clinicas, sobre uma paginacao definida
	private String getClinicsByDistrictId(long districtId, UssdIn ussdIn, Menu currentMenu) {

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
			return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menuText);

		} else {
			if (lastIndex > clinicsList.size()) {
				String menu = getClinicsMenu(clinicsList.subList(startIndex, clinicsList.size()));
				List<Menu> menus = currentMenu.getMenuItems();
				menus.removeIf(t -> t.getOption().equalsIgnoreCase("99"));
				return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menu);

			}
			String menuText = getClinicsMenu(clinicsList.subList(startIndex, lastIndex));
			startIndex = lastIndex;
			lastIndex = lastIndex + pagingSize;
			return MessageFormat.format(MessageUtils.getMenuText(currentMenu), menuText);
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

	private String getClinicsForSms(List<Clinic> list) {
		String menuClinics = StringUtils.EMPTY;
		int i = 1;
		for (Clinic item : list) {
			menuClinics += i + "." +MessageUtils.removeAccent( item.getName()) + ",";
			i++;
		}

		return menuClinics;
	}
}
