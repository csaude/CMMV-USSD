package mz.org.fgh.vmmc.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.model.SmsConfiguration;
import mz.org.fgh.vmmc.repository.SmsConfigurationRepository;

@Service
public class SmsConfigurationService {

	@Autowired
	SmsConfigurationRepository smsConfigurationRepo;

	public Map<String, SmsConfiguration> findSmsConfigurationByCode(String code) {
		List<SmsConfiguration> configurationList = smsConfigurationRepo.findByCode(code);
		Map<String, SmsConfiguration> map = configurationList.stream()
				.collect(Collectors.toMap(SmsConfiguration::getAttrName, Function.identity()));
		return map;
	}

}
