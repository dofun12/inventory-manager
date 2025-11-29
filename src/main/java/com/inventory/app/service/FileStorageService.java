package com.inventory.app.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "png"; // Default to png for consistency
            if (originalFilename != null && originalFilename.contains(".")) {
                // Keep original extension if it's an image, but let's standardize on png for
                // simplicity as requested
                // Or actually, let's just use the original extension if we can, but user said
                // "return the png".
                // Let's stick to converting to PNG to be safe and consistent.
            }

            String filename = UUID.randomUUID().toString() + ".png";

            Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            // Read original image
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("Invalid image file.");
            }

            // Save as PNG
            ImageIO.write(originalImage, "png", destinationFile.toFile());

            // Generate Thumbnail (also PNG)
            String thumbFilename = "thumb_" + filename;
            Path thumbFile = this.rootLocation.resolve(Paths.get(thumbFilename));

            Thumbnails.of(originalImage)
                    .size(300, 300)
                    .outputFormat("png")
                    .toFile(thumbFile.toFile());

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
