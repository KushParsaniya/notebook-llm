package dev.kush.notebookllm.exception;

import jnr.ffi.annotations.In;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
