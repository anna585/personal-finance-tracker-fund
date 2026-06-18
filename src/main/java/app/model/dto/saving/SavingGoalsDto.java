package app.model.dto.saving;

import app.model.dto.user.UserDto;
import app.model.entities.transaction.CategoryType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class SavingGoalsDto {

    private UUID id;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private CategoryType category;
}
