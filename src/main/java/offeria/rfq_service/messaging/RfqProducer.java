package offeria.rfq_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.rfq_service.web.dto.RfqResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka Producer to publish RFQ-related events.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RfqProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String RFQ_CREATED_TOPIC = "rfq.created";

    /**
     * Publishes rfq.created event to Kafka.
     * @param rfq The created RFQ data.
     */
    public void publishRfqCreated(RfqResponse rfq) {
        log.info("Publishing RFQ created event for RFQ ID: {}", rfq.getId());
        kafkaTemplate.send(RFQ_CREATED_TOPIC, rfq.getId().toString(), rfq);
    }
}
