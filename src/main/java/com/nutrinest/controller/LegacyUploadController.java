package com.nutrinest.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serves current administrator uploads and keeps pre-existing database image
 * references usable when their old upload directory was not deployed. Legacy
 * names are UUID_original-file-name, so the fallback only uses that original
 * file name from the application's own image library.
 */
@Controller
public class LegacyUploadController {

    @GetMapping("/uploads/products/{fileName:.+}")
    public ResponseEntity<Resource> productUpload(@PathVariable String fileName) throws Exception {
        Path upload = Path.of("uploads", "products", fileName).normalize();
        Resource resource;

        if (Files.isRegularFile(upload)) {
            resource = new UrlResource(upload.toUri());
        } else {
            int separator = fileName.indexOf('_');
            if (separator < 1 || separator == fileName.length() - 1) {
                return ResponseEntity.notFound().build();
            }
            resource = new ClassPathResource("static/images/" + fileName.substring(separator + 1));
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
        }

        MediaType type = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(type).body(resource);
    }
}
