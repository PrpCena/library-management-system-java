// src/main/java/com/yourusername/library/cli/MainApp.java
package com.prpcena.library.cli; // Adjust package name

import com.prpcena.library.exception.*;
import com.prpcena.library.model.Book;
import com.prpcena.library.model.Member;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.InMemoryBookRepository;
import com.prpcena.library.repository.MemberRepository; // New
import com.prpcena.library.repository.InMemoryMemberRepository; // New
import com.prpcena.library.service.LibraryService;
import com.prpcena.library.service.LibraryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainApp {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    private static LibraryService libraryService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Setup: Dependency Injection
        BookRepository bookRepository = new InMemoryBookRepository();
        MemberRepository memberRepository = new InMemoryMemberRepository(); // New
        libraryService = new LibraryServiceImpl(bookRepository, memberRepository); // Updated

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
        // System.out.println("9. Return Book"); // For Iteration 3
        System.out.println("-----------------------");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    // ... existing Book UI methods ...
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