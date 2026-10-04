package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.WorkLocationService;
import offeria.rfq_service.web.dto.location.WorkLocationRequest;
import offeria.rfq_service.web.dto.location.WorkLocationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/clients/{clientId}/projects/{projectId}/work-locations"
)
@RequiredArgsConstructor
public class WorkLocationController {

    private final WorkLocationService workLocationService;

    @PostMapping
    public ResponseEntity<WorkLocationResponse> create(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @Valid @RequestBody WorkLocationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        workLocationService.create(
                                clientId,
                                projectId,
                                request
                        )
                );
    }

    @GetMapping("/{workLocationId}")
    public ResponseEntity<WorkLocationResponse> getById(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @PathVariable UUID workLocationId
    ) {
        return ResponseEntity.ok(
                workLocationService.getById(
                        clientId,
                        projectId,
                        workLocationId
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<WorkLocationResponse>> getAll(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                workLocationService.getAll(
                        clientId,
                        projectId
                )
        );
    }

    @PutMapping("/{workLocationId}")
    public ResponseEntity<WorkLocationResponse> update(
            @PathVariable UUID clientId,
            @PathVariable UUID projectId,
            @PathVariable UUID workLocationId,
            @Valid @RequestBody WorkLocationRequest request
    ) {
        return ResponseEntity.ok(
                workLocationService.update(
                        clientId,
                        projectId,
                        workLocationId,
                        request
                )
        );
    }
}
