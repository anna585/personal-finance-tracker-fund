package app.exeption.budget;

import app.exeption.ApplicationException;

public class BudgetNotEnoughException extends ApplicationException {


    public BudgetNotEnoughException() {

        super(
                "The monthly budget is not sufficient to create this transaction.",
                "409",
                "Budget Not Enough"
        );
    }
}
