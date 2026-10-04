package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.domain.entity.Contract;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.repository.ContractRepository;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.web.dto.contract.ContractRequest;
import offeria.rfq_service.web.dto.contract.ContractResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ProjectRepository projectRepository;

    private ContractService contractService;

    @BeforeEach
    void setUp() {
        contractService = new ContractService(
                contractRepository,
                projectRepository
        );
    }

    @Test
    void createShouldNormalizeCodeAndPreserveArabicName() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        ContractRequest request = new ContractRequest(
                " hfy-2026 ",
                "Halfaya Contract",
                "عقد الحلفاية",
                null
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository
                .existsByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "HFY-2026"
                ))
                .thenReturn(false);

        when(contractRepository.save(any(Contract.class)))
                .thenAnswer(invocation -> {
                    Contract contract =
                            invocation.getArgument(0);

                    contract.setId(UUID.randomUUID());
                    contract.setCreatedAt(
                            LocalDateTime.now()
                    );
                    contract.setUpdatedAt(
                            LocalDateTime.now()
                    );

                    return contract;
                });

        ContractResponse response =
                contractService.create(
                        clientId,
                        projectId,
                        request
                );

        assertEquals(
                projectId,
                response.projectId()
        );
        assertEquals(
                "HFY-2026",
                response.code()
        );
        assertEquals(
                "Halfaya Contract",
                response.nameEn()
        );
        assertEquals(
                "عقد الحلفاية",
                response.nameAr()
        );
        assertTrue(response.active());
    }

    @Test
    void createShouldRejectProjectFromWrongClient() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        ContractRequest request = new ContractRequest(
                "HFY-2026",
                "Halfaya Contract",
                null,
                true
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> contractService.create(
                        clientId,
                        projectId,
                        request
                )
        );

        verify(contractRepository, never())
                .save(any(Contract.class));
    }

    @Test
    void createShouldRejectDuplicateCodeForProject() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        ContractRequest request = new ContractRequest(
                "hfy-2026",
                "Halfaya Contract",
                null,
                true
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository
                .existsByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "HFY-2026"
                ))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> contractService.create(
                                clientId,
                                projectId,
                                request
                        )
                );

        assertEquals(
                "Contract code already exists for project: HFY-2026",
                exception.getMessage()
        );

        verify(contractRepository, never())
                .save(any(Contract.class));
    }

    @Test
    void getByIdShouldRejectContractFromDifferentProject() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID contractId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> contractService.getById(
                        clientId,
                        projectId,
                        contractId
                )
        );
    }

    @Test
    void getAllShouldReturnOnlyProjectContracts() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        Contract first = buildContract(
                UUID.randomUUID(),
                project,
                "HFY-001"
        );

        Contract second = buildContract(
                UUID.randomUUID(),
                project,
                "HFY-002"
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findAllByProjectId(
                projectId
        )).thenReturn(List.of(first, second));

        List<ContractResponse> result =
                contractService.getAll(
                        clientId,
                        projectId
                );

        assertEquals(2, result.size());
        assertEquals(
                "HFY-001",
                result.get(0).code()
        );
        assertEquals(
                "HFY-002",
                result.get(1).code()
        );
    }

    @Test
    void updateShouldRejectCodeOwnedByAnotherContract() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID contractId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        Contract current = buildContract(
                contractId,
                project,
                "HFY-001"
        );

        Contract duplicate = buildContract(
                UUID.randomUUID(),
                project,
                "HFY-002"
        );

        ContractRequest request = new ContractRequest(
                "hfy-002",
                "Updated Contract",
                "عقد محدث",
                true
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(current));

        when(contractRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "HFY-002"
                ))
                .thenReturn(Optional.of(duplicate));

        assertThrows(
                IllegalArgumentException.class,
                () -> contractService.update(
                        clientId,
                        projectId,
                        contractId,
                        request
                )
        );

        verify(contractRepository, never())
                .save(any(Contract.class));
    }

    @Test
    void updateShouldAllowContractToKeepItsCode() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID contractId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        Contract current = buildContract(
                contractId,
                project,
                "HFY-001"
        );

        ContractRequest request = new ContractRequest(
                " hfy-001 ",
                "Updated Contract",
                "العقد المحدث",
                false
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(current));

        when(contractRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "HFY-001"
                ))
                .thenReturn(Optional.of(current));

        when(contractRepository.save(current))
                .thenReturn(current);

        ContractResponse response =
                contractService.update(
                        clientId,
                        projectId,
                        contractId,
                        request
                );

        assertEquals(
                "HFY-001",
                response.code()
        );
        assertEquals(
                "Updated Contract",
                response.nameEn()
        );
        assertEquals(
                "العقد المحدث",
                response.nameAr()
        );
        assertFalse(response.active());
    }

    private Project buildProject(
            UUID clientId,
            UUID projectId
    ) {
        Client client = Client.builder()
                .id(clientId)
                .code("CLIENT")
                .nameEn("Client")
                .active(true)
                .build();

        return Project.builder()
                .id(projectId)
                .client(client)
                .code("HFY")
                .nameEn("Halfaya")
                .active(true)
                .build();
    }

    private Contract buildContract(
            UUID contractId,
            Project project,
            String code
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Contract.builder()
                .id(contractId)
                .project(project)
                .code(code)
                .nameEn("Contract " + code)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
