package com.bookshelf.bookshelfapi.controller;

import com.bookshelf.bookshelfapi.dto.CategoryResponse;
import com.bookshelf.bookshelfapi.model.Category;
import com.bookshelf.bookshelfapi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Errors (404, 409 duplicate name / category in use, 400 validation) are turned into JSON by GlobalExceptionHandler
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // POST /api/categories
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.status(201).body(categoryService.createCategory(category));
    }

    // PUT /api/categories/{id}
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    // DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
