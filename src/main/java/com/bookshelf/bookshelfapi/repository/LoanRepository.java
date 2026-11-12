package com.bookshelf.bookshelfapi.repository;

import com.bookshelf.bookshelfapi.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Method-name queries: counts for the dashboard and loan rules
    long countByReturnedAtIsNull();

    long countByReturnedAtIsNullAndDueDateBefore(LocalDate date);

    long countByReturnedAtIsNotNull();

    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    boolean existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnedAtIsNull(Long bookId, String borrowerEmail);

    // Custom JPQL query: one loan list for every status tab, searchable by borrower or title
    @Query("SELECT l FROM Loan l JOIN l.book b " +
            "WHERE (LOWER(l.borrowerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(l.borrowerEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status = 'ALL' " +
            "    OR (:status = 'ACTIVE' AND l.returnedAt IS NULL) " +
            "    OR (:status = 'OVERDUE' AND l.returnedAt IS NULL AND l.dueDate < :today) " +
            "    OR (:status = 'RETURNED' AND l.returnedAt IS NOT NULL))")
    Page<Loan> search(@Param("keyword") String keyword,
                      @Param("status") String status,
                      @Param("today") LocalDate today,
                      Pageable pageable);

    // Custom JPQL query: how many copies of each listed book are out right now ([bookId, count] rows)
    @Query("SELECT l.book.id, COUNT(l) FROM Loan l " +
            "WHERE l.returnedAt IS NULL AND l.book.id IN :bookIds " +
            "GROUP BY l.book.id")
    List<Object[]> countActiveLoansByBook(@Param("bookIds") Collection<Long> bookIds);
}
