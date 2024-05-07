package mz.org.fgh.vmmc;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mz.org.fgh.vmmc.controller.VmmcService;
import mz.org.fgh.vmmc.utils.ConstantUtils;

@SpringBootApplication(scanBasePackages = "mz.org.fgh")
@RestController
@Api(tags = "USSD API Documentation")
public class VodacomRestController {
	 Logger logger = Logger.getLogger(VmmcService.class.getName());
	private VmmcService service;

	public VodacomRestController(VmmcService service) {
		this.service = service;
	}

	@PostMapping(path = "vmmcUssd")
    @ApiOperation("Return Ussd Menus")
	public String ussdIngress(@RequestParam String sessionId, @RequestParam String serviceCode,
			@RequestParam String phoneNumber, @RequestParam String text) {
		try {
			return service.invokeVmmcUssdService(sessionId, serviceCode, phoneNumber, text);
		} catch (Throwable e) {
			 logger.error("[VodacomRestController.ussdIngress] Ocorreu um erro inesperado: "+ e);
			 return ConstantUtils.MESSAGE_UNEXPECTED_ERROR;
		  }
		
	}

}
