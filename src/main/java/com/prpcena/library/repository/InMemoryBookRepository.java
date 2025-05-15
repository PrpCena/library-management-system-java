// src/main/java/com/yourusername/library/repository/InMemoryBookRepository.java
package com.prpcena.library.repository; // Adjust package name

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional; // Preserves insertion order, good for findAll
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prpcena.library.model.Book;

public class InMemoryBookRepository implements BookRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryBookRepository.class);
    private final Map<String, Book> books = new LinkedHashMap<>();
    // For basic thread safety if we were to simulate concurrent access later.
    // For a simple CLI, this might be overkill, but good practice to think about.
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Book save(Book book) {
        if (book == null || book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            logger.error("Attempted to save a null book or book with null/empty ISBN.");
            throw new IllegalArgumentException("Book and ISBN cannot be null or empty.");
        }
        lock.writeLock().lock();
        try {
            // This implementation will overwrite if ISBN exists, effectively handling
            // updates.
            books.put(book.getIsbn(), book);
            logger.info("Saved/Updated book with ISBN: {}", book.getIsbn());
            return book;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            logger.warn("Attempted to find book with null or empty ISBN.");
            return Optional.empty();
        }
        lock.readLock().lock();
        try {
            Book book = books.get(isbn);
            if (book != null) {
                logger.debug("Found book with ISBN: {}", isbn);
            } else {
                logger.debug("No book found with ISBN: {}", isbn);
            }
            return Optional.ofNullable(book);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Book> findAll() {
        lock.readLock().lock();
        try {
            logger.debug("Retrieving all books. Total count: {}", books.size());
            // Return a copy to prevent external modification of the internal list
            return new ArrayList<>(books.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean deleteByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            logger.warn("Attempted to delete book with null or empty ISBN.");
            return false;
        }
        lock.writeLock().lock();
        try {
            Book removedBook = books.remove(isbn);
            if (removedBook != null) {
                logger.info("Deleted book with ISBN: {}", isbn);
                return true;
            } else {
                logger.info("No book found with ISBN {} to delete.", isbn);
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}