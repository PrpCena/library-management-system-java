// src/main/java/com/yourusername/library/service/LibraryService.java
package com.prpcena.library.service; // Adjust package name

import java.time.Year;
import java.util.List;
import java.util.Optional;

import com.prpcena.library.model.Book;

public interface LibraryService {
    /**
     * Adds a new book to the library.
     * 
     * @param title           Title of the book.
     * @param authorFirstName First name of the author.
     * @param authorLastName  Last name of the author.
     * @param isbn            ISBN of the book.
     * @param genre           Genre of the book.
     * @param publicationYear Publication year of the book.
     * @param initialCopies   Number of copies available.
     * @return The newly added Book.
     * @throws com.yourusername.library.exception.BookAlreadyExistsException if a
     *                                                                       book
     *                                                                       with
     *                                                                       the
     *                                                                       same
     *                                                                       ISBN
     *                                                                       already
     *                                                                       exists
     *                                                                       (optional
     *                                                                       behavior)
     * @throws IllegalArgumentException                                      if
     *                                                                       inputs
     *                                                                       are
     *                                                                       invalid.
     */
    Book addBook(String title, String authorFirstName, String authorLastName, String isbn, String genre,
            Year publicationYear, int initialCopies);

    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn The ISBN of the book to find.
     * @return An Optional containing the book if found, or an empty Optional
     *         otherwise.
     */
    Optional<Book> findBookByIsbn(String isbn);

    /**
     * Retrieves all books in the library.
     * 
     * @return A list of all books.
     */
    List<Book> getAllBooks();

    /**
     * Removes a book from the library by its ISBN.
     * 
     * @param isbn The ISBN of the book to remove.
     * @return true if the book was successfully removed, false otherwise.
     */
    boolean removeBookByIsbn(String isbn);

    // (We'll add updateBook in a later iteration or refine addBook to handle
    // updates)
}