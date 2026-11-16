package ma.enset.ebankcqrses.commands.controllers;

import ma.enset.ebankcqrses.commands.commands.AddAccountCommand;
import ma.enset.ebankcqrses.commands.commands.CreditAccountCommand;
import ma.enset.ebankcqrses.commands.commands.DebitAccountCommand;
import ma.enset.ebankcqrses.commands.commands.UpdateAccountStatusCommand;
import ma.enset.ebankcqrses.commands.dto.AddNewAccountRequestDTO;
import ma.enset.ebankcqrses.commands.dto.CreditAccountRequestDTO;
import ma.enset.ebankcqrses.commands.dto.DebitAccountRequestDTO;
import ma.enset.ebankcqrses.commands.dto.UpdateAccountStatusRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {
    private final CommandGateway commandGateway;
    private final EventStore eventStore;

    public AccountCommandController(CommandGateway commandGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addAccount(@RequestBody AddNewAccountRequestDTO request) {
        return commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(), request.initialBalance(), request.currency()));
    }

    @PostMapping("/credit")
    public CompletableFuture<Void> credit(@RequestBody CreditAccountRequestDTO request) {
        return commandGateway.send(new CreditAccountCommand(request.accountId(), request.amount(), request.currency()));
    }

    @PostMapping("/debit")
    public CompletableFuture<Void> debit(@RequestBody DebitAccountRequestDTO request) {
        return commandGateway.send(new DebitAccountCommand(request.accountId(), request.amount(), request.currency()));
    }

    @PutMapping("/updateStatus")
    public CompletableFuture<Void> updateStatus(@RequestBody UpdateAccountStatusRequestDTO request) {
        return commandGateway.send(new UpdateAccountStatusCommand(request.accountId(), request.status()));
    }

    /** Lecture directe de l'Event Store : tous les événements d'un agrégat. */
    @GetMapping("/events/{accountId}")
    public List<Map<String, Object>> eventStore(@PathVariable String accountId) {
        return eventStore.readEvents(accountId).asStream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("sequenceNumber", e.getSequenceNumber());
                    m.put("type", e.getPayloadType().getSimpleName());
                    m.put("timestamp", e.getTimestamp().toString());
                    m.put("payload", e.getPayload());
                    return m;
                })
                .toList();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> exceptionHandler(Exception exception) {
        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) cause = cause.getCause();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", String.valueOf(cause.getMessage())));
    }
}
