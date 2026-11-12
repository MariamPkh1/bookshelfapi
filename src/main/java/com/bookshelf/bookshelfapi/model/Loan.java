package com.bookshelf.bookshelfapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 100)
    private String borrowerName;

    @Column(nullable = false)
    private String borrowerEmail;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    // Null while the copy is still out
    private LocalDate returnedAt;

    public Loan() {}

    public Loan(Book book, String borrowerName, String borrowerEmail, LocalDate loanDate, LocalDate dueDate) {
        this.book = book;
        this.borrowerName = borrowerName;
        this.borrowerEmail = borrowerEmail;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    // Derived from the dates rather than stored, so "overdue" can never go stale
    public LoanStatus getStatus() {
        if (returnedAt != null) return LoanStatus.RETURNED;
        return dueDate.isBefore(LocalDate.now()) ? LoanStatus.OVERDUE : LoanStatus.ACTIVE;
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDate returnedAt) { this.returnedAt = returnedAt; }
}
