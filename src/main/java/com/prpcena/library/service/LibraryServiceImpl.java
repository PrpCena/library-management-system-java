// src/main/java/com/yourusername/library/service/LibraryServiceImpl.java
package com.prpcena.library.service; // Adjust package name

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Optional; // New import
import java.util.stream.Collectors; // New import

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prpcena.library.exception.BookAlreadyBorrowedException;
import com.prpcena.library.exception.BookNotBorrowedException;
import com.prpcena.library.exception.BookNotFoundException;
import com.prpcena.library.exception.MemberNotFoundException;
import com.prpcena.library.exception.NoCopiesAvailableException;
import com.prpcena.library.exception.OperationFailedException;
import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.model.Transaction; // New import
import com.prpcena.library.model.TransactionType;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.MemberRepository;
import com.prpcena.library.repository.TransactionRepository;
import com.prpcena.library.service.search.AuthorSearchStrategy;
import com.prpcena.library.service.search.GenreSearchStrategy;
import com.prpcena.library.service.search.SearchStrategy;
import com.prpcena.library.service.search.TitleSearchStrategy;

public class LibraryServiceImpl implements LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository; 
    private final TransactionRepository transactionRepository; // New field
    private static final int DEFAULT_LOAN_DURATION_DAYS = 14; // e.g., 2 weeks
        private final SearchStrategy<Book> titleSearchStrategy = new TitleSearchStrategy();
    private final SearchStrategy<Book> authorSearchStrategy = new AuthorSearchStrategy();
    private final SearchStrategy<Book> genreSearchStrategy = new GenreSearchStrategy();

    // Updated Constructor Injection
    // Updated Constructor Injection
    public LibraryServiceImpl(BookRepository bookRepository, MemberRepository memberRepository,
            TransactionRepository transactionRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
        this.memberRepository = Objects.requireNonNull(memberRepository, "MemberRepository cannot be null.");
        this.transactionRepository = Objects.requireNonNull(transactionRepository,
                "TransactionRepository cannot be null.");
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

      // --- New Member Methods ---
    @Override
    public Member registerMember(String name, String contactInfo) {
        // Validation is handled by Member constructor
        Member newMember = new Member(name, contactInfo);
        logger.info("Registering new member: {}", newMember.getName());
        return memberRepository.save(newMember);
    }

    @Override
    public Optional<Member> findMemberById(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            logger.warn("Attempted to find member with null or empty ID in service.");
            return Optional.empty();
        }
        logger.debug("Service finding member by ID: {}", memberId);
        return memberRepository.findById(memberId);
    }

    @Override
    public List<Member> getAllMembers() {
        logger.debug("Service retrieving all members.");
        return memberRepository.findAll();
    }

    // --- Borrowing and Returning Methods ---
    @Override
    public void borrowBook(String memberId, String bookIsbn) {
        logger.info("Attempting to borrow book ISBN {} for member ID {}", bookIsbn, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        Book book = bookRepository.findByIsbn(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + bookIsbn + " not found."));

        if (transactionRepository.findOpenBorrowTransactionByMemberAndBook(memberId, bookIsbn).isPresent()) {
            logger.warn("Borrow failed: Member {} already has an open loan for book ISBN {}", memberId, bookIsbn);
            throw new BookAlreadyBorrowedException("Member " + member.getName() + " has already borrowed book '" + book.getTitle() + "'.");
        }

        if (book.getAvailableCopies() <= 0) {
            logger.warn("Borrow failed: No copies available for book ISBN {}", bookIsbn);
            throw new NoCopiesAvailableException("No copies available for book: " + book.getTitle());
        }

        try {
            book.decreaseAvailableCopies();
            bookRepository.save(book); // Persist the change in available copies

            LocalDate dueDate = LocalDate.now().plusDays(DEFAULT_LOAN_DURATION_DAYS);
            Transaction borrowTransaction = new Transaction(bookIsbn, memberId, dueDate);
            transactionRepository.save(borrowTransaction);

            logger.info("Book '{}' (ISBN: {}) successfully borrowed by member '{}' (ID: {}). Due date: {}. Copies remaining: {}",
                    book.getTitle(), bookIsbn, member.getName(), memberId, dueDate, book.getAvailableCopies());
        } catch (Exception e) {
            // This is a general catch. If bookRepository.save or transactionRepository.save fails.
            // A real system might need to roll back book.decreaseAvailableCopies() if it's not idempotent or if other parts failed.
            // For this in-memory version, the state of 'book' object might be inconsistent if not handled carefully.
            logger.error("Borrow operation failed unexpectedly for book ISBN {} and member ID {}", bookIsbn, memberId, e);
            // Re-increment copies if decreased but transaction failed, only if it makes sense and is safe.
            // book.increaseAvailableCopies(); // Risky without more context on failure point.
            throw new OperationFailedException("Failed to complete borrow operation for book " + bookIsbn, e);
        }
    }

    @Override
    public void returnBook(String memberId, String bookIsbn) {
        logger.info("Attempting to return book ISBN {} for member ID {}", bookIsbn, memberId);

        // Validate member exists (optional, as transaction check is primary)
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));

        Book book = bookRepository.findByIsbn(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN " + bookIsbn + " not found in catalog."));

        Transaction openTransaction = transactionRepository.findOpenBorrowTransactionByMemberAndBook(memberId, bookIsbn)
                .orElseThrow(() -> {
                    logger.warn("Return failed: No open borrow transaction found for member {} and book ISBN {}", memberId, bookIsbn);
                    return new BookNotBorrowedException("Book '" + book.getTitle() + "' was not found as borrowed by member ID " + memberId + " or already returned.");
                });

        try {
            book.increaseAvailableCopies();
            bookRepository.save(book); // Persist change in available copies

            openTransaction.setReturnDateTime(LocalDateTime.now());
            // Here you could also change transaction type if you had a separate RETURN record
            // but for "closing" a BORROW, just setting returnDateTime is fine.
            transactionRepository.save(openTransaction); // Update the transaction

            logger.info("Book '{}' (ISBN: {}) successfully returned by member ID {}. Overdue: {}",
                    book.getTitle(), bookIsbn, memberId, openTransaction.isOverdue());
            if (openTransaction.isOverdue()) {
                // TODO: Handle fines in a later iteration or as an extension
                System.out.println("Notification: Book '" + book.getTitle() + "' was returned LATE.");
                logger.warn("Book ISBN {} returned LATE by member ID {}. Due: {}, Returned: {}",
                           bookIsbn, memberId, openTransaction.getDueDate(), openTransaction.getReturnDateTime());
            }

        } catch (Exception e) {
            logger.error("Return operation failed unexpectedly for book ISBN {} and member ID {}", bookIsbn, memberId, e);
            // Consider rollback logic if applicable (e.g., if book save fails after transaction update or vice-versa)
            throw new OperationFailedException("Failed to complete return operation for book " + bookIsbn, e);
        }
    }

    @Override
    public List<Transaction> getBorrowedBooksByMember(String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found."));
        logger.debug("Fetching borrowed books for member ID: {}", memberId);
        return transactionRepository.findByMemberId(memberId).stream()
                .filter(t -> t.getType() == TransactionType.BORROW && t.getReturnDateTime() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getAllOverdueBooks() {
        logger.debug("Fetching all overdue books.");
        return transactionRepository.findAllOpenBorrowTransactions().stream()
                .filter(Transaction::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBooksByTitle(String titleQuery) {
        logger.debug("Searching books by title with query: '{}'", titleQuery);
        List<Book> allBooks = bookRepository.findAll();
        return titleSearchStrategy.search(allBooks, titleQuery);
    }

    @Override
    public List<Book> searchBooksByAuthor(String authorQuery) {
        logger.debug("Searching books by author with query: '{}'", authorQuery);
        List<Book> allBooks = bookRepository.findAll();
        return authorSearchStrategy.search(allBooks, authorQuery);
    }

    @Override
    public List<Book> searchBooksByGenre(String genreQuery) {
        logger.debug("Searching books by genre with query: '{}'", genreQuery);
        List<Book> allBooks = bookRepository.findAll();
        return genreSearchStrategy.search(allBooks, genreQuery);
    }
}