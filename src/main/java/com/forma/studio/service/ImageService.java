package com.forma.studio.service;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Handles all image upload, resize, and deletion operations.
 *
 * WHY this approach matters for performance:
 * A photographer's raw image can be 8-12MB. If 12 project cards on a page each
 * load a full-size image, that's 96-144MB just to display the grid. By automatically
 * creating three smaller versions on upload, the grid page loads ~300KB instead.
 *
 * The three versions we create for every image:
 *   LARGE   - max 1920px wide, 85% quality  →  ~200KB  (detail page, lightbox)
 *   MEDIUM  - max 800px wide,  80% quality  →  ~80KB   (carousels, about page)
 *   THUMB   - exactly 400x300px cropped,    →  ~25KB   (project grid cards)
 *              75% quality
 *
 * =====================================================================
 * HOW TO UPGRADE TO CLOUDINARY (when you're ready to deploy to production):
 * =====================================================================
 * Currently this service saves files to local disk. To switch to Cloudinary:
 *
 * 1. Add the Cloudinary SDK to pom.xml:
 *    <dependency>
 *        <groupId>com.cloudinary</groupId>
 *        <artifactId>cloudinary-http5</artifactId>
 *        <version>2.x.x</version>
 *    </dependency>
 *
 * 2. Add to application.properties:
 *    app.cloudinary.cloud-name=your_cloud_name
 *    app.cloudinary.api-key=your_api_key
 *    app.cloudinary.api-secret=your_api_secret
 *
 * 3. Replace the saveToLocalDisk() calls inside processAndSave() with:
 *    cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(
 *        "public_id", filename + "_large",
 *        "transformation", new Transformation().width(1920).quality(85)
 *    ));
 *    Then store the returned secure_url in the ImageResult.
 *
 * 4. Replace the deleteFromLocalDisk() call inside deleteImage() with:
 *    cloudinary.uploader().destroy(filename + "_large", ObjectUtils.emptyMap());
 *    cloudinary.uploader().destroy(filename + "_medium", ObjectUtils.emptyMap());
 *    cloudinary.uploader().destroy(filename + "_thumb", ObjectUtils.emptyMap());
 *
 * Nothing else in the codebase needs to change — only this file.
 * =====================================================================
 */
