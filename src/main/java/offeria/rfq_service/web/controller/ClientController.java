package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.ClientService;
import offeria.rfq_service.web.dto.client.ClientRequest;
import offeria.rfq_service.web.dto.client.ClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(
            @Valid @RequestBody ClientRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequest request
    ) {
        return ResponseEntity.ok(
                clientService.update(id, request)
        );
    }
}
