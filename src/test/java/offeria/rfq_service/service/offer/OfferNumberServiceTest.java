package offeria.rfq_service.service.offer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferNumberServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    private OfferNumberService offerNumberService;

    @BeforeEach
    void setUp() {
        offerNumberService =
                new OfferNumberService(entityManager);
    }

    @Test
    void allocateNextNumberShouldUseDatabaseSequence() {
        when(entityManager.createNativeQuery(
                "SELECT nextval('offer_number_seq')"
        )).thenReturn(query);

        when(query.getSingleResult())
                .thenReturn(3198L);

        long result =
                offerNumberService.allocateNextNumber();

        assertEquals(3198L, result);

        verify(entityManager).createNativeQuery(
                "SELECT nextval('offer_number_seq')"
        );

        verify(query).getSingleResult();
    }

    @Test
    void allocateNextNumberShouldReturnDifferentSequenceValues() {
        when(entityManager.createNativeQuery(
                "SELECT nextval('offer_number_seq')"
        )).thenReturn(query);

        when(query.getSingleResult())
                .thenReturn(3198L)
                .thenReturn(3199L);

        long first =
                offerNumberService.allocateNextNumber();

        long second =
                offerNumberService.allocateNextNumber();

        assertEquals(3198L, first);
        assertEquals(3199L, second);
        assertNotEquals(first, second);
    }

    @Test
    void formatShouldReturnCanonicalOfferIdentifier() {
        assertEquals(
                "Offer#3198",
                offerNumberService.format(3198L)
        );
    }

    @Test
    void formatShouldRejectZeroOrNegativeNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> offerNumberService.format(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> offerNumberService.format(-1)
        );
    }
}
