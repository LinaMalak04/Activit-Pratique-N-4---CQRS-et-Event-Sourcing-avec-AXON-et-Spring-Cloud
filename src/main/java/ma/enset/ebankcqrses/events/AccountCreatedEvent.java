package ma.enset.ebankcqrses.events;

import ma.enset.ebankcqrses.enums.AccountStatus;

public record AccountCreatedEvent(String accountId, double initialBalance, AccountStatus status, String currency) {
}
