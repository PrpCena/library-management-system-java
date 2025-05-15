// src/main/java/com/yourusername/library/cli/MainApp.java
package com.prpcena.library.cli; // Adjust package name

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner; // New

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // New

import com.prpcena.library.exception.BookNotBorrowedException;
import com.prpcena.library.exception.BookNotFoundException;
import com.prpcena.library.exception.MemberNotFoundException;
import com.prpcena.library.exception.NoCopiesAvailableException;
import com.prpcena.library.exception.OperationFailedException;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.model.Transaction;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.InMemoryBookRepository;
import com.prpcena.library.repository.InMemoryMemberRepository;
import com.prpcena.library.repository.InMemoryTransactionRepository;
import com.prpcena.library.repository.MemberRepository;
import com.prpcena.library.repository.TransactionRepository;
import com.prpcena.library.service.LibraryService;
import com.prpcena.library.service.LibraryServiceImpl;

public class MainApp {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static LibraryService libraryService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Setup: Dependency Injection
        BookRepository bookRepository = new InMemoryBookRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository(); // New
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();
        libraryService = new LibraryServiceImpl(bookRepository, memberRepository, transactionRepository);
        
        logger.info("Library Management System CLI started.");
        boolean running = true;
        while (running) {
            printMenu(); // Updated menu
            int choice = -1;
            try {
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) {
                    System.out.println("No input provided. Please enter a number.");
                    continue;
                }
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                logger.warn("Invalid menu input: not a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    addBookUI();
                    break;
                case 2:
                    findBookByIsbnUI();
                    break;
                case 3:
                    listAllBooksUI();
                    break;
                case 4:
                    removeBookByIsbnUI();
                    break;
                case 5:
                    registerMemberUI();
                    break; // New
                case 6:
                    findMemberByIdUI();
                    break; // New
                case 7:
                    listAllMembersUI();
                    break; // New
                case 8:
                    borrowBookUI();
                    break; // New
                case 9:
                    returnBookUI();
                    break; // New
                case 10:
                    listBorrowedBooksByMemberUI();
                    break; // New
                case 11:
                    listAllOverdueBooksUI();
                    break; // New
                case 12:
                    searchBooksMenuUI();
                    break; // New option
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    logger.warn("Invalid menu choice: {}", choice);
            }
        }
        System.out.println("Exiting Library Management System. Goodbye!");
        logger.info("Library Management System CLI stopped.");
        scanner.close();
    }

