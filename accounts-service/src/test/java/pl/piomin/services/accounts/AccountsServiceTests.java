package pl.piomin.services.accounts;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import pl.piomin.services.common.model.Order;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.properties.security.protocol=PLAINTEXT"
})
@EmbeddedKafka(topics = {"orders"},
        partitions = 1,
        brokerProperties = {
           "offsets.topic.replication.factor=1",
           "transaction.state.log.replication.factor=1",
           "transaction.state.log.min.isr=1",
           "num.partitions=1"
        },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountsServiceTests {

    private static final Logger LOG = LoggerFactory
            .getLogger(AccountsServiceTests.class);

    @Autowired
    private EmbeddedKafkaBroker kafka;
    @Autowired
    private KafkaTemplate<Long, Order> template;
    @Autowired
    private ConsumerFactory<Long, Order> factory;

    @Test
    @org.junit.jupiter.api.Order(1)
    void accept() throws ExecutionException, InterruptedException, TimeoutException {
        Order o = new Order(1L, 1L, 2L, 999, "NEW", 1L);
        SendResult<Long, Order> r = template.executeInTransaction(ko -> ko.send("transactions", o.getId(), o))
                .get(1000, TimeUnit.MILLISECONDS);;
        LOG.info("Sent: {}", r.getProducerRecord().value());

        template.setConsumerFactory(factory);
        ConsumerRecord<Long, Order> rec = template.receive("orders", 0, 0, Duration.ofSeconds(5));
        assertNotNull(rec);
        assertNotNull(rec.value());
        assertEquals("PROCESSED", rec.value().getStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void reject() throws ExecutionException, InterruptedException, TimeoutException {
        Order o = new Order(2L, 3L, 4L, 10001, "NEW", 2L);
        SendResult<Long, Order> r = template.executeInTransaction(ko -> ko.send("transactions", o.getId(), o))
                .get(1000, TimeUnit.MILLISECONDS);
        LOG.info("Sent: {}", r.getProducerRecord().value());

        Thread.sleep(1000);
        template.setConsumerFactory(factory);
        ConsumerRecord<Long, Order> rec = template.receive("orders", 0, 1, Duration.ofSeconds(5));
        assertNotNull(rec);
        assertNotNull(rec.value());
        assertEquals("FAILED", rec.value().getStatus());
    }

}
