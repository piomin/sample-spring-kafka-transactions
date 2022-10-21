package pl.piomin.services.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import pl.piomin.services.accounts.domain.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {
}
