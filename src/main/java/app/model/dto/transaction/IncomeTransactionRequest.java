package app.model.dto.transaction;

import app.model.entities.transaction.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class IncomeTransactionRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal incomeAmount;
    @NotNull(message = "Please select an appropriate category of the transaction.")
    private CategoryType incomeCategory;

}
