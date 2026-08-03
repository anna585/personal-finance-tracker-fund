package app.budget;


import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import app.web.dto.budget.MonthlyBudgetRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Test
    public void updateMonthlyBudget_whenCurrentBudgetIsPresent_thenSetNewBudgetLimit(){
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(4000.00))
                .month(now.getMonth())
                .year(now.getYear())
                .id(user.getId())
                .build();

        MonthlyBudgetRequest request = MonthlyBudgetRequest.builder()
                .monthlyBudget(BigDecimal.valueOf(6000.00))
                .build();

        when(budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear()))
                .thenReturn(Optional.of(budget));

        budgetService.updateMonthlyBudget(user, request);

        assertEquals(BigDecimal.valueOf(6000.00), budget.getMonthlyLimit());
        verify(budgetRepository).save(budget);
        verify(budgetRepository).findByUserAndMonthAndYear(user, now.getMonth(), now.getYear());

    }

    @Test
    public void calculateRemainingBudget_whenResultIsPositive_thenReturnRemainBudget(){
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Budget budget = Budget.builder()
                .id(user.getId())
                .month(now.getMonth())
                .year(now.getYear())
                .monthlyLimit(BigDecimal.valueOf(5000.00))
                .build();

        when(budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear()))
                .thenReturn(Optional.of(budget));

        when(transactionService.getTotalSpentByUser(user)).thenReturn(BigDecimal.valueOf(4000.00));

        BigDecimal result = budgetService.calculateRemainingBudget(user);

        assertEquals(BigDecimal.valueOf(1000.00), result);
        verify(budgetRepository).findByUserAndMonthAndYear(user, now.getMonth(), now.getYear());
        verify(transactionService).getTotalSpentByUser(user);

    }

    @Test
    public void getAllBudgets_thenReturnListOfAllBudgets(){

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(200.00))
                .build();

        List<Budget> list1 = List.of(budget, budget);
        List<Budget> list2 = List.of(budget, budget, budget);

        List<Budget> allList = new ArrayList<>();
        allList.addAll(list1);
        allList.addAll(list2);

        when(budgetRepository.findAll()).thenReturn(allList);


        assertEquals(allList.size(), budgetService.getAllBudgets().size());
        verify(budgetRepository).findAll();

    }

    @Test
    public void createBudgetForCurrentMonth_whenBudgetFromPreviousMonthExist_thenCreateWithLimitLikePreviousMonth(){
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Budget budget = Budget.builder()
                .user(user)
                .monthlyLimit(BigDecimal.valueOf(5000.00))
                .month(now.getMonth())
                .year(now.getYear())
                .build();

        when(budgetRepository.findLatestBudget(user)).thenReturn(Optional.of(budget));

        budgetService.createBudgetForCurrentMonth(user, LocalDateTime.now());

        verify(budgetRepository).findLatestBudget(user);

    }

    @Test
    public void createBudgetForCurrentMonth_whenBudgetFromPreviousMonthNotExist_thenCreateNewWithZeroLimit(){
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();


        when(budgetRepository.findLatestBudget(user)).thenReturn(Optional.empty());

        budgetService.createBudgetForCurrentMonth(user, LocalDateTime.now());

        verify(budgetRepository).findLatestBudget(user);

    }

    @Test
    public void getCurrentBudget_notFindBudgetForCurrentMonth_thenCreateBudgetForCurrentMonth(){

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear())).thenReturn(Optional.empty());

        budgetService.getCurrentBudget(user);

        verify(budgetRepository).findByUserAndMonthAndYear(user, now.getMonth(), now.getYear());

    }
}
