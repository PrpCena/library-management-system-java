// src/test/java/com/prpcena/library/service/LibraryServiceImplTest.java
package com.prpcena.library.service;

import java.time.LocalDate;
import java.time.LocalDateTime; // Book, Member, Author, Transaction, TransactionType
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times; // For more complex argument matching
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prpcena.library.exception.BookAlreadyBorrowedException;
import com.prpcena.library.exception.BookNotBorrowedException;
import com.prpcena.library.exception.BookNotFoundException;
import com.prpcena.library.exception.MemberNotFoundException;
import com.prpcena.library.exception.NoCopiesAvailableException;
import com.prpcena.library.exception.OperationFailedException;
import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.model.Transaction;
import com.prpcena.library.model.TransactionType;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.MemberRepository;
import com.prpcena.library.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private MemberRepository mockMemberRepository;

    @Mock
    private TransactionRepository mockTransactionRepository;

    @InjectMocks
    private LibraryServiceImpl libraryService;

    private Author author1;
    private Book book1;
    private Book book2;
    private Member member1;

    @BeforeEach
    void setUp() {
        author1 = new Author("Test", "Author");
        // Initialize book1 with a positive number of copies for borrowing tests
        book1 = new Book("Title 1", author1, "ISBN001", "Genre1", Year.of(2000), 5);
        book2 = new Book("Title 2", author1, "ISBN002", "Genre2", Year.of(2001), 3);
        member1 = new Member("Test Member", "test@example.com");
    }

    // --- Book Service Tests (from Iteration 1 & 2) ---
    @Test
    void addBook_ValidDetails_ShouldSaveAndReturnBook() {
        when(mockBookRepository.save(any(Book.class))).thenReturn(book1);
        Book addedBook = libraryService.addBook(
                book1.getTitle(), book1.getAuthor().getFirstName(), book1.getAuthor().getLastName(),
                book1.getIsbn(), book1.getGenre(), book1.getPublicationYear(), book1.getAvailableCopies());
        assertNotNull(addedBook);
        assertEquals(book1.getIsbn(), addedBook.getIsbn());
        verify(mockBookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void addBook_NullIsbn_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> libraryService.addBook("Title", "First", "Last", null, "Genre", Year.now(), 1));
        verify(mockBookRepository, never()).save(any(Book.class));
    }

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
        Optional<Book> foundBook = libraryService.findBookByIsbn(null);
        assertFalse(foundBook.isPresent());
        verify(mockBookRepository, never()).findByIsbn(any());
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
        boolean result = libraryService.removeBookByIsbn(null);
        assertFalse(result);
        verify(mockBookRepository, never()).deleteByIsbn(any());
    }

    // --- Member Service Tests (from Iteration 2) ---
    @Test
    void registerMember_ValidDetails_ShouldSaveAndReturnMember() {
        when(mockMemberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Member registeredMember = libraryService.registerMember("New Member", "new@example.com");
        assertNotNull(registeredMember);
        assertEquals("New Member", registeredMember.getName());
        assertNotNull(registeredMember.getMemberId());
        verify(mockMemberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void registerMember_NullName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> libraryService.registerMember(null, "contact@example.com"));
        verify(mockMemberRepository, never()).save(any(Member.class));
    }

    @Test
    void findMemberById_ExistingId_ShouldReturnMember() {
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        Optional<Member> foundMember = libraryService.findMemberById(member1.getMemberId());
        assertTrue(foundMember.isPresent());
        assertEquals(member1.getName(), foundMember.get().getName());
        verify(mockMemberRepository, times(1)).findById(member1.getMemberId());
    }

    @Test
    void findMemberById_NonExistingId_ShouldReturnEmptyOptional() {
        when(mockMemberRepository.findById("UNKNOWN_ID")).thenReturn(Optional.empty());
        Optional<Member> foundMember = libraryService.findMemberById("UNKNOWN_ID");
        assertFalse(foundMember.isPresent());
    }

    @Test
    void findMemberById_NullId_ShouldReturnEmptyOptional() {
        Optional<Member> foundMember = libraryService.findMemberById(null);
        assertFalse(foundMember.isPresent());
        verify(mockMemberRepository, never()).findById(any());
    }

    @Test
    void getAllMembers_ShouldReturnListOfMembers() {
        Member anotherMember = new Member("Another Member", "another@example.com");
        when(mockMemberRepository.findAll()).thenReturn(Arrays.asList(member1, anotherMember));
        List<Member> members = libraryService.getAllMembers();
        assertEquals(2, members.size());
        verify(mockMemberRepository, times(1)).findAll();
    }

    @Test
    void getAllMembers_WhenNoMembers_ShouldReturnEmptyList() {
        when(mockMemberRepository.findAll()).thenReturn(Collections.emptyList());
        List<Member> members = libraryService.getAllMembers();
        assertTrue(members.isEmpty());
    }

    // --- Borrowing Logic Tests (from Iteration 2 & 3) ---
    @Test
    void borrowBook_ValidMemberAndBook_BookAvailable_ShouldSucceedAndCreateTransaction() {
        Book availableBook = new Book("Borrowable Book", author1, "ISBN_BORROW", "Test", Year.now(), 1);
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(availableBook.getIsbn())).thenReturn(Optional.of(availableBook));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(),
                availableBook.getIsbn()))
                .thenReturn(Optional.empty()); // No existing open loan
        when(mockBookRepository.save(any(Book.class))).thenReturn(availableBook);
        when(mockTransactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> libraryService.borrowBook(member1.getMemberId(), availableBook.getIsbn()));

        assertEquals(0, availableBook.getAvailableCopies());
        verify(mockBookRepository, times(1)).save(availableBook);
        verify(mockTransactionRepository, times(1)).save(argThat(t -> t.getBookIsbn().equals(availableBook.getIsbn()) &&
                t.getMemberId().equals(member1.getMemberId()) &&
                t.getType() == TransactionType.BORROW &&
                t.getDueDate() != null));
    }

    @Test
    void borrowBook_MemberNotFound_ShouldThrowMemberNotFoundException() {
        when(mockMemberRepository.findById("UNKNOWN_MEMBER_ID")).thenReturn(Optional.empty());
        assertThrows(MemberNotFoundException.class,
                () -> libraryService.borrowBook("UNKNOWN_MEMBER_ID", book1.getIsbn()));
        verify(mockBookRepository, never()).findByIsbn(anyString());
        verify(mockBookRepository, never()).save(any(Book.class));
        verify(mockTransactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void borrowBook_BookNotFound_ShouldThrowBookNotFoundException() {
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn("UNKNOWN_ISBN")).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class,
                () -> libraryService.borrowBook(member1.getMemberId(), "UNKNOWN_ISBN"));
        verify(mockBookRepository, never()).save(any(Book.class));
        verify(mockTransactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void borrowBook_NoCopiesAvailable_ShouldThrowNoCopiesAvailableException() {
        Book unavailableBook = new Book("No Copies Book", author1, "ISBN_NOCOPY", "Test", Year.now(), 0);
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(unavailableBook.getIsbn())).thenReturn(Optional.of(unavailableBook));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(),
                unavailableBook.getIsbn()))
                .thenReturn(Optional.empty());

        assertThrows(NoCopiesAvailableException.class,
                () -> libraryService.borrowBook(member1.getMemberId(), unavailableBook.getIsbn()));
        verify(mockBookRepository, never()).save(any(Book.class));
        verify(mockTransactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void borrowBook_RepositorySaveFails_ShouldThrowOperationFailedException() {
        Book bookToBorrow = new Book("Borrow Test", author1, "ISBN_FAIL_SAVE", "Test", Year.now(), 1);
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(bookToBorrow.getIsbn())).thenReturn(Optional.of(bookToBorrow));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(),
                bookToBorrow.getIsbn()))
                .thenReturn(Optional.empty());
        // Simulate book save works, but transaction save fails
        when(mockBookRepository.save(any(Book.class))).thenReturn(bookToBorrow);
        when(mockTransactionRepository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(OperationFailedException.class,
                () -> libraryService.borrowBook(member1.getMemberId(), bookToBorrow.getIsbn()));

        // Copies would have been decremented before transaction save attempt
        assertEquals(0, bookToBorrow.getAvailableCopies());
        verify(mockBookRepository, times(1)).save(bookToBorrow); // Book save was attempted (and mocked as successful)
        verify(mockTransactionRepository, times(1)).save(any(Transaction.class)); // Transaction save was attempted
    }

    @Test
    void borrowBook_MemberAlreadyBorrowed_ShouldThrowBookAlreadyBorrowedException() {
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(book1.getIsbn())).thenReturn(Optional.of(book1));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(), book1.getIsbn()))
                .thenReturn(Optional
                        .of(new Transaction(book1.getIsbn(), member1.getMemberId(), LocalDate.now().plusDays(5))));

        assertThrows(BookAlreadyBorrowedException.class,
                () -> libraryService.borrowBook(member1.getMemberId(), book1.getIsbn()));
        verify(mockBookRepository, never()).save(any(Book.class));
        verify(mockTransactionRepository, never()).save(any(Transaction.class));
    }

    // --- Returning Book Tests (from Iteration 3) ---
    @Test
    void returnBook_Successful_ShouldUpdateTransactionAndIncreaseCopies() {
        Book bookToReturn = new Book("Borrowed Title", author1, "ISBN_RETURN", "Genre", Year.now(), 0);
        Transaction openTransaction = new Transaction(bookToReturn.getIsbn(), member1.getMemberId(),
                LocalDate.now().plusDays(7));
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(bookToReturn.getIsbn())).thenReturn(Optional.of(bookToReturn));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(),
                bookToReturn.getIsbn()))
                .thenReturn(Optional.of(openTransaction));
        when(mockBookRepository.save(any(Book.class))).thenReturn(bookToReturn);
        when(mockTransactionRepository.save(any(Transaction.class))).thenReturn(openTransaction);

        assertDoesNotThrow(() -> libraryService.returnBook(member1.getMemberId(), bookToReturn.getIsbn()));

        assertEquals(1, bookToReturn.getAvailableCopies());
        assertNotNull(openTransaction.getReturnDateTime());
        verify(mockBookRepository, times(1)).save(bookToReturn);
        verify(mockTransactionRepository, times(1)).save(openTransaction);
    }

    @Test
    void returnBook_NoOpenTransaction_ShouldThrowBookNotBorrowedException() {
        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        when(mockBookRepository.findByIsbn(book1.getIsbn())).thenReturn(Optional.of(book1));
        when(mockTransactionRepository.findOpenBorrowTransactionByMemberAndBook(member1.getMemberId(), book1.getIsbn()))
                .thenReturn(Optional.empty());

        assertThrows(BookNotBorrowedException.class,
                () -> libraryService.returnBook(member1.getMemberId(), book1.getIsbn()));
        verify(mockBookRepository, never()).save(any(Book.class));
        verify(mockTransactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getBorrowedBooksByMember_ShouldReturnActiveLoans() {
        Transaction tActive = new Transaction("ISBN001", member1.getMemberId(), LocalDate.now().plusDays(1));
        Transaction tReturned = new Transaction("ISBN002", member1.getMemberId(), LocalDate.now().plusDays(1));
        tReturned.setReturnDateTime(LocalDateTime.now());

        when(mockMemberRepository.findById(member1.getMemberId())).thenReturn(Optional.of(member1));
        // The service filters, so mock repo returning all for this member
        when(mockTransactionRepository.findByMemberId(member1.getMemberId())).thenReturn(List.of(tActive, tReturned));

        List<Transaction> borrowedBooks = libraryService.getBorrowedBooksByMember(member1.getMemberId());

        assertEquals(1, borrowedBooks.size());
        assertEquals("ISBN001", borrowedBooks.get(0).getBookIsbn());
    }

    @Test
    void getAllOverdueBooks_ShouldReturnOnlyOverdueOpenLoans() {
        Transaction overdueBook = new Transaction("ISBN_OVERDUE", "MEMBER_X", LocalDate.now().minusDays(1));
        Transaction notOverdueBook = new Transaction("ISBN_OK", "MEMBER_Y", LocalDate.now().plusDays(1));
        // This one is returned, so findAllOpenBorrowTransactions shouldn't include it.
        Transaction returnedOverdueBook = new Transaction("ISBN_RETOVER", "MEMBER_Z", LocalDate.now().minusDays(5));
        returnedOverdueBook.setReturnDateTime(LocalDateTime.now().minusDays(1));

        when(mockTransactionRepository.findAllOpenBorrowTransactions())
                .thenReturn(List.of(overdueBook, notOverdueBook));

        List<Transaction> overdueBooks = libraryService.getAllOverdueBooks();

        assertEquals(1, overdueBooks.size());
        assertEquals("ISBN_OVERDUE", overdueBooks.get(0).getBookIsbn());
    }

    // --- Search Service Tests (from Step 7 / Iteration 3 Enhancements) ---
    @Test
    void searchBooksByTitle_WhenBooksExist_ShouldReturnMatchingBooks() {
        Book bookA = new Book("Java Programming", author1, "ISBN100", "Education", Year.now(), 1);
        Book bookB = new Book("Advanced Java", author1, "ISBN101", "Education", Year.now(), 1);
        Book bookC = new Book("Python Basics", author1, "ISBN102", "Education", Year.now(), 1);
        List<Book> allBooks = Arrays.asList(bookA, bookB, bookC);

        when(mockBookRepository.findAll()).thenReturn(allBooks);

        List<Book> results = libraryService.searchBooksByTitle("Java");
        assertEquals(2, results.size());
        assertTrue(results.contains(bookA));
        assertTrue(results.contains(bookB));
        assertFalse(results.contains(bookC));
    }

    @Test
    void searchBooksByAuthor_WhenBooksExist_ShouldReturnMatchingBooks() {
        Author authorX = new Author("John", "Smith");
        Author authorY = new Author("Jane", "Smithson");
        Book bookX = new Book("Book By John", authorX, "ISBN200", "Fiction", Year.now(), 1);
        Book bookY = new Book("Another By Jane", authorY, "ISBN201", "Fiction", Year.now(), 1);
        List<Book> allBooks = Arrays.asList(bookX, bookY);

        when(mockBookRepository.findAll()).thenReturn(allBooks);

        List<Book> results = libraryService.searchBooksByAuthor("Smith");
        assertEquals(2, results.size());
        assertTrue(results.contains(bookX));
        assertTrue(results.contains(bookY));
    }

    @Test
    void searchBooksByGenre_WhenBooksExist_ShouldReturnMatchingBooks() {
        Book bookSciFi = new Book("Dune", author1, "ISBN300", "Science Fiction", Year.now(), 1);
        Book bookFantasy = new Book("Narnia", author1, "ISBN301", "Fantasy", Year.now(), 1);
        List<Book> allBooks = Arrays.asList(bookSciFi, bookFantasy);

        when(mockBookRepository.findAll()).thenReturn(allBooks);

        List<Book> results = libraryService.searchBooksByGenre("Science Fiction");
        assertEquals(1, results.size());
        assertTrue(results.contains(bookSciFi));
    }

    @Test
    void searchBooksByTitle_NoMatchingBooks_ShouldReturnEmptyList() {
        Book bookA = new Book("Java Programming", author1, "ISBN100", "Education", Year.now(), 1);
        List<Book> allBooks = Collections.singletonList(bookA); // Use singletonList for immutable single-element list
        when(mockBookRepository.findAll()).thenReturn(allBooks);

        List<Book> results = libraryService.searchBooksByTitle("Python");
        assertTrue(results.isEmpty());
    }
} // THIS IS THE FINAL CLOSING BRACE FOR THE CLASS