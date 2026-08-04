package app.exeption.user;

import app.exeption.ApplicationException;

public class AccessDeniedException extends ApplicationException {


    public AccessDeniedException() {
        super(
                "Administrators cannot change their own role.",
                "403",
                "Can Not Change The Role!");
    }

}
