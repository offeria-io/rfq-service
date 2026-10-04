package offeria.rfq_service.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.rfq_service.service.AttentionContactService;
import offeria.rfq_service.web.dto.contact.AttentionContactRequest;
import offeria.rfq_service.web.dto.contact.AttentionContactResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/contacts")
@RequiredArgsConstructor
public class AttentionContactController {

    private final AttentionContactService contactService;

    @PostMapping
    public ResponseEntity<AttentionContactResponse> create(
            @PathVariable UUID clientId,
            @Valid @RequestBody AttentionContactRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contactService.create(clientId, request));
    }

    @GetMapping
    public ResponseEntity<List<AttentionContactResponse>> getAll(
            @PathVariable UUID clientId
    ) {
        return ResponseEntity.ok(
                contactService.getAll(clientId)
        );
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<AttentionContactResponse> get(
            @PathVariable UUID clientId,
            @PathVariable UUID contactId
    ) {
        return ResponseEntity.ok(
                contactService.get(clientId, contactId)
        );
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<AttentionContactResponse> update(
            @PathVariable UUID clientId,
            @PathVariable UUID contactId,
            @Valid @RequestBody AttentionContactRequest request
    ) {
        return ResponseEntity.ok(
                contactService.update(
                        clientId,
                        contactId,
                        request
                )
        );
    }
}
