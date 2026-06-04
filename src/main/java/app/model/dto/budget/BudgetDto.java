package app.model.dto.budget;

import app.model.entities.user.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Month;
import java.util.UUID;

@Builder
@Data
public class BudgetDto {

    private UUID id;
    private BigDecimal monthlyLimit;
    private Month month;
    private int year;
    private User user;
}
