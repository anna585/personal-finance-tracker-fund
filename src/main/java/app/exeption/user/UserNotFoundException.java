package app.exeption.user;

import app.exeption.ApplicationException;

import java.util.UUID;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(UUID id){

        super(
                "User with Id: " + id +  " not found!",
                "404",
                "Not Found");
    }
}
