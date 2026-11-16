package ma.enset.ebankcqrses.commands.aggregates;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankcqrses.commands.commands.AddAccountCommand;
import ma.enset.ebankcqrses.commands.commands.CreditAccountCommand;
import ma.enset.ebankcqrses.commands.commands.DebitAccountCommand;
import ma.enset.ebankcqrses.commands.commands.UpdateAccountStatusCommand;
import ma.enset.ebankcqrses.enums.AccountStatus;
import ma.enset.ebankcqrses.events.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
@NoArgsConstructor // requis par Axon pour reconstruire l'agrégat à partir des événements
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double currentBalance;
    private String currency;
    private AccountStatus status;

    /* ---------------- Fonction de décision (Command Handlers) ---------------- */

    @CommandHandler
    public AccountAggregate(AddAccountCommand command) {
        log.info("AddAccountCommand reçue : {}", command);
        if (command.initialBalance() < 0)
            throw new IllegalArgumentException("Le solde initial ne peut pas être négatif");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.id(), command.initialBalance(), AccountStatus.CREATED, command.currency()));
        AggregateLifecycle.apply(new AccountActivatedEvent(command.id(), AccountStatus.ACTIVATED));
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        log.info("CreditAccountCommand reçue : {}", command);
        checkActivated();
        if (command.amount() <= 0) throw new IllegalArgumentException("Le montant doit être positif");
        AggregateLifecycle.apply(new AccountCreditedEvent(command.id(), command.amount(), command.currency()));
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        log.info("DebitAccountCommand reçue : {}", command);
        checkActivated();
        if (command.amount() <= 0) throw new IllegalArgumentException("Le montant doit être positif");
        if (command.amount() > currentBalance)
            throw new IllegalStateException("Solde insuffisant (solde actuel : " + currentBalance + ")");
        AggregateLifecycle.apply(new AccountDebitedEvent(command.id(), command.amount(), command.currency()));
    }

    @CommandHandler
    public void handle(UpdateAccountStatusCommand command) {
        log.info("UpdateAccountStatusCommand reçue : {}", command);
        if (command.status() == status)
            throw new IllegalStateException("Le compte est déjà dans le statut " + status);
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(command.id(), status, command.status()));
    }

    private void checkActivated() {
        if (status != AccountStatus.ACTIVATED)
            throw new IllegalStateException("Le compte " + accountId + " n'est pas activé (statut : " + status + ")");
    }

    /* ---------------- Fonction d'évolution (Event Sourcing Handlers) ---------------- */

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.accountId();
        this.currentBalance = event.initialBalance();
        this.currency = event.currency();
        this.status = event.status();
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        this.status = event.status();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.currentBalance += event.amount();
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.currentBalance -= event.amount();
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        this.status = event.toStatus();
    }
}
