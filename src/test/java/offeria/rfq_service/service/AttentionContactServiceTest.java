package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.AttentionContact;
import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.repository.AttentionContactRepository;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.web.dto.contact.AttentionContactRequest;
import offeria.rfq_service.web.dto.contact.AttentionContactResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttentionContactServiceTest {

    @Mock
    private AttentionContactRepository contactRepository;

    @Mock
    private ClientRepository clientRepository;

    private AttentionContactService service;

    @BeforeEach
    void setUp() {
        service = new AttentionContactService(
                contactRepository,
                clientRepository
        );
    }

    @Test
    void createShouldCreateContactForValidClient() {
        UUID clientId = UUID.randomUUID();
        Client client = buildClient(clientId);

        AttentionContactRequest request =
                new AttentionContactRequest(
                        " Ahmed Ali ",
                        " Project Manager ",
                        " ahmed@example.com ",
                        " +9647700000000 ",
                        null
                );

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(contactRepository.save(any()))
                .thenAnswer(invocation -> {
                    AttentionContact contact =
                            invocation.getArgument(0);

                    contact.setId(UUID.randomUUID());
                    contact.setCreatedAt(LocalDateTime.now());
                    contact.setUpdatedAt(LocalDateTime.now());

                    return contact;
                });

        AttentionContactResponse response =
                service.create(clientId, request);

        assertEquals(clientId, response.clientId());
        assertEquals("Ahmed Ali", response.name());
        assertEquals("Project Manager", response.title());
        assertEquals("ahmed@example.com", response.email());
        assertEquals("+9647700000000", response.phone());
        assertTrue(response.active());
    }

    @Test
    void createShouldRejectInvalidClient() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.empty());

        AttentionContactRequest request =
                new AttentionContactRequest(
                        "Ahmed",
                        null,
                        null,
                        null,
                        true
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.create(clientId, request)
        );

        verify(contactRepository, never()).save(any());
    }

    @Test
    void getAllShouldReturnClientContacts() {
        UUID clientId = UUID.randomUUID();
        Client client = buildClient(clientId);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(contactRepository
                .findAllByClientIdOrderByNameAsc(clientId))
                .thenReturn(List.of(
                        buildContact(client, "Ahmed"),
                        buildContact(client, "Mohammed")
                ));

        List<AttentionContactResponse> result =
                service.getAll(clientId);

        assertEquals(2, result.size());
        assertEquals("Ahmed", result.get(0).name());
        assertEquals("Mohammed", result.get(1).name());
    }

    @Test
    void getShouldRejectContactFromDifferentClient() {
        UUID clientId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        Client client = buildClient(clientId);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(contactRepository
                .findByIdAndClientId(contactId, clientId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.get(clientId, contactId)
        );
    }

    @Test
    void updateShouldUpdateContactFields() {
        UUID clientId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        Client client = buildClient(clientId);
        AttentionContact contact =
                buildContact(client, "Old Name");
        contact.setId(contactId);

        when(clientRepository.findById(clientId))
                .thenReturn(Optional.of(client));

        when(contactRepository
                .findByIdAndClientId(contactId, clientId))
                .thenReturn(Optional.of(contact));

        when(contactRepository.save(contact))
                .thenReturn(contact);

        AttentionContactRequest request =
                new AttentionContactRequest(
                        "New Name",
                        "Director",
                        "new@example.com",
                        "+9647800000000",
                        false
                );

        AttentionContactResponse response =
                service.update(
                        clientId,
                        contactId,
                        request
                );

        assertEquals("New Name", response.name());
        assertEquals("Director", response.title());
        assertEquals("new@example.com", response.email());
        assertEquals("+9647800000000", response.phone());
        assertFalse(response.active());
    }

    private Client buildClient(UUID id) {
        return Client.builder()
                .id(id)
                .code("CPECC")
                .nameEn("CPECC")
                .nameAr("سي بي إي سي سي")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AttentionContact buildContact(
            Client client,
            String name
    ) {
        return AttentionContact.builder()
                .id(UUID.randomUUID())
                .client(client)
                .name(name)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
