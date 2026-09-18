package com.nutrinest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    // =====================================================
    // ALLOWED IMAGE EXTENSIONS
    // =====================================================

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );


    // =====================================================
    // ALLOWED IMAGE CONTENT TYPES
    // =====================================================

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );


    // =====================================================
    // MAX FILE SIZE
    // =====================================================

    private static final long MAX_FILE_SIZE =
            10L * 1024L * 1024L;


    // =====================================================
    // PRODUCT / CATEGORY / BRAND UPLOAD DIRECTORY
    // =====================================================

    @Value("${file.upload-dir}")
    private String uploadDir;


    // =====================================================
    // PRODUCT / CATEGORY / BRAND IMAGE UPLOAD
    // =====================================================

    public String uploadFile(MultipartFile file)
            throws IOException {

        // -------------------------------------------------
        // Empty file check
        // -------------------------------------------------

        if (file == null || file.isEmpty()) {
            return null;
        }


        // -------------------------------------------------
        // File size validation
        // -------------------------------------------------

        validateFileSize(file);


        // -------------------------------------------------
        // Original filename validation
        // -------------------------------------------------

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid image filename."
            );
        }


        // -------------------------------------------------
        // Remove any directory information
        // -------------------------------------------------

        String cleanFilename = Paths
                .get(originalFilename)
                .getFileName()
                .toString();


        // -------------------------------------------------
        // Get extension
        // -------------------------------------------------

        String extension =
                getExtension(cleanFilename);


        // -------------------------------------------------
        // Extension validation
        // -------------------------------------------------

        validateExtension(extension);


        // -------------------------------------------------
        // Content type validation
        // -------------------------------------------------

        validateContentType(file);


        // -------------------------------------------------
        // Create upload directory
        // -------------------------------------------------

        Path uploadPath = Paths
                .get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadPath);


        // -------------------------------------------------
        // Generate safe random filename
        // -------------------------------------------------

        String safeFileName =
                UUID.randomUUID()
                        + "."
                        + extension;


        // -------------------------------------------------
        // Resolve final file path
        // -------------------------------------------------

        Path filePath = uploadPath
                .resolve(safeFileName)
                .normalize();


        // -------------------------------------------------
        // Path traversal protection
        // -------------------------------------------------

        if (!filePath.startsWith(uploadPath)) {

            throw new IOException(
                    "Invalid upload file path."
            );
        }


        // -------------------------------------------------
        // Save file
        // -------------------------------------------------

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // -------------------------------------------------
        // Return public image URL
        // -------------------------------------------------

        return "/uploads/products/" + safeFileName;
    }


    // =====================================================
    // PROFILE IMAGE UPLOAD
    // =====================================================

    public String uploadProfileImage(
            MultipartFile file)
            throws IOException {

        // -------------------------------------------------
        // Empty file check
        // -------------------------------------------------

        if (file == null || file.isEmpty()) {
            return null;
        }


        // -------------------------------------------------
        // File size validation
        // -------------------------------------------------

        validateFileSize(file);


        // -------------------------------------------------
        // Original filename validation
        // -------------------------------------------------

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid profile image filename."
            );
        }


        // -------------------------------------------------
        // Remove directory information
        // -------------------------------------------------

        String cleanFilename = Paths
                .get(originalFilename)
                .getFileName()
                .toString();


        // -------------------------------------------------
        // Get extension
        // -------------------------------------------------

        String extension =
                getExtension(cleanFilename);


        // -------------------------------------------------
        // Extension validation
        // -------------------------------------------------

        validateExtension(extension);


        // -------------------------------------------------
        // Content type validation
        // -------------------------------------------------

        validateContentType(file);


        // -------------------------------------------------
        // Profile upload directory
        // -------------------------------------------------

        Path uploadPath = Paths
                .get("uploads", "profile")
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(uploadPath);


        // -------------------------------------------------
        // Generate safe random filename
        // -------------------------------------------------

        String safeFileName =
                UUID.randomUUID()
                        + "."
                        + extension;


        // -------------------------------------------------
        // Resolve final file path
        // -------------------------------------------------

        Path filePath = uploadPath
                .resolve(safeFileName)
                .normalize();


        // -------------------------------------------------
        // Path traversal protection
        // -------------------------------------------------

        if (!filePath.startsWith(uploadPath)) {

            throw new IOException(
                    "Invalid profile image path."
            );
        }


        // -------------------------------------------------
        // Save profile image
        // -------------------------------------------------

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // -------------------------------------------------
        // Return profile image URL
        // -------------------------------------------------

        return "/uploads/profile/" + safeFileName;
    }


    // =====================================================
    // DELETE PRODUCT / CATEGORY / BRAND IMAGE
    // =====================================================

    public void deleteFile(String imagePath) {

        if (imagePath == null
                || imagePath.isBlank()) {

            return;
        }


        try {

            // -------------------------------------------------
            // Normalize URL path
            // -------------------------------------------------

            String normalizedImagePath =
                    imagePath.trim()
                            .replace("\\", "/");


            // -------------------------------------------------
            // Only allow product upload path
            // -------------------------------------------------

            String prefix =
                    "/uploads/products/";

            if (!normalizedImagePath.startsWith(prefix)) {
                return;
            }


            // -------------------------------------------------
            // Extract filename
            // -------------------------------------------------

            String fileName =
                    normalizedImagePath.substring(
                            prefix.length()
                    );


            // -------------------------------------------------
            // Filename safety validation
            // -------------------------------------------------

            if (fileName.isBlank()
                    || fileName.contains("/")
                    || fileName.contains("\\")
                    || fileName.contains("..")) {

                return;
            }


            // -------------------------------------------------
            // Upload directory
            // -------------------------------------------------

            Path uploadPath = Paths
                    .get(uploadDir)
                    .toAbsolutePath()
                    .normalize();


            // -------------------------------------------------
            // Resolve file
            // -------------------------------------------------

            Path filePath = uploadPath
                    .resolve(fileName)
                    .normalize();


            // -------------------------------------------------
            // Path traversal protection
            // -------------------------------------------------

            if (!filePath.startsWith(uploadPath)) {
                return;
            }


            // -------------------------------------------------
            // Delete file
            // -------------------------------------------------

            Files.deleteIfExists(filePath);

        } catch (IOException ignored) {

            /*
             * File deletion failure should not break
             * the main request.
             */
        }
    }


    // =====================================================
    // DELETE PROFILE IMAGE
    // =====================================================

    public void deleteProfileImage(String imagePath) {

        if (imagePath == null
                || imagePath.isBlank()) {

            return;
        }


        try {

            // -------------------------------------------------
            // Normalize URL path
            // -------------------------------------------------

            String normalizedPath =
                    imagePath.trim()
                            .replace("\\", "/");


            // -------------------------------------------------
            // Only allow profile upload path
            // -------------------------------------------------

            String prefix =
                    "/uploads/profile/";

            if (!normalizedPath.startsWith(prefix)) {
                return;
            }


            // -------------------------------------------------
            // Extract filename
            // -------------------------------------------------

            String fileName =
                    normalizedPath.substring(
                            prefix.length()
                    );


            // -------------------------------------------------
            // Filename safety validation
            // -------------------------------------------------

            if (fileName.isBlank()
                    || fileName.contains("/")
                    || fileName.contains("\\")
                    || fileName.contains("..")) {

                return;
            }


            // -------------------------------------------------
            // Profile directory
            // -------------------------------------------------

            Path uploadPath = Paths
                    .get("uploads", "profile")
                    .toAbsolutePath()
                    .normalize();


            // -------------------------------------------------
            // Resolve file
            // -------------------------------------------------

            Path filePath = uploadPath
                    .resolve(fileName)
                    .normalize();


            // -------------------------------------------------
            // Path traversal protection
            // -------------------------------------------------

            if (!filePath.startsWith(uploadPath)) {
                return;
            }


            // -------------------------------------------------
            // Delete profile image
            // -------------------------------------------------

            Files.deleteIfExists(filePath);

        } catch (IOException ignored) {

            /*
             * Profile image deletion failure should not
             * break the account update request.
             */
        }
    }


    // =====================================================
    // FILE SIZE VALIDATION
    // =====================================================

    private void validateFileSize(MultipartFile file) {

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "Image size must not exceed 10 MB."
            );
        }
    }


    // =====================================================
    // EXTENSION VALIDATION
    // =====================================================

    private void validateExtension(String extension) {

        if (!ALLOWED_EXTENSIONS.contains(extension)) {

            throw new IllegalArgumentException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed."
            );
        }
    }


    // =====================================================
    // CONTENT TYPE VALIDATION
    // =====================================================

    private void validateContentType(
            MultipartFile file) {

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(
                contentType.toLowerCase(Locale.ROOT)
        )) {

            throw new IllegalArgumentException(
                    "Invalid image file type."
            );
        }
    }


    // =====================================================
    // GET FILE EXTENSION
    // =====================================================

    private String getExtension(String filename) {

        int lastDot =
                filename.lastIndexOf('.');

        if (lastDot < 0
                || lastDot == filename.length() - 1) {

            return "";
        }

        return filename
                .substring(lastDot + 1)
                .toLowerCase(Locale.ROOT);
    }
}