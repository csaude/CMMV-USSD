package mz.org.fgh.vmmc.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.service.MenuManagerService;

@RestController
public class VmmcController {

	String menuText;
	String sessionId;
	String serviceCode;
	String phoneNumber;
	String text;
	

	@Autowired
	MenuManagerService menuManagerService;

	private Menu handleResponse(String sessionId, String inputText) {
		Menu menu = null;
		// menu corrente
		Optional<Menu> currentMenuOptional = menuManagerService.getCurrentMenuBySessionId(sessionId);
		CurrentState state = new CurrentState(sessionId);

		if (currentMenuOptional.isPresent()) {
			Menu currentMenu = currentMenuOptional.get();
			if (currentMenu.getMenuItems().size() > 1
					|| (currentMenu.getMenuItems().size() == 1 && StringUtils.trim(inputText).equals("0"))) {

				return navegate(currentMenu, inputText, state);
			} else {
				state.setIdMenu(Long.parseLong(currentMenu.getLevel()));
				menuManagerService.saveCurrentState(state);
				return menuManagerService.findMenuById(Long.parseLong(currentMenu.getLevel()));
			}

		} else {
			// seta o prmeiro menu como currentstate
			state.setIdMenu(1);

		}
		// Actualiza ou cria novo
		menuManagerService.saveCurrentState(state);
		return menu;

	}

	private Menu navegate(Menu currentMenu, String inputText, CurrentState state) {
		Optional<Menu> nextMenu = menuManagerService.getCurrentMenuBySessionId(currentMenu.getId(), inputText);
		state.setIdMenu(Long.parseLong(nextMenu.get().getLevel()));
		menuManagerService.saveCurrentState(state);
		return menuManagerService.findMenuById(Long.parseLong(nextMenu.get().getLevel()));
	}

	private String getMenuText(Menu menu) {
		StringBuilder sb = new StringBuilder();

		if (menu != null) {
			sb.append(menu.getDescription()).append("\n");
			for (Menu item : menu.getMenuItems()) {
				sb.append(item.getDescription()).append("\n");
			}
		} else {
			sb.append("CON Bem Vindo ao CMMV \n \n " + " 1. Entrar \n " + " 2. Registar-se");

		}

		return sb.toString();

	}

	@PostMapping(path = "vmmcUssd")
	public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) throws IOException {

		// doGet(request, response);
		this.sessionId = sessionId;
		this.serviceCode = serviceCode;
		this.phoneNumber = phoneNumber;
		this.text = StringUtils.trim(text);
		Menu currentMenu = handleResponse(sessionId, text);

		return getMenuText(currentMenu);

	}

	

	/*
	 * public void handleResponse2() {
	 * 
	 * int level = StringUtils.split(text, "*").length; String[] selectedOptions =
	 * StringUtils.split(text, "*"); String currentOption = level > 0 ?
	 * selectedOptions[level - 1] : text; String option = level == 0 ? text :
	 * selectedOptions[0];
	 * 
	 * // String option =(text =="" || text.length()==1)? text : text.split("*")[0];
	 * 
	 * switch (option) { case (""): menuText = "CON Bem Vindo ao CMMV \n \n " +
	 * " 1. Entrar \n " + " 2. Registar-se";
	 * 
	 * break; case ("1"): if (level == 1) { menuText =
	 * "CON Digite o Codigo unico do utilizador \n 0. Voltar "; break; } else if
	 * (level == 2) { menuText = "CON 1. Marcar Consulta" +
	 * "\n 2.Visualizar a consulta marcada" + "\n 3.Listar US do meu Distrito " +
	 * "\n 4. Mensagens Educativas" + "\n 0.Voltar"; break;
	 * 
	 * } case ("2"): if (level == 1) { menuText =
	 * "CON Preencha o seu nome: \n  0. Voltar"; break; } else if (level == 2) {
	 * menuText = "CON Preencha o seu apelido: \n  0. Voltar"; break; } else if
	 * (level == 3) { menuText = "CON Preencha a Idade: \n 0. Voltar"; break; }
	 * 
	 * else if (level == 4) { menuText =
	 * "CON Indique  o seu numero de telefone: \n 1. Usar o numero corrente \n 2. Introduzir outro numero \n 0. Voltar"
	 * ; break; } else if (level == 5 && currentOption.equalsIgnoreCase("2")) {
	 * menuText = "CON Digite o seu numero de telefone: \n 0. Voltar"; break; } else
	 * if (level == 5 && currentOption.equalsIgnoreCase("1")) { menuText =
	 * "CON   Nummero corrente e:" + phoneNumber +
	 * ": \n 1. Confirmar \n  0.V oltar"; break; }
	 * 
	 * else if (level == 6 && currentOption.equalsIgnoreCase("1")) { menuText =
	 * getProvinces() + " \n 0. Voltar";
	 * 
	 * break; }
	 * 
	 * else if (level == 7) { menuText =
	 * getDistricts(Integer.parseInt(currentOption)) + " Ver mais  \n 0. Voltar";
	 * break; }
	 * 
	 * else if (level == 8) { menuText = "CON Preencha o endereco  \n 0. Voltar";
	 * break; } else if (level == 9) { menuText =
	 * "CON Tem parceiro?  \n 1. Sim \n 2. Nao  \n 0. Voltar"; break; }
	 * 
	 * else if (level == 10 && currentOption.equalsIgnoreCase("1")) { menuText =
	 * "CON Confirmar o registo?  \n 1. Sim \n 2. Nao  \n 0. Voltar"; break; } else
	 * if (level == 10 && currentOption.equalsIgnoreCase("2")) { menuText =
	 * "CON Confirmar o registo?  \n 1. Sim \n 2. Nao  \n 0. Voltar"; break; }
	 * 
	 * else if (level == 11 && currentOption.equalsIgnoreCase("1")) { menuText =
	 * "END O seu cadastro foi feito com sucesso!\n" +
	 * "Será enviado um SMS com o codigo de acesso  a sua conta. " +
	 * " \n digite *123# e introduza o codigo enviado"; break; }
	 * 
	 * else if (level == 11 && currentOption.equalsIgnoreCase("2")) { menuText =
	 * "END O seu cadastro nao foi efectuado!\n" +
	 * " \n digite *123# para efectuaar o cadastro"; break; }
	 * 
	 * }
	 * 
	 * }
	 */
}
