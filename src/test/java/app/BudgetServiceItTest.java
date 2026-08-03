package app;

import app.model.entities.budget.Budget;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.model.entities.user.UserRole;
import app.repositories.budget.BudgetRepository;
import app.repositories.transaction.TransactionRepository;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import app.web.dto.budget.BudgetDto;
import app.web.dto.budget.MonthlyBudgetRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class BudgetServiceItTest {

    @Autowired
    private BudgetService underTest;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createBudgetForCurrentMonth_whenBudgetFromPreviousMonthIsFind_thenBudgetIsRenewInCurrentMonthWithAmountFromPreviousMount(){
        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        Budget previousBudget = Budget.builder()
                .user(user)
                .month(Month.JULY)
                .year(2026)
                .monthlyLimit(BigDecimal.valueOf(5000))
                .build();
        budgetRepository.save(previousBudget);

        LocalDateTime dateTime = LocalDateTime.now();

        Budget budget1 = underTest.createBudgetForCurrentMonth(user, dateTime);

        assertEquals(BigDecimal.valueOf(5000), budget1.getMonthlyLimit());
        assertEquals(dateTime.getMonth(), budget1.getMonth());
        assertEquals(dateTime.getYear(), budget1.getYear());
        assertEquals(user.getId(), budget1.getUser().getId());
    }

    @Test
    public void createBudgetForCurrentMonth_whenBudgetFromPreviousMonthIsNotFind_thenBudgetIsRenewInCurrentMonthWithZeroAmount(){
        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        LocalDateTime dateTime = LocalDateTime.now();

        Budget budget1 = underTest.createBudgetForCurrentMonth(user, dateTime);

        assertEquals(BigDecimal.ZERO, budget1.getMonthlyLimit());
        assertEquals(dateTime.getMonth(), budget1.getMonth());
        assertEquals(dateTime.getYear(), budget1.getYear());
        assertEquals(user.getId(), budget1.getUser().getId());
    }

    @Test
    public void calculateRemainingBudget_whenHaveIncomeTransactionsAndExpenseTransactions_thenRemainBudgetSubtractExpenseTransactions(){

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        user = userRepository.save(user);

        Budget currentBudget = Budget.builder()
                .user(user)
                .month(now.getMonth())
                .year(now.getYear())
                .monthlyLimit(BigDecimal.valueOf(5000))
                .build();
        budgetRepository.save(currentBudget);

        Transaction transactionExpense1 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(250))
                .categoryType(CategoryType.FOOD)
                .type(TransactionType.EXPENSE)
                .createdAt(now.minusDays(1))
                .build();

        Transaction transactionExpense2 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.EXPENSE)
                .createdAt(now.minusDays(2))
                .build();

        Transaction transactionIncome = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.INCOME)
                .createdAt(now.minusDays(2))
                .build();

        transactionRepository.save(transactionExpense1);
        transactionRepository.save(transactionExpense2);
        transactionRepository.save(transactionIncome);

        BigDecimal spent = underTest.calculateRemainingBudget(user);

        assertEquals(0, BigDecimal.valueOf(4700).compareTo(spent));

    }

    @Test
    public void updateBudget_thenBudgetIsUpdateSuccess(){
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        user = userRepository.save(user);

        Budget currentBudget = Budget.builder()
                .user(user)
                .month(now.getMonth())
                .year(now.getYear())
                .monthlyLimit(BigDecimal.valueOf(5000))
                .build();

        budgetRepository.save(currentBudget);

        MonthlyBudgetRequest monthlyBudgetRequest = MonthlyBudgetRequest.builder()
                .monthlyBudget(BigDecimal.valueOf(8000))
                .build();

        BudgetDto budgetDto = underTest.updateMonthlyBudget(user, monthlyBudgetRequest);

        assertEquals(BigDecimal.valueOf(8000), budgetDto.getMonthlyLimit());
        assertEquals(1, budgetRepository.findAll().size());
    }
}
