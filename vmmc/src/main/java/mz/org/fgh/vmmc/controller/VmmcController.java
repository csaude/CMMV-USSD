package mz.org.fgh.vmmc.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.menu.MenuHandler;
import mz.org.fgh.vmmc.utils.MessageUtils;

@RestController
public class VmmcController {

	@Autowired
	MenuHandler menuHandler;

	@PostMapping(path = "vmmcUssd")
	public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) throws IOException {

		UssdRequest ussdRequest = new UssdRequest(sessionId, serviceCode, phoneNumber, MessageUtils.removeAccent(text));
		return menuHandler.handleResponse(ussdRequest);

	}
 
}
