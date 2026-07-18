package app.mapper.user;

import app.mapper.budget.BudgetMapper;
import app.mapper.saving.SavingGoalsMapper;
import app.mapper.transaction.TransactionMapper;
import app.web.dto.budget.BudgetDto;
import app.web.dto.saving.SavingGoalsDto;
import app.web.dto.transaction.TransactionDto;
import app.web.dto.user.UserDto;
import app.web.dto.user.UserRegisterRequest;
import app.model.entities.user.User;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
public class UserMapper {

    public static User toUserEntity(UserRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null){
            return null;
        }

        return User.builder()
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .userRole(userRegisterRequest.getUserRole())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public static UserDto toUserDto(User user){

        if(user == null){
            return null;
        }

        List<TransactionDto> transactionDtoList = user.getTransactions()
                .stream()
                .map(TransactionMapper::toDto)
                .toList();

        List<BudgetDto> budgetDtoList = user.getBudgets()
                .stream()
                .map(BudgetMapper::toDto)
                .toList();

        List<SavingGoalsDto> savingGoalsDtoList = user.getSavingGoals()
                .stream()
                .map(SavingGoalsMapper::toDto)
                .toList();

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .transactions(transactionDtoList)
                .budgets(budgetDtoList)
                .saving(savingGoalsDtoList)
                .build();
    }
}
