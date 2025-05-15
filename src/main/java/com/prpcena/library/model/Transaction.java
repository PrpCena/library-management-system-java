// src/main/java/com/prpcena/library/model/Transaction.java
package com.prpcena.library.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final String bookIsbn;
    private final String memberId;
    private final TransactionType type; // BORROW or RETURN
    private final LocalDateTime transactionDateTime;
    private LocalDate dueDate; // Applicable for BORROW transactions
    private LocalDateTime returnDateTime; // Applicable when a BORROW transaction is completed by a RETURN

    // Constructor for a new BORROW transaction
    public Transaction(String bookIsbn, String memberId, LocalDate dueDate) {
        this.transactionId = UUID.randomUUID().toString();
        this.bookIsbn = Objects.requireNonNull(bookIsbn, "Book ISBN cannot be null");
        this.memberId = Objects.requireNonNull(memberId, "Member ID cannot be null");
        this.type = TransactionType.BORROW;
        this.transactionDateTime = LocalDateTime.now();
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null for a borrow transaction");
    }

    // Constructor for a RETURN transaction (less common to create directly, usually
    // update BORROW)
    // Or this could be a general constructor if we load from persistence
    public Transaction(String transactionId, String bookIsbn, String memberId, TransactionType type,
            LocalDateTime transactionDateTime, LocalDate dueDate, LocalDateTime returnDateTime) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.bookIsbn = Objects.requireNonNull(bookIsbn);
        this.memberId = Objects.requireNonNull(memberId);
        this.type = Objects.requireNonNull(type);
        this.transactionDateTime = Objects.requireNonNull(transactionDateTime);
        this.dueDate = dueDate; // Can be null for a pure RETURN event not linked to a borrow
        this.returnDateTime = returnDateTime;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDateTime() {
        return returnDateTime;
    }

    // Setters for fields that change
    public void setReturnDateTime(LocalDateTime returnDateTime) {
        // Potentially change type to RETURN if this transaction was originally a BORROW
        // For now, we'll handle RETURNs by "closing" a BORROW transaction
        this.returnDateTime = returnDateTime;
    }

    public boolean isOverdue() {
        // A book is overdue if it was a BORROW transaction, it hasn't been returned
        // yet,
        // and the current date is past the due date.
        return this.type == TransactionType.BORROW &&
                this.returnDateTime == null &&
                this.dueDate != null &&
                LocalDate.now().isAfter(this.dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", memberId='" + memberId + '\'' +
                ", type=" + type +
                ", transactionDateTime=" + transactionDateTime +
                ", dueDate=" + dueDate +
                ", returnDateTime=" + returnDateTime +
                ", overdue=" + (dueDate != null ? isOverdue() : "N/A") +
                '}';
    }
}