package app.services.budget;

import app.mapper.budget.BudgetMapper;
import app.model.dto.budget.BudgetDto;
import app.model.dto.budget.MonthlyBudgetRequest;
import app.model.entities.budget.Budget;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.services.transaction.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;


    public BudgetService(BudgetRepository budgetRepository,TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.transactionService = transactionService;
    }

    public Budget createDefaultBudget(User user){

        LocalDateTime now = LocalDateTime.now();

        Budget budget = Budget.builder()
                .user(user)
                .monthlyLimit(BigDecimal.ZERO)
                .month(now.getMonth())
                .year(now.getYear())
                .build();

        budgetRepository.save(budget);

        return budget;
    }

    public BudgetDto updateMonthlyBudget(User user, MonthlyBudgetRequest monthlyBudgetRequest) {

        LocalDateTime now = LocalDateTime.now();

        Optional<Budget> currentBudget = budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear());
        

        Budget budget;
        
        if(currentBudget.isPresent()){
            budget=currentBudget.get();
            budget.setMonthlyLimit(monthlyBudgetRequest.getMonthlyBudget());
        }else {
             budget = Budget.builder()
                     .user(user)
                    .monthlyLimit(monthlyBudgetRequest.getMonthlyBudget())
                    .month(now.getMonth())
                    .year(now.getYear())
                    .build();

        }

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
                .orElseThrow(() ->
                        new RuntimeException("No budget for current month"));

        return BudgetMapper.toDto(budget);
    }

    public Long getCountOfBudgets() {

        return budgetRepository.count();
    }

    public BigDecimal calculateRemainingBudget(User user) {

        BudgetDto budget = getCurrentBudget(user);
        BigDecimal spent = transactionService.getTotalSpentByUser(user.getId());

        return  budget.getMonthlyLimit().subtract(spent);
    }
}
