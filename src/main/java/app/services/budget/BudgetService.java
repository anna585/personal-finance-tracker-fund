package app.services.budget;

import app.aspect.LogAction;
import app.mapper.budget.BudgetMapper;
import app.web.dto.budget.BudgetDto;
import app.web.dto.budget.MonthlyBudgetRequest;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.services.transaction.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;


    @LogAction("Create default budget.")
    @CacheEvict(value = "statistic", allEntries = true)
    @Transactional
    public Budget createDefaultBudget(User user){

        LocalDateTime now = LocalDateTime.now();

        Budget budget = Budget.builder()
                .user(user)
                .monthlyLimit(BigDecimal.ZERO)
                .month(now.getMonth())
                .year(now.getYear())
                .build();
        return budgetRepository.save(budget);
    }

    @LogAction("Updating budget")
    @Transactional
    public BudgetDto updateMonthlyBudget(User user, MonthlyBudgetRequest monthlyBudgetRequest) {

        LocalDateTime now = LocalDateTime.now();

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear()).get();

        budget.setMonthlyLimit(monthlyBudgetRequest.getMonthlyBudget());

        budgetRepository.save(budget);

        return BudgetMapper.toDto(budget);
    }

    public BudgetDto getCurrentBudget(User user) {

        LocalDateTime now = LocalDateTime.now();

        Budget budget = budgetRepository
                .findByUserAndMonthAndYear(
                        user,
                        now.getMonth(),
                        now.getYear())
                . orElseGet(() ->
                        createBudgetForCurrentMonth(user, now));

        return BudgetMapper.toDto(budget);
    }

    public BigDecimal calculateRemainingBudget(User user) {

        BudgetDto budget = getCurrentBudget(user);
        BigDecimal spent = transactionService.getTotalSpentByUser(user);

        return  budget.getMonthlyLimit()
                .subtract(spent)
                .max(BigDecimal.ZERO);
    }

    public List<Budget> getAllBudgets() {

        return budgetRepository.findAll();
    }

    @LogAction("Create budget for current mount.")
    @CacheEvict(value = "statistic", allEntries = true)
    @Transactional
    public Budget createBudgetForCurrentMonth(User user, LocalDateTime now) {

        Budget budget = new Budget();
        budget.setUser(user);
        budget.setMonth(now.getMonth());
        budget.setYear(now.getYear());


        BigDecimal monthlyLimit = budgetRepository.findLatestBudget(user)
                .map(Budget::getMonthlyLimit)
                .orElse(BigDecimal.ZERO);

            budget.setMonthlyLimit(monthlyLimit);

        return budgetRepository.save(budget);
    }
}
