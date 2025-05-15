// src/main/java/com/yourusername/library/repository/BookRepository.java
package com.prpcena.library.repository; // Adjust package name

import java.util.List;
import java.util.Optional;

import com.prpcena.library.model.Book;

public interface BookRepository {
    /**
     * Saves a new book or updates an existing one.
     * If a book with the same ISBN already exists, it should be updated.
     * Otherwise, the new book is added.
     * 
     * @param book The book to save.
     * @return The saved book.
     */
    Book save(Book book);

    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn The ISBN of the book to find.
     * @return An Optional containing the book if found, or an empty Optional
     *         otherwise.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Retrieves all books.
     * 
     * @return A list of all books. If no books exist, an empty list is returned.
     */
    List<Book> findAll();

    /**
     * Deletes a book by its ISBN.
     * 
     * @param isbn The ISBN of the book to delete.
     * @return true if a book was deleted, false otherwise.
     */
    boolean deleteByIsbn(String isbn);

    // (We'll add updateBook later if 'save' isn't sufficient, or make 'save'
    // smarter.
    // For now, 'save' can handle both add and update based on ISBN existence.
    // The 'updateBook' method mentioned in the initial plan might be merged into
    // 'save'
    // or added explicitly if a different return type or behavior is needed.)
}