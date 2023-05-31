package dev.artur.deliveryfeeservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenVehicleUsageException.class)
    public ResponseEntity<ErrorMessage> handleForbiddenVehicleUsage(ForbiddenVehicleUsageException e) {
        //Decided to use 422 - unprocessable entity:
        //request is correct, but calculation cannot be performed because of constraints
        ErrorMessage msg = new ErrorMessage(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "Usage of selected vehicle type is forbidden",
                e.getMessage()
        );
        return new ResponseEntity<>(msg, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoDataAvailableException.class)
    public ResponseEntity<ErrorMessage> handleNoDataAvailable(NoDataAvailableException e) {
        ErrorMessage msg = new ErrorMessage(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "Currently, it is impossible to calculate fee",
                e.getMessage()
        );
        return new ResponseEntity<>(msg, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
