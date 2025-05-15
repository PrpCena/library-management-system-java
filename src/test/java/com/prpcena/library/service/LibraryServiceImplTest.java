// src/test/java/com/yourusername/library/service/LibraryServiceImplTest.java
package com.prpcena.library.service; // Adjust package name

import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import com.prpcena.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Integrates Mockito with JUnit 5
class LibraryServiceImplTest {

    @Mock // Mockito creates a mock implementation for BookRepository
    private BookRepository mockBookRepository;

    @InjectMocks // Mockito injects the mockBookRepository into libraryService
    private LibraryServiceImpl libraryService;

    private Author author1;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Mocks are reinitialized for each test by MockitoExtension
        author1 = new Author("Test", "Author");
        book1 = new Book("Title 1", author1, "ISBN001", "Genre1", Year.of(2000), 5);
        book2 = new Book("Title 2", author1, "ISBN002", "Genre2", Year.of(2001), 3);
    }

    @Test
    void addBook_ValidDetails_ShouldSaveAndReturnBook() {
        // Arrange: Define behavior of the mock repository
        when(mockBookRepository.save(any(Book.class))).thenReturn(book1);
        // when(mockBookRepository.findByIsbn(book1.getIsbn())).thenReturn(Optional.empty());
        // // If checking for existing

        // Act: Call the service method
        Book addedBook = libraryService.addBook(
                book1.getTitle(),
                book1.getAuthor().getFirstName(),
                book1.getAuthor().getLastName(),
                book1.getIsbn(),
                book1.getGenre(),
                book1.getPublicationYear(),
                book1.getAvailableCopies());

        // Assert: Verify results and interactions
        assertNotNull(addedBook);
        assertEquals(book1.getIsbn(), addedBook.getIsbn());
        verify(mockBookRepository, times(1)).save(any(Book.class)); // Verify save was called once
        // verify(mockBookRepository, times(1)).findByIsbn(book1.getIsbn()); // If
        // checking
    }

    @Test
    void addBook_NullIsbn_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.addBook("Title", "First", "Last", null, "Genre", Year.now(), 1));
        verify(mockBookRepository, never()).save(any(Book.class)); // Ensure save was not called
    }

    // Example of testing the BookAlreadyExistsException if you implement it
    /*
     * @Test
     * void addBook_ExistingIsbn_ShouldThrowBookAlreadyExistsException() {
     * // Arrange
     * when(mockBookRepository.findByIsbn(book1.getIsbn())).thenReturn(Optional.of(
     * book1));
     * 
     * // Act & Assert
     * assertThrows(BookAlreadyExistsException.class, () ->
     * libraryService.addBook(
     * book1.getTitle(), book1.getAuthor().getFirstName(),
     * book1.getAuthor().getLastName(),
     * book1.getIsbn(), book1.getGenre(), book1.getPublicationYear(),
     * book1.getAvailableCopies()
     * )
     * );
     * verify(mockBookRepository, never()).save(any(Book.class));
     * }
     */

    @Test
    void findBookByIsbn_ExistingIsbn_ShouldReturnBook() {
        when(mockBookRepository.findByIsbn("ISBN001")).thenReturn(Optional.of(book1));

        Optional<Book> foundBook = libraryService.findBookByIsbn("ISBN001");

        assertTrue(foundBook.isPresent());
        assertEquals(book1.getTitle(), foundBook.get().getTitle());
        verify(mockBookRepository, times(1)).findByIsbn("ISBN001");
    }

    @Test
    void findBookByIsbn_NonExistingIsbn_ShouldReturnEmptyOptional() {
        when(mockBookRepository.findByIsbn(anyString())).thenReturn(Optional.empty());

        Optional<Book> foundBook = libraryService.findBookByIsbn("NONEXISTENT");

        assertFalse(foundBook.isPresent());
        verify(mockBookRepository, times(1)).findByIsbn("NONEXISTENT");
    }

    @Test
    void findBookByIsbn_NullIsbn_ShouldReturnEmptyOptional() {
        // The service layer method itself handles null/empty ISBNs before calling the
        // repository
        Optional<Book> foundBook = libraryService.findBookByIsbn(null);
        assertFalse(foundBook.isPresent());
        verify(mockBookRepository, never()).findByIsbn(any()); // Repository method should not be called
    }

    @Test
    void getAllBooks_WhenBooksExist_ShouldReturnListOfBooks() {
        when(mockBookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> books = libraryService.getAllBooks();

        assertEquals(2, books.size());
        verify(mockBookRepository, times(1)).findAll();
    }

    @Test
    void getAllBooks_WhenNoBooksExist_ShouldReturnEmptyList() {
        when(mockBookRepository.findAll()).thenReturn(Collections.emptyList());

        List<Book> books = libraryService.getAllBooks();

        assertTrue(books.isEmpty());
        verify(mockBookRepository, times(1)).findAll();
    }

    @Test
    void removeBookByIsbn_ExistingIsbn_ShouldReturnTrue() {
        when(mockBookRepository.deleteByIsbn("ISBN001")).thenReturn(true);

        boolean result = libraryService.removeBookByIsbn("ISBN001");

        assertTrue(result);
        verify(mockBookRepository, times(1)).deleteByIsbn("ISBN001");
    }

    @Test
    void removeBookByIsbn_NonExistingIsbn_ShouldReturnFalse() {
        when(mockBookRepository.deleteByIsbn("NONEXISTENT")).thenReturn(false);

        boolean result = libraryService.removeBookByIsbn("NONEXISTENT");

        assertFalse(result);
        verify(mockBookRepository, times(1)).deleteByIsbn("NONEXISTENT");
    }

    @Test
    void removeBookByIsbn_NullIsbn_ShouldReturnFalse() {
        // Service layer handles null ISBN
        boolean result = libraryService.removeBookByIsbn(null);
        assertFalse(result);
        verify(mockBookRepository, never()).deleteByIsbn(any());
    }
}