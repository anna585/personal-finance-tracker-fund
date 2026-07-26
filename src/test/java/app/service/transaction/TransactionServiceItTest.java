package app.service.transaction;

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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void createNewTransaction_shouldThrowBudgetNotEnoughException_whenExpenseExceedsBudget(){

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
    public void createNewTransaction_shouldCreateTransaction_whenBudgetIsEnough(){

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

//        Transaction saved = transactionRepository.findAll().getFirst();
//
//        assertEquals(BigDecimal.valueOf(500.00), saved.getAmount());
//        assertEquals(CategoryType.EDUCATION, saved.getCategoryType());
//        assertEquals(TransactionType.EXPENSE, saved.getType());
//        assertEquals(user.getId(), saved.getUser().getId());
    }
}
