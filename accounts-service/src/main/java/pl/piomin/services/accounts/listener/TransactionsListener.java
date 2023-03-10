package pl.piomin.services.accounts.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.accounts.domain.Account;
import pl.piomin.services.accounts.repository.AccountRepository;
import pl.piomin.services.common.model.Order;

import java.util.concurrent.CompletableFuture;

@Service
public class TransactionsListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(TransactionsListener.class);

    KafkaTemplate<Long, Order> kafkaTemplate;
    AccountRepository repository;

    public TransactionsListener(KafkaTemplate<Long, Order> kafkaTemplate, AccountRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }

    @KafkaListener(
            id = "transactions",
            topics = "transactions",
            groupId = "a",
            concurrency = "3")
    @Transactional("kafkaTransactionManager")
    public void listen(Order order) {
        LOG.info("Received: {}", order);
        process(order);
    }

    private void process(Order order) {
        Account accountSource = repository
                .findById(order.getSourceAccountId())
                .orElseThrow();
        Account accountTarget = repository
                .findById(order.getTargetAccountId())
                .orElseThrow();
        if (accountSource.getBalance() >= order.getAmount()) {
            accountSource.setBalance(accountSource.getBalance() - order.getAmount());
            repository.save(accountSource);
            accountTarget.setBalance(accountTarget.getBalance() + order.getAmount());
            repository.save(accountTarget);
            order.setStatus("PROCESSED");
        } else {
            order.setStatus("FAILED");
        }
        LOG.info("After processing: {}", order);
        CompletableFuture<SendResult<Long, Order>> result = kafkaTemplate.send("orders", order.getId(), order);
        result.whenComplete((sr, ex) ->
                LOG.info("Sent(key={},partition={}): {}",
                        sr.getProducerRecord().partition(),
                        sr.getProducerRecord().key(),
                        sr.getProducerRecord().value()));
    }
}
