package dev.artur.deliveryfeeservice.exception;

public class ForbiddenVehicleUsageException extends RuntimeException {
    public ForbiddenVehicleUsageException(String message) {
        super(message);
    }

}