@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    // Allowed file types. We reject anything else to prevent malicious uploads.
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    // The folder on disk where we save images. Comes from application.properties.
    @Value("${app.upload.dir}")
    private String uploadDir;

    // The base URL of our server. Used to build full URLs like http://localhost:8080/uploads/large/abc.jpg
    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Holds the three URLs produced after processing one uploaded image.
     * This is a simple container — not a database entity.
     */
    public static class ImageResult {
        public final String filename;     // UUID base name (no extension), used for deletion
        public final String largeUrl;     // Full URL to the large version
        public final String mediumUrl;    // Full URL to the medium version
        public final String thumbnailUrl; // Full URL to the thumbnail version

        public ImageResult(String filename, String largeUrl, String mediumUrl, String thumbnailUrl) {
            this.filename = filename;
            this.largeUrl = largeUrl;
            this.mediumUrl = mediumUrl;
            this.thumbnailUrl = thumbnailUrl;
        }
    }

    /**
     * Takes an uploaded image file, validates it, creates three resized versions,
     * saves them to disk, and returns the URLs.
     *
     * This is the main method called by ProjectService and TeamService when an image is uploaded.
     *
     * @param file The raw uploaded file from the HTTP request
     * @return ImageResult containing URLs for all three saved versions
     * @throws IOException if file cannot be read or written
     * @throws IllegalArgumentException if the file type is not allowed
     */
    public ImageResult processAndSave(MultipartFile file) throws IOException {
        // Step 1: Reject files that aren't images
        validateFileType(file);

        // Step 2: Create the upload directories if they don't exist yet
        ensureDirectoriesExist();

        // Step 3: Generate a unique filename. We never use the original filename
        // because it could contain spaces, special characters, or security issues.
        String filename = UUID.randomUUID().toString();

        // Step 4: Define where each version will be saved on disk
        Path largePath  = Paths.get(uploadDir, "large",  filename + ".jpg");
        Path mediumPath = Paths.get(uploadDir, "medium", filename + ".jpg");
        Path thumbPath  = Paths.get(uploadDir, "thumb",  filename + ".jpg");

        // Step 5: Resize and save all three versions using Thumbnailator
        // We use the uploaded file's input stream for each operation
        // (Thumbnailator reads the stream efficiently without loading everything into memory at once)

        // LARGE: scale down to max 1920px width, keep aspect ratio, 85% JPEG quality
        Thumbnails.of(file.getInputStream())
            .width(1920)
            .keepAspectRatio(true)
            .outputQuality(0.85)
            .outputFormat("jpg")
            .toFile(largePath.toFile());

        // MEDIUM: scale down to max 800px width, keep aspect ratio, 80% quality
        Thumbnails.of(file.getInputStream())
            .width(800)
            .keepAspectRatio(true)
            .outputQuality(0.80)
            .outputFormat("jpg")
            .toFile(mediumPath.toFile());

        // THUMB: crop to EXACTLY 400x300px from the center, 75% quality
        // We use cropFirst=true so the image fills the box without distortion.
        // This ensures consistent card sizes in the project grid.
        Thumbnails.of(file.getInputStream())
            .size(400, 300)
            .crop(Positions.CENTER)
            .outputQuality(0.75)
            .outputFormat("jpg")
            .toFile(thumbPath.toFile());

        // Step 6: Build public URLs for each version
        String largeUrl  = buildUrl("large",  filename);
        String mediumUrl = buildUrl("medium", filename);
        String thumbUrl  = buildUrl("thumb",  filename);

        logger.info("Processed image '{}' from original file '{}'", filename, file.getOriginalFilename());

        return new ImageResult(filename, largeUrl, mediumUrl, thumbUrl);
    }

    /**
     * Deletes all three versions of an image from disk.
     * Called when an admin deletes an image or when a project is deleted.
     *
     * @param filename The UUID base filename (without extension or folder path)
     */
    public void deleteImage(String filename) {
        deleteFileIfExists(Paths.get(uploadDir, "large",  filename + ".jpg"));
        deleteFileIfExists(Paths.get(uploadDir, "medium", filename + ".jpg"));
        deleteFileIfExists(Paths.get(uploadDir, "thumb",  filename + ".jpg"));
        logger.info("Deleted all versions of image '{}'", filename);
    }

    /**
     * Checks that the uploaded file is an image type we support.
     * This prevents admins from accidentally uploading PDFs or other files.
     */
    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "Invalid file type: " + contentType + ". Only JPG, PNG, and WEBP images are allowed."
            );
        }
    }

    /**
     * Creates the upload directories if they don't already exist.
     * This runs once on first upload — after that the directories exist.
     */
    private void ensureDirectoriesExist() throws IOException {
        Files.createDirectories(Paths.get(uploadDir, "large"));
        Files.createDirectories(Paths.get(uploadDir, "medium"));
        Files.createDirectories(Paths.get(uploadDir, "thumb"));
    }

    /**
     * Builds a full public URL for an image file.
     * Example: http://localhost:8080/uploads/large/abc123.jpg
     *
     * The "/uploads" path is served as a static resource by WebConfig.
     * In production, you might serve these files through Nginx instead,
     * which is much faster than letting Spring Boot serve static files.
     */
    private String buildUrl(String folder, String filename) {
        return baseUrl + "/uploads/" + folder + "/" + filename + ".jpg";
    }

    /**
     * Deletes a single file from disk without throwing an error if it doesn't exist.
     * We use "silently" here because the file might already be gone (e.g. manual cleanup).
     */
    private void deleteFileIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log a warning but don't stop execution — a missing file isn't critical
            logger.warn("Could not delete file at path '{}': {}", path, e.getMessage());
        }
    }
}
