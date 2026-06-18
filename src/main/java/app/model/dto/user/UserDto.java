package app.model.dto.user;

import app.model.dto.budget.BudgetDto;
import app.model.dto.saving.SavingGoalsDto;
import app.model.dto.transaction.TransactionDto;
import app.model.entities.user.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class UserDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole userRole;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private List<TransactionDto> transactions;
    private List<BudgetDto> budgets;
    private List<SavingGoalsDto> saving;
}
