package app.web.dto.user;

import app.web.dto.budget.BudgetDto;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.transaction.TransactionDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UsersDetails {

    private List<UserDto> users;
    private List<TransactionDto> transactions;
    private List<BudgetDto> budgets;
    private List<SavingGoalsDto> saving;
}
