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
            String extension = "webp"; // Force WebP
            String filename = UUID.randomUUID().toString() + "." + extension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new RuntimeException("Cannot store file outside current directory.");
            }

            // Convert and Save Original as WebP
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("Invalid image file.");
            }

            // Save optimized WebP
            ImageIO.write(originalImage, "webp", destinationFile.toFile());

            // Generate Thumbnail
            String thumbFilename = "thumb_" + filename;
            Path thumbFile = this.rootLocation.resolve(Paths.get(thumbFilename));

            Thumbnails.of(originalImage)
                    .size(300, 300)
                    .outputFormat("webp")
                    .toFile(thumbFile.toFile());

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
