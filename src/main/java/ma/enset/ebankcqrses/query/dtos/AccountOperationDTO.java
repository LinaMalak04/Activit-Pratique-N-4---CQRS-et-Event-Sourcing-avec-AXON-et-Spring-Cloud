package ma.enset.ebankcqrses.query.dtos;

import ma.enset.ebankcqrses.enums.OperationType;

import java.time.Instant;

public record AccountOperationDTO(Long id, String accountId, Instant date, double amount,
                                  OperationType type, String currency) {
}
