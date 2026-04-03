package offeria.rfq_service.service;

import offeria.rfq_service.domain.entity.Rfq;
import offeria.rfq_service.messaging.RfqProducer;
import offeria.rfq_service.repository.RfqRepository;
import offeria.rfq_service.web.dto.RfqRequest;
import offeria.rfq_service.web.dto.RfqResponse;
import offeria.rfq_service.web.mapper.RfqMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RfqServiceTest {

    @Mock
    private RfqRepository rfqRepository;

    @Mock
    private RfqMapper rfqMapper;

    @Mock
    private RfqProducer rfqProducer;

    @InjectMocks
    private RfqService rfqService;

    private RfqRequest rfqRequest;
    private Rfq rfq;
    private RfqResponse rfqResponse;

    @BeforeEach
    void setUp() {
        rfqRequest = RfqRequest.builder()
                .title("Test RFQ")
                .clientName("Test Client")
                .build();

        rfq = Rfq.builder()
                .id(UUID.randomUUID())
                .title("Test RFQ")
                .clientName("Test Client")
                .build();

        rfqResponse = RfqResponse.builder()
                .id(rfq.getId())
                .title(rfq.getTitle())
                .clientName(rfq.getClientName())
                .offerNumber("OFF-20240101-0001")
                .build();
    }

    @Test
    void createRfq_ShouldReturnResponse_WhenValidRequest() {
        // Arrange
        when(rfqMapper.toEntity(any(RfqRequest.class))).thenReturn(rfq);
        when(rfqRepository.save(any(Rfq.class))).thenReturn(rfq);
        when(rfqMapper.toResponse(any(Rfq.class))).thenReturn(rfqResponse);
        when(rfqRepository.count()).thenReturn(0L);

        // Act
        RfqResponse result = rfqService.createRfq(rfqRequest);

        // Assert
        assertNotNull(result);
        assertEquals(rfqResponse.getTitle(), result.getTitle());
        verify(rfqRepository).save(any(Rfq.class));
        verify(rfqProducer).publishRfqCreated(any(RfqResponse.class));
    }
}
