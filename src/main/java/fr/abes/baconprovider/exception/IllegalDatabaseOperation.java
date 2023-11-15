package fr.abes.baconprovider.exception;

public class IllegalDatabaseOperation extends Throwable {
    public IllegalDatabaseOperation(String message) {
        super(message);
    }

    public IllegalDatabaseOperation(String message, Throwable cause) {
        super(message, cause);
    }
}
