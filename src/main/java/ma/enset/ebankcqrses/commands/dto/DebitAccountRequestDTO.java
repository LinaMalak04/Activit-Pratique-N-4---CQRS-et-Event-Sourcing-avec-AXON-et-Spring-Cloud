package ma.enset.ebankcqrses.commands.dto;

public record DebitAccountRequestDTO(String accountId, double amount, String currency) {
}
