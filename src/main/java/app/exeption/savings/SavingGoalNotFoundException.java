package app.exeption.savings;

import app.exeption.ApplicationException;

import java.util.UUID;

public class SavingGoalNotFoundException extends ApplicationException {


    public SavingGoalNotFoundException(UUID id) {
        super(
                "SavingGoal does not exist.",
                "404",
                "Saving Goal Not Found");
    }
}
