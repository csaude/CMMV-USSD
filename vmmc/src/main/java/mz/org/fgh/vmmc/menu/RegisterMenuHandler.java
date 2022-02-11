package mz.org.fgh.vmmc.menu;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.commons.RegisterStatus;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.Address;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.model.Utente;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

@Service
public class RegisterMenuHandler implements MenuHandler {
    Logger LOG = Logger.getLogger(RegisterMenuHandler.class);

    private static RegisterMenuHandler instance = new RegisterMenuHandler();

    private RegisterMenuHandler() {
    }

    public static RegisterMenuHandler getInstance() {
	return instance;
    }

    private static Map<String, Province> mapProvinces;
    private static Map<String, District> mapDistricts;
    private static List<Province> allProvinces;

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdRequest.getSessionId(), true);
	if (currentMenu != null) {

	    if (currentMenu.getMenuField().equalsIgnoreCase("isConfirmed") && ussdRequest.getText().equalsIgnoreCase("1")) {
		Utente utente = operationMetadataService.createUtenteByMetadatas(ussdRequest, currentState.getLocation());
		UtenteRegisterResponse response = registerUtente(utente);
		if ((response.getStatusCode() != 200 && response.getStatusCode() != 201) || response.getSystemNumber() == null) {
		    currentState.setIdMenu(1);
		    currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
		    menuService.saveCurrentState(currentState);
		    return ConstantUtils.MESSAGE_REGISTER_FAILED;
		} else {
		    currentState.setActive(false);
		    menuService.saveCurrentState(currentState);
		}

	    }

	    if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {
		if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
		    if (mapProvinces.containsKey(ussdRequest.getText())) {
			ussdRequest.setText(mapProvinces.get(ussdRequest.getText()).getId() + "");
		    } else {
			// Erro introduziu uma opcao que nao existe TODO:
			return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		    }
		} else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {

		    if (mapDistricts.containsKey(ussdRequest.getText())) {
			ussdRequest.setText(mapDistricts.get(ussdRequest.getText()).getId() + "");
		    } else {
			return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		    }
		}

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

	    if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
	    } else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	    }

	    if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

		nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), request.getPhoneNumber()));
	    }
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

	    if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
	    } else if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	    }
	    return MessageUtils.getMenuText(nextMenu);
	}

    }

    private String getProvincesMenu() {
	String provinces = StringUtils.EMPTY;
	mapProvinces = new HashMap<>();

	try {
	    allProvinces = RestClient.getInstance().getAllProvinces();
	    int key = 1;
	    for (Province province : allProvinces) {
		provinces += key + ". " + province.getDescription() + "\n";
		mapProvinces.put(String.valueOf(key), province);
		key++;
	    }
	} catch (Exception e) {
	    LOG.error("ocorreu erro ao listar provincias: " + e);
	}
	return provinces;
    }

    private String getDistrictsMenu(int idProvince, List<Province> allProvinces) {
//
	mapDistricts = new HashMap<String, District>();

	List<District> districtsList = allProvinces.stream().filter(p -> p.getId() == (idProvince)).findFirst().get().getDistricts();

	String districts = StringUtils.EMPTY;
	int key = 1;
	for (District district : districtsList) {
	    mapDistricts.put(String.valueOf(key), district);
	    districts += key + ". " + district.getDescription() + "\n";
	    key++;
	}
	return districts;
    }

    public UtenteRegisterResponse registerUtente(Utente utente) {

	try {
	    UtenteRegisterRequest request = getUtenteRegisterRequest(utente);
	    return RestClient.getInstance().registerUtente(request);
	} catch (Exception e) {
	    LOG.error("[RegisterMenuHandler.registerUtente] Ocorreu um erro inesperado: " + e);
	    return new UtenteRegisterResponse(-2, ConstantUtils.MESSAGE_UNEXPECTED_ERROR);
	}

    }

    /**
     * //Compor o request com base nos dados recolhidos
     * 
     * @param utente
     * @return
     */
    private UtenteRegisterRequest getUtenteRegisterRequest(Utente utente) {

	int year = LocalDate.now().getYear() - Integer.parseInt(utente.getAge());
	int month = LocalDate.now().getDayOfMonth();
	int day = LocalDate.now().getDayOfMonth();

	UtenteRegisterRequest request = new UtenteRegisterRequest();
	request.setFirstNames(utente.getFirstNames());
	request.setLastNames(utente.getLastNames());
	request.setBirthDate(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
	request.setRegisterDate(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
	request.setStatus(RegisterStatus.REGISTER_PENDING.getCode());

	District district = new District();
	district.setId(utente.getDistrictId());
	district.setProvinceId(utente.getProvinceId());
	Address address = new Address();
	address.setResidence(utente.getAddress());
	address.setDistrict(district);
	request.setAddress(address);
	request.setCellNumber(utente.getCellNumber());
	request.setWhatsappNumber(utente.getCellNumber());
	request.setHaspartner(utente.getHaspartner());
	request.setAge(utente.getAge());
	return request;

    }

    @Override
    public String recoverSession(CurrentState currentState, MenuService menuService) {
	Menu menu = menuService.findMenuById(currentState.getIdMenu());

	if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

	    menu.setDescription(MessageFormat.format(menu.getDescription(), currentState.getPhoneNumber()));
	} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
	    return MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu());
	} else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
	    // TODO: Buscar a provincia seleccionada na tabela de metadados e invocar o
	    // metodo que lista os distritos por provincia
	    // return MessageFormat.format(MessageUtils.getMenuText(menu),
	    // getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	}

	return MessageUtils.getMenuText(menu);
    }

}
