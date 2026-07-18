package app.services.transaction;

import app.mapper.transaction.TransactionMapper;
import app.mapper.user.UserMapper;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import app.web.dto.user.UserDto;
import app.model.entities.budget.Budget;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;

import app.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final SavingRepository savingRepository;


    public UserDto createNewTransaction(UUID id, TransactionRequest transactionRequest) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found!"));

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, now.getMonth(), now.getYear())
                .orElseThrow(() ->
                        new RuntimeException("No budget found. Please create a budget first."));


        if(budget.getMonthlyLimit().compareTo(transactionRequest.getAmount()) < 0 && transactionRequest.getType().equals(TransactionType.EXPENSE)){
            throw new RuntimeException("The monthly budget is not sufficient to create this transaction.");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(transactionRequest.getAmount())
                .type(transactionRequest.getType())
                .categoryType(transactionRequest.getCategory())
                .date(LocalDate.now())
                .build();

        transactionRepository.save(transaction);

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

        savingRepository.findByTransactionId(id)
                .ifPresent(savingRepository::delete);

        transactionRepository.deleteById(id);
    }

    public List<Transaction> getAllTransactionsByUser(UUID id) {

        return transactionRepository.findAllByUserIdOrderByDateDesc(id);
    }

    public List<Transaction> getTransactionForReport(UUID userId, LocalDate start, LocalDate end ) {
        return transactionRepository
                .findByUserIdAndDateBetween(userId, start, end);
    }

    public TransactionDto getTransactionById(UUID id) {

        Transaction transaction = transactionRepository.findTransactionById(id);

        return TransactionMapper.toDto(transaction);
    }

    public TransactionDto updateTransaction(UUID id, TransactionRequest transactionRequest) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with id [%s] does not exist".formatted(id)));

        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCategoryType(transaction.getCategoryType());
        transaction.setDate(transaction.getDate());


        transactionRepository.save(transaction);

        return TransactionMapper.toDto(transaction);
    }
}
