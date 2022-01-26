package mz.org.fgh.vmmc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.client.RestClient;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.District;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.model.Province;
import mz.org.fgh.vmmc.repository.CurrentStateRepository;
import mz.org.fgh.vmmc.repository.MenuRepository;

@Service
public class MenuManagerService {

	@Autowired
	MenuRepository menuRepo;

	@Autowired
	CurrentStateRepository stateRepo;

	public Optional<Menu> getCurrentMenuBySessionId(String sessionId) {
		List<CurrentState> states = menuRepo.findCurrentStateBySessionIdAndId(sessionId);
		return !states.isEmpty() ? Optional.of(menuRepo.findById(states.get(0).getIdMenu()).get())
				:Optional.empty();
	}

	public Optional<Menu> getCurrentMenuBySessionId(Long menuId, String inputText) {
		List<Menu> menus = menuRepo.findByMenuIdAndCode(menuId, inputText);
		return !menus.isEmpty() ? Optional.of(menus.get(0))
				:Optional.empty();
	}
	
	public List<Menu> getAllMenus() {
		return menuRepo.findAll();

	}
	
	public Menu findMenuById(Long menuId) {
		return menuRepo.findById(menuId).get();
	}

	public Long saveCurrentState(CurrentState currentState) {
		CurrentState state = stateRepo.findBySessionId(currentState.getSessionId());
		if (state == null) {
			return stateRepo.save(currentState).getId();
		} else {
			state.setIdMenu(currentState.getIdMenu());
			state.setSessionId(currentState.getSessionId());
			return stateRepo.save(state).getId();
		}

	}
	
	private String getProvincesMenu() {
		String provinces = "CON Seleccione a provincia \n";

		try {
			List<Province>  allProvinces = RestClient.getInstance().getAllProvinces();
			for (Province province : allProvinces) {
				provinces += province.getId() + ". " + province.getDescription() + "\n";

			}
		} catch (Exception e) {
			// substitur por log4j
			System.out.println("ocorreu erro: " + e);
		}
		return provinces;
	}

	private String getDistrictsMenu(int idProvincia , List<Province> allProvinces) {

		Province matchingObject = (Province) allProvinces.stream().filter(p -> p.getId() == (idProvincia)).findFirst()
				.get();
		String districts = "CON Seleccione o distrito \n";
		for (District province : matchingObject.getDistricts()) {
			districts += province.getId() + ". " + province.getDescription() + "\n";

		}
		return districts;
	}

}
