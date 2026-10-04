package offeria.rfq_service.service;

import lombok.RequiredArgsConstructor;
import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.web.dto.client.ClientRequest;
import offeria.rfq_service.web.dto.client.ClientResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public ClientResponse create(ClientRequest request) {
        String code = normalizeCode(request.code());

        if (clientRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    "Client code already exists: " + code
            );
        }

        Client client = Client.builder()
                .code(code)
                .nameEn(request.nameEn().trim())
                .nameAr(normalizeOptional(request.nameAr()))
                .active(request.active() == null || request.active())
                .build();

        return toResponse(clientRepository.save(client));
    }

    public ClientResponse getById(UUID id) {
        return toResponse(findClient(id));
    }

    public List<ClientResponse> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = findClient(id);
        String code = normalizeCode(request.code());

        clientRepository.findByCodeIgnoreCase(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Client code already exists: " + code
                    );
                });

        client.setCode(code);
        client.setNameEn(request.nameEn().trim());
        client.setNameAr(normalizeOptional(request.nameAr()));

        if (request.active() != null) {
            client.setActive(request.active());
        }

        return toResponse(clientRepository.save(client));
    }

    private Client findClient(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Client not found: " + id
                        )
                );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getCode(),
                client.getNameEn(),
                client.getNameAr(),
                client.isActive(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
