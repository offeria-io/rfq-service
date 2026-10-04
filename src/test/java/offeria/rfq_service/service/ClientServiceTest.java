package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.Client;
import offeria.rfq_service.repository.ClientRepository;
import offeria.rfq_service.web.dto.client.ClientRequest;
import offeria.rfq_service.web.dto.client.ClientResponse;
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
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);
    }

    @Test
    void createShouldNormalizeCodeAndPreserveArabicName() {
        ClientRequest request = new ClientRequest(
                " cpecc ",
                "China Petroleum Engineering",
                "الشركة الصينية للهندسة البترولية",
                null
        );

        when(clientRepository.existsByCodeIgnoreCase("CPECC"))
                .thenReturn(false);

        when(clientRepository.save(any(Client.class)))
                .thenAnswer(invocation -> {
                    Client client = invocation.getArgument(0);
                    client.setId(UUID.randomUUID());
                    client.setCreatedAt(LocalDateTime.now());
                    client.setUpdatedAt(LocalDateTime.now());
                    return client;
                });

        ClientResponse response = clientService.create(request);

        assertEquals("CPECC", response.code());
        assertEquals(
                "China Petroleum Engineering",
                response.nameEn()
        );
        assertEquals(
                "الشركة الصينية للهندسة البترولية",
                response.nameAr()
        );
        assertTrue(response.active());

        verify(clientRepository)
                .existsByCodeIgnoreCase("CPECC");

        verify(clientRepository)
                .save(any(Client.class));
    }

    @Test
    void createShouldRejectDuplicateCode() {
        ClientRequest request = new ClientRequest(
                "CPECC",
                "Client",
                "العميل",
                true
        );

        when(clientRepository.existsByCodeIgnoreCase("CPECC"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> clientService.create(request)
                );

        assertEquals(
                "Client code already exists: CPECC",
                exception.getMessage()
        );

        verify(clientRepository, never())
                .save(any(Client.class));
    }

    @Test
    void getByIdShouldReturnClient() {
        UUID id = UUID.randomUUID();

        Client client = buildClient(id, "CNOOC");

        when(clientRepository.findById(id))
                .thenReturn(Optional.of(client));

        ClientResponse response =
                clientService.getById(id);

        assertEquals(id, response.id());
        assertEquals("CNOOC", response.code());
    }

    @Test
    void getByIdShouldRejectUnknownClient() {
        UUID id = UUID.randomUUID();

        when(clientRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getById(id)
        );
    }

    @Test
    void getAllShouldReturnClients() {
        Client first =
                buildClient(UUID.randomUUID(), "CPECC");

        Client second =
                buildClient(UUID.randomUUID(), "CNOOC");

        when(clientRepository.findAll())
                .thenReturn(List.of(first, second));

        List<ClientResponse> result =
                clientService.getAll();

        assertEquals(2, result.size());
        assertEquals("CPECC", result.get(0).code());
        assertEquals("CNOOC", result.get(1).code());
    }

    @Test
    void updateShouldRejectCodeOwnedByAnotherClient() {
        UUID id = UUID.randomUUID();

        Client current =
                buildClient(id, "CPECC");

        Client duplicate =
                buildClient(UUID.randomUUID(), "CNOOC");

        ClientRequest request =
                new ClientRequest(
                        "CNOOC",
                        "Updated Client",
                        "عميل محدث",
                        true
                );

        when(clientRepository.findById(id))
                .thenReturn(Optional.of(current));

        when(clientRepository.findByCodeIgnoreCase("CNOOC"))
                .thenReturn(Optional.of(duplicate));

        assertThrows(
                IllegalArgumentException.class,
                () -> clientService.update(id, request)
        );

        verify(clientRepository, never())
                .save(any(Client.class));
    }

    @Test
    void updateShouldAllowExistingClientToKeepItsCode() {
        UUID id = UUID.randomUUID();

        Client current =
                buildClient(id, "CPECC");

        ClientRequest request =
                new ClientRequest(
                        "cpecc",
                        "Updated English Name",
                        "الاسم العربي المحدث",
                        false
                );

        when(clientRepository.findById(id))
                .thenReturn(Optional.of(current));

        when(clientRepository.findByCodeIgnoreCase("CPECC"))
                .thenReturn(Optional.of(current));

        when(clientRepository.save(current))
                .thenReturn(current);

        ClientResponse response =
                clientService.update(id, request);

        assertEquals("CPECC", response.code());
        assertEquals(
                "Updated English Name",
                response.nameEn()
        );
        assertEquals(
                "الاسم العربي المحدث",
                response.nameAr()
        );
        assertFalse(response.active());
    }

    private Client buildClient(
            UUID id,
            String code
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Client.builder()
                .id(id)
                .code(code)
                .nameEn(code + " English")
                .nameAr(code + " عربي")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
