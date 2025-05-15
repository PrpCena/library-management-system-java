// src/main/java/com/yourusername/library/exception/ResourceNotFoundException.java
package com.prpcena.library.exception; // Adjust package name

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}