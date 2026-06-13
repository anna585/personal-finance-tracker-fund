package app.model.dto.transaction;

import app.model.entities.category.CategoryType;
import app.model.entities.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class IncomeTransactionRequest {

    private BigDecimal incomeAmount;
    @NotNull
    private CategoryType incomeCategory;

}
