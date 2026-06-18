package app.model.entities.saving;

import app.model.entities.transaction.Transaction;
import app.model.entities.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "saving")
public class SavingGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NonNull
    private String goalName;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal targetAmount;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}
