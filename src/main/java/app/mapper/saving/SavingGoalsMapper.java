package app.mapper.saving;

import app.web.dto.saving.SavingGoalsDto;
import app.model.entities.saving.SavingGoal;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SavingGoalsMapper {

    public static SavingGoalsDto toDto(SavingGoal savingGoal){


        if(savingGoal == null){
            return null;
        }
        return SavingGoalsDto.builder()
                .id(savingGoal.getId())
                .goalName(savingGoal.getGoalName())
                .targetAmount(savingGoal.getTargetAmount())
                .currentAmount(savingGoal.getCurrentAmount())
                .targetDate(savingGoal.getTargetDate())
                .build();
    }
}
