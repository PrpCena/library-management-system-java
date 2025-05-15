// src/main/java/com/yourusername/library/exception/NoCopiesAvailableException.java
package com.prpcena.library.exception; // Adjust package name

public class NoCopiesAvailableException extends RuntimeException {
    public NoCopiesAvailableException(String message) {
        super(message);
    }
}