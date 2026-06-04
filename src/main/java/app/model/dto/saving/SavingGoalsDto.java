package app.model.dto.saving;

import app.model.entities.saving.SavingType;
import app.model.entities.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class SavingGoalsDto {

    private UUID id;
    private SavingType goalType;
    private User user;
}
