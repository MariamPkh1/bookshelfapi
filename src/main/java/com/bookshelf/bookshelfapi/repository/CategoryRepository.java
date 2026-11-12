package com.bookshelf.bookshelfapi.repository;

import com.bookshelf.bookshelfapi.dto.CategoryResponse;
import com.bookshelf.bookshelfapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    // Custom JPQL query: every category with how many books it holds, in one round trip
    @Query("SELECT new com.bookshelf.bookshelfapi.dto.CategoryResponse(c.id, c.name, c.description, COUNT(b)) " +
            "FROM Category c LEFT JOIN c.books b " +
            "GROUP BY c.id, c.name, c.description " +
            "ORDER BY c.name")
    List<CategoryResponse> findAllWithBookCount();
}
