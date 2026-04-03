package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.messaging.RfqProducer;
import offeria.rfq_service.repository.RfqRepository;
import offeria.rfq_service.web.dto.RfqRequest;
import offeria.rfq_service.web.dto.RfqResponse;
import offeria.rfq_service.web.mapper.RfqMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing RFQ business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RfqService {

    private final RfqRepository rfqRepository;
    private final RfqMapper rfqMapper;
    private final RfqProducer rfqProducer;

    /**
     * Creates a new RFQ, generates metadata, and publishes an event.
     * @param request Creation data.
     * @return Created RFQ response.
     */
    @Transactional
    public RfqResponse createRfq(RfqRequest request) {
        log.info("Creating new RFQ with title: {}", request.getTitle());

        Rfq rfq = rfqMapper.toEntity(request);
        
        // Automatic generation logic
        rfq.setOfferNumber(generateOfferNumber());
        rfq.setFolderName(generateFolderName(rfq.getOfferNumber(), rfq.getClientName()));
        rfq.setStatus("PENDING");

        Rfq savedRfq = rfqRepository.save(rfq);
        RfqResponse response = rfqMapper.toResponse(savedRfq);

        // Publish event to Kafka
        rfqProducer.publishRfqCreated(response);

        return response;
    }

    /**
     * Gets all RFQs.
     * @return List of RFQ responses.
     */
    @Transactional(readOnly = true)
    public List<RfqResponse> getAllRfqs() {
        return rfqRepository.findAll().stream()
                .map(rfqMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets RFQ by ID.
     * @param id RFQ ID.
     * @return RFQ response.
     */
    @Transactional(readOnly = true)
    public RfqResponse getRfqById(UUID id) {
        Rfq rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RFQ not found with ID: " + id));
        return rfqMapper.toResponse(rfq);
    }

    /**
     * Generates a unique offer number: OFF-YYYYMMDD-COUNT
     */
    private String generateOfferNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = rfqRepository.count() + 1;
        return String.format("OFF-%s-%04d", datePart, count);
    }

    /**
     * Generates a structured folder name: [OFFER_NUMBER]_[CLIENT_NAME]
     */
    private String generateFolderName(String offerNumber, String clientName) {
        String cleanClientName = clientName.replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%s", offerNumber, cleanClientName);
    }
}
