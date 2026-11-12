package com.bookshelf.bookshelfapi.controller;

import com.bookshelf.bookshelfapi.model.Tag;
import com.bookshelf.bookshelfapi.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // GET /api/tags
    @GetMapping
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    // GET /api/tags/{id}
    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return tagService.getTagById(id);
    }

    // POST /api/tags
    @PostMapping
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
        return ResponseEntity.status(201).body(tagService.createTag(tag));
    }

    // PUT /api/tags/{id}
    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @Valid @RequestBody Tag tag) {
        return tagService.updateTag(id, tag);
    }

    // DELETE /api/tags/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
