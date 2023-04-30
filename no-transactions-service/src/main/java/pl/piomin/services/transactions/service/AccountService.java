package pl.piomin.services.transactions.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.common.model.Order;
import pl.piomin.services.transactions.domain.Account;
import pl.piomin.services.transactions.domain.OrderDTO;
import pl.piomin.services.transactions.repository.AccountRepository;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class AccountService {

    private static final Logger LOG = LoggerFactory
            .getLogger(AccountService.class);
    private final Random RAND = new Random();

    KafkaTemplate<Long, Order> kafkaTemplate;
    AccountRepository repository;

    public AccountService(KafkaTemplate<Long, Order> kafkaTemplate, AccountRepository repository) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }

    @Transactional
    public void process(Order order) {
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

        try {
            Thread.sleep(RAND.nextLong(1, 20));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Processed: order->{}", new OrderDTO(order, accountSource, accountTarget));

        CompletableFuture<SendResult<Long, Order>> result = kafkaTemplate.send("orders", order.getId(), order);
        result.whenComplete((sr, ex) ->
                LOG.debug("Sent(key={},partition={}): {}",
                        sr.getProducerRecord().partition(),
                        sr.getProducerRecord().key(),
                        sr.getProducerRecord().value()));
    }

}
