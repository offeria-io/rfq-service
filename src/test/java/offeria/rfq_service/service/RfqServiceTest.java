package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.AttentionContact;
import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.domain.entity.Contract;
import offeria.rfq_service.domain.entity.Project;
import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.domain.entity.WorkLocation;
import offeria.rfq_service.messaging.RfqProducer;
import offeria.rfq_service.repository.AttentionContactRepository;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.repository.ContractRepository;
import offeria.rfq_service.repository.ProjectRepository;
import offeria.rfq_service.repository.RfqRepository;
import offeria.rfq_service.repository.WorkLocationRepository;
import offeria.rfq_service.service.offer.OfferFolderNameService;
import offeria.rfq_service.service.offer.OfferNumberService;
import offeria.rfq_service.web.dto.RfqRequest;
import offeria.rfq_service.web.dto.RfqResponse;
import offeria.rfq_service.web.mapper.RfqMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RfqServiceTest {

    @Mock
    private RfqRepository rfqRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private WorkLocationRepository workLocationRepository;

    @Mock
    private AttentionContactRepository attentionContactRepository;

    @Mock
    private OfferNumberService offerNumberService;

    @Mock
    private OfferFolderNameService offerFolderNameService;

    @Mock
    private RfqMapper rfqMapper;

    @Mock
    private RfqProducer rfqProducer;

    private RfqService rfqService;

    private UUID clientId;
    private UUID projectId;
    private UUID contractId;
    private UUID workLocationId;
    private UUID attentionContactId;

    private Client client;
    private Project project;
    private Contract contract;
    private WorkLocation workLocation;
    private AttentionContact attentionContact;

    private RfqRequest request;

    @BeforeEach
    void setUp() {
        rfqService = new RfqService(
                rfqRepository,
                clientRepository,
                projectRepository,
                contractRepository,
                workLocationRepository,
                attentionContactRepository,
                offerNumberService,
                offerFolderNameService,
                rfqMapper,
                rfqProducer
        );

        clientId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        contractId = UUID.randomUUID();
        workLocationId = UUID.randomUUID();
        attentionContactId = UUID.randomUUID();

        client = Client.builder()
                .id(clientId)
                .code("CPECC")
                .nameEn("CPECC")
                .active(true)
                .build();

        project = Project.builder()
                .id(projectId)
                .client(client)
                .code("RML")
                .nameEn("Rumaila")
                .active(true)
                .build();

        contract = Contract.builder()
                .id(contractId)
                .project(project)
                .code("CTR-001")
                .nameEn("Main Contract")
                .active(true)
                .build();

        workLocation = WorkLocation.builder()
                .id(workLocationId)
                .project(project)
                .code("SITE-A")
                .nameEn("Site A")
                .active(true)
                .build();

        attentionContact = AttentionContact.builder()
                .id(attentionContactId)
                .client(client)
                .name("John Doe")
                .active(true)
                .build();

        request = RfqRequest.builder()
                .rfqNumber("YJRML-2026-0078")
                .title("Civil engineering consumables")
                .clientId(clientId)
                .projectId(projectId)
                .contractId(contractId)
                .workLocationId(workLocationId)
                .attentionContactId(attentionContactId)
                .build();
    }

    @Test
    void createRfq_ShouldCreateCanonicalRfq_WhenReferencesAreValid() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(contract));

        when(workLocationRepository.findByIdAndProjectId(
                workLocationId,
                projectId
        )).thenReturn(Optional.of(workLocation));

        when(attentionContactRepository.findByIdAndClientId(
                attentionContactId,
                clientId
        )).thenReturn(Optional.of(attentionContact));

        when(rfqRepository.existsByClientIdAndRfqNumberIgnoreCase(
                clientId,
                "YJRML-2026-0078"
        )).thenReturn(false);

        when(offerNumberService.allocateNextNumber())
                .thenReturn(3198L);

        when(offerNumberService.format(3198L))
                .thenReturn("Offer#3198");

        when(offerFolderNameService.generate(
                3198L,
                "RML",
                "YJRML-2026-0078",
                "Civil engineering consumables"
        )).thenReturn(
                "Offer#3198 RML YJRML-2026-0078 Civil engineering consumables"
        );

        when(rfqRepository.save(any(Rfq.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RfqResponse expectedResponse = RfqResponse.builder()
                .rfqNumber("YJRML-2026-0078")
                .offerNumber("Offer#3198")
                .title("Civil engineering consumables")
                .clientId(clientId)
                .projectId(projectId)
                .folderName(
                        "Offer#3198 RML YJRML-2026-0078 Civil engineering consumables"
                )
                .status("PENDING")
                .build();

        when(rfqMapper.toResponse(any(Rfq.class)))
                .thenReturn(expectedResponse);

        RfqResponse result =
                rfqService.createRfq(request);

        assertNotNull(result);
        assertEquals(
                "Offer#3198",
                result.getOfferNumber()
        );
        assertEquals(
                "YJRML-2026-0078",
                result.getRfqNumber()
        );

        ArgumentCaptor<Rfq> captor =
                ArgumentCaptor.forClass(Rfq.class);

        verify(rfqRepository).save(captor.capture());

        Rfq saved = captor.getValue();

        assertEquals(
                "YJRML-2026-0078",
                saved.getRfqNumber()
        );
        assertEquals(
                "Offer#3198",
                saved.getOfferNumber()
        );
        assertEquals(
                "Civil engineering consumables",
                saved.getTitle()
        );
        assertEquals(client, saved.getClient());
        assertEquals(project, saved.getProject());
        assertEquals(contract, saved.getContract());
        assertEquals(
                workLocation,
                saved.getWorkLocation()
        );
        assertEquals(
                attentionContact,
                saved.getAttentionContact()
        );
        assertEquals(
                "CPECC",
                saved.getClientName()
        );
        assertEquals(
                "Offer#3198 RML YJRML-2026-0078 Civil engineering consumables",
                saved.getFolderName()
        );
        assertEquals(
                "PENDING",
                saved.getStatus()
        );

        verify(rfqProducer)
                .publishRfqCreated(expectedResponse);
    }

    @Test
    void createRfq_ShouldAllowOptionalReferencesToBeAbsent() {
        request.setContractId(null);
        request.setWorkLocationId(null);
        request.setAttentionContactId(null);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(rfqRepository.existsByClientIdAndRfqNumberIgnoreCase(
                clientId,
                "YJRML-2026-0078"
        )).thenReturn(false);

        when(offerNumberService.allocateNextNumber())
                .thenReturn(3199L);

        when(offerNumberService.format(3199L))
                .thenReturn("Offer#3199");

        when(offerFolderNameService.generate(
                3199L,
                "RML",
                "YJRML-2026-0078",
                "Civil engineering consumables"
        )).thenReturn(
                "Offer#3199 RML YJRML-2026-0078 Civil engineering consumables"
        );

        when(rfqRepository.save(any(Rfq.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RfqResponse response = RfqResponse.builder()
                .offerNumber("Offer#3199")
                .build();

        when(rfqMapper.toResponse(any(Rfq.class)))
                .thenReturn(response);

        rfqService.createRfq(request);

        ArgumentCaptor<Rfq> captor =
                ArgumentCaptor.forClass(Rfq.class);

        verify(rfqRepository).save(captor.capture());

        Rfq saved = captor.getValue();

        assertNull(saved.getContract());
        assertNull(saved.getWorkLocation());
        assertNull(saved.getAttentionContact());

        verify(
                contractRepository,
                never()
        ).findByIdAndProjectId(any(), any());

        verify(
                workLocationRepository,
                never()
        ).findByIdAndProjectId(any(), any());

        verify(
                attentionContactRepository,
                never()
        ).findByIdAndClientId(any(), any());
    }

    @Test
    void createRfq_ShouldReject_WhenClientDoesNotExist() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "Client not found: " + clientId,
                exception.getMessage()
        );

        verify(rfqRepository, never())
                .save(any());

        verify(offerNumberService, never())
                .allocateNextNumber();
    }

    @Test
    void createRfq_ShouldReject_WhenProjectDoesNotBelongToClient() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "Project does not belong to client",
                exception.getMessage()
        );

        verify(rfqRepository, never())
                .save(any());

        verify(offerNumberService, never())
                .allocateNextNumber();
    }

    @Test
    void createRfq_ShouldReject_WhenContractDoesNotBelongToProject() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "Contract does not belong to project",
                exception.getMessage()
        );

        verify(rfqRepository, never())
                .save(any());

        verify(offerNumberService, never())
                .allocateNextNumber();
    }

    @Test
    void createRfq_ShouldReject_WhenWorkLocationDoesNotBelongToProject() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(contract));

        when(workLocationRepository.findByIdAndProjectId(
                workLocationId,
                projectId
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "Work location does not belong to project",
                exception.getMessage()
        );

        verify(rfqRepository, never())
                .save(any());

        verify(offerNumberService, never())
                .allocateNextNumber();
    }

    @Test
    void createRfq_ShouldReject_WhenAttentionContactDoesNotBelongToClient() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(contract));

        when(workLocationRepository.findByIdAndProjectId(
                workLocationId,
                projectId
        )).thenReturn(Optional.of(workLocation));

        when(attentionContactRepository.findByIdAndClientId(
                attentionContactId,
                clientId
        )).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "Attention contact does not belong to client",
                exception.getMessage()
        );

        verify(rfqRepository, never())
                .save(any());

        verify(offerNumberService, never())
                .allocateNextNumber();
    }

    @Test
    void createRfq_ShouldRejectDuplicateRfqNumberBeforeOfferAllocation() {
        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(contract));

        when(workLocationRepository.findByIdAndProjectId(
                workLocationId,
                projectId
        )).thenReturn(Optional.of(workLocation));

        when(attentionContactRepository.findByIdAndClientId(
                attentionContactId,
                clientId
        )).thenReturn(Optional.of(attentionContact));

        when(rfqRepository.existsByClientIdAndRfqNumberIgnoreCase(
                clientId,
                "YJRML-2026-0078"
        )).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rfqService.createRfq(request)
                );

        assertEquals(
                "RFQ number already exists for client: YJRML-2026-0078",
                exception.getMessage()
        );

        verify(offerNumberService, never())
                .allocateNextNumber();

        verify(rfqRepository, never())
                .save(any());

        verify(rfqProducer, never())
                .publishRfqCreated(any());
    }

    @Test
    void createRfq_ShouldNormalizeRfqNumberAndTitle() {
        request.setRfqNumber(
                "   YJRML-2026-0078   "
        );

        request.setTitle(
                "  Civil   engineering   consumables  "
        );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(projectRepository.findByIdAndClientId(
                projectId,
                clientId
        )).thenReturn(Optional.of(project));

        when(contractRepository.findByIdAndProjectId(
                contractId,
                projectId
        )).thenReturn(Optional.of(contract));

        when(workLocationRepository.findByIdAndProjectId(
                workLocationId,
                projectId
        )).thenReturn(Optional.of(workLocation));

        when(attentionContactRepository.findByIdAndClientId(
                attentionContactId,
                clientId
        )).thenReturn(Optional.of(attentionContact));

        when(rfqRepository.existsByClientIdAndRfqNumberIgnoreCase(
                clientId,
                "YJRML-2026-0078"
        )).thenReturn(false);

        when(offerNumberService.allocateNextNumber())
                .thenReturn(3200L);

        when(offerNumberService.format(3200L))
                .thenReturn("Offer#3200");

        when(offerFolderNameService.generate(
                3200L,
                "RML",
                "YJRML-2026-0078",
                "Civil engineering consumables"
        )).thenReturn(
                "Offer#3200 RML YJRML-2026-0078 Civil engineering consumables"
        );

        when(rfqRepository.save(any(Rfq.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(rfqMapper.toResponse(any(Rfq.class)))
                .thenReturn(
                        RfqResponse.builder()
                                .offerNumber("Offer#3200")
                                .build()
                );

        rfqService.createRfq(request);

        ArgumentCaptor<Rfq> captor =
                ArgumentCaptor.forClass(Rfq.class);

        verify(rfqRepository).save(captor.capture());

        assertEquals(
                "YJRML-2026-0078",
                captor.getValue().getRfqNumber()
        );

        assertEquals(
                "Civil engineering consumables",
                captor.getValue().getTitle()
        );
    }
}
