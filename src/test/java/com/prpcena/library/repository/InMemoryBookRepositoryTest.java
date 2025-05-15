// src/test/java/com/yourusername/library/repository/InMemoryBookRepositoryTest.java
package com.prpcena.library.repository; // Adjust package name

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;

class InMemoryBookRepositoryTest {
    private BookRepository bookRepository;
    private Author author1;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        bookRepository = new InMemoryBookRepository();
        author1 = new Author("Test", "Author");
        // Use distinct ISBNs for book1 and book2 for clarity in tests
        book1 = new Book("Title 1", author1, "ISBN001", "Genre1", Year.of(2000), 5);
        book2 = new Book("Title 2", author1, "ISBN002", "Genre2", Year.of(2001), 3);
    }

    @Test
    void save_NewBook_ShouldAddBook() {
        Book savedBook = bookRepository.save(book1);
        assertNotNull(savedBook);
        assertEquals(book1.getIsbn(), savedBook.getIsbn());
        assertEquals(1, bookRepository.findAll().size());
    }

    @Test
    void save_ExistingBook_ShouldUpdateBook() {
        bookRepository.save(book1); // Save initial version
        Book updatedBook1 = new Book("Title 1 Updated", author1, "ISBN001", "Genre1 Updated", Year.of(2000), 10);
        Book savedBook = bookRepository.save(updatedBook1);

        assertNotNull(savedBook);
        assertEquals("ISBN001", savedBook.getIsbn());
        assertEquals("Title 1 Updated", savedBook.getTitle());
        assertEquals(10, savedBook.getAvailableCopies());
        assertEquals(1, bookRepository.findAll().size()); // Should still be 1 book
    }

    @Test
    void save_NullBook_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> bookRepository.save(null));
    }
    
    @Test
    void findByIsbn_ExistingIsbn_ShouldReturnBook() {
        bookRepository.save(book1);
        Optional<Book> foundBook = bookRepository.findByIsbn("ISBN001");
        assertTrue(foundBook.isPresent());
        assertEquals(book1.getTitle(), foundBook.get().getTitle());
    }

    @Test
    void findByIsbn_NonExistingIsbn_ShouldReturnEmptyOptional() {
        Optional<Book> foundBook = bookRepository.findByIsbn("NONEXISTENT_ISBN");
        assertFalse(foundBook.isPresent());
    }

    @Test
    void findByIsbn_NullIsbn_ShouldReturnEmptyOptional() {
        Optional<Book> foundBook = bookRepository.findByIsbn(null);
        assertFalse(foundBook.isPresent());
    }

    @Test
    void findAll_WhenBooksExist_ShouldReturnAllBooks() {
        bookRepository.save(book1);
        bookRepository.save(book2);
        List<Book> books = bookRepository.findAll();
        assertEquals(2, books.size());
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book2));
    }

    @Test
    void findAll_WhenNoBooksExist_ShouldReturnEmptyList() {
        List<Book> books = bookRepository.findAll();
        assertTrue(books.isEmpty());
    }

    @Test
    void deleteByIsbn_ExistingIsbn_ShouldDeleteBookAndReturnTrue() {
        bookRepository.save(book1);
        assertTrue(bookRepository.deleteByIsbn("ISBN001"));
        assertFalse(bookRepository.findByIsbn("ISBN001").isPresent());
        assertEquals(0, bookRepository.findAll().size());
    }

    @Test
    void deleteByIsbn_NonExistingIsbn_ShouldReturnFalse() {
        assertFalse(bookRepository.deleteByIsbn("NONEXISTENT_ISBN"));
    }

    @Test
    void deleteByIsbn_NullIsbn_ShouldReturnFalse() {
        assertFalse(bookRepository.deleteByIsbn(null));
    }
}