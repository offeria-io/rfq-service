package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.domain.entity.WorkLocation;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.repository.WorkLocationRepository;
import offeria.rfq_service.web.dto.location.WorkLocationRequest;
import offeria.rfq_service.web.dto.location.WorkLocationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkLocationService {

    private final WorkLocationRepository workLocationRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public WorkLocationResponse create(
            UUID clientId,
            UUID projectId,
            WorkLocationRequest request
    ) {
        Project project = findProject(clientId, projectId);
        String code = normalizeCode(request.code());

        if (workLocationRepository
                .existsByProjectIdAndCodeIgnoreCase(
                        projectId,
                        code
                )) {
            throw new IllegalArgumentException(
                    "Work location code already exists for project: "
                            + code
            );
        }

        WorkLocation workLocation = WorkLocation.builder()
                .project(project)
                .code(code)
                .nameEn(request.nameEn().trim())
                .nameAr(normalizeOptional(request.nameAr()))
                .active(request.active() == null || request.active())
                .build();

        return toResponse(
                workLocationRepository.save(workLocation)
        );
    }

    public WorkLocationResponse getById(
            UUID clientId,
            UUID projectId,
            UUID workLocationId
    ) {
        findProject(clientId, projectId);

        return toResponse(
                findWorkLocation(
                        projectId,
                        workLocationId
                )
        );
    }

    public List<WorkLocationResponse> getAll(
            UUID clientId,
            UUID projectId
    ) {
        findProject(clientId, projectId);

        return workLocationRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WorkLocationResponse update(
            UUID clientId,
            UUID projectId,
            UUID workLocationId,
            WorkLocationRequest request
    ) {
        findProject(clientId, projectId);

        WorkLocation workLocation =
                findWorkLocation(
                        projectId,
                        workLocationId
                );

        String code = normalizeCode(request.code());

        workLocationRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        code
                )
                .filter(existing ->
                        !existing.getId().equals(
                                workLocationId
                        )
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Work location code already exists for project: "
                                    + code
                    );
                });

        workLocation.setCode(code);
        workLocation.setNameEn(
                request.nameEn().trim()
        );
        workLocation.setNameAr(
                normalizeOptional(request.nameAr())
        );

        if (request.active() != null) {
            workLocation.setActive(request.active());
        }

        return toResponse(
                workLocationRepository.save(workLocation)
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

    private WorkLocation findWorkLocation(
            UUID projectId,
            UUID workLocationId
    ) {
        return workLocationRepository
                .findByIdAndProjectId(
                        workLocationId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Work location not found for project: "
                                        + workLocationId
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

    private WorkLocationResponse toResponse(
            WorkLocation workLocation
    ) {
        return new WorkLocationResponse(
                workLocation.getId(),
                workLocation.getProject().getId(),
                workLocation.getCode(),
                workLocation.getNameEn(),
                workLocation.getNameAr(),
                workLocation.isActive(),
                workLocation.getCreatedAt(),
                workLocation.getUpdatedAt()
        );
    }
}
