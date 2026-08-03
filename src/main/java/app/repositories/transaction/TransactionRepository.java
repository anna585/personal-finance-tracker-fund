package app.repositories.transaction;

import app.model.entities.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.id = :userId
        AND t.type = 'EXPENSE'
        AND t.createdAt BETWEEN :start AND :end
        """)
    BigDecimal findTotalSpentByUser(UUID userId,  LocalDateTime start, LocalDateTime end);

    List<Transaction> findAllByUserIdOrderByCreatedAtDesc(UUID id);

   List<Transaction> findByUserIdAndCreatedAtBetween(UUID userId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findTop5ByUserIdOrderByCreatedAtDesc(UUID id);
}
