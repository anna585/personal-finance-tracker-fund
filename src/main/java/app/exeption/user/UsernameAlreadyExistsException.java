package app.exeption.user;

import app.exeption.ApplicationException;

public class UsernameAlreadyExistsException extends ApplicationException {

    public UsernameAlreadyExistsException(String username){

        super(
                "User with username: " + username + " already exists!",
                "409",
                "Username Already Exists"
        );
    }
}
