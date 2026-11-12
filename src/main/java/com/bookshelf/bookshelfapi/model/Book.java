package com.bookshelf.bookshelfapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    // What the library charges if a borrowed copy is lost or damaged
    @NotNull(message = "Replacement cost is required")
    @DecimalMin(value = "0.01", message = "Replacement cost must be at least 0.01")
    @Column(nullable = false)
    private BigDecimal replacementCost;

    // Copies currently on the shelf; lending one lowers it, returning raises it
    @NotNull(message = "Copies available is required")
    @Min(value = 0, message = "Copies available cannot be negative")
    @Column(nullable = false)
    private Integer copiesAvailable;

    private String isbn;

    @NotBlank(message = "Language is required")
    @Size(max = 40, message = "Language cannot exceed 40 characters")
    @Column(nullable = false, length = 40)
    private String language;

    // Call number on the shelf, e.g. FIC-ORW-01 (section, author code, copy number)
    @Pattern(regexp = "^[A-Z]{2,4}-[A-Z]{2,4}(-[0-9]{1,3})?$",
            message = "Shelf location must look like FIC-ORW or FIC-ORW-01")
    @Column(length = 20)
    private String shelfLocation;

    // Name of the uploaded cover image stored in the upload directory
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String coverImageFileName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "book_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Tag> tags = new HashSet<>();

    // Loan history goes with the book when it is removed from the collection
    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Loan> loans = new ArrayList<>();

    // Default constructor (required by JPA)
    public Book() {}

    // Constructor with fields
    public Book(String title, String author, String description,
                BigDecimal replacementCost, Integer copiesAvailable, String isbn, String language) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.replacementCost = replacementCost;
        this.copiesAvailable = copiesAvailable;
        this.isbn = isbn;
        this.language = language;
    }

    // Keep both sides of the many-to-many relationship in sync
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getBooks().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getBooks().remove(this);
    }

    // ===== GETTERS AND SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getReplacementCost() { return replacementCost; }
    public void setReplacementCost(BigDecimal replacementCost) { this.replacementCost = replacementCost; }

    public Integer getCopiesAvailable() { return copiesAvailable; }
    public void setCopiesAvailable(Integer copiesAvailable) { this.copiesAvailable = copiesAvailable; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }

    public String getCoverImageFileName() { return coverImageFileName; }
    public void setCoverImageFileName(String coverImageFileName) { this.coverImageFileName = coverImageFileName; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public List<Loan> getLoans() { return loans; }
    public void setLoans(List<Loan> loans) { this.loans = loans; }
}
