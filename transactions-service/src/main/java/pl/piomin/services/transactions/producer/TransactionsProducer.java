package pl.piomin.services.transactions.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import pl.piomin.services.common.model.Order;
import pl.piomin.services.transactions.callback.TransactionsResultCallback;
import pl.piomin.services.transactions.domain.OrderGroup;
import pl.piomin.services.transactions.repository.OrderGroupRepository;

@Service
public class TransactionsProducer {

    long id = 1;
    KafkaTemplate<Long, Order> kafkaTemplate;
    TransactionsResultCallback callback;
    OrderGroupRepository repository;

    public TransactionsProducer(KafkaTemplate<Long, Order> kafkaTemplate,
                                TransactionsResultCallback callback,
                                OrderGroupRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.callback = callback;
        this.repository = repository;
    }

    @Transactional("kafkaTransactionManager")
    public void sendOrderGroup(boolean error) throws InterruptedException {
        OrderGroup og = repository.save(new OrderGroup("SENT", 10, 0));
        generateAndSendPackage(error, og.getId());
    }

    private void generateAndSendPackage(boolean error, Long groupId)
            throws InterruptedException {
        for (long i = 0; i < 10; i++) {
            Order o = new Order(id++, i+1, i+2, 1000, "NEW", groupId);
            ListenableFuture<SendResult<Long, Order>> result =
                    kafkaTemplate.send("transactions", o.getId(), o);
            result.addCallback(callback);
            if (error && i > 5)
                throw new RuntimeException();
            Thread.sleep(1000);
        }
    }

}
