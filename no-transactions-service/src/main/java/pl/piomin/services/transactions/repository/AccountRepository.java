package pl.piomin.services.transactions.repository;

import org.springframework.data.repository.CrudRepository;
import pl.piomin.services.transactions.domain.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
