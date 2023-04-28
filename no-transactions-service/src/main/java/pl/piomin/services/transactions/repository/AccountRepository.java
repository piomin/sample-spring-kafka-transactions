package pl.piomin.services.transactions.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import pl.piomin.services.transactions.domain.Account;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(Long id);
}
