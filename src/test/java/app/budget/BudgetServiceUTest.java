package app.budget;


import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceUTest {

    @Mock
    private  BudgetRepository budgetRepository;

    @Mock
    private  TransactionService transactionService;

    @InjectMocks
    BudgetService budgetService;

    @Test
    public void createDefaultBudget_thenCreatingBudgetWithZeroMonthlyLimit(){
        LocalDateTime date = LocalDateTime.now();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Budget budget = Budget.builder()
                .user(user)
                .monthlyLimit(BigDecimal.ZERO)
                .month(date.getMonth())
                .year(date.getYear())
                .build();
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget result = budgetService.createDefaultBudget(user);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getMonthlyLimit());
        assertEquals(date.getMonth(), result.getMonth());
        assertEquals(date.getYear(), result.getYear());

        verify(budgetRepository, times(1)).save(any(Budget.class));
    }
}
