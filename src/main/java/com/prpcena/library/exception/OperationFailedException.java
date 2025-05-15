// src/main/java/com/yourusername/library/exception/OperationFailedException.java
// A more general exception for when an operation can't complete as expected
package com.prpcena.library.exception; // Adjust package name

public class OperationFailedException extends RuntimeException {
    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}