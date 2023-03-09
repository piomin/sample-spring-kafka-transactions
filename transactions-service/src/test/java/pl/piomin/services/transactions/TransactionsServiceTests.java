package pl.piomin.services.transactions;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import pl.piomin.services.common.model.Order;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.properties.security.protocol=PLAINTEXT"
}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders"},
        partitions = 1,
        brokerProperties = {
                "offsets.topic.replication.factor=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1"
        },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionsServiceTests {

    private static final Logger LOG = LoggerFactory
            .getLogger(TransactionsServiceTests.class);

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private EmbeddedKafkaBroker kafka;
    @Autowired
    private KafkaTemplate<Long, Order> template;
    @Autowired
    private ConsumerFactory<Long, Order> factory;

    @Test
    @org.junit.jupiter.api.Order(1)
    void sendTransactionFailed() {
        restTemplate.postForObject("/transactions", true, Void.class);

        template.setConsumerFactory(factory);
        ConsumerRecord<Long, Order> rec = template.receive("transactions", 0, 0, Duration.ofSeconds(5));
        assertNull(rec);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void sendTransaction() {
        restTemplate.postForObject("/transactions", false, Void.class);

        template.setConsumerFactory(factory);
        ConsumerRecord<Long, Order> rec = template.receive("transactions", 0, 0, Duration.ofSeconds(5));
        assertNotNull(rec);
        assertNotNull(rec.value());
        assertEquals("NEW", rec.value().getStatus());
    }

//    @Test
//    @org.junit.jupiter.api.Order(2)
//    void accept() throws ExecutionException, InterruptedException, TimeoutException {
//        Order o = new Order(1L, 1L, 2L, 999, "NEW", 1L);
//        SendResult<Long, Order> r = template.executeInTransaction(ko -> ko.send("transactions", o.getId(), o))
//                .get(1000, TimeUnit.MILLISECONDS);;
//        LOG.info("Sent: {}", r.getProducerRecord().value());
//
//        template.setConsumerFactory(factory);
//        ConsumerRecord<Long, Order> rec = template.receive("orders", 0, 0, Duration.ofSeconds(5));
//        assertNotNull(rec);
//        assertNotNull(rec.value());
//        assertEquals("PROCESSED", rec.value().getStatus());
//    }
}
