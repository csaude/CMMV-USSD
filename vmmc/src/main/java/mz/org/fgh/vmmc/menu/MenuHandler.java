package mz.org.fgh.vmmc.menu;

import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;

@Service
public interface MenuHandler {

	public String handleMenu(UssdRequest ussdRequest,CurrentState currentState, MenuService menuService, OperationMetadataService  operationMetadataService) throws Throwable;
	
	public String recoverSession( CurrentState currentState, MenuService menuService) throws Throwable;

}
