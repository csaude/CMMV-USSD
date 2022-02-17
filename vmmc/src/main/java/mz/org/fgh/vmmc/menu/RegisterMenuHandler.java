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
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.commons.LocationType;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterRequest;
import mz.org.fgh.vmmc.inout.UtenteRegisterResponse;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;
import mz.org.fgh.vmmc.utils.ConstantUtils;
import mz.org.fgh.vmmc.utils.MessageUtils;

@Service
public class RegisterMenuHandler implements MenuHandler {
       Logger LOG = Logger.getLogger(RegisterMenuHandler.class);

       private RegisterMenuHandler() {
       }

       public static RegisterMenuHandler getInstance() {
	     return instance;
       }

       private List<District> districtList = new ArrayList<District>();
       private static RegisterMenuHandler instance = new RegisterMenuHandler();
       private int lastIndex;
       private int startIndex;
       private final int pagingSize = 4;
       private Map<String, Province> mapProvinces;
       private Map<String, District> mapDistricts;
       private List<Province> allProvinces;
       private UtenteRegisterRequest utenteRequest;

       @Override
       public String handleMenu(UssdRequest ussdRequest, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService,
		   SessionDataService sessionDataService, HttpSession httpSession) {

	     Menu currentMenu = menuService.getCurrentMenuBySessionId(currentState.getSessionId(), true);
	     if (currentMenu != null) {
		   //
		   if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE) && ussdRequest.getText().equalsIgnoreCase("1")) {

			 UtenteRegisterResponse response = RestClient.getInstance().registerUtente(utenteRequest);
			 if ((response.getStatusCode() != 200 && response.getStatusCode() != 201) || response.getSystemNumber() == null) {
			        resetSession(currentState, menuService);
			        return ConstantUtils.MESSAGE_REGISTER_FAILED;
			 } else {
			        resetSession(currentState, menuService);
			 }

		   }

		   if (!ussdRequest.getText().equals("0") && StringUtils.isNotBlank(currentMenu.getMenuField())) {
			 if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
			        if (mapProvinces.containsKey(ussdRequest.getText())) {
				      httpSession.setAttribute("idProvinceSession", mapProvinces.get(ussdRequest.getText()).getId());
				      ussdRequest.setText(mapProvinces.get(ussdRequest.getText()).getId() + "");
			        } else {
				      // Erro introduziu uma opcao que nao existe TODO:
				      return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
			        }
			 } else if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {

			        if (ussdRequest.getText().equalsIgnoreCase("#")) {
				      int selectedProvinceId = Integer.parseInt(httpSession.getAttribute("idProvinceSession") + "");
				      return MessageFormat.format(MessageUtils.getMenuText(currentMenu), getDistrictsMenu(selectedProvinceId, allProvinces, ussdRequest));
			        } else if (mapDistricts.containsKey(ussdRequest.getText())) {
				      ussdRequest.setText(mapDistricts.get(ussdRequest.getText()).getId() + "");
			        } else {
				      resetSession(currentState, menuService);
				      return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
			        }
			 }

			 // Grava os dados introduzidos na tabela de metadados (Ex: attrName: age;
			 OperationMetadata metadata = new OperationMetadata(ussdRequest.getSessionId(), ussdRequest.getPhoneNumber(), currentState.getLocation(), currentMenu,
				      currentMenu.getMenuField(), ussdRequest.getText());
			 operationMetadataService.saveOperationMetadata(metadata);

		   }

		   return navegate(currentMenu, ussdRequest, currentState, menuService, operationMetadataService);

	     } else {
		   return MainMenuHandler.getInstance().handleMenu(ussdRequest, currentState, menuService, operationMetadataService, sessionDataService, httpSession);
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

		   // caso particular
		   if (currentMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
			 Menu nextMenu = menuService.findMenuById(currentMenu.getNextMenuId());
			 currentState.setIdMenu(nextMenu.getId());
			 menuService.saveCurrentState(currentState);
			 return MessageUtils.getMenuText(nextMenu);

		   }
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
			 return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces, request));
		   }

		   if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

			 nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), request.getPhoneNumber()));
		   }
		   if (nextMenu.getCode().equalsIgnoreCase(ConstantUtils.MENU_REGISTER_CONFIRMATION_CODE)) {

			 utenteRequest = operationMetadataService.createUtenteByMetadatas(request, currentState.getLocation());
			 String details = operationMetadataService.getRegisterConfirmationData(utenteRequest);
			 nextMenu.setDescription(MessageFormat.format(nextMenu.getDescription(), details));

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
			 return MessageFormat.format(MessageUtils.getMenuText(nextMenu), getDistrictsMenu(Integer.parseInt(request.getText()), allProvinces, request));
		   }
		   return MessageUtils.getMenuText(nextMenu);
	     }

       }

       private void resetSession(CurrentState currentState, MenuService menuService) {
	     currentState.setIdMenu(1);
	     currentState.setActive(false);
	     currentState.setLocation(LocationType.MENU_PRINCIPAL.getCode());
	     menuService.saveCurrentState(currentState);
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

	     if (ussdRequest == null || !ussdRequest.getText().equalsIgnoreCase("#")) {

		   startIndex = 0;
		   lastIndex = pagingSize;
		   districtList = allProvinces.stream().filter(p -> p.getId() == (idProvince)).findFirst().get().getDistricts();

		   String menuText = getFormatedDistrictByList(districtList.subList(startIndex, lastIndex));

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
	     int key = 1;
	     for (District item : list) {
		   menuDistricts += key + ". " + item.getDescription() + "\n";
		   System.out.println(menuDistricts);
		   mapDistricts.put(String.valueOf(key), item);
		   key++;
	     }

	     return menuDistricts;
       }

       @Override
       public String recoverSession(UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService operationMetadataService) {
	     currentState.setSessionId(request.getSessionId());
	     menuService.saveCurrentState(currentState);
	     operationMetadataService.updateOperationMetadataSessionId(currentState.getSessionId(), request.getPhoneNumber());
	     Menu menu = menuService.findMenuById(currentState.getIdMenu());

	     if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_CELLNUMBER_FROM_SESSION_CODE)) {

		   menu.setDescription(MessageFormat.format(menu.getDescription(), currentState.getPhoneNumber()));
	     } else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_PROVINCES_CODE)) {
		   return MessageFormat.format(MessageUtils.getMenuText(menu), getProvincesMenu());
	     } else if (menu.getCode().equalsIgnoreCase(ConstantUtils.MENU_DISTRICTS_CODE)) {
		   // TODO: arrumar
		   resetSession(currentState, menuService);
	     }

	     return MessageUtils.getMenuText(menu);
       }

}
