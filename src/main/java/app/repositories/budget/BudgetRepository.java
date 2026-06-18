package app.repositories.budget;

import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Optional<Budget> findBudgetByUserAndMonthAndYear (User user,Month month, int year);

    Optional<Budget> findByUserAndMonthAndYear(User user, Month month, int year);

    Optional<Budget> findByUserId(UUID id);

}
