package app.web.controllers.saving;

import app.model.entities.saving.SavingGoal;
import app.model.entities.transaction.Transaction;
import app.web.dto.saving.SavingGoalsDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class SavingGoalFactoryDto {

    public static SavingGoal getSavingGoals(){

        return SavingGoal.builder()
                .id(UUID.randomUUID())
                .goalName("Summer Holiday")
                .targetAmount(BigDecimal.valueOf(1000.00))
                .currentAmount(BigDecimal.valueOf(50.00))
                .transaction(new Transaction())
                .build();
    }

    public static SavingGoalsDto getSavingGoalsDto(){

        return SavingGoalsDto.builder()
                .id(UUID.randomUUID())
                .goalName("Winter Holiday")
                .targetAmount(BigDecimal.valueOf(7000.00))
                .currentAmount(BigDecimal.valueOf(500.00))
                .build();
    }
}
