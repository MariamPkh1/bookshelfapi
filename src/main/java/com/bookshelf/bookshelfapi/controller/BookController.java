package com.bookshelf.bookshelfapi.controller;

import com.bookshelf.bookshelfapi.exception.ResourceNotFoundException;
import com.bookshelf.bookshelfapi.model.Book;
import com.bookshelf.bookshelfapi.service.BookService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import com.bookshelf.bookshelfapi.dto.BookResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books?page=0&size=5&sort=title,asc
    // Optional filters: &keyword=orwell&categoryId=2&language=English&availableOnly=true
    @GetMapping
    public Page<BookResponse> getAllBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "false") boolean availableOnly,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return toResponses(bookService.filterBooksPaginated(keyword, categoryId, language, availableOnly, pageable));
    }

    // GET /api/books/languages -> ["English", "French", ...]
    @GetMapping("/languages")
    public List<String> getLanguages() {
        return bookService.getLanguages();
    }

    private Page<BookResponse> toResponses(Page<Book> books) {
        Map<Long, Long> onLoan = bookService.countCopiesOnLoan(books.getContent());
        return books.map(book -> BookResponse.fromEntity(book, onLoan.getOrDefault(book.getId(), 0L)));
    }
    // GET /api/books/{id}
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
    // POST /api/books
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book,
                                           @RequestParam(required = false) Long categoryId) {
        Book created = bookService.createBook(book, categoryId);
        return ResponseEntity.status(201).body(created);
    }
    // PUT /api/books/{id}
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id,
                           @Valid @RequestBody Book book,
                           @RequestParam(required = false) Long categoryId) {
        return bookService.updateBook(id, book, categoryId);
    }
    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/books/{id}/cover  (multipart/form-data, field name "file")
    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book uploadCoverImage(@PathVariable Long id,
                                 @RequestParam("file") MultipartFile file) {
        return bookService.uploadCoverImage(id, file);
    }

    // DELETE /api/books/{id}/cover
    @DeleteMapping("/{id}/cover")
    public Book removeCoverImage(@PathVariable Long id) {
        return bookService.removeCoverImage(id);
    }

    // GET /api/books/{id}/cover
    @GetMapping("/{id}/cover")
    public ResponseEntity<Resource> getCoverImage(@PathVariable Long id) {
        Resource image = bookService.getCoverImage(id);
        MediaType contentType = MediaTypeFactory.getMediaType(image)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(contentType).body(image);
    }

    // POST /api/books/{bookId}/tags/{tagId}
    @PostMapping("/{bookId}/tags/{tagId}")
    public Book addTagToBook(@PathVariable Long bookId, @PathVariable Long tagId) {
        return bookService.addTagToBook(bookId, tagId);
    }

    // DELETE /api/books/{bookId}/tags/{tagId}
    @DeleteMapping("/{bookId}/tags/{tagId}")
    public Book removeTagFromBook(@PathVariable Long bookId, @PathVariable Long tagId) {
        return bookService.removeTagFromBook(bookId, tagId);
    }

    // GET /api/books/category/{categoryId}
    @GetMapping("/category/{categoryId}")
    public List<Book> getBooksByCategory(@PathVariable Long categoryId) {
        return bookService.getBooksByCategory(categoryId);
    }

    // GET /api/books/category/{categoryId}/available
    @GetMapping("/category/{categoryId}/available")
    public List<Book> getAvailableBooksByCategory(@PathVariable Long categoryId) {
        return bookService.getAvailableBooksByCategory(categoryId);
    }

    // GET /api/books/tag?name=classic
    @GetMapping("/tag")
    public List<Book> getBooksByTag(@RequestParam String name) {
        return bookService.getBooksByTag(name);
    }

    // GET /api/books/search?keyword=spring&page=0&size=5&sort=title,asc
    @GetMapping("/search")
    public Page<BookResponse> searchBooks(
            @RequestParam String keyword,
            @PageableDefault(size = 5, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
        return toResponses(bookService.searchBooksPaginated(keyword, pageable));
    }

    // GET /api/books/replacement-cost?min=10&max=20
    @GetMapping("/replacement-cost")
    public List<Book> getBooksByReplacementCost(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return bookService.getBooksByReplacementCost(min, max);
    }
}
