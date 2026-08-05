package app.services.transaction;

import app.aspect.LogAction;
import app.exeption.budget.BudgetNotEnoughException;
import app.exeption.budget.BudgetNotFoundException;
import app.exeption.transaction.TransactionNotFoundException;
import app.exeption.user.UserNotFoundException;
import app.mapper.transaction.TransactionMapper;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.transaction.TransactionRequest;
import app.model.entities.budget.Budget;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.model.entities.user.User;
import app.repositories.budget.BudgetRepository;
import app.repositories.saving.SavingRepository;
import app.repositories.transaction.TransactionRepository;

import app.repositories.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final SavingRepository savingRepository;


    @Caching(evict = {
            @CacheEvict(value = "summary", key = "#id"),
            @CacheEvict(value = "statistic", allEntries = true)
    })
    @Transactional
    @LogAction("Create transaction")
    public TransactionDto createNewTransaction(UUID id, TransactionRequest transactionRequest) {
        LocalDateTime createAt = LocalDateTime.now();

        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, createAt.getMonth(), createAt.getYear())
                .orElseThrow(() -> new BudgetNotFoundException(id));

        BigDecimal totalSpentForMonth = transactionRepository.findTotalSpentByUser(user.getId(), start, end);

        if(totalSpentForMonth.add(transactionRequest.getAmount()).compareTo(budget.getMonthlyLimit()) > 0
                && transactionRequest.getType().equals(TransactionType.EXPENSE)){
            throw new BudgetNotEnoughException();
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(transactionRequest.getAmount())
                .type(transactionRequest.getType())
                .categoryType(transactionRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return TransactionMapper.toDto(transaction);
    }

    public BigDecimal getTotalSpentByUser(User user) {

        YearMonth currentMonth = YearMonth.now();

        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(LocalTime.MAX);

        return transactionRepository
                .findTotalSpentByUser(
                        user.getId(),
                        start,
                        end);
    }

    @Caching(evict = {
            @CacheEvict(value = "summary", key = "#id"),
            @CacheEvict(value = "statistic", allEntries = true)
    })
    @Transactional
    @LogAction("Delete transaction.")
    public void deleteTransaction(UUID id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        savingRepository.findByTransactionId(id)
                .ifPresent(savingRepository::delete);

        transactionRepository.delete(transaction);
    }

    public List<Transaction> getAllTransactionsByUser(UUID id) {

        return transactionRepository.findAllByUserIdOrderByCreatedAtDesc(id);
    }

    public List<TransactionDto> getTransactionForReport(UUID id, LocalDateTime start, LocalDateTime end ) {

        List<Transaction> transaction = transactionRepository
                .findByUserIdAndCreatedAtBetween(id, start, end);

        return transaction
                .stream()
                .map(TransactionMapper::toDto)
                .toList();
    }

    public TransactionDto getTransactionById(UUID id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return TransactionMapper.toDto(transaction);
    }

    @CacheEvict(value = "summary", key = "#id")
    @Transactional
    @LogAction("Update transaction.")
    public TransactionDto updateTransaction(UUID id, TransactionRequest transactionRequest) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCategoryType(transactionRequest.getCategory());
        transaction.setCreatedAt(LocalDateTime.now());


        transactionRepository.save(transaction);

        return TransactionMapper.toDto(transaction);
    }

    public List<TransactionDto> getTop5Transactions(UUID id){

        return transactionRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(id)
                .stream()
                .map(TransactionMapper::toDto)
                .toList();
    }


    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();
    }
}
