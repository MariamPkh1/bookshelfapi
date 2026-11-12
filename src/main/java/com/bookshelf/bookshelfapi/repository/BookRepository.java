package com.bookshelf.bookshelfapi.repository;

import com.bookshelf.bookshelfapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Find books in a category
    List<Book> findByCategoryId(Long categoryId);

    // Method-name query with And: books in a category that still have copies on the shelf
    List<Book> findByCategoryIdAndCopiesAvailableGreaterThan(Long categoryId, Integer copies);

    // Custom JPQL query
    @Query("SELECT b FROM Book b WHERE b.replacementCost BETWEEN :min AND :max")
    List<Book> findByReplacementCostRange(@Param("min") BigDecimal min,
                                          @Param("max") BigDecimal max);

    // Custom JPQL query across the many-to-many relationship
    @Query("SELECT DISTINCT b FROM Book b JOIN b.tags t WHERE LOWER(t.name) = LOWER(:tagName)")
    List<Book> findByTagName(@Param("tagName") String tagName);

    // Custom JPQL query combining every catalog filter; paging and sorting come from the Pageable.
    // The keyword matches title, author, ISBN or shelf location.
    @Query("SELECT b FROM Book b LEFT JOIN b.category c " +
            "WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(COALESCE(b.isbn, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(COALESCE(b.shelfLocation, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:language = '' OR LOWER(b.language) = LOWER(:language)) " +
            "AND b.copiesAvailable >= :minCopies")
    Page<Book> filterBooks(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("language") String language,
                           @Param("minCopies") int minCopies,
                           Pageable pageable);

    // Custom JPQL query: languages present in the collection, for the catalog filter
    @Query("SELECT DISTINCT b.language FROM Book b ORDER BY b.language")
    List<String> findAllLanguages();
}
