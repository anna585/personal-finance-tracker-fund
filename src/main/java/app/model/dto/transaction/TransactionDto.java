package app.model.dto.transaction;

import app.model.dto.user.UserDto;
import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class TransactionDto {

    private UUID id;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "Please enter type of the transaction.")
    private TransactionType type;
    @NotNull(message = "Target date is required")
    private LocalDateTime date;
    @NotBlank(message = "Please enter category of the transaction.")
    private CategoryType category;
}
