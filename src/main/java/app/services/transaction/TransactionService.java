package app.services.transaction;

import app.model.entities.transaction.Transaction;
import app.model.entities.user.User;
import app.repositories.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createDefaultTransaction(User user){

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(BigDecimal.valueOf(0.00))
                .description("")
                .date(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return transaction;
    }
}
