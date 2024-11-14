package mz.org.fgh.vmmc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import mz.org.fgh.vmmc.controller.VmmcService;
import mz.org.fgh.vmmc.inout.UssdOut;
import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.inout.UssdResponse;
import mz.org.fgh.vmmc.utils.ConstantUtils;

@SpringBootApplication(scanBasePackages = "mz.org.fgh")
@RestController
public class VodacomRestController {
	Logger logger = Logger.getLogger(VmmcService.class.getName());
	private VmmcService service;

	public VodacomRestController(VmmcService service) {
		this.service = service;
	}

	/*
	 * @PostMapping(path = "vmmcUssd") public UssdResponse ussdIngress(@RequestParam
	 * String sessionId, @RequestParam String transactionId,
	 * 
	 * @RequestParam String fromField, @RequestParam String toField, @RequestParam
	 * String contentField,
	 * 
	 * @RequestParam String actionField)
	 */
	@PostMapping(path = "vmmcUssd", consumes = "application/json", produces = "application/json")
	public UssdResponse ussdIngress(@RequestBody UssdRequest ussdRequest) {

		String sessionId = ussdRequest.getSession();
		String transactionId = ussdRequest.getTransaction();
		String fromField = ussdRequest.getFrom();
		String toField = ussdRequest.getTo();
		String contentField = ussdRequest.getContent();
		String actionField = ussdRequest.getAction();

		try {

			UssdOut out = service.invokeVmmcUssdService(sessionId, transactionId, fromField, toField, contentField,
					actionField);
			//logger.error(out, null);
			//logger.error(new UssdResponse(out), null);
			return new UssdResponse(out);
		} catch (Throwable e) {
		    OffsetDateTime now = OffsetDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	        String formattedDate = now.format(formatter);
	        
			logger.error("[VodacomRestController.ussdIngress] Ocorreu um erro inesperado: " + e, e);
			UssdResponse response = new UssdResponse();
			response.setFrom(toField);
			response.setTo(fromField);
			response.setSession(sessionId);
			response.setAction("End");
			response.setTransaction(transactionId); 
			response.setDateTime(formattedDate);
			response.setContent(ConstantUtils.MESSAGE_UNEXPECTED_ERROR);
			return response;
		}
	}
 
}
