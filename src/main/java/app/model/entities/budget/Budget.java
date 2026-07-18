package app.model.entities.budget;

import app.model.entities.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Month;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyLimit;
    @Column(nullable = false)
    private Month month;
    private int year;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
