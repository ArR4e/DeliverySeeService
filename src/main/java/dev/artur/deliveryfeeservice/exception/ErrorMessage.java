package dev.artur.deliveryfeeservice.exception;

public class ErrorMessage {
    private int statusCode;
    private String error;
    private String message;
    private String description;

    public ErrorMessage(int statusCode, String error, String message, String description) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
