package offeria.rfq_service.service.offer;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfferNumberService {

    private static final String OFFER_PREFIX = "Offer#";

    private final EntityManager entityManager;

    @Transactional
    public long allocateNextNumber() {
        Number value = (Number) entityManager
                .createNativeQuery(
                        "SELECT nextval('offer_number_seq')"
                )
                .getSingleResult();

        return value.longValue();
    }

    @Transactional
    public void initializeNextNumber(long nextOfferNumber) {
        if (nextOfferNumber < 1) {
            throw new IllegalArgumentException(
                    "Next offer number must be greater than zero"
            );
        }

        entityManager
                .createNativeQuery(
                        "SELECT setval('offer_number_seq', :value, false)"
                )
                .setParameter("value", nextOfferNumber)
                .getSingleResult();
    }

    public String format(long offerNumber) {
        if (offerNumber < 1) {
            throw new IllegalArgumentException(
                    "Offer number must be greater than zero"
            );
        }

        return OFFER_PREFIX + offerNumber;
    }
}
