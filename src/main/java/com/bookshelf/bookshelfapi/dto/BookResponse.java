package com.bookshelf.bookshelfapi.dto;

import com.bookshelf.bookshelfapi.model.Book;
import com.bookshelf.bookshelfapi.model.Tag;
import java.math.BigDecimal;
import java.util.List;

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal replacementCost;
    private Integer copiesAvailable;
    private long copiesOnLoan;
    private String isbn;
    private String language;
    private String shelfLocation;
    private String categoryName;
    private Long categoryId;
    private String coverImageFileName;
    private List<String> tags;

    // Build from entity; copiesOnLoan comes from the loans table
    public static BookResponse fromEntity(Book book, long copiesOnLoan) {
        BookResponse dto = new BookResponse();
        dto.id = book.getId();
        dto.title = book.getTitle();
        dto.author = book.getAuthor();
        dto.description = book.getDescription();
        dto.replacementCost = book.getReplacementCost();
        dto.copiesAvailable = book.getCopiesAvailable();
        dto.copiesOnLoan = copiesOnLoan;
        dto.isbn = book.getIsbn();
        dto.language = book.getLanguage();
        dto.shelfLocation = book.getShelfLocation();
        dto.coverImageFileName = book.getCoverImageFileName();
        dto.tags = book.getTags().stream().map(Tag::getName).sorted().toList();
        if (book.getCategory() != null) {
            dto.categoryName = book.getCategory().getName();
            dto.categoryId = book.getCategory().getId();
        }
        return dto;
    }

    // Getters (no setters needed — this is read-only)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public BigDecimal getReplacementCost() { return replacementCost; }
    public Integer getCopiesAvailable() { return copiesAvailable; }
    public long getCopiesOnLoan() { return copiesOnLoan; }
    public String getIsbn() { return isbn; }
    public String getLanguage() { return language; }
    public String getShelfLocation() { return shelfLocation; }
    public String getCategoryName() { return categoryName; }
    public Long getCategoryId() { return categoryId; }
    public String getCoverImageFileName() { return coverImageFileName; }
    public List<String> getTags() { return tags; }
}
