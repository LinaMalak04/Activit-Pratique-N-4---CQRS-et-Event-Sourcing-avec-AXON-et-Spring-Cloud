package ma.enset.ebankcqrses.commands.dto;

public record CreditAccountRequestDTO(String accountId, double amount, String currency) {
}
