package com.bookshelf.bookshelfapi.dto;

import jakarta.validation.constraints.*;

// What the librarian submits when lending a copy; dates are set by the server
public class LoanRequest {

    @NotNull(message = "Book is required")
    private Long bookId;

    @NotBlank(message = "Borrower name is required")
    @Size(max = 100, message = "Borrower name cannot exceed 100 characters")
    private String borrowerName;

    @NotBlank(message = "Borrower email is required")
    @Email(message = "Borrower email must be a valid email address")
    private String borrowerEmail;

    @NotNull(message = "Loan period is required")
    @Min(value = 1, message = "Loan period must be at least 1 day")
    @Max(value = 60, message = "Loan period cannot exceed 60 days")
    private Integer days;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
