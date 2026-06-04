package app.model.entities.category;

import app.model.entities.transaction.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private CategoryType type;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    private List<Transaction> transactions;

}
