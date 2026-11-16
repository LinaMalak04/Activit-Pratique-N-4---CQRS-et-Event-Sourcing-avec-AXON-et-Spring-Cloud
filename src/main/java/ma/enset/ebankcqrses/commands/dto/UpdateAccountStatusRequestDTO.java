package ma.enset.ebankcqrses.commands.dto;

import ma.enset.ebankcqrses.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId, AccountStatus status) {
}
