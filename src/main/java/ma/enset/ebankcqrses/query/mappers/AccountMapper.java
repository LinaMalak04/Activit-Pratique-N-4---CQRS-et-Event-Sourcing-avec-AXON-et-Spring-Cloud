package ma.enset.ebankcqrses.query.mappers;

import ma.enset.ebankcqrses.query.dtos.AccountDTO;
import ma.enset.ebankcqrses.query.dtos.AccountOperationDTO;
import ma.enset.ebankcqrses.query.entities.Account;
import ma.enset.ebankcqrses.query.entities.AccountOperation;

public final class AccountMapper {
    private AccountMapper() {
    }

    public static AccountDTO toDTO(Account a) {
        return new AccountDTO(a.getId(), a.getBalance(), a.getStatus(), a.getCurrency(), a.getCreatedAt());
    }

    public static AccountOperationDTO toDTO(AccountOperation op) {
        return new AccountOperationDTO(op.getId(), op.getAccount().getId(), op.getDate(),
                op.getAmount(), op.getType(), op.getCurrency());
    }
}
