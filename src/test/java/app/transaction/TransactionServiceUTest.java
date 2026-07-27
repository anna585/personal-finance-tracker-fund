package app.transaction;


import app.exeption.budget.BudgetNotEnoughException;
import app.exeption.transaction.TransactionNotFoundException;
import app.model.entities.budget.Budget;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;
import app.repositories.user.UserRepository;
import app.services.budget.BudgetService;
import app.services.transaction.TransactionService;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceUTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SavingRepository savingRepository;

    @Mock
    private BudgetService budgetService;


    @InjectMocks
    TransactionService transactionService;

    @Test
    public void whenBudgetIsEnough_thenTransactionIsSuccessRecord(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(200.00))
                .type(TransactionType.INCOME)
                .category(CategoryType.BONUS)
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(1000.00))
                .month(Month.JULY)
                .year(2026)
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, Month.JULY, 2026)).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId())).thenReturn(BigDecimal.ZERO);

        transactionService.createNewTransaction(user.getId(), transactionRequest);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(BigDecimal.valueOf(200.00), transaction.getAmount());
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals(CategoryType.BONUS, transaction.getCategoryType());

    }

    @Test
    public void createTransaction_whenBudgetIsNotEnoughAndTransactionTypeIsExpense_thenThrowBudgetNotEnoughException(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(500.00))
                .type(TransactionType.EXPENSE)
                .category(CategoryType.EDUCATION)
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(200.00))
                .month(Month.JULY)
                .year(2026)
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, Month.JULY, 2026)).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId())).thenReturn(BigDecimal.ZERO);

        assertThrows(BudgetNotEnoughException.class, () -> transactionService.createNewTransaction(user.getId(), transactionRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));

    }

    @Test
    public void createTransaction_whenBudgetIsNotEnoughAndTransactionTypeIsIncome_thenTransactionInSuccessRecord(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(500.00))
                .type(TransactionType.INCOME)
                .category(CategoryType.BONUS)
                .build();

        Budget budget = Budget.builder()
                .monthlyLimit(BigDecimal.valueOf(200.00))
                .month(Month.JULY)
                .year(2026)
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, Month.JULY, 2026)).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId())).thenReturn(BigDecimal.ZERO);

        transactionService.createNewTransaction(user.getId(), transactionRequest);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction transaction = captor.getValue();

        assertEquals(BigDecimal.valueOf(500.00), transaction.getAmount());
        assertEquals(TransactionType.INCOME, transaction.getType());

    }

    @Test
    public void deleteTransaction_whenTransactionIsNotFound_thenThrowTransactionNotFoundException(){

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, ()-> transactionService.deleteTransaction(any()));
    }

    @Test
    public void deleteTransaction_whenTransactionIsFound_thenTransactionDeleteSuccess(){

        UUID userId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .build();

        when(transactionRepository.findById(userId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(userId);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    public void getTotalSpent_whenTransactionRepositoryIsNull_thenTotalSpentIsZero(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(transactionRepository.findTotalSpentByUser(user.getId())).thenReturn(null);

        assertEquals(BigDecimal.ZERO, transactionService.getTotalSpentByUser(user));
    }

    @Test
    public void getTotalSpent_whenTransactionRepositoryIsNotNull_thenResultIsPositive(){

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(100.00))
                .type(TransactionType.EXPENSE)
                .categoryType(CategoryType.FOOD)
                .build();

        List<Transaction> transactions = List.of(transaction, transaction, transaction);
        user.setTransactions(transactions);

        when(transactionRepository.findTotalSpentByUser(user.getId())).thenReturn(BigDecimal.valueOf(300.00));

        assertEquals(BigDecimal.valueOf(300.00), transactionService.getTotalSpentByUser(user));
    }

    @Test
    public void updateTransaction_whenTransactionNotExist_thenThrowTransactionNotFoundException(){

        UUID userId = UUID.randomUUID();
        TransactionRequest request = TransactionRequest.builder().build();

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class , () -> transactionService.updateTransaction(userId, request));

    }

    @Test
    public void updateTransaction_whenTransactionExist_thenTransactionUpdateSuccess(){

        UUID userId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(20.00))
                .categoryType(CategoryType.FOOD)
                .type(TransactionType.INCOME)
                .build();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(50.00))
                .category(CategoryType.HEALTH)
                .type(TransactionType.EXPENSE)
                .build();

        when(transactionRepository.findById(userId)).thenReturn(Optional.of(transaction));

        transactionService.updateTransaction(userId,request);

        verify(transactionRepository).save(transaction);

        assertEquals(BigDecimal.valueOf(50.00), transaction.getAmount());
        assertEquals(CategoryType.HEALTH, transaction.getCategoryType());
        assertEquals(TransactionType.EXPENSE, transaction.getType());

    }

    @Test
    public void getTransaction_whenTransactionNotExist_thenThrowTransactionNotFoundException(){

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, ()->  transactionService.getTransactionById(any()));

    }

    @Test
    public void getTransaction_whenTransactionExist_thenReturnTransactionDto(){

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(10.00))
                .build();

        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));

        TransactionDto dto = transactionService.getTransactionById(any());

        assertEquals(transaction.getId(), dto.getId());

    }
}
