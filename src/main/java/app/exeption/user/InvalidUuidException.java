package app.exeption.user;

import app.exeption.ApplicationException;

public class InvalidUuidException extends ApplicationException {

    public InvalidUuidException() {
        super(
                "Invalid identifier.",
                "400",
                "Bad Request"
        );
    }
}
