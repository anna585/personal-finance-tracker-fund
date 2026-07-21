package app.exeption.budget;

import app.exeption.ApplicationException;

public class MonthlyBudgetExceededException extends ApplicationException {

    public MonthlyBudgetExceededException() {
        super(
                "The entered amount exceeds the remaining monthly budget.",
                "409",
                "Budget Exceeded"
        );
    }
}
