package app.web.controllers.transaction;

import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.Transaction;
import app.model.entities.transaction.TransactionType;
import app.web.dto.transaction.TransactionDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class TransactionFactoryDto {

    public static Transaction getTransaction(){

        return Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(200.00))
                .type(TransactionType.EXPENSE)
                .categoryType(CategoryType.FOOD)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static TransactionDto getTransactionDto(){

        return TransactionDto.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(200.00))
                .type(TransactionType.EXPENSE)
                .category(CategoryType.FOOD)
                .createAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
