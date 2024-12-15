package dev.kush.notebookllm.exception;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionMessage handleIllegalArgumentException(IllegalArgumentException e) {
        return new ExceptionMessage(e.getMessage(), 400);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionMessage handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ExceptionMessage(e.getMessage(), 404);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionMessage handleEntityExistsException(EntityExistsException e) {
        return new ExceptionMessage(e.getMessage(), 409);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionMessage handleRuntimeException(RuntimeException e) {
        return new ExceptionMessage(e.getMessage(), 500);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionMessage handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new ExceptionMessage(e.getMessage(), 401);
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionMessage handleInvalidBearerTokenException(InvalidBearerTokenException e) {
        return new ExceptionMessage(e.getMessage(), 401);
    }
}
