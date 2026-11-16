package ma.enset.ebankcqrses.query.repositories;

import ma.enset.ebankcqrses.query.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByAccountIdOrderByDateAsc(String accountId);
}
