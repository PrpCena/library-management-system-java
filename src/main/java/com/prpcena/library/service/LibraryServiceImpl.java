// src/main/java/com/yourusername/library/service/LibraryServiceImpl.java
package com.prpcena.library.service; // Adjust package name

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prpcena.library.exception.BookNotFoundException;
import com.prpcena.library.exception.MemberNotFoundException;
import com.prpcena.library.exception.NoCopiesAvailableException;
import com.prpcena.library.exception.OperationFailedException;
import com.prpcena.library.model.Author;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.MemberRepository;

public class LibraryServiceImpl implements LibraryService {
    private static final Logger logger = LoggerFactory.getLogger(LibraryServiceImpl.class);
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository; 

    // Updated Constructor Injection
    public LibraryServiceImpl(BookRepository bookRepository, MemberRepository memberRepository) {
        if (bookRepository == null) {
            logger.error("BookRepository cannot be null for LibraryServiceImpl initialization.");
            throw new IllegalArgumentException("BookRepository cannot be null.");
        }
        if (memberRepository == null) {
            logger.error("MemberRepository cannot be null for LibraryServiceImpl initialization.");
            throw new IllegalArgumentException("MemberRepository cannot be null.");
        }
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository; // Initialize
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

    // --- New Borrowing Method ---
    @Override
    public void borrowBook(String memberId, String bookIsbn) {
        logger.info("Attempting to borrow book ISBN {} for member ID {}", bookIsbn, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    logger.warn("Borrow failed: Member not found with ID {}", memberId);
                    return new MemberNotFoundException("Member with ID " + memberId + " not found.");
                });

        Book book = bookRepository.findByIsbn(bookIsbn)
                .orElseThrow(() -> {
                    logger.warn("Borrow failed: Book not found with ISBN {}", bookIsbn);
                    return new BookNotFoundException("Book with ISBN " + bookIsbn + " not found.");
                });

        if (book.getAvailableCopies() <= 0) {
            logger.warn("Borrow failed: No copies available for book ISBN {}", bookIsbn);
            throw new NoCopiesAvailableException("No copies available for book: " + book.getTitle());
        }

        // Optional: Check if member already borrowed this specific book instance (if rules apply)
        // This simple implementation doesn't track which specific copy is borrowed, just that a book of this ISBN is borrowed.
        // For a more robust system, you'd have a Loan entity/table.
        // For now, we'll assume a member can borrow multiple copies of the same ISBN if they are available
        // or that the "BookAlreadyBorrowed" refers to a specific item instance.
        // Let's simplify and not implement BookAlreadyBorrowedException for *this iteration* unless a member has a list of borrowed ISBNs.

        // If we add a list of borrowed book ISBNs to the Member class:
        // if (member.getBorrowedBookIsbns().contains(bookIsbn)) {
        //     throw new BookAlreadyBorrowedException("Member " + memberId + " has already borrowed book " + bookIsbn);
        // }

        try {
            book.decreaseAvailableCopies(); // This mutates the Book object
            bookRepository.save(book); // Persist the change in available copies

            // TODO: Record the transaction. For this iteration, we could:
            // 1. Add a List<String> borrowedBookIsbns to Member (simplest for now)
            //    (This requires Member to be mutable and getters/setters for this list)
            // 2. Create a Transaction object and store it in a new TransactionRepository (more robust, for later)
            // For now, let's go with a conceptual "transaction recorded" log.
            // We'll enhance this in Iteration 3 with a Transaction class.

            logger.info("Book '{}' (ISBN: {}) successfully borrowed by member '{}' (ID: {}). Copies remaining: {}",
                    book.getTitle(), bookIsbn, member.getName(), memberId, book.getAvailableCopies());
        } catch (Exception e) {
            // This catch block is a fallback. Specific exceptions should be handled above.
            // If decreaseAvailableCopies or save fails unexpectedly.
            logger.error("Borrow operation failed unexpectedly for book ISBN {} and member ID {}", bookIsbn, memberId, e);
            // Potentially revert any partial changes if in a real transactional system.
            throw new OperationFailedException("Failed to complete borrow operation for book " + bookIsbn, e);
        }
    }
}