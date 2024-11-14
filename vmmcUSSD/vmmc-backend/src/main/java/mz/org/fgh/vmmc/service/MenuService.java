package mz.org.fgh.vmmc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.model.Menu;
import mz.org.fgh.vmmc.repository.CurrentStateRepository;
import mz.org.fgh.vmmc.repository.MenuRepository;

@Service
public class MenuService {

	@Autowired
	MenuRepository menuRepo;

	@Autowired
	CurrentStateRepository stateRepo;

	public Menu getCurrentMenuBySessionId(String sessionId, boolean isActive) {
		List<CurrentState> states = menuRepo.findCurrentStateBySessionId(sessionId, isActive);
		return !states.isEmpty() ? menuRepo.findById(states.get(0).getIdMenu()).get() : null;
	}

	public Optional<Menu> getCurrentMenuBySessionId(Long menuId, String inputText) {
		List<Menu> menus = menuRepo.findByMenuIdAndOption(menuId, inputText);
		return !menus.isEmpty() ? Optional.of(menus.get(0)) : Optional.empty();
	}

	public List<Menu> getAllMenus() {
		return menuRepo.findAll();

	}

	public Menu findMenuById(Long menuId) {
		return menuRepo.findById(menuId).get();
	}

	public Long saveCurrentState(CurrentState currentState) {
		CurrentState state = this.findCurrentStateBySessionId(currentState.getSessionId());
		if (state == null) {
			return stateRepo.save(currentState).getId();
		} else {
			state.setIdMenu(currentState.getIdMenu());
			state.setSessionId(currentState.getSessionId());
			state.setLocation(currentState.getLocation());
			state.setCreatedDate(currentState.getCreatedDate());
			state.setPhoneNumber(currentState.getPhoneNumber());
			return stateRepo.save(state).getId();
		}

	}

	public CurrentState findCurrentStateBySessionId(String sessionId) {
		List<CurrentState> states = stateRepo.findBySessionIdAndIsActive(sessionId, true);
		return !states.isEmpty() ? states.get(0) : null;
	}

	public CurrentState findCurrentStateByPhoneNumber(String phoneNumber, boolean isActive) {
		List<CurrentState> states = stateRepo.findByPhoneNumberAndIsActive(phoneNumber, isActive);
		return !states.isEmpty() ? states.get(0) : null;
	}

}
