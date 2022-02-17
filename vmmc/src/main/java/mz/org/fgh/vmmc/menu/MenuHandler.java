package mz.org.fgh.vmmc.menu;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.CurrentState;
import mz.org.fgh.vmmc.service.MenuService;
import mz.org.fgh.vmmc.service.OperationMetadataService;
import mz.org.fgh.vmmc.service.SessionDataService;

@Service
public interface MenuHandler {

	public String handleMenu(UssdRequest ussdRequest,CurrentState currentState, MenuService menuService, OperationMetadataService  operationMetadataService,SessionDataService sessionDataService,HttpSession session) throws Throwable;
	
	public String recoverSession( UssdRequest request, CurrentState currentState, MenuService menuService, OperationMetadataService  operationMetadataService) throws Throwable;

}
