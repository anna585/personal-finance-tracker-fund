package app.model.dto.transaction;

import app.model.dto.user.UserDto;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TransactionDto {

    private UUID id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime date;
    private CategoryType category;
}
