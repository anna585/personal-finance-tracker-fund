package app.web.dto.saving;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
@Data
public class SavingRequest {


    @NotBlank(message = "Please enter description!")
    private String goalName;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal targetAmount;
    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Current amount cannot be negative")
    private BigDecimal currentAmount;
    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

}
