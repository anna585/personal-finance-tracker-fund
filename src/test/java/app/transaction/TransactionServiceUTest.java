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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        LocalDateTime dateTime = LocalDateTime.now();

        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

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
                .month(dateTime.getMonth())
                .year(dateTime.getYear())
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, dateTime.getMonth(), dateTime.getYear())).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId(), start, end)).thenReturn(BigDecimal.ZERO);

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
        LocalDateTime dateTime = LocalDateTime.now();

        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

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
                .month(dateTime.getMonth())
                .year(dateTime.getYear())
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, dateTime.getMonth(), dateTime.getYear())).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId(), start, end)).thenReturn(BigDecimal.ZERO);

        assertThrows(BudgetNotEnoughException.class, () -> transactionService.createNewTransaction(user.getId(), transactionRequest));
        verify(transactionRepository, never()).save(any(Transaction.class));

    }

    @Test
    public void createTransaction_whenBudgetIsNotEnoughAndTransactionTypeIsIncome_thenTransactionInSuccessRecord(){
        LocalDateTime dateTime = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

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
                .month(dateTime.getMonth())
                .year(dateTime.getYear())
                .build();


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(budgetRepository.findByUserAndMonthAndYear(user, dateTime.getMonth(), dateTime.getYear())).thenReturn(Optional.of(budget));
        when(transactionRepository.findTotalSpentByUser(user.getId(), start, end)).thenReturn(BigDecimal.ZERO);

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
        verify(transactionRepository).findById(any());
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
        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        when(transactionRepository.findTotalSpentByUser(user.getId(), start, end)).thenReturn(null);

        assertEquals(null, transactionService.getTotalSpentByUser(user));
        verify(transactionRepository).findTotalSpentByUser(user.getId(), start, end);
    }

    @Test
    public void getTotalSpent_whenTransactionRepositoryIsNotNull_thenResultIsPositive(){

        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

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

        when(transactionRepository.findTotalSpentByUser(user.getId(), start, end)).thenReturn(BigDecimal.valueOf(300.00));

        assertEquals(BigDecimal.valueOf(300.00), transactionService.getTotalSpentByUser(user));
    }

    @Test
    public void updateTransaction_whenTransactionNotExist_thenThrowTransactionNotFoundException(){

        UUID userId = UUID.randomUUID();
        TransactionRequest request = TransactionRequest.builder().build();

        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class , () -> transactionService.updateTransaction(userId, request));
        verify(transactionRepository).findById(any());

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
        verify(transactionRepository).findById(any());

    }

    @Test
    public void getTransaction_whenTransactionExist_thenReturnTransactionDto(){

        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(10.00))
                .build();

        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));

        TransactionDto dto = transactionService.getTransactionById(any());

        assertEquals(transaction.getId(), dto.getId());
        verify(transactionRepository).findById(any());

    }

    @Test
    public void getAllTransactionByUserId_thenReturnListOfAllTransactionByUserId(){
        UUID userId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(5.00))
                .build();

        List<Transaction> transactions = List.of(transaction, transaction, transaction, transaction);

        when(transactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(transactions);

        List<Transaction> list = transactionService.getAllTransactionsByUser(userId);

        assertEquals(transactions.size(), list.size());
        verify(transactionRepository).findAllByUserIdOrderByCreatedAtDesc(userId);

    }

    @Test
    public void getTransactionsForReportByUserId_thenReturnListOfTransactionDtoFindByIdUserAndCreateBetweenStartAndEndDate(){
        UUID userId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(5.00))
                .createdAt(LocalDateTime.of(2026, 1, 15, 10, 30))
                .build();

        LocalDateTime startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 31, 23, 59);

        List<Transaction> transactions = List.of(transaction, transaction, transaction, transaction);

        when(transactionRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate)).thenReturn(transactions);

        List<TransactionDto> list = transactionService.getTransactionForReport(userId, startDate, endDate);

        assertEquals(4, list.size());

        verify(transactionRepository, times(1))
                .findByUserIdAndCreatedAtBetween(
                        userId,
                        startDate,
                        endDate
                );

    }

    @Test
    public void getAllTransactions_thenListOfAllTransactions(){

        Transaction transaction1 = Transaction.builder()
                .amount(BigDecimal.valueOf(5.00))
                .build();

        List<Transaction> transactions1 = List.of(transaction1, transaction1, transaction1, transaction1);
        List<Transaction> transactions2 = List.of(transaction1, transaction1, transaction1, transaction1);

        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(transactions1);
        allTransactions.addAll(transactions2);

        when(transactionRepository.findAll()).thenReturn(allTransactions);

        assertEquals(allTransactions.size(), transactionService.getAllTransactions().size());
        verify(transactionRepository).findAll();
    }

    @Test
    public void getTop5Transactions_thenReturnListWith5TransactionsByUserIdOrderByCreatedAtDesc(){
        UUID userId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(20.00))
                .createdAt(LocalDateTime.now())
                .build();

        List<Transaction> list = IntStream.range(0, 5)
                .mapToObj(i -> Transaction.builder()
                        .amount(BigDecimal.valueOf(20 + i))
                        .createdAt(LocalDateTime.now().minusDays(i))
                        .build())
                .toList();

        when(transactionRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId)).thenReturn(list);

        List<TransactionDto> transactionDtos = transactionService.getTop5Transactions(userId);

        assertEquals(5, transactionDtos.size());
        verify(transactionRepository).findTop5ByUserIdOrderByCreatedAtDesc(userId);
    }
}
