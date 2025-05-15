// src/main/java/com/prpcena/library/repository/TransactionRepository.java
package com.prpcena.library.repository;

import com.prpcena.library.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String transactionId);

    List<Transaction> findAll();

    List<Transaction> findByMemberId(String memberId);

    List<Transaction> findByBookIsbn(String bookIsbn);

    /**
     * Finds an open (not yet returned) borrow transaction for a specific member and
     * book.
     * 
     * @param memberId The ID of the member.
     * @param bookIsbn The ISBN of the book.
     * @return An Optional containing the open borrow transaction if found.
     */
    Optional<Transaction> findOpenBorrowTransactionByMemberAndBook(String memberId, String bookIsbn);

    /**
     * Finds all open (not yet returned) borrow transactions.
     * 
     * @return A list of all open borrow transactions.
     */
    List<Transaction> findAllOpenBorrowTransactions();
}