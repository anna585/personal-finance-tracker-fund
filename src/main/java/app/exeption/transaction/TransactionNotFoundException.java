package app.exeption.transaction;

import app.exeption.ApplicationException;

import java.util.UUID;

public class TransactionNotFoundException extends ApplicationException {

    public TransactionNotFoundException(UUID id) {
        super(
                "Transaction does not exist.",
                "409",
                "Transaction Not Found"
        );
    }
}
