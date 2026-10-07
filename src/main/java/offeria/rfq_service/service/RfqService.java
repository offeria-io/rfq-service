package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RfqService {

    private static final String INITIAL_STATUS = "PENDING";

    private final RfqRepository rfqRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final WorkLocationRepository workLocationRepository;
    private final AttentionContactRepository attentionContactRepository;

    private final OfferNumberService offerNumberService;
    private final OfferFolderNameService offerFolderNameService;

    private final RfqMapper rfqMapper;
    private final RfqProducer rfqProducer;

    @Transactional
    public RfqResponse createRfq(RfqRequest request) {
        String rfqNumber = normalizeRequired(
                request.getRfqNumber(),
                "RFQ number"
        );

        String title = normalizeRequired(
                request.getTitle(),
                "Title"
        );

        Client client = clientRepository
                .findById(request.getClientId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Client not found: " + request.getClientId()
                ));

        Project project = projectRepository
                .findByIdAndClientId(
                        request.getProjectId(),
                        client.getId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project does not belong to client"
                ));

        Contract contract = resolveContract(
                request.getContractId(),
                project.getId()
        );

        WorkLocation workLocation = resolveWorkLocation(
                request.getWorkLocationId(),
                project.getId()
        );

        AttentionContact attentionContact =
                resolveAttentionContact(
                        request.getAttentionContactId(),
                        client.getId()
                );

        if (rfqRepository.existsByClientIdAndRfqNumberIgnoreCase(
                client.getId(),
                rfqNumber
        )) {
            throw new IllegalArgumentException(
                    "RFQ number already exists for client: "
                            + rfqNumber
            );
        }

        long allocatedOfferNumber =
                offerNumberService.allocateNextNumber();

        String offerNumber =
                offerNumberService.format(allocatedOfferNumber);

        String folderName =
                offerFolderNameService.generate(
                        allocatedOfferNumber,
                        project.getCode(),
                        rfqNumber,
                        title
                );

        Rfq rfq = Rfq.builder()
                .rfqNumber(rfqNumber)
                .offerNumber(offerNumber)
                .title(title)
                .clientName(client.getNameEn())
                .client(client)
                .project(project)
                .contract(contract)
                .workLocation(workLocation)
                .attentionContact(attentionContact)
                .folderName(folderName)
                .status(INITIAL_STATUS)
                .build();

        Rfq savedRfq = rfqRepository.save(rfq);

        RfqResponse response =
                rfqMapper.toResponse(savedRfq);

        log.info(
                "Created RFQ {} with offer number {}",
                savedRfq.getRfqNumber(),
                savedRfq.getOfferNumber()
        );

        rfqProducer.publishRfqCreated(response);

        return response;
    }

    @Transactional(readOnly = true)
    public List<RfqResponse> getAllRfqs() {
        return rfqRepository.findAll()
                .stream()
                .map(rfqMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RfqResponse getRfqById(UUID id) {
        Rfq rfq = rfqRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "RFQ not found with ID: " + id
                ));

        return rfqMapper.toResponse(rfq);
    }

    private Contract resolveContract(
            UUID contractId,
            UUID projectId
    ) {
        if (contractId == null) {
            return null;
        }

        return contractRepository
                .findByIdAndProjectId(
                        contractId,
                        projectId
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contract does not belong to project"
                ));
    }

    private WorkLocation resolveWorkLocation(
            UUID workLocationId,
            UUID projectId
    ) {
        if (workLocationId == null) {
            return null;
        }

        return workLocationRepository
                .findByIdAndProjectId(
                        workLocationId,
                        projectId
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Work location does not belong to project"
                ));
    }

    private AttentionContact resolveAttentionContact(
            UUID attentionContactId,
            UUID clientId
    ) {
        if (attentionContactId == null) {
            return null;
        }

        return attentionContactRepository
                .findByIdAndClientId(
                        attentionContactId,
                        clientId
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Attention contact does not belong to client"
                ));
    }

    private String normalizeRequired(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }
}
