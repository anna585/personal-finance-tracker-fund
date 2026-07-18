package app.web.dto.transaction;

import app.model.entities.transaction.CategoryType;
import app.model.entities.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotNull(message = "Please select an appropriate type of the transaction.")
    private TransactionType type;
    @NotNull(message = "Please select an appropriate category of the transaction.")
    private CategoryType category;
}
