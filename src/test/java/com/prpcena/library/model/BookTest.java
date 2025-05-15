package com.prpcena.library.model; // Adjust package name

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class BookTest {
    private final Author testAuthor = new Author("Test", "Author");

    @Test
    void testBookCreation_Valid() {
        Book book = new Book("The Great Test", testAuthor, "1234567890", "Testing", Year.of(2023), 5);
        assertEquals("The Great Test", book.getTitle());
        assertEquals(testAuthor, book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals("Testing", book.getGenre());
        assertEquals(Year.of(2023), book.getPublicationYear());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    void testBookCreation_NullTitle_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(null, testAuthor, "123", "Genre", Year.now(), 1));
    }

    @Test
    void testBookCreation_NegativeCopies_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Title", testAuthor, "123", "Genre", Year.now(), -1));
    }

    @Test
    void testDecreaseAvailableCopies() {
        Book book = new Book("Test Book", testAuthor, "111", "Test", Year.now(), 2);
        book.decreaseAvailableCopies();
        assertEquals(1, book.getAvailableCopies());
        book.decreaseAvailableCopies();
        assertEquals(0, book.getAvailableCopies());
        book.decreaseAvailableCopies(); // Try to decrease below zero
        assertEquals(0, book.getAvailableCopies(), "Copies should not go below zero.");
    }

    @Test
    void testIncreaseAvailableCopies() {
        Book book = new Book("Test Book", testAuthor, "222", "Test", Year.now(), 1);
        book.increaseAvailableCopies();
        assertEquals(2, book.getAvailableCopies());
    }

    @Test
    void testEqualsAndHashCode_BasedOnIsbn() {
        Book book1 = new Book("Title A", new Author("FirstA", "LastA"), "ISBN001", "GenreA", Year.of(2000), 1);
        Book book2 = new Book("Title B", new Author("FirstB", "LastB"), "ISBN001", "GenreB", Year.of(2001), 2); // Same
                                                                                                                // ISBN
        Book book3 = new Book("Title C", new Author("FirstC", "LastC"), "ISBN002", "GenreC", Year.of(2002), 3); // Different
                                                                                                                // ISBN

        assertEquals(book1, book2, "Books with the same ISBN should be equal.");
        assertEquals(book1.hashCode(), book2.hashCode(), "Hashcodes for books with same ISBN should be equal.");
        assertNotEquals(book1, book3, "Books with different ISBNs should not be equal.");
    }
}