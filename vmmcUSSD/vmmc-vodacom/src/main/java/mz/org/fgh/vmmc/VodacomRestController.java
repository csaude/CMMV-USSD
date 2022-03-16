package mz.org.fgh.vmmc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mz.org.fgh.vmmc.controller.VmmcService;

@SpringBootApplication(scanBasePackages = "mz.org.fgh")
@RestController
public class VodacomRestController {

	private VmmcService service;

	public VodacomRestController(VmmcService service) {
		this.service = service;
	}

	@PostMapping(path = "vmmcUssd")
	public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) throws Throwable {

		return service.invokeVmmcUssdService(sessionId, serviceCode, phoneNumber, text);
	}

}
