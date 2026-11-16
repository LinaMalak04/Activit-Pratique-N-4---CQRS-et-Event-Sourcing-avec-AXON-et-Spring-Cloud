package ma.enset.ebankcqrses.events;

public record AccountDebitedEvent(String accountId, double amount, String currency) {
}
