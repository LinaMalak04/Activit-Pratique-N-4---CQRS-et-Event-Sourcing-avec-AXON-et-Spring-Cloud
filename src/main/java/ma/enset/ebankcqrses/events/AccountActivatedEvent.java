package ma.enset.ebankcqrses.events;

import ma.enset.ebankcqrses.enums.AccountStatus;

public record AccountActivatedEvent(String accountId, AccountStatus status) {
}
