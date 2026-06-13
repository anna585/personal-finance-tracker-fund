package app.model.dto.budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class MonthlyBudgetRequest {

    @Positive
    @NotNull
    private BigDecimal monthlyBudget;
    private BigDecimal spent;
    private BigDecimal remaining;
}
