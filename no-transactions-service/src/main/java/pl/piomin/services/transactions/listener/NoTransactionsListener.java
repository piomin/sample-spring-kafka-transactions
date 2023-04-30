package pl.piomin.services.transactions.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.piomin.services.common.model.Order;
import pl.piomin.services.transactions.service.AccountService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NoTransactionsListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(NoTransactionsListener.class);

    AccountService service;
    ExecutorService executorService = Executors.newFixedThreadPool(30);

    public NoTransactionsListener(AccountService service) {
        this.service = service;
    }

    @KafkaListener(
            id = "transactions",
            topics = "transactions",
            groupId = "a"
    )
    public void listen(Order order) {
        LOG.info("Received: {}", order);
        service.process(order);
//        executorService.submit(() -> service.process(order));
    }

}
