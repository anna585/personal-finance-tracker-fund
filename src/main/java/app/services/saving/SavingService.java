package app.services.saving;

import app.model.entities.saving.SavingGoal;
import app.model.entities.saving.SavingType;
import app.model.entities.user.User;
import app.repositories.saving.SavingRepository;
import org.springframework.stereotype.Service;

@Service
public class SavingService {

    private final SavingRepository savingRepository;

    public SavingService(SavingRepository savingRepository) {
        this.savingRepository = savingRepository;
    }

    public SavingGoal createDefaultSaving(User user){

        SavingGoal savingGoal = SavingGoal.builder()
                .user(user)
                .goalType(SavingType.OTHER)
                .build();

        savingRepository.save(savingGoal);

        return savingGoal;
    }
}
