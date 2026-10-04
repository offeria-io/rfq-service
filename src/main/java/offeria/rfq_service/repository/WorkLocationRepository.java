package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkLocationRepository
        extends JpaRepository<WorkLocation, UUID> {

    List<WorkLocation> findAllByProjectId(UUID projectId);

    Optional<WorkLocation> findByIdAndProjectId(
            UUID id,
            UUID projectId
    );

    Optional<WorkLocation> findByProjectIdAndCodeIgnoreCase(
            UUID projectId,
            String code
    );

    boolean existsByProjectIdAndCodeIgnoreCase(
            UUID projectId,
            String code
    );
}
