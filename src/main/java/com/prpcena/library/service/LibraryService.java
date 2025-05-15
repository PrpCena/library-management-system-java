// src/main/java/com/yourusername/library/service/LibraryService.java
package com.prpcena.library.service; // Adjust package name

import java.time.Year;
import java.util.List;
import java.util.Optional;

import com.prpcena.library.exception.BookNotBorrowedException;
import com.prpcena.library.exception.BookNotFoundException;
import com.prpcena.library.exception.MemberNotFoundException;
import com.prpcena.library.exception.OperationFailedException;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.model.Transaction;

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

    // New Member methods
    /**
     * Registers a new member in the library.
     * 
     * @param name        The name of the member.
     * @param contactInfo The contact information (e.g., email) of the member.
     * @return The newly registered Member.
     * @throws IllegalArgumentException if inputs are invalid.
     */
    Member registerMember(String name, String contactInfo);

    /**
     * Finds a member by their ID.
     * 
     * @param memberId The ID of the member to find.
     * @return An Optional containing the member if found, or an empty Optional
     *         otherwise.
     */
    Optional<Member> findMemberById(String memberId);

    /**
     * Retrieves all registered members.
     * 
     * @return A list of all members.
     */
    List<Member> getAllMembers(); // New method

    // New Borrowing method
    /**
     * Allows a member to borrow a book.
     * 
     * @param memberId The ID of the member borrowing the book.
     * @param bookIsbn The ISBN of the book to be borrowed.
     * @throws com.yourusername.library.exception.MemberNotFoundException      if
     *                                                                         the
     *                                                                         member
     *                                                                         is
     *                                                                         not
     *                                                                         found.
     * @throws com.yourusername.library.exception.BookNotFoundException        if
     *                                                                         the
     *                                                                         book
     *                                                                         is
     *                                                                         not
     *                                                                         found.
     * @throws com.yourusername.library.exception.NoCopiesAvailableException   if no
     *                                                                         copies
     *                                                                         of
     *                                                                         the
     *                                                                         book
     *                                                                         are
     *                                                                         available.
     * @throws com.yourusername.library.exception.BookAlreadyBorrowedException (optional)
     *                                                                         if
     *                                                                         member
     *                                                                         already
     *                                                                         has
     *                                                                         this
     *                                                                         book.
     * @throws com.yourusername.library.exception.OperationFailedException     if
     *                                                                         the
     *                                                                         borrowing
     *                                                                         operation
     *                                                                         fails
     *                                                                         for
     *                                                                         other
     *                                                                         reasons.
     */
    void borrowBook(String memberId, String bookIsbn);

    // (We'll add returnBook in Iteration 3)
    /**
     * Allows a member to return a book.
     * @param memberId The ID of the member returning the book.
     * @param bookIsbn The ISBN of the book being returned.
     * @throws MemberNotFoundException if the member is not found.
     * @throws BookNotFoundException if the book (by ISBN) is not found in general catalog (though less likely to be the primary issue here).
     * @throws BookNotBorrowedException if there's no record of this member borrowing this book or it's already returned.
     * @throws OperationFailedException if the return operation fails for other reasons.
     */
    void returnBook(String memberId, String bookIsbn);

    /**
     * Gets a list of books currently borrowed by a specific member.
     * @param memberId The ID of the member.
     * @return A list of Transaction objects representing active loans.
     * @throws MemberNotFoundException if the member is not found.
     */
    List<Transaction> getBorrowedBooksByMember(String memberId);

    /**
     * Gets a list of all currently overdue books across all members.
     * @return A list of Transaction objects representing overdue loans.
     */
    List<Transaction> getAllOverdueBooks();
}