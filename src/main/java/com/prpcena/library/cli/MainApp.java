// src/main/java/com/yourusername/library/cli/MainApp.java
package com.prpcena.library.cli; // Adjust package name

import com.prpcena.library.model.Book;
import com.prpcena.library.repository.BookRepository;
import com.prpcena.library.repository.InMemoryBookRepository;
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
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Setup: Dependency Injection
        BookRepository bookRepository = new InMemoryBookRepository();
        libraryService = new LibraryServiceImpl(bookRepository);

        logger.info("Library Management System CLI started.");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = -1;
            try {
                String input = scanner.nextLine();
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
        System.out.println("1. Add Book");
        System.out.println("2. Find Book by ISBN");
        System.out.println("3. List All Books");
        System.out.println("4. Remove Book by ISBN");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addBookUI() {
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
            Year publicationYear = Year.parse(scanner.nextLine()); // Can throw DateTimeParseException
            System.out.print("Enter number of copies: ");
            int copies = Integer.parseInt(scanner.nextLine()); // Can throw NumberFormatException

            Book addedBook = libraryService.addBook(title, authorFirstName, authorLastName, isbn, genre,
                    publicationYear, copies);
            System.out.println("Book added successfully: " + addedBook.getTitle());
            logger.info("Book added via UI: {}", addedBook.getIsbn());
        } catch (IllegalArgumentException | DateTimeParseException | NumberFormatException e) {
            System.out.println("Error adding book: " + e.getMessage());
            logger.error("Error during addBookUI: ", e);
        }
    }

    private static void findBookByIsbnUI() {
        System.out.print("Enter ISBN to find: ");
        String isbn = scanner.nextLine();
        Optional<Book> bookOpt = libraryService.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            System.out.println("Book found: " + bookOpt.get());
        } else {
            System.out.println("Book with ISBN " + isbn + " not found.");
        }
        logger.debug("findBookByIsbnUI called for ISBN: {}", isbn);
    }

    private static void listAllBooksUI() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            System.out.println("All books in library:");
            books.forEach(System.out::println);
        }
        logger.debug("listAllBooksUI called. Found {} books.", books.size());
    }

    private static void removeBookByIsbnUI() {
        System.out.print("Enter ISBN of the book to remove: ");
        String isbn = scanner.nextLine();
        try {
            boolean removed = libraryService.removeBookByIsbn(isbn);
            if (removed) {
                System.out.println("Book with ISBN " + isbn + " removed successfully.");
                logger.info("Book removed via UI: {}", isbn);
            } else {
                System.out.println("Could not remove book with ISBN " + isbn + ". It might not exist.");
                logger.warn("Failed to remove book via UI (not found or error): {}", isbn);
            }
        } catch (Exception e) { // Catching broader exceptions that might arise from service/repo
            System.out.println("Error removing book: " + e.getMessage());
            logger.error("Error during removeBookByIsbnUI for ISBN {}: ", isbn, e);
        }
    }
}