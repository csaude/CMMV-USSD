package mz.org.fgh.vmmc.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mz.org.fgh.vmmc.inout.UssdRequest;
import mz.org.fgh.vmmc.model.OperationMetadata;
import mz.org.fgh.vmmc.model.Utente;
import mz.org.fgh.vmmc.repository.OperationMetadataRepository;

@Service
public class OperationMetadataService {

    @Autowired
    OperationMetadataRepository operationMetadataRepository;

    public OperationMetadata saveOperationMetadata(OperationMetadata operationMetadata) {
	OperationMetadata metadata = operationMetadataRepository.findBySessionIdAndMenuIdAndOperationType(operationMetadata.getSessionId(), operationMetadata.getMenu().getId(),
		operationMetadata.getOperationType());

	if (metadata == null) {
	    return operationMetadataRepository.save(operationMetadata);
	} else {
	    metadata.setAttrName(operationMetadata.getAttrName());
	    metadata.setAttrValue(operationMetadata.getAttrValue());
	    return operationMetadataRepository.save(metadata);
	}
    }

    public Map<String, OperationMetadata> getMetadatasByOperationTypeAndSessionId(String sessionId, String operationType) {

	List<OperationMetadata> metadataList = operationMetadataRepository.findBySessionIdAndOperationType(sessionId, operationType);
	Map<String, OperationMetadata> map = metadataList.stream().collect(Collectors.toMap(OperationMetadata::getAttrName, Function.identity()));
	return map;
    }

    public Utente createUtenteByMetadatas(UssdRequest request, String operationType) {

	Map<String, OperationMetadata> metadatas = getMetadatasByOperationTypeAndSessionId(request.getSessionId(), operationType);

	Utente utente = new Utente(metadatas.get("firstNames").getAttrValue(), metadatas.get("lastNames").getAttrValue(), "", // TODO: birth date
		metadatas.get("cellNumber") == null ? request.getPhoneNumber() : metadatas.get("cellNumber").getAttrValue(), metadatas.get("hasPartner").getAttrValue(),
		metadatas.get("age").getAttrValue(), metadatas.get("provinceId").getAttrValue(), metadatas.get("districtId").getAttrValue());

	return utente;

    }

    public void updateOperationMetadataSessionId(String sessionId, String phoneNumber) {
	 operationMetadataRepository.updateOperationMetadataSessionId(sessionId,phoneNumber);
    }
}
