package ma.enset.ebankcqrses.query.handlers;

import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankcqrses.enums.OperationType;
import ma.enset.ebankcqrses.events.*;
import ma.enset.ebankcqrses.query.entities.Account;
import ma.enset.ebankcqrses.query.entities.AccountOperation;
import ma.enset.ebankcqrses.query.repositories.AccountOperationRepository;
import ma.enset.ebankcqrses.query.repositories.AccountRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Projection : écoute les événements de l'Event Store et met à jour la base de lecture.
 */
@Component
@Slf4j
public class AccountEventHandler {
    private final AccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;

    public AccountEventHandler(AccountRepository accountRepository,
                               AccountOperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, @Timestamp Instant timestamp) {
        log.info("Projection AccountCreatedEvent : {}", event);
        accountRepository.save(Account.builder()
                .id(event.accountId())
                .balance(event.initialBalance())
                .status(event.status())
                .currency(event.currency())
                .createdAt(timestamp)
                .build());
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("Projection AccountActivatedEvent : {}", event);
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.status());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("Projection AccountStatusUpdatedEvent : {}", event);
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.toStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, @Timestamp Instant timestamp) {
        log.info("Projection AccountCreditedEvent : {}", event);
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setBalance(account.getBalance() + event.amount());
        accountRepository.save(account);
        operationRepository.save(AccountOperation.builder()
                .date(timestamp).amount(event.amount()).type(OperationType.CREDIT)
                .currency(event.currency()).account(account).build());
    }

    @EventHandler
    public void on(AccountDebitedEvent event, @Timestamp Instant timestamp) {
        log.info("Projection AccountDebitedEvent : {}", event);
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setBalance(account.getBalance() - event.amount());
        accountRepository.save(account);
        operationRepository.save(AccountOperation.builder()
                .date(timestamp).amount(event.amount()).type(OperationType.DEBIT)
                .currency(event.currency()).account(account).build());
    }
}
