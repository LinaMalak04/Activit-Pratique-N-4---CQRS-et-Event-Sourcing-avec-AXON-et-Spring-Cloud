package ma.enset.ebankcqrses.query.repositories;

import ma.enset.ebankcqrses.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
