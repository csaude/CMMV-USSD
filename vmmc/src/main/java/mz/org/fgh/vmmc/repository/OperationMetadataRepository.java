package mz.org.fgh.vmmc.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mz.org.fgh.vmmc.model.OperationMetadata;


public interface OperationMetadataRepository extends JpaRepository<OperationMetadata, Long> {

    public OperationMetadata findBySessionIdAndMenuIdAndOperationType(String sessionId, long menuId, String operationType);

    public List<OperationMetadata> findBySessionIdAndOperationType(String sessionId, String operationType);

    @Modifying
    @Transactional
    @Query(value = "update OperationMetadata m set m.sessionId =:sessionId where m.phoneNumber=:phoneNumber")
    public void updateOperationMetadataSessionId(@Param("sessionId") String sessionId,@Param("phoneNumber")  String phoneNumber);

}
