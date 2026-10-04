package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import offeria.rfq_service.domain.entity.AttentionContact;
import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.repository.AttentionContactRepository;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.web.dto.contact.AttentionContactRequest;
import offeria.rfq_service.web.dto.contact.AttentionContactResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttentionContactService {

    private final AttentionContactRepository contactRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public AttentionContactResponse create(
            UUID clientId,
            AttentionContactRequest request
    ) {
        Client client = findClient(clientId);

        AttentionContact contact = AttentionContact.builder()
                .client(client)
                .name(request.name().trim())
                .title(normalizeOptional(request.title()))
                .email(normalizeOptional(request.email()))
                .phone(normalizeOptional(request.phone()))
                .active(request.active() == null || request.active())
                .build();

        return toResponse(contactRepository.save(contact));
    }

    public AttentionContactResponse get(
            UUID clientId,
            UUID contactId
    ) {
        findClient(clientId);
        return toResponse(findContact(clientId, contactId));
    }

    public List<AttentionContactResponse> getAll(UUID clientId) {
        findClient(clientId);

        return contactRepository
                .findAllByClientIdOrderByNameAsc(clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AttentionContactResponse update(
            UUID clientId,
            UUID contactId,
            AttentionContactRequest request
    ) {
        findClient(clientId);

        AttentionContact contact =
                findContact(clientId, contactId);

        contact.setName(request.name().trim());
        contact.setTitle(normalizeOptional(request.title()));
        contact.setEmail(normalizeOptional(request.email()));
        contact.setPhone(normalizeOptional(request.phone()));

        if (request.active() != null) {
            contact.setActive(request.active());
        }

        return toResponse(contactRepository.save(contact));
    }

    private Client findClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Client not found: " + clientId
                        )
                );
    }

    private AttentionContact findContact(
            UUID clientId,
            UUID contactId
    ) {
        return contactRepository
                .findByIdAndClientId(contactId, clientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Attention contact not found: " + contactId
                        )
                );
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private AttentionContactResponse toResponse(
            AttentionContact contact
    ) {
        return new AttentionContactResponse(
                contact.getId(),
                contact.getClient().getId(),
                contact.getName(),
                contact.getTitle(),
                contact.getEmail(),
                contact.getPhone(),
                contact.isActive(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }
}
