package pl.piomin.services.transactions.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piomin.services.common.model.Order;
import pl.piomin.services.transactions.domain.OrderGroup;
import pl.piomin.services.transactions.repository.OrderGroupRepository;

@Service
public class OrdersListener {

    private static final Logger LOG = LoggerFactory
            .getLogger(OrdersListener.class);

    OrderGroupRepository repository;

    public OrdersListener(OrderGroupRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            id = "orders",
            topics = "orders",
            groupId = "a",
            concurrency = "3")
    @Transactional("transactionManager")
    public void listen(Order order) {
        LOG.info("Received: {}", order);
        OrderGroup og = repository
                .findById(order.getGroupId())
                .orElseThrow();
        if (order.getStatus().equals("PROCESSED")) {
            og.setProcessedNoOfOrders(og.getProcessedNoOfOrders() + 1);
            og = repository.save(og);
            LOG.info("Current: {}", og);
        }
    }
}
