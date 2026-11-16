package ma.enset.ebankcqrses.events;

import ma.enset.ebankcqrses.enums.AccountStatus;

public record AccountStatusUpdatedEvent(String accountId, AccountStatus fromStatus, AccountStatus toStatus) {
}
