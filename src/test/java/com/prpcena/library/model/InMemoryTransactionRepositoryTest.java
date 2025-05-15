// src/test/java/com/prpcena/library/repository/InMemoryTransactionRepositoryTest.java
package com.prpcena.library.repository;

import java.time.LocalDate;
import java.time.LocalDateTime; // if needed for manual construction
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.prpcena.library.model.Transaction;

class InMemoryTransactionRepositoryTest {
    private TransactionRepository transactionRepository;
    private Transaction t1, t2_returned, t3_open_diff_member;

    @BeforeEach
    void setUp() {
        transactionRepository = new InMemoryTransactionRepository();
        LocalDate today = LocalDate.now();
        t1 = new Transaction("ISBN001", "MEMBER001", today.plusDays(14)); // Open

        t2_returned = new Transaction("ISBN002", "MEMBER001", today.plusDays(14));
        t2_returned.setReturnDateTime(LocalDateTime.now().minusDays(1)); // Returned yesterday

        t3_open_diff_member = new Transaction("ISBN001", "MEMBER002", today.plusDays(7)); // Open, different member

        transactionRepository.save(t1);
        transactionRepository.save(t2_returned);
        transactionRepository.save(t3_open_diff_member);
    }

    @Test
    void saveAndFindById() {
        LocalDate dueDate = LocalDate.now().plusDays(10);
        Transaction newTransaction = new Transaction("ISBNNEW", "MEMBERNEW", dueDate);
        transactionRepository.save(newTransaction);
        Optional<Transaction> found = transactionRepository.findById(newTransaction.getTransactionId());
        assertTrue(found.isPresent());
        assertEquals("ISBNNEW", found.get().getBookIsbn());
    }

    @Test
    void findOpenBorrowTransactionByMemberAndBook_Found() {
        Optional<Transaction> found = transactionRepository.findOpenBorrowTransactionByMemberAndBook("MEMBER001",
                "ISBN001");
        assertTrue(found.isPresent());
        assertEquals(t1.getTransactionId(), found.get().getTransactionId());
    }

    @Test
    void findOpenBorrowTransactionByMemberAndBook_NotFound_BookReturned() {
        Optional<Transaction> found = transactionRepository.findOpenBorrowTransactionByMemberAndBook("MEMBER001",
                "ISBN002");
        assertFalse(found.isPresent());
    }

    @Test
    void findOpenBorrowTransactionByMemberAndBook_NotFound_DifferentMember() {
        Optional<Transaction> found = transactionRepository.findOpenBorrowTransactionByMemberAndBook("MEMBER001",
                "ISBN003_NonExistent");
        assertFalse(found.isPresent());
    }

    @Test
    void findAllOpenBorrowTransactions() {
        List<Transaction> openTransactions = transactionRepository.findAllOpenBorrowTransactions();
        assertEquals(2, openTransactions.size()); // t1 and t3_open_diff_member
        assertTrue(openTransactions.stream().anyMatch(t -> t.getTransactionId().equals(t1.getTransactionId())));
        assertTrue(openTransactions.stream()
                .anyMatch(t -> t.getTransactionId().equals(t3_open_diff_member.getTransactionId())));
    }

    @Test
    void findByMemberId_ShouldReturnAllTransactionTypesForMember() {
        List<Transaction> member1Transactions = transactionRepository.findByMemberId("MEMBER001");
        assertEquals(2, member1Transactions.size()); // t1 (open borrow) and t2_returned (closed borrow)
    }
}