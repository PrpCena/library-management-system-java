// src/test/java/com/prpcena/library/service/search/TitleSearchStrategyTest.java
package com.prpcena.library.service.search;

import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TitleSearchStrategyTest {
    private SearchStrategy<Book> strategy;
    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        strategy = new TitleSearchStrategy();
        Author author = new Author("Test", "Author");
        book1 = new Book("The Lord of the Rings", author, "ISBN001", "Fantasy", Year.of(1954), 1);
        book2 = new Book("The Hobbit", author, "ISBN002", "Fantasy", Year.of(1937), 1);
        book3 = new Book("Another Great Book", new Author("Jane", "Doe"), "ISBN003", "Fiction", Year.of(2000), 1);
    }

    @Test
    void search_WithMatchingQuery_ShouldReturnMatchingBooks() {
        List<Book> books = Arrays.asList(book1, book2, book3);
        List<Book> results = strategy.search(books, "Lord");
        assertEquals(1, results.size());
        assertTrue(results.contains(book1));
    }

    @Test
    void search_WithPartialMatchingQuery_ShouldReturnMatchingBooks() {
        List<Book> books = Arrays.asList(book1, book2, book3); // book3 is "Another Great Book"
        List<Book> results = strategy.search(books, "the"); // "the" is in "The", "The", and "Another"
        assertEquals(3, results.size(), "Expected 3 books containing 'the'"); // ADJUSTED
        assertTrue(results.contains(book1), "Book1 ('The Lord of the Rings') should be found");
        assertTrue(results.contains(book2), "Book2 ('The Hobbit') should be found");
        assertTrue(results.contains(book3),
                "Book3 ('Another Great Book') should be found because 'the' is in 'Another'"); // ADDED
    }

    @Test
    void search_WithNonMatchingQuery_ShouldReturnEmptyList() {
        List<Book> books = Arrays.asList(book1, book2, book3);
        List<Book> results = strategy.search(books, "NonExistent");
        assertTrue(results.isEmpty());
    }

    @Test
    void search_WithEmptyQuery_ShouldReturnAllBooks() {
        List<Book> books = Arrays.asList(book1, book2, book3);
        List<Book> results = strategy.search(books, "");
        assertEquals(3, results.size());
    }

    @Test
    void search_WithNullQuery_ShouldReturnAllBooks() {
        List<Book> books = Arrays.asList(book1, book2, book3);
        List<Book> results = strategy.search(books, null);
        assertEquals(3, results.size());
    }

    @Test
    void search_WithEmptyBookList_ShouldReturnEmptyList() {
        List<Book> results = strategy.search(Collections.emptyList(), "Query");
        assertTrue(results.isEmpty());
    }
}