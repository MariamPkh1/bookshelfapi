package com.bookshelf.bookshelfapi.service;

import com.bookshelf.bookshelfapi.exception.InvalidFileException;
import com.bookshelf.bookshelfapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif",
            "image/webp", "webp"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    // Saves the image to disk under a random name and returns that name
    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Uploaded file is empty");
        }
        String extension = ALLOWED_IMAGE_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new InvalidFileException("Only JPEG, PNG, GIF or WEBP images are allowed");
        }

        String fileName = UUID.randomUUID() + "." + extension;
        Path target = uploadDir.resolve(fileName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
        return fileName;
    }

    public Resource load(String fileName) {
        Path file = resolve(fileName);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists()) {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    public void delete(String fileName) {
        if (fileName == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file " + fileName, e);
        }
    }

    private Path resolve(String fileName) {
        Path file = uploadDir.resolve(fileName).normalize();
        if (!file.startsWith(uploadDir)) {
            throw new InvalidFileException("Invalid file name: " + fileName);
        }
        return file;
    }
}
