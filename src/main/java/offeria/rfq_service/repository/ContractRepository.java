package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findAllByProjectId(UUID projectId);

    Optional<Contract> findByIdAndProjectId(
            UUID id,
            UUID projectId
    );

    Optional<Contract> findByProjectIdAndCodeIgnoreCase(
            UUID projectId,
            String code
    );

    boolean existsByProjectIdAndCodeIgnoreCase(
            UUID projectId,
            String code
    );
}
