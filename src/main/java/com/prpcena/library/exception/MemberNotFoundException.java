// src/main/java/com/yourusername/library/exception/MemberNotFoundException.java
package com.prpcena.library.exception; // Adjust package name

public class MemberNotFoundException extends ResourceNotFoundException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}