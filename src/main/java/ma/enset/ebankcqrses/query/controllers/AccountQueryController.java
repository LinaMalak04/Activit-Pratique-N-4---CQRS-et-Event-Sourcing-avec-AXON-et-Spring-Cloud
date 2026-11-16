package ma.enset.ebankcqrses.query.controllers;

import ma.enset.ebankcqrses.query.dtos.AccountDTO;
import ma.enset.ebankcqrses.query.dtos.AccountOperationDTO;
import ma.enset.ebankcqrses.query.dtos.AccountStatementResponseDTO;
import ma.enset.ebankcqrses.query.queries.GetAccountStatementQuery;
import ma.enset.ebankcqrses.query.queries.GetAllAccountsQuery;
import ma.enset.ebankcqrses.query.queries.WatchEventQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/query/accounts")
public class AccountQueryController {
    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/all")
    public CompletableFuture<List<AccountDTO>> allAccounts() {
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(AccountDTO.class));
    }

    @GetMapping("/accountStatement/{accountId}")
    public CompletableFuture<AccountStatementResponseDTO> accountStatement(@PathVariable String accountId) {
        return queryGateway.query(new GetAccountStatementQuery(accountId),
                ResponseTypes.instanceOf(AccountStatementResponseDTO.class));
    }

    /**
     * Suivi temps réel (Server-Sent Events) : historique des opérations du compte,
     * puis chaque nouvelle opération (crédit/débit) dès qu'elle est projetée.
     */
    @GetMapping(value = "/{accountId}/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperationDTO> watch(@PathVariable String accountId) {
        SubscriptionQueryResult<List<AccountOperationDTO>, AccountOperationDTO> result =
                queryGateway.subscriptionQuery(new WatchEventQuery(accountId),
                        ResponseTypes.multipleInstancesOf(AccountOperationDTO.class),
                        ResponseTypes.instanceOf(AccountOperationDTO.class));
        return result.initialResult()
                .flatMapMany(Flux::fromIterable)
                .concatWith(result.updates())
                .doFinally(signal -> result.close());
    }
}
