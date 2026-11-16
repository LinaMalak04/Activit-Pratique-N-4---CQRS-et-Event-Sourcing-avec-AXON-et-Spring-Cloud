package ma.enset.ebankcqrses.events;

public record AccountCreditedEvent(String accountId, double amount, String currency) {
}
