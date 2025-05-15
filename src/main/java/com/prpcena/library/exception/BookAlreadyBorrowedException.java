// src/main/java/com/yourusername/library/exception/BookAlreadyBorrowedException.java
// (Optional for now, but good to have if a member can't borrow the same ISBN twice simultaneously)
package com.prpcena.library.exception; // Adjust package name

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(String message) {
        super(message);
    }
}