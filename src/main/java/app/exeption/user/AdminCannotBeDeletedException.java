package app.exeption.user;

import app.exeption.ApplicationException;

import java.util.UUID;

public class AdminCannotBeDeletedException extends ApplicationException {

    public AdminCannotBeDeletedException(UUID id){

        super(
                "ADMIN users cannot be deleted!",
                "403",
                "Cannot Delete!"
        );
    }
}
