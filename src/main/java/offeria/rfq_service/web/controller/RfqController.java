package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.RfqService;
import offeria.rfq_service.web.dto.RfqRequest;
import offeria.rfq_service.web.dto.RfqResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for RFQ APIs.
 */
@RestController
@RequestMapping("/api/v1/rfqs")
@RequiredArgsConstructor
public class RfqController {

    private final RfqService rfqService;

    /**
     * Endpoint to create a new RFQ.
     */
    @PostMapping
    public ResponseEntity<RfqResponse> createRfq(@Valid @RequestBody RfqRequest request) {
        return new ResponseEntity<>(rfqService.createRfq(request), HttpStatus.CREATED);
    }

    /**
     * Endpoint to list all RFQs.
     */
    @GetMapping
    public ResponseEntity<List<RfqResponse>> getAllRfqs() {
        return ResponseEntity.ok(rfqService.getAllRfqs());
    }

    /**
     * Endpoint to get a specific RFQ by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RfqResponse> getRfqById(@PathVariable UUID id) {
        return ResponseEntity.ok(rfqService.getRfqById(id));
    }
}
