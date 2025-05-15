// src/main/java/com/prpcena/library/repository/InMemoryTransactionRepository.java
package com.prpcena.library.repository;

import com.prpcena.library.model.Transaction;
import com.prpcena.library.model.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTransactionRepository.class);
    private final Map<String, Transaction> transactions = new LinkedHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction == null || transaction.getTransactionId() == null) {
            logger.error("Attempted to save a null transaction or transaction with null ID.");
            throw new IllegalArgumentException("Transaction and Transaction ID cannot be null.");
        }
        lock.writeLock().lock();
        try {
            transactions.put(transaction.getTransactionId(), transaction);
            logger.info("Saved transaction with ID: {}", transaction.getTransactionId());
            return transaction;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(transactions.get(transactionId));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(transactions.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> findByMemberId(String memberId) {
        lock.readLock().lock();
        try {
            return transactions.values().stream()
                    .filter(t -> t.getMemberId().equals(memberId))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> findByBookIsbn(String bookIsbn) {
        lock.readLock().lock();
        try {
            return transactions.values().stream()
                    .filter(t -> t.getBookIsbn().equals(bookIsbn))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Transaction> findOpenBorrowTransactionByMemberAndBook(String memberId, String bookIsbn) {
        lock.readLock().lock();
        try {
            return transactions.values().stream()
                    .filter(t -> t.getType() == TransactionType.BORROW &&
                            t.getMemberId().equals(memberId) &&
                            t.getBookIsbn().equals(bookIsbn) &&
                            t.getReturnDateTime() == null) // Check if not returned
                    .findFirst(); // Assuming a member can only have one open loan for a specific book ISBN
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Transaction> findAllOpenBorrowTransactions() {
        lock.readLock().lock();
        try {
            return transactions.values().stream()
                    .filter(t -> t.getType() == TransactionType.BORROW && t.getReturnDateTime() == null)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}