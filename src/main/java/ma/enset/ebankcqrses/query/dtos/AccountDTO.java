package ma.enset.ebankcqrses.query.dtos;

import ma.enset.ebankcqrses.enums.AccountStatus;

import java.time.Instant;

public record AccountDTO(String id, double balance, AccountStatus status, String currency, Instant createdAt) {
}
