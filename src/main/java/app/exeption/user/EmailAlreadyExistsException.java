package app.exeption.user;

import app.exeption.ApplicationException;

public class EmailAlreadyExistsException extends ApplicationException {

    public EmailAlreadyExistsException(String email){

        super(
                "User with email: " + email + " already exists!",
                "409",
                "Email Already Exist"
        );
    }
}
