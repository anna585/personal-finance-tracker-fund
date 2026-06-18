package app.services.transaction;

import app.mapper.user.UserMapper;
import app.model.dto.transaction.ExpenseTransactionRequest;
import app.model.dto.transaction.IncomeTransactionRequest;
import app.model.dto.transaction.TransactionRequest;
import app.model.dto.user.UserDto;
import app.model.entities.budget.Budget;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.transaction.TransactionRepository;

import app.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              BudgetRepository budgetRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public UserDto createNewTransaction(UUID id, TransactionRequest transactionRequest) {

        Budget budget = budgetRepository.findByUserId(id)
                .orElseThrow(() ->
                        new RuntimeException("Budget is empty. Please create new budget."));

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));

        if(budget.getMonthlyLimit().compareTo(transactionRequest.getAmount()) < 0 && transactionRequest.getType().equals(TransactionType.EXPENSE)){
            throw new RuntimeException("Transaction is not successful. Please check your budget!");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(transactionRequest.getAmount())
                .type(transactionRequest.getType())
                .categoryType(transactionRequest.getCategory())
                .date(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

//        user.getTransactions().add(transaction);
//        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    public UserDto createExpenseTransaction(UUID id, ExpenseTransactionRequest expenseTransactionRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));


        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(expenseTransactionRequest.getExpenseAmount())
                .categoryType(expenseTransactionRequest.getExpenseCategory())
                .type(TransactionType.EXPENSE)
                .date(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

//        user.getTransactions().add(transaction);
//        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    public UserDto createIncomeTransaction(UUID id, IncomeTransactionRequest incomeTransactionRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));


        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(incomeTransactionRequest.getIncomeAmount())
                .categoryType(incomeTransactionRequest.getIncomeCategory())
                .type(TransactionType.INCOME)
                .date(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

//        user.getTransactions().add(transaction);
//        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }
    public BigDecimal getTotalSpentByUser(UUID userId) {

       return transactionRepository.getTotalSpentByUser(userId) != null
               ? transactionRepository.getTotalSpentByUser(userId)
               : BigDecimal.ZERO;


    }

    public BigDecimal getTotalIncomeByUser(UUID userId) {

        return transactionRepository.getTotalIncomeByUser(userId) != null
                ? transactionRepository.getTotalIncomeByUser(userId)
                : BigDecimal.ZERO;
    }

    public void deleteTransaction(UUID id) {

        transactionRepository.deleteById(id);
    }

    public BigDecimal calculateCurrentBalance(UUID userId) {

        BigDecimal income = getTotalIncomeByUser(userId);
        BigDecimal expenses = getTotalSpentByUser(userId);

        return income.subtract(expenses);
    }

    public long getCountOfTransaction() {

        return transactionRepository.count();
    }
}
