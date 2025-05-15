// src/test/java/com/prpcena/library/model/TransactionTest.java
package com.prpcena.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TransactionTest {
    @Test
    void testBorrowTransactionCreation() {
        LocalDate dueDate = LocalDate.now().plusDays(14);
        Transaction transaction = new Transaction("ISBN123", "MEMBER001", dueDate);
        assertEquals("ISBN123", transaction.getBookIsbn());
        assertEquals("MEMBER001", transaction.getMemberId());
        assertEquals(TransactionType.BORROW, transaction.getType());
        assertEquals(dueDate, transaction.getDueDate());
        assertNotNull(transaction.getTransactionId());
        assertNotNull(transaction.getTransactionDateTime());
        assertNull(transaction.getReturnDateTime());
        assertFalse(transaction.isOverdue()); // Assuming not created overdue initially
    }

    @Test
    void testIsOverdue_WhenPastDueDateAndNotReturned() {
        LocalDate dueDate = LocalDate.now().minusDays(1); // Due yesterday
        Transaction transaction = new Transaction("ISBN123", "MEMBER001", dueDate);
        assertTrue(transaction.isOverdue());
    }

    @Test
    void testIsOverdue_WhenNotPastDueDate() {
        LocalDate dueDate = LocalDate.now().plusDays(1); // Due tomorrow
        Transaction transaction = new Transaction("ISBN123", "MEMBER001", dueDate);
        assertFalse(transaction.isOverdue());
    }

    @Test
    void testIsOverdue_WhenReturned() {
        LocalDate dueDate = LocalDate.now().minusDays(1); // Due yesterday
        Transaction transaction = new Transaction("ISBN123", "MEMBER001", dueDate);
        transaction.setReturnDateTime(LocalDateTime.now());
        assertFalse(transaction.isOverdue(), "Should not be considered overdue for display if returned, even if late.");
        // Note: isOverdue() as implemented checks returnDateTime == null.
        // If you need to know if it *was* returned late, you'd compare returnDateTime
        // to dueDate.
    }
}