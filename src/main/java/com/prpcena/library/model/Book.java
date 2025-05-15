package com.prpcena.library.model; 

import java.time.Year;
import java.util.Objects; // Using java.time.Year for publication year

/**
 * Represents a book in the library.
 * This class aims for immutability for core properties once created,
 * but 'numberOfCopies' might be managed separately or this class might be
 * a DTO (Data Transfer Object) if copies are managed by a different entity.
 * For now, we'll include it and make book details immutable.
 */
public final class Book { // Made final
    private final String title;
    private final Author author; // Using Author class
    private final String isbn; // International Standard Book Number
    private final String genre;
    private final Year publicationYear;
    private int availableCopies; // This field makes the Book object mutable if changed directly.
                                 // Consider if this belongs here or in an inventory management class.
                                 // For now, we'll keep it simple.

    public Book(String title, Author author, String isbn, String genre, Year publicationYear, int initialCopies) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty.");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null.");
        }
        if (isbn == null || isbn.trim().isEmpty()) { // Basic ISBN validation
            throw new IllegalArgumentException("ISBN cannot be null or empty.");
        }
        // Add more validation as needed (e.g., ISBN format)

        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.publicationYear = publicationYear;
        if (initialCopies < 0) {
            throw new IllegalArgumentException("Initial copies cannot be negative.");
        }
        this.availableCopies = initialCopies;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    } // Returns immutable Author object

    public String getIsbn() {
        return isbn;
    }

    public String getGenre() {
        return genre;
    }

    public Year getPublicationYear() {
        return publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // Methods to manage copies (these make the Book object mutable regarding
    // copies)
    public void decreaseAvailableCopies() {
        if (this.availableCopies > 0) {
            this.availableCopies--;
        } else {
            // This state should ideally be prevented by checks before calling
            System.err.println("Warning: Tried to decrease copies for '" + title + "' when none were available.");
        }
    }

    public void increaseAvailableCopies() {
        this.availableCopies++;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author=" + author.getFullName() +
                ", isbn='" + isbn + '\'' +
                ", genre='" + genre + '\'' +
                ", publicationYear=" + publicationYear +
                ", availableCopies=" + availableCopies +
                '}';
    }

    // ISBN is typically the unique identifier for a book edition
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn); // Primary key for equality
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn); // Based on primary key
    }
}