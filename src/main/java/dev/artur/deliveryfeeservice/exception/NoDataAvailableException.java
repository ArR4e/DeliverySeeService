package dev.artur.deliveryfeeservice.exception;

public class NoDataAvailableException extends RuntimeException {
    public NoDataAvailableException() {
        super("The database currently has no entries for specified city, please wait for about an hour");
    }
}
