package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.web.dto.project.ProjectRequest;
import offeria.rfq_service.web.dto.project.ProjectResponse;
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
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientRepository clientRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(
                projectRepository,
                clientRepository
        );
    }

    @Test
    void createShouldNormalizeCodeAndPreserveArabicName() {
        UUID clientId = UUID.randomUUID();
        Client client = buildClient(clientId);

        ProjectRequest request = new ProjectRequest(
                " hfy ",
                "Halfaya Project",
                "مشروع الحلفاية",
                null
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository
                .existsByClientIdAndCodeIgnoreCase(
                        clientId,
                        "HFY"
                ))
                .thenReturn(false);

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> {
                    Project project =
                            invocation.getArgument(0);

                    project.setId(UUID.randomUUID());
                    project.setCreatedAt(LocalDateTime.now());
                    project.setUpdatedAt(LocalDateTime.now());

                    return project;
                });

        ProjectResponse response =
                projectService.create(clientId, request);

        assertEquals(clientId, response.clientId());
        assertEquals("HFY", response.code());
        assertEquals(
                "Halfaya Project",
                response.nameEn()
        );
        assertEquals(
                "مشروع الحلفاية",
                response.nameAr()
        );
        assertTrue(response.active());
    }

    @Test
    void createShouldRejectUnknownClient() {
        UUID clientId = UUID.randomUUID();

        ProjectRequest request = new ProjectRequest(
                "HFY",
                "Halfaya",
                null,
                true
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(
                        clientId,
                        request
                )
        );

        verify(projectRepository, never())
                .save(any(Project.class));
    }

    @Test
    void createShouldRejectDuplicateCodeForSameClient() {
        UUID clientId = UUID.randomUUID();
        Client client = buildClient(clientId);

        ProjectRequest request = new ProjectRequest(
                "hfy",
                "Halfaya",
                null,
                true
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository
                .existsByClientIdAndCodeIgnoreCase(
                        clientId,
                        "HFY"
                ))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> projectService.create(
                                clientId,
                                request
                        )
                );

        assertEquals(
                "Project code already exists for client: HFY",
                exception.getMessage()
        );

        verify(projectRepository, never())
                .save(any(Project.class));
    }

    @Test
    void getByIdShouldRejectProjectFromDifferentClient() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(clientRepository.findById(clientId))
                .thenReturn(
                        Optional.of(buildClient(clientId))
                );

        when(projectRepository
                .findByIdAndClientId(projectId, clientId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.getById(
                        clientId,
                        projectId
                )
        );
    }

    @Test
    void getAllShouldReturnOnlyClientProjects() {
        UUID clientId = UUID.randomUUID();
        Client client = buildClient(clientId);

        Project first =
                buildProject(
                        UUID.randomUUID(),
                        client,
                        "HFY"
                );

        Project second =
                buildProject(
                        UUID.randomUUID(),
                        client,
                        "RML"
                );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository
                .findAllByClientId(clientId))
                .thenReturn(List.of(first, second));

        List<ProjectResponse> result =
                projectService.getAllByClient(clientId);

        assertEquals(2, result.size());
        assertEquals("HFY", result.get(0).code());
        assertEquals("RML", result.get(1).code());
    }

    @Test
    void updateShouldRejectCodeOwnedByAnotherProject() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Client client = buildClient(clientId);

        Project current =
                buildProject(projectId, client, "HFY");

        Project duplicate =
                buildProject(
                        UUID.randomUUID(),
                        client,
                        "RML"
                );

        ProjectRequest request = new ProjectRequest(
                "rml",
                "Updated Project",
                "مشروع محدث",
                true
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository
                .findByIdAndClientId(
                        projectId,
                        clientId
                ))
                .thenReturn(Optional.of(current));

        when(projectRepository
                .findByClientIdAndCodeIgnoreCase(
                        clientId,
                        "RML"
                ))
                .thenReturn(Optional.of(duplicate));

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.update(
                        clientId,
                        projectId,
                        request
                )
        );

        verify(projectRepository, never())
                .save(any(Project.class));
    }

    @Test
    void updateShouldAllowProjectToKeepItsCode() {
        UUID clientId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Client client = buildClient(clientId);

        Project current =
                buildProject(projectId, client, "HFY");

        ProjectRequest request = new ProjectRequest(
                "hfy",
                "Updated Halfaya",
                "الحلفاية المحدث",
                false
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository
                .findByIdAndClientId(
                        projectId,
                        clientId
                ))
                .thenReturn(Optional.of(current));

        when(projectRepository
                .findByClientIdAndCodeIgnoreCase(
                        clientId,
                        "HFY"
                ))
                .thenReturn(Optional.of(current));

        when(projectRepository.save(current))
                .thenReturn(current);

        ProjectResponse response =
                projectService.update(
                        clientId,
                        projectId,
                        request
                );

        assertEquals("HFY", response.code());
        assertEquals(
                "Updated Halfaya",
                response.nameEn()
        );
        assertEquals(
                "الحلفاية المحدث",
                response.nameAr()
        );
        assertFalse(response.active());
    }

    private Client buildClient(UUID id) {
        return Client.builder()
                .id(id)
                .code("CPECC")
                .nameEn("Client")
                .active(true)
                .build();
    }

    private Project buildProject(
            UUID id,
            Client client,
            String code
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Project.builder()
                .id(id)
                .client(client)
                .code(code)
                .nameEn(code + " Project")
                .nameAr(code + " مشروع")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
