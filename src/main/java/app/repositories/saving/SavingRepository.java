package app.repositories.saving;

import app.model.entities.saving.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavingRepository extends JpaRepository<SavingGoal, UUID> {

    List<SavingGoal> findAllByUserId(UUID userId);

    Optional<SavingGoal> findByTransactionId(UUID transactionId);
}
