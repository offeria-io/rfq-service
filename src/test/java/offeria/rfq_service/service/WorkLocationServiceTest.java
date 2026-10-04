package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.domain.entity.WorkLocation;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.repository.WorkLocationRepository;
import offeria.rfq_service.web.dto.location.WorkLocationRequest;
import offeria.rfq_service.web.dto.location.WorkLocationResponse;
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
class WorkLocationServiceTest {

    @Mock
    private WorkLocationRepository workLocationRepository;

    @Mock
    private ProjectRepository projectRepository;

    private WorkLocationService workLocationService;

    @BeforeEach
    void setUp() {
        workLocationService = new WorkLocationService(
                workLocationRepository,
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

        WorkLocationRequest request =
                new WorkLocationRequest(
                        " cpf ",
                        "Central Processing Facility",
                        "منشأة المعالجة المركزية",
                        null
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .existsByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "CPF"
                ))
                .thenReturn(false);

        when(workLocationRepository.save(
                any(WorkLocation.class)
        )).thenAnswer(invocation -> {
            WorkLocation workLocation =
                    invocation.getArgument(0);

            workLocation.setId(UUID.randomUUID());
            workLocation.setCreatedAt(
                    LocalDateTime.now()
            );
            workLocation.setUpdatedAt(
                    LocalDateTime.now()
            );

            return workLocation;
        });

        WorkLocationResponse response =
                workLocationService.create(
                        clientId,
                        projectId,
                        request
                );

        assertEquals(
                projectId,
                response.projectId()
        );
        assertEquals(
                "CPF",
                response.code()
        );
        assertEquals(
                "Central Processing Facility",
                response.nameEn()
        );
        assertEquals(
                "منشأة المعالجة المركزية",
                response.nameAr()
        );
        assertTrue(response.active());
    }

    @Test
    void createShouldRejectProjectFromWrongClient() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        WorkLocationRequest request =
                new WorkLocationRequest(
                        "CPF",
                        "Central Processing Facility",
                        null,
                        true
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> workLocationService.create(
                        clientId,
                        projectId,
                        request
                )
        );

        verify(workLocationRepository, never())
                .save(any(WorkLocation.class));
    }

    @Test
    void createShouldRejectDuplicateCodeForProject() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        WorkLocationRequest request =
                new WorkLocationRequest(
                        "cpf",
                        "Central Processing Facility",
                        null,
                        true
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .existsByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "CPF"
                ))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> workLocationService.create(
                                clientId,
                                projectId,
                                request
                        )
                );

        assertEquals(
                "Work location code already exists for project: CPF",
                exception.getMessage()
        );

        verify(workLocationRepository, never())
                .save(any(WorkLocation.class));
    }

    @Test
    void getByIdShouldRejectLocationFromDifferentProject() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID workLocationId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .findByIdAndProjectId(
                        workLocationId,
                        projectId
                ))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> workLocationService.getById(
                        clientId,
                        projectId,
                        workLocationId
                )
        );
    }

    @Test
    void getAllShouldReturnOnlyProjectLocations() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        WorkLocation first =
                buildWorkLocation(
                        UUID.randomUUID(),
                        project,
                        "CPF"
                );

        WorkLocation second =
                buildWorkLocation(
                        UUID.randomUUID(),
                        project,
                        "WELL-01"
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .findAllByProjectId(projectId))
                .thenReturn(List.of(first, second));

        List<WorkLocationResponse> result =
                workLocationService.getAll(
                        clientId,
                        projectId
                );

        assertEquals(2, result.size());
        assertEquals(
                "CPF",
                result.get(0).code()
        );
        assertEquals(
                "WELL-01",
                result.get(1).code()
        );
    }

    @Test
    void updateShouldRejectCodeOwnedByAnotherLocation() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID workLocationId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        WorkLocation current =
                buildWorkLocation(
                        workLocationId,
                        project,
                        "CPF"
                );

        WorkLocation duplicate =
                buildWorkLocation(
                        UUID.randomUUID(),
                        project,
                        "WELL-01"
                );

        WorkLocationRequest request =
                new WorkLocationRequest(
                        "well-01",
                        "Updated Location",
                        "موقع محدث",
                        true
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .findByIdAndProjectId(
                        workLocationId,
                        projectId
                ))
                .thenReturn(Optional.of(current));

        when(workLocationRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "WELL-01"
                ))
                .thenReturn(Optional.of(duplicate));

        assertThrows(
                IllegalArgumentException.class,
                () -> workLocationService.update(
                        clientId,
                        projectId,
                        workLocationId,
                        request
                )
        );

        verify(workLocationRepository, never())
                .save(any(WorkLocation.class));
    }

    @Test
    void updateShouldAllowLocationToKeepItsCode() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        UUID workLocationId = UUID.randomUUID();

        Project project = buildProject(
                clientId,
                projectId
        );

        WorkLocation current =
                buildWorkLocation(
                        workLocationId,
                        project,
                        "CPF"
                );

        WorkLocationRequest request =
                new WorkLocationRequest(
                        " cpf ",
                        "Updated CPF",
                        "منشأة محدثة",
                        false
                );

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(workLocationRepository
                .findByIdAndProjectId(
                        workLocationId,
                        projectId
                ))
                .thenReturn(Optional.of(current));

        when(workLocationRepository
                .findByProjectIdAndCodeIgnoreCase(
                        projectId,
                        "CPF"
                ))
                .thenReturn(Optional.of(current));

        when(workLocationRepository.save(current))
                .thenReturn(current);

        WorkLocationResponse response =
                workLocationService.update(
                        clientId,
                        projectId,
                        workLocationId,
                        request
                );

        assertEquals(
                "CPF",
                response.code()
        );
        assertEquals(
                "Updated CPF",
                response.nameEn()
        );
        assertEquals(
                "منشأة محدثة",
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

    private WorkLocation buildWorkLocation(
            UUID workLocationId,
            Project project,
            String code
    ) {
        LocalDateTime now = LocalDateTime.now();

        return WorkLocation.builder()
                .id(workLocationId)
                .project(project)
                .code(code)
                .nameEn("Location " + code)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
