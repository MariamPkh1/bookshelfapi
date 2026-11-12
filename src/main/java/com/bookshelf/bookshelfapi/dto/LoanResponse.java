package com.bookshelf.bookshelfapi.dto;

import com.bookshelf.bookshelfapi.model.Book;
import com.bookshelf.bookshelfapi.model.Loan;
import com.bookshelf.bookshelfapi.model.LoanStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String shelfLocation;
    private String borrowerName;
    private String borrowerEmail;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private LoanStatus status;
    private long daysOverdue;

    public static LoanResponse fromEntity(Loan loan) {
        LoanResponse dto = new LoanResponse();
        Book book = loan.getBook();
        dto.id = loan.getId();
        dto.bookId = book.getId();
        dto.bookTitle = book.getTitle();
        dto.bookAuthor = book.getAuthor();
        dto.shelfLocation = book.getShelfLocation();
        dto.borrowerName = loan.getBorrowerName();
        dto.borrowerEmail = loan.getBorrowerEmail();
        dto.loanDate = loan.getLoanDate();
        dto.dueDate = loan.getDueDate();
        dto.returnedAt = loan.getReturnedAt();
        dto.status = loan.getStatus();
        dto.daysOverdue = dto.status == LoanStatus.OVERDUE
                ? ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now())
                : 0;
        return dto;
    }

    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public String getShelfLocation() { return shelfLocation; }
    public String getBorrowerName() { return borrowerName; }
    public String getBorrowerEmail() { return borrowerEmail; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedAt() { return returnedAt; }
    public LoanStatus getStatus() { return status; }
    public long getDaysOverdue() { return daysOverdue; }
}
