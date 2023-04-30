package pl.piomin.services.transactions;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import pl.piomin.services.transactions.domain.Account;
import pl.piomin.services.transactions.repository.AccountRepository;

import java.util.Random;

@SpringBootApplication
@EnableKafka
public class NoTransactionsService {

    public static void main(String[] args) {
        SpringApplication.run(NoTransactionsService.class, args);
    }

    private static final Logger LOG = LoggerFactory.getLogger(NoTransactionsService.class);

    Random r = new Random();

    @Autowired
    AccountRepository repository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 1000; i++) {
            repository.save(new Account(r.nextInt(1000, 10000)));
        }
    }

//    @Bean
//    ConcurrentKafkaListenerContainerFactory<Integer, String>
//    kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        factory.setConcurrency(3);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        return factory;
//    }

}
