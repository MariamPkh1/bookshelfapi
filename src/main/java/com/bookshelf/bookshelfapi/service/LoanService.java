package com.bookshelf.bookshelfapi.service;

import com.bookshelf.bookshelfapi.dto.LoanRequest;
import com.bookshelf.bookshelfapi.exception.BusinessRuleException;
import com.bookshelf.bookshelfapi.exception.ResourceNotFoundException;
import com.bookshelf.bookshelfapi.model.Book;
import com.bookshelf.bookshelfapi.model.Loan;
import com.bookshelf.bookshelfapi.model.LoanStatus;
import com.bookshelf.bookshelfapi.repository.BookRepository;
import com.bookshelf.bookshelfapi.repository.LoanRepository;
import org.springframework.data.core.PropertyPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    // A null status means every loan
    public Page<Loan> searchLoans(LoanStatus status, String keyword, Pageable pageable) {
        pageable.getSort().forEach(order -> PropertyPath.from(order.getProperty(), Loan.class));
        return loanRepository.search(
                keyword == null ? "" : keyword.trim(),
                status == null ? "ALL" : status.name(),
                LocalDate.now(),
                pageable);
    }

    public Loan getLoan(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
    }

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("active", loanRepository.countByReturnedAtIsNull());
        summary.put("overdue", loanRepository.countByReturnedAtIsNullAndDueDateBefore(LocalDate.now()));
        summary.put("returned", loanRepository.countByReturnedAtIsNotNull());
        return summary;
    }

    // Saving the loan and taking a copy off the shelf happen together or not at all
    @Transactional
    public Loan lendBook(LoanRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        if (book.getCopiesAvailable() <= 0) {
            throw new BusinessRuleException("No copies of '" + book.getTitle() + "' are available to lend");
        }
        String email = request.getBorrowerEmail().trim();
        if (loanRepository.existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnedAtIsNull(book.getId(), email)) {
            throw new BusinessRuleException(email + " already has a copy of '" + book.getTitle() + "' on loan");
        }

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, request.getBorrowerName().trim(), email, today, today.plusDays(request.getDays()));
        return loanRepository.save(loan);
    }

    // Marking the loan returned and putting the copy back on the shelf happen together or not at all
    @Transactional
    public Loan returnLoan(Long id) {
        Loan loan = getLoan(id);
        if (loan.getReturnedAt() != null) {
            throw new BusinessRuleException("This loan was already returned on " + loan.getReturnedAt());
        }

        loan.setReturnedAt(LocalDate.now());
        Book book = loan.getBook();
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepository.save(book);
        return loanRepository.save(loan);
    }
}
