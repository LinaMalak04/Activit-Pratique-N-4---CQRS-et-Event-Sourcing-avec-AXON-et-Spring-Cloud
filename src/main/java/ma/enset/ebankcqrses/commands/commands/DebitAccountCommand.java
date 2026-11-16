package ma.enset.ebankcqrses.commands.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record DebitAccountCommand(@TargetAggregateIdentifier String id, double amount, String currency) {
}
