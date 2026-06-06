package app.mapper.saving;

import app.model.dto.saving.SavingGoalsDto;
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
                .user(savingGoal.getUser())
                .name(savingGoal.getName())
                .targetAmount(savingGoal.getTargetAmount())
                .currentAmount(savingGoal.getCurrentAmount())
                .targetDate(savingGoal.getTargetDate())
                .build();
    }
}
