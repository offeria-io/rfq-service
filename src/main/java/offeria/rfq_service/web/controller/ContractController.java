package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.ContractService;
import offeria.rfq_service.web.dto.contract.ContractRequest;
import offeria.rfq_service.web.dto.contract.ContractResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/clients/{clientId}/projects/{projectId}/contracts"
)
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractResponse> create(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @Valid @RequestBody ContractRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        contractService.create(
                                clientId,
                                projectId,
                                request
                        )
                );
    }

    @GetMapping("/{contractId}")
    public ResponseEntity<ContractResponse> getById(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @PathVariable UUID contractId
    ) {
        return ResponseEntity.ok(
                contractService.getById(
                        clientId,
                        projectId,
                        contractId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<ContractResponse>> getAll(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                contractService.getAll(
                        clientId,
                        projectId
                )
        );
    }

    @PutMapping("/{contractId}")
    public ResponseEntity<ContractResponse> update(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @PathVariable UUID contractId,
            @Valid @RequestBody ContractRequest request
    ) {
        return ResponseEntity.ok(
                contractService.update(
                        clientId,
                        projectId,
                        contractId,
                        request
                )
        );
    }
}
