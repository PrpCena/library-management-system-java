// src/main/java/com/prpcena/library/exception/BookNotBorrowedException.java
package com.prpcena.library.exception;

public class BookNotBorrowedException extends RuntimeException {
    public BookNotBorrowedException(String message) {
        super(message);
    }
}