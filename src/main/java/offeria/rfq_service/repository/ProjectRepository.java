package offeria.rfq_service.repository;

import offeria.rfq_service.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findAllByClientId(UUID clientId);

    Optional<Project> findByIdAndClientId(UUID id, UUID clientId);

    Optional<Project> findByClientIdAndCodeIgnoreCase(
            UUID clientId,
            String code
    );

    boolean existsByClientIdAndCodeIgnoreCase(
            UUID clientId,
            String code
    );
}
