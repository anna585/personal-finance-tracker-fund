package app;

import app.exeption.budget.BudgetNotEnoughException;
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
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class TransactionServiceItTest {

    @Autowired
    private TransactionService underTest;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void createNewTransaction_whenExpenseExceedsBudget_shouldThrowBudgetNotEnoughException(){

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        Budget budget = budgetService.createDefaultBudget(user);
        budget.setMonthlyLimit(BigDecimal.valueOf(100.00));
        budgetRepository.save(budget);

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(500.00))
                .category(CategoryType.FOOD)
                .type(TransactionType.EXPENSE)
                .build();

        UUID userId = user.getId();

        assertThrows(BudgetNotEnoughException.class,
                ()-> underTest.createNewTransaction(userId, request));
        assertEquals(0, transactionRepository.count());

    }

    @Test
    public void createNewTransaction_whenBudgetIsEnough_shouldCreateTransaction(){

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        Budget budget = budgetService.createDefaultBudget(user);
        budget.setMonthlyLimit(BigDecimal.valueOf(2000.00));
        budgetRepository.save(budget);

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(500.00))
                .category(CategoryType.EDUCATION)
                .type(TransactionType.EXPENSE)
                .build();

        UUID userId = user.getId();

        TransactionDto transaction = underTest.createNewTransaction(userId, transactionRequest);

        assertEquals(BigDecimal.valueOf(500.00), transaction.getAmount());
        assertEquals(CategoryType.EDUCATION, transaction.getCategory());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(1, transactionRepository.count());

        Transaction saved = transactionRepository.findById(transaction.getId()).orElseThrow();

        assertEquals(BigDecimal.valueOf(500.00), saved.getAmount());
        assertNotNull(saved.getId());
        assertEquals(CategoryType.EDUCATION, saved.getCategoryType());
        assertEquals(TransactionType.EXPENSE, saved.getType());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    public void getTotalSpent_shouldReturnSumOfExpenseTransactions(){

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);


        Transaction transactionExpense1 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(250.00))
                .categoryType(CategoryType.FOOD)
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Transaction transactionExpense2 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50.00))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        Transaction transactionIncome = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50.00))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.INCOME)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        transactionRepository.save(transactionExpense1);
        transactionRepository.save(transactionExpense2);
        transactionRepository.save(transactionIncome);

        BigDecimal totalSpent = underTest.getTotalSpentByUser(user);

        assertEquals(BigDecimal.valueOf(300.00).setScale(2), totalSpent);
    }

    @Test
    public void getTransactionsByUser_shouldReturnOnlyUserTransactions(){

        User user = User.builder()
                .username("annapetrova")
                .userRole(UserRole.USER)
                .firstName("Anna")
                .lastName("Petrova")
                .email("anna@abv.bg")
                .build();

        userRepository.save(user);

        User anotherUser = User.builder()
                .username("ivan")
                .userRole(UserRole.USER)
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan@test.com")
                .build();

        userRepository.save(anotherUser);

        Transaction anotherTransaction = Transaction.builder()
                .user(anotherUser)
                .amount(BigDecimal.valueOf(999))
                .categoryType(CategoryType.OTHER)
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(anotherTransaction);

        Transaction transactionExpense1 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(250.00))
                .categoryType(CategoryType.FOOD)
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Transaction transactionExpense2 = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50.00))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.EXPENSE)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        Transaction transactionIncome = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(50.00))
                .categoryType(CategoryType.EDUCATION)
                .type(TransactionType.INCOME)
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        transactionRepository.save(transactionExpense1);
        transactionRepository.save(transactionExpense2);
        transactionRepository.save(transactionIncome);

       List<Transaction> allTransactions = underTest.getAllTransactionsByUser(user.getId());

        assertEquals(3, allTransactions.size());
        assertTrue(
                allTransactions.stream()
                        .allMatch(t -> t.getUser().getId().equals(user.getId()))
        );
    }

}
