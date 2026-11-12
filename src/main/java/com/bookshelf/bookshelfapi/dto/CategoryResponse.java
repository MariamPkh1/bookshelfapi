package com.bookshelf.bookshelfapi.dto;

public class CategoryResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final long bookCount;

    // Used by the JPQL constructor expression in CategoryRepository
    public CategoryResponse(Long id, String name, String description, long bookCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.bookCount = bookCount;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getBookCount() { return bookCount; }
}
