package app.exeption.budget;

import app.exeption.ApplicationException;

import java.util.UUID;

public class BudgetNotFoundException extends ApplicationException {

    public BudgetNotFoundException(UUID id) {

        super(
                "No budget found. Please create a budget first.",
                "404",
                "No Budget Found");
    }
}
