// src/main/java/com/yourusername/library/service/LibraryServiceImpl.java
package com.prpcena.library.service; // Adjust package name

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import com.prpcena.library.repository.BookRepository;

public class LibraryServiceImpl implements LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);
    private final BookRepository bookRepository;

    // Constructor Injection for BookRepository
    public LibraryServiceImpl(BookRepository bookRepository) {
        if (bookRepository == null) {
            logger.error("BookRepository cannot be null for LibraryServiceImpl initialization.");
            throw new IllegalArgumentException("BookRepository cannot be null.");
        }
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(String title, String authorFirstName, String authorLastName, String isbn, String genre,
            Year publicationYear, int initialCopies) {
        // Basic validation (more can be added, or handled by Book/Author constructors)
        if (isbn == null || isbn.trim().isEmpty()) {
            logger.warn("Attempted to add book with null or empty ISBN.");
            throw new IllegalArgumentException("ISBN cannot be null or empty for addBook.");
        }
        // Optional: Check if book already exists if 'addBook' should not update
        // if (bookRepository.findByIsbn(isbn).isPresent()) {
        // logger.warn("Attempted to add a book with existing ISBN: {}", isbn);
        // throw new BookAlreadyExistsException("Book with ISBN " + isbn + " already
        // exists.");
        // }

        Author author = new Author(authorFirstName, authorLastName); // Validation within Author constructor
        Book newBook = new Book(title, author, isbn, genre, publicationYear, initialCopies); // Validation within Book
                                                                                             // constructor

        logger.info("Adding new book with ISBN: {}", isbn);
        return bookRepository.save(newBook);
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            logger.warn("Attempted to find book with null or empty ISBN in service.");
            return Optional.empty(); // Or throw IllegalArgumentException
        }
        logger.debug("Service finding book by ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> getAllBooks() {
        logger.debug("Service retrieving all books.");
        return bookRepository.findAll();
    }

    @Override
    public boolean removeBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            logger.warn("Attempted to remove book with null or empty ISBN in service.");
            return false; // Or throw IllegalArgumentException
        }
        // Optional: Add check if book exists before trying to delete, or rely on
        // repository's return
        logger.info("Service attempting to remove book with ISBN: {}", isbn);
        boolean deleted = bookRepository.deleteByIsbn(isbn);
        if (deleted) {
            logger.info("Book with ISBN {} removed successfully by service.", isbn);
        } else {
            logger.info("Book with ISBN {} not found or could not be removed by service.", isbn);
        }
        return deleted;
    }
}