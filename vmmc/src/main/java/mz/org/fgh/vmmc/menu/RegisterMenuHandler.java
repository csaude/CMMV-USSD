package mz.org.fgh.vmmc.menu;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
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

    private static RegisterMenuHandler instance = new RegisterMenuHandler();

    private RegisterMenuHandler() {
    }

    public static RegisterMenuHandler getInstance() {
	return instance;
    }

    private static List<Province> allProvinces;
    private static Menu menuResp;

    @Override
    public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {

	Menu currentMenu = menuService.getCurrentMenuBySessionId(ussdRequest.getSessionId());
	if (currentMenu != null) {

	    if (currentMenu.getMenuField().equalsIgnoreCase("isConfirmed") && ussdRequest.getText().equalsIgnoreCase("1")) {
		Utente utente = operationMetadataService.createUtenteByMetadatas(ussdRequest, currentState.getLocation());
		UtenteRegisterResponse response = registerUtente(utente);
		System.out.println("Teste: " + utente.toString());
		System.out.println(response.toString());
	    }

	    if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {
		// Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
		OperationMetadata metadata = new OperationMetadata(ussdRequest.getSessionId(), ussdRequest.getPhoneNumber(), currentState.getLocation(), currentMenu,
			currentMenu.getMenuField(), ussdRequest.getText());
		operationMetadataService.saveOperationMetadata(metadata);

	    }

	    return navegate(currentMenu, ussdRequest, currentState, menuService);

	} else {
	    // seta o prmeiro menu como currentstate
	    CurrentState stateMainMenu = new CurrentState();
	    stateMainMenu.setSessionId(ussdRequest.getSessionId());
	    stateMainMenu.setActive(true);
	    stateMainMenu.setLocation(LocationType.MENU_PRINCIPAL.getCode());
	    stateMainMenu.setPhoneNumber(ussdRequest.getPhoneNumber());
	    stateMainMenu.setCreatedDate(LocalDateTime.now());
	    menuService.saveCurrentState(stateMainMenu);
	    return MessageUtils.getMenuText(menuResp);

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

	    if (nextMenu.getId() == ConstantUtils.MENU_PROVINCES_ID) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
	    } else if (menu.get().getNextMenuId() == ConstantUtils.MENU_DISTRICTS_ID) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	    }

	    if (nextMenu.getId() == ConstantUtils.MENU_CELLNUMBER_FROM_SESSION) {

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

	    if (currentMenu.getNextMenuId() == ConstantUtils.MENU_PROVINCES_ID) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getProvincesMenu());
	    } else if (currentMenu.getNextMenuId() == ConstantUtils.MENU_DISTRICTS_ID) {
		return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	    }
	    return MessageUtils.getMenuText(nextMenu);
	}

    }

    private String getProvincesMenu() {
	String provinces = StringUtils.EMPTY;

	try {
	    allProvinces = RestClient.getInstance().getAllProvinces();
	    for (Province province : allProvinces) {
		provinces += province.getId() + ". " + province.getDescription() + "\n";

	    }
	} catch (Exception e) {
	    // substitur por log4j
	    System.out.println("ocorreu erro ao listar provincias: " + e);
	}
	return provinces;
    }

    private String getDistrictsMenu(int idProvincia, List<Province> allProvinces) {

	Province matchingObject = (Province) allProvinces.stream().filter(p -> p.getId() == (idProvincia)).findFirst().get();
	String districts = StringUtils.EMPTY;
	for (District province : matchingObject.getDistricts()) {
	    districts += province.getId() + ". " + province.getDescription() + "\n";

	}
	return districts;
    }

    public UtenteRegisterResponse registerUtente(Utente utente) {

	try {
	    UtenteRegisterRequest request = getUtenteRegisterRequest(utente);
	    return RestClient.getInstance().registerUtente(request);
	} catch (Exception e) {
	    // TODO: usar Logger
	    System.out.println("Ocooreru erro ao registar utente: " + e);
	}
	return new UtenteRegisterResponse();
    }

    private UtenteRegisterRequest getUtenteRegisterRequest(Utente utente) {
	UtenteRegisterRequest request = new UtenteRegisterRequest();
	request.setFirstNames(utente.getFirstNames());
	request.setLastNames(utente.getLastNames());
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

	if (menu.getId() == ConstantUtils.MENU_CELLNUMBER_FROM_SESSION) {

	    menu.setDescription(MessageFormat.format(menu.getDescription(), currentState.getPhoneNumber()));
	} else if (menu.getId() == ConstantUtils.MENU_PROVINCES_ID) {
	    return MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu());
	} else if (menu.getId() == ConstantUtils.MENU_DISTRICTS_ID) {
	    //TODO: Buscar a provincia seleccionada na tabela de metadados e invocar o metodo que lista os distritos por provincia
	  //  return MessageFormat.format(MessageUtils.getMenuText(menu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces));
	}

	 
	return MessageUtils.getMenuText(menu);
    }

}
