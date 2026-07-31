package app.web.dto.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class MonthlyBudgetRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Budget cannot be negative.")
    private BigDecimal monthlyBudget;
}
