package com.bookshelf.bookshelfapi.controller;

import com.bookshelf.bookshelfapi.dto.LoanRequest;
import com.bookshelf.bookshelfapi.dto.LoanResponse;
import com.bookshelf.bookshelfapi.model.LoanStatus;
import com.bookshelf.bookshelfapi.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // GET /api/loans?status=OVERDUE&keyword=emma&page=0&size=10&sort=dueDate,asc
    // status is ACTIVE, OVERDUE or RETURNED; leave it out for all loans
    @GetMapping
    public Page<LoanResponse> getLoans(
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return loanService.searchLoans(status, keyword, pageable).map(LoanResponse::fromEntity);
    }

    // GET /api/loans/summary -> { "active": 6, "overdue": 4, "returned": 6 }
    @GetMapping("/summary")
    public Map<String, Long> getSummary() {
        return loanService.getSummary();
    }

    // GET /api/loans/{id}
    @GetMapping("/{id}")
    public LoanResponse getLoan(@PathVariable Long id) {
        return LoanResponse.fromEntity(loanService.getLoan(id));
    }

    // POST /api/loans  { "bookId": 3, "borrowerName": "...", "borrowerEmail": "...", "days": 14 }
    @PostMapping
    public ResponseEntity<LoanResponse> lendBook(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.status(201).body(LoanResponse.fromEntity(loanService.lendBook(request)));
    }

    // PUT /api/loans/{id}/return
    @PutMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable Long id) {
        return LoanResponse.fromEntity(loanService.returnLoan(id));
    }
}
