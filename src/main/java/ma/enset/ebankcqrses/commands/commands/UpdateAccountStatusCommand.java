package ma.enset.ebankcqrses.commands.commands;

import ma.enset.ebankcqrses.enums.AccountStatus;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record UpdateAccountStatusCommand(@TargetAggregateIdentifier String id, AccountStatus status) {
}
