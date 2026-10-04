package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.web.dto.project.ProjectRequest;
import offeria.rfq_service.web.dto.project.ProjectResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public ProjectResponse create(
            UUID clientId,
            ProjectRequest request
    ) {
        Client client = findClient(clientId);
        String code = normalizeCode(request.code());

        if (projectRepository.existsByClientIdAndCodeIgnoreCase(
                clientId,
                code
        )) {
            throw new IllegalArgumentException(
                    "Project code already exists for client: " + code
            );
        }

        Project project = Project.builder()
                .client(client)
                .code(code)
                .nameEn(request.nameEn().trim())
                .nameAr(normalizeOptional(request.nameAr()))
                .active(request.active() == null || request.active())
                .build();

        return toResponse(projectRepository.save(project));
    }

    public ProjectResponse getById(
            UUID clientId,
            UUID projectId
    ) {
        findClient(clientId);

        return toResponse(
                findProject(clientId, projectId)
        );
    }

    public List<ProjectResponse> getAllByClient(
            UUID clientId
    ) {
        findClient(clientId);

        return projectRepository.findAllByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse update(
            UUID clientId,
            UUID projectId,
            ProjectRequest request
    ) {
        findClient(clientId);

        Project project =
                findProject(clientId, projectId);

        String code = normalizeCode(request.code());

        projectRepository
                .findByClientIdAndCodeIgnoreCase(clientId, code)
                .filter(existing ->
                        !existing.getId().equals(projectId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Project code already exists for client: "
                                    + code
                    );
                });

        project.setCode(code);
        project.setNameEn(request.nameEn().trim());
        project.setNameAr(
                normalizeOptional(request.nameAr())
        );

        if (request.active() != null) {
            project.setActive(request.active());
        }

        return toResponse(
                projectRepository.save(project)
        );
    }

    private Client findClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Client not found: " + clientId
                        )
                );
    }

    private Project findProject(
            UUID clientId,
            UUID projectId
    ) {
        return projectRepository
                .findByIdAndClientId(projectId, clientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Project not found for client: "
                                        + projectId
                        )
                );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getClient().getId(),
                project.getCode(),
                project.getNameEn(),
                project.getNameAr(),
                project.isActive(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
