package mz.org.fgh.vmmc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mz.org.fgh.vmmc.model.OperationMetadata;


public interface OperationMetadataRepository extends JpaRepository<OperationMetadata, Long> {

    public OperationMetadata findByCurrentStateIdAndMenuIdAndOperationType(long  currentStateId, long menuId, String operationType);

    public List<OperationMetadata> findByCurrentStateIdAndOperationType(long currentStateId, String operationType);

  /*  @Modifying
    @Transactional
    @Query(value = "update OperationMetadata m set m.sessionId =:sessionId where m.phoneNumber=:phoneNumber")
    public void updateOperationMetadataSessionId(@Param("sessionId") String sessionId,@Param("phoneNumber")  String phoneNumber);*/

}
