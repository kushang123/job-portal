package com.kushang.jobportal.exception;

public class InvalidApplicationStatusException extends RuntimeException {
    public InvalidApplicationStatusException(String message) {
        super(message);
    }
}