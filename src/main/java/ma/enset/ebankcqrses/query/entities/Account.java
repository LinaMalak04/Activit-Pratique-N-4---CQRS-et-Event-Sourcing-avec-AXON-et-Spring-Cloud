package ma.enset.ebankcqrses.query.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.enset.ebankcqrses.enums.AccountStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id
    private String id;
    private double balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String currency;
    private Instant createdAt;
    @OneToMany(mappedBy = "account")
    @Builder.Default
    private List<AccountOperation> operations = new ArrayList<>();
}
