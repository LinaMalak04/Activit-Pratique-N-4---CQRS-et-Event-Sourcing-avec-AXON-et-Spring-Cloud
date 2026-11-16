package ma.enset.ebankcqrses.query.dtos;

import java.util.List;

public record AccountStatementResponseDTO(AccountDTO account, List<AccountOperationDTO> operations) {
}