// New UI method for search submenu
    private static void searchBooksMenuUI() {
        System.out.println("\n--- Search Books ---");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by Genre");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter search type: ");
        String choiceStr = scanner.nextLine();
        int choice;
        try {
            choice = Integer.parseInt(choiceStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (choice == 0) return;

        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        List<Book> results = Collections.emptyList();

        try {
            switch (choice) {
                case 1:
                    results = libraryService.searchBooksByTitle(query);
                    break;
                case 2:
                    results = libraryService.searchBooksByAuthor(query);
                    break;
                case 3:
                    results = libraryService.searchBooksByGenre(query);
                    break;
                default:
                    System.out.println("Invalid search type.");
                    return;
            }

            if (results.isEmpty()) {
                System.out.println("No books found matching your query: '" + query + "'");
            } else {
                System.out.println("Search results for query '" + query + "':");
                results.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("An error occurred during search: " + e.getMessage());
            logger.error("Error in searchBooksMenuUI", e);
        }
    }

    private static void printMenu() {
        System.out.println("\nLibrary Menu:");
        System.out.println("--- Book Management ---");
        System.out.println("1. Add Book");
        System.out.println("2. Find Book by ISBN");
        System.out.println("3. List All Books");
        System.out.println("4. Remove Book by ISBN");
        System.out.println("--- Member Management ---");
        System.out.println("5. Register Member");
        System.out.println("6. Find Member by ID");
        System.out.println("7. List All Members");
        System.out.println("--- Library Operations ---");
        System.out.println("8. Borrow Book");
        System.out.println("9. Return Book");
        System.out.println("10. List Member's Borrowed Books");
        System.out.println("11. List All Overdue Books");
        System.out.println("--- Book Searching ---");
        System.out.println("12. Search Books");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void returnBookUI() {
        try {
            System.out.print("Enter Member ID returning the book: ");
            String memberId = scanner.nextLine();
            System.out.print("Enter Book ISBN being returned: ");
            String bookIsbn = scanner.nextLine();

            libraryService.returnBook(memberId, bookIsbn);
            System.out.println("Book (ISBN: " + bookIsbn + ") successfully returned by member (ID: " + memberId + ").");
            logger.info("Book ISBN {} returned by member ID {} via UI.", bookIsbn, memberId);
        } catch (MemberNotFoundException | BookNotFoundException | BookNotBorrowedException | OperationFailedException e) {
            System.out.println("Error returning book: " + e.getMessage());
            logger.warn("Error during returnBookUI: {}", e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred while returning the book.");
            logger.error("Unexpected error during returnBookUI: ", e);
        }
    }

    private static void listBorrowedBooksByMemberUI() {
        try {
            System.out.print("Enter Member ID to list borrowed books: ");
            String memberId = scanner.nextLine();
            List<Transaction> borrowedTransactions = libraryService.getBorrowedBooksByMember(memberId);
            if (borrowedTransactions.isEmpty()) {
                System.out.println("Member ID " + memberId + " has no books currently borrowed.");
            } else {
                System.out.println("Books currently borrowed by member ID " + memberId + ":");
                borrowedTransactions.forEach(transaction -> {
                    // Fetch book details for better display
                    Optional<Book> bookOpt = libraryService.findBookByIsbn(transaction.getBookIsbn());
                    String bookTitle = bookOpt.map(Book::getTitle).orElse("N/A - Book details not found");
                    System.out.println(" - ISBN: " + transaction.getBookIsbn() + ", Title: " + bookTitle +
                                       ", Due: " + transaction.getDueDate() +
                                       (transaction.isOverdue() ? " (OVERDUE)" : ""));
                });
            }
        } catch (MemberNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            logger.error("Error in listBorrowedBooksByMemberUI: ", e);
        }
    }

    private static void listAllOverdueBooksUI() {
        try {
            List<Transaction> overdueTransactions = libraryService.getAllOverdueBooks();
            if (overdueTransactions.isEmpty()) {
                System.out.println("There are no overdue books currently.");
            } else {
                System.out.println("All overdue books:");
                overdueTransactions.forEach(transaction -> {
                    Optional<Book> bookOpt = libraryService.findBookByIsbn(transaction.getBookIsbn());
                    String bookTitle = bookOpt.map(Book::getTitle).orElse("N/A");
                    Optional<Member> memberOpt = libraryService.findMemberById(transaction.getMemberId());
                    String memberName = memberOpt.map(Member::getName).orElse("N/A");

                    System.out.println(" - Member: " + memberName + " (ID: " + transaction.getMemberId() + ")");
                    System.out.println("   Book: " + bookTitle + " (ISBN: " + transaction.getBookIsbn() + ")");
                    System.out.println("   Due Date: " + transaction.getDueDate());
                    System.out.println("   Borrowed On: " + transaction.getTransactionDateTime().toLocalDate());
                    System.out.println("   ---");
                });
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred while listing overdue books.");
            logger.error("Error in listAllOverdueBooksUI: ", e);
        }
    }

    private static void addBookUI() { /* ... as before ... */
        try {
            System.out.print("Enter title: ");
            String title = scanner.nextLine();
            System.out.print("Enter author first name: ");
            String authorFirstName = scanner.nextLine();
            System.out.print("Enter author last name: ");
            String authorLastName = scanner.nextLine();
            System.out.print("Enter ISBN: ");
            String isbn = scanner.nextLine();
            System.out.print("Enter genre: ");
            String genre = scanner.nextLine();
            System.out.print("Enter publication year (YYYY): ");
            String yearInput = scanner.nextLine();
            if (yearInput.trim().isEmpty())
                throw new IllegalArgumentException("Publication year cannot be empty.");
            Year publicationYear = Year.parse(yearInput);
            System.out.print("Enter number of copies: ");
            String copiesInput = scanner.nextLine();
            if (copiesInput.trim().isEmpty())
                throw new IllegalArgumentException("Number of copies cannot be empty.");
            int copies = Integer.parseInt(copiesInput);
            Book addedBook = libraryService.addBook(title, authorFirstName, authorLastName, isbn, genre,
                    publicationYear, copies);
            System.out.println("Book added successfully: " + addedBook.getTitle());
            logger.info("Book added via UI: {}", addedBook.getIsbn());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            System.out.println("Error adding book: " + e.getMessage());
            logger.error("Error during addBookUI: ", e);
        }
    }

    private static void findBookByIsbnUI() { /* ... as before ... */
        System.out.print("Enter ISBN to find: ");
        String isbn = scanner.nextLine();
        Optional<Book> bookOpt = libraryService.findBookByIsbn(isbn);
        if (bookOpt.isPresent())
            System.out.println("Book found: " + bookOpt.get());
        else
            System.out.println("Book with ISBN " + isbn + " not found.");
        logger.debug("findBookByIsbnUI called for ISBN: {}", isbn);
    }

    private static void listAllBooksUI() { /* ... as before ... */
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty())
            System.out.println("No books in the library.");
        else {
            System.out.println("All books in library:");
            books.forEach(System.out::println);
        }
        logger.debug("listAllBooksUI called. Found {} books.", books.size());
    }

    private static void removeBookByIsbnUI() { /* ... as before ... */
        System.out.print("Enter ISBN of the book to remove: ");
        String isbn = scanner.nextLine();
        try {
            boolean removed = libraryService.removeBookByIsbn(isbn);
            if (removed)
                System.out.println("Book with ISBN " + isbn + " removed successfully.");
            else
                System.out.println("Could not remove book with ISBN " + isbn + ". It might not exist.");
        } catch (Exception e) {
            System.out.println("Error removing book: " + e.getMessage());
            logger.error("Error during removeBookByIsbnUI for ISBN {}: ", isbn, e);
        }
    }

    // --- New Member UI Methods ---
    private static void registerMemberUI() {
        try {
            System.out.print("Enter member name: ");
            String name = scanner.nextLine();
            System.out.print("Enter member contact info (e.g., email): ");
            String contactInfo = scanner.nextLine();

            Member newMember = libraryService.registerMember(name, contactInfo);
            System.out.println("Member registered successfully!");
            System.out.println("Name: " + newMember.getName() + ", ID: " + newMember.getMemberId());
            logger.info("Member registered via UI: ID {}", newMember.getMemberId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering member: " + e.getMessage());
            logger.error("Error during registerMemberUI: ", e);
        }
    }

    private static void findMemberByIdUI() {
        System.out.print("Enter member ID to find: ");
        String memberId = scanner.nextLine();
        Optional<Member> memberOpt = libraryService.findMemberById(memberId);
        if (memberOpt.isPresent()) {
            System.out.println("Member found: " + memberOpt.get());
        } else {
            System.out.println("Member with ID " + memberId + " not found.");
        }
        logger.debug("findMemberByIdUI called for ID: {}", memberId);
    }

    private static void listAllMembersUI() {
        List<Member> members = libraryService.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered in the library.");
        } else {
            System.out.println("All registered members:");
            members.forEach(System.out::println); // Assumes Member.toString() is well-defined
        }
        logger.debug("listAllMembersUI called. Found {} members.", members.size());
    }

    // --- New Borrowing UI Method ---
    private static void borrowBookUI() {
        try {
            System.out.print("Enter Member ID: ");
            String memberId = scanner.nextLine();
            System.out.print("Enter Book ISBN to borrow: ");
            String bookIsbn = scanner.nextLine();

            libraryService.borrowBook(memberId, bookIsbn);
            System.out.println("Book (ISBN: " + bookIsbn + ") successfully borrowed by member (ID: " + memberId + ").");
            logger.info("Book ISBN {} borrowed by member ID {} via UI.", bookIsbn, memberId);
        } catch (MemberNotFoundException | BookNotFoundException | NoCopiesAvailableException
                | OperationFailedException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
            logger.warn("Error during borrowBookUI: {}", e.getMessage());
        } catch (IllegalArgumentException e) { // For bad input like null IDs/ISBNs if service doesn't catch them first
            System.out.println("Invalid input for borrowing: " + e.getMessage());
            logger.warn("Invalid input during borrowBookUI: {}", e.getMessage());
        } catch (Exception e) { // Catch-all for unexpected errors
            System.out.println("An unexpected error occurred while borrowing the book.");
            logger.error("Unexpected error during borrowBookUI: ", e);
        }
    }
}