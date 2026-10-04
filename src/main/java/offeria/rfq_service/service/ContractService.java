package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import offeria.rfq_service.domain.entity.Contract;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.repository.ContractRepository;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.web.dto.contract.ContractRequest;
import offeria.rfq_service.web.dto.contract.ContractResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ContractResponse create(
            UUID clientId,
            UUID projectId,
            ContractRequest request
    ) {
        Project project = findProject(clientId, projectId);
        String code = normalizeCode(request.code());

        if (contractRepository.existsByProjectIdAndCodeIgnoreCase(
                projectId,
                code
        )) {
            throw new IllegalArgumentException(
                    "Contract code already exists for project: " + code
            );
        }

        Contract contract = Contract.builder()
                .project(project)
                .code(code)
                .nameEn(request.nameEn().trim())
                .nameAr(normalizeOptional(request.nameAr()))
                .active(request.active() == null || request.active())
                .build();

        return toResponse(contractRepository.save(contract));
    }

    public ContractResponse getById(
            UUID clientId,
            UUID projectId,
            UUID contractId
    ) {
        findProject(clientId, projectId);

        return toResponse(
                findContract(projectId, contractId)
        );
    }

    public List<ContractResponse> getAll(
            UUID clientId,
            UUID projectId
    ) {
        findProject(clientId, projectId);

        return contractRepository
                .findAllByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ContractResponse update(
            UUID clientId,
            UUID projectId,
            UUID contractId,
            ContractRequest request
    ) {
        findProject(clientId, projectId);

        Contract contract =
                findContract(projectId, contractId);

        String code = normalizeCode(request.code());

        contractRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        code
                )
                .filter(existing ->
                        !existing.getId().equals(contractId)
                )
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Contract code already exists for project: "
                                    + code
                    );
                });

        contract.setCode(code);
        contract.setNameEn(request.nameEn().trim());
        contract.setNameAr(
                normalizeOptional(request.nameAr())
        );

        if (request.active() != null) {
            contract.setActive(request.active());
        }

        return toResponse(
                contractRepository.save(contract)
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

    private Contract findContract(
            UUID projectId,
            UUID contractId
    ) {
        return contractRepository
                .findByIdAndProjectId(
                        contractId,
                        projectId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contract not found for project: "
                                        + contractId
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

    private ContractResponse toResponse(
            Contract contract
    ) {
        return new ContractResponse(
                contract.getId(),
                contract.getProject().getId(),
                contract.getCode(),
                contract.getNameEn(),
                contract.getNameAr(),
                contract.isActive(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }
}
