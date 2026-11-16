package ma.enset.ebankcqrses.query.handlers;

import ma.enset.ebankcqrses.query.dtos.AccountDTO;
import ma.enset.ebankcqrses.query.dtos.AccountOperationDTO;
import ma.enset.ebankcqrses.query.dtos.AccountStatementResponseDTO;
import ma.enset.ebankcqrses.query.entities.Account;
import ma.enset.ebankcqrses.query.mappers.AccountMapper;
import ma.enset.ebankcqrses.query.queries.GetAccountStatementQuery;
import ma.enset.ebankcqrses.query.queries.GetAllAccountsQuery;
import ma.enset.ebankcqrses.query.queries.WatchEventQuery;
import ma.enset.ebankcqrses.query.repositories.AccountOperationRepository;
import ma.enset.ebankcqrses.query.repositories.AccountRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountQueryHandler {
    private final AccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository,
                               AccountOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public List<AccountDTO> on(GetAllAccountsQuery query) {
        return accountRepository.findAll().stream().map(AccountMapper::toDTO).toList();
    }

    @QueryHandler
    public AccountStatementResponseDTO on(GetAccountStatementQuery query) {
        Account account = accountRepository.findById(query.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + query.accountId()));
        return new AccountStatementResponseDTO(AccountMapper.toDTO(account),
                operationRepository.findByAccountIdOrderByDateAsc(query.accountId()).stream()
                        .map(AccountMapper::toDTO).toList());
    }

    /** Résultat initial de la subscription query : l'historique des opérations du compte. */
    @QueryHandler
    public List<AccountOperationDTO> on(WatchEventQuery query) {
        return operationRepository.findByAccountIdOrderByDateAsc(query.accountId()).stream()
                .map(AccountMapper::toDTO).toList();
    }
}
