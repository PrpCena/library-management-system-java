// src/main/java/com/yourusername/library/exception/BookNotFoundException.java
package com.prpcena.library.exception; // Adjust package name

public class BookNotFoundException extends ResourceNotFoundException {
    public BookNotFoundException(String message) {
        super(message);
    }
}