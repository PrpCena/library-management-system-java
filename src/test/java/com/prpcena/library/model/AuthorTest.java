package com.prpcena.library.model; // Adjust package name

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AuthorTest {
    @Test
    void testAuthorCreation_Valid() {
        Author author = new Author("J.R.R.", "Tolkien");
        assertEquals("J.R.R.", author.getFirstName());
        assertEquals("Tolkien", author.getLastName());
        assertEquals("J.R.R. Tolkien", author.getFullName());
    }

    @Test
    void testAuthorCreation_NullFirstName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Author(null, "Tolkien"));
    }

    @Test
    void testAuthorCreation_EmptyLastName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Author("George", "  "));
    }

    @Test
    void testEqualsAndHashCode() {
        Author author1 = new Author("George", "Orwell");
        Author author2 = new Author("George", "Orwell");
        Author author3 = new Author("Jane", "Austen");

        assertEquals(author1, author2, "Authors with same name should be equal.");
        assertEquals(author1.hashCode(), author2.hashCode(), "Hashcodes for equal authors should be same.");
        assertNotEquals(author1, author3, "Authors with different names should not be equal.");
        assertNotEquals(author1.hashCode(), author3.hashCode(),
                "Hashcodes for different authors should ideally be different.");
    }
}