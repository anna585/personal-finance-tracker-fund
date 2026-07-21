package app.exeption.user;

import app.exeption.ApplicationException;

public class TargetDateInPastException extends ApplicationException {

    public TargetDateInPastException() {
        super(
                "The target date cannot be in the past.",
                "400",
                "Invalid Target Date"
        );
    }
}
