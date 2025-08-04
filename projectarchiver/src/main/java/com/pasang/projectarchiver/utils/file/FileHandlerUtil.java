//package com.pasang.projectarchiver.utils.file;
//
//import com.pasang.projectarchiver.config.file.FileConfig;
//import com.pasang.projectarchiver.utils.file.dto.FileSaveResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class FileHandlerUtil {
//
//    private final FileConfig fileConfig;
//
//    public FileSaveResponse saveFile(MultipartFile file, String additionalPath) {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("Invalid file");
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null || originalFilename.isBlank()) {
//            throw new IllegalArgumentException("Invalid file name");
//        }
//
//        // Clean and generate unique file name
//        String baseName = getBaseName(originalFilename);
//        String extension = getFileExtension(originalFilename);
//        String cleanedName = cleanFileName(baseName);
//        String uniqueFileName = cleanedName + "-" + System.currentTimeMillis() + "." + extension;
//
//        // Prepare relative and full path
//        String relativePath = (additionalPath != null && !additionalPath.isBlank())
//                ? additionalPath + "/" + uniqueFileName
//                : uniqueFileName;
//        String basePath = fileConfig.getFilePath();
//        Path fullPath = Paths.get(basePath, relativePath);
//
//        log.info("[FileHandlerUtil:saveFile] Saving file: {} to {}", uniqueFileName, fullPath);
//
//        String fileDimension = null;
//        if (isImageFile(extension)) {
//            fileDimension = getFileDimension(file);
//        }
//
//        try {
//            Files.createDirectories(fullPath.getParent());
//            file.transferTo(fullPath.toFile());
//
//            FileType fileType = determineFileType(extension);
//            log.info("[FileHandlerUtil:saveFile] File saved at: {}", fullPath);
//
//            return new FileSaveResponse(uniqueFileName, relativePath, fileType, fileDimension);
//        } catch (IOException e) {
//            log.error("[FileHandlerUtil:saveFile] Error saving file: {}", e.getMessage());
//            throw new RuntimeException("Failed to save file", e);
//        }
//    }
//
//    public FileType determineFileType(String extension) {
//        log.info("[FileHandlerUtil:determineFileType] File extension: {}", extension);
//        return switch (extension.toLowerCase()) {
//            case "png", "jpg", "jpeg", "gif", "bmp" -> FileType.IMAGE;
//            case "pdf", "doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx" -> FileType.DOCUMENT;
//            case "mp4", "avi", "mkv", "mov" -> FileType.VIDEO;
//            case "mp3", "wav", "aac" -> FileType.AUDIO;
//            default -> FileType.UNKNOWN;
//        };
//    }
//
//    public String getFileDimension(MultipartFile file) {
//        try {
//            BufferedImage image = ImageIO.read(file.getInputStream());
//            return image.getWidth() + "x" + image.getHeight();
//        } catch (IOException e) {
//            log.error("[FileHandlerUtil:getFileDimension] Error getting file dimension: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    private boolean isImageFile(String extension) {
//        return switch (extension.toLowerCase()) {
//            case "png", "jpg", "jpeg", "gif", "bmp" -> true;
//            default -> false;
//        };
//    }
//
//    private String cleanFileName(String name) {
//        return name.toLowerCase()
//                .replaceAll("[^a-z0-9\\-]", "-")     // replace special characters with dash
//                .replaceAll("-{2,}", "-")            // replace multiple dashes with single
//                .replaceAll("^-|-$", "");            // trim leading/trailing dashes
//    }
//
//    private String getFileExtension(String fileName) {
//        return fileName.contains(".")
//                ? fileName.substring(fileName.lastIndexOf('.') + 1)
//                : "";
//    }
//
//    private String getBaseName(String fileName) {
//        return fileName.contains(".")
//                ? fileName.substring(0, fileName.lastIndexOf('.'))
//                : fileName;
//    }
//
//    public FileSaveResponse saveCompressedData(String compressedData, String fileName, String additionalPath) {
//        if (compressedData == null || compressedData.isBlank()) {
//            throw new IllegalArgumentException("Invalid compressed data");
//        }
//
//        String cleanedName = cleanFileName(fileName);
//        String uniqueFileName = cleanedName + "-" + System.currentTimeMillis() + ".compressed";
//
//        // Prepare relative and full path
//        String relativePath = (additionalPath != null && !additionalPath.isBlank())
//                ? additionalPath + "/" + uniqueFileName
//                : uniqueFileName;
//        String basePath = fileConfig.getFilePath();
//        Path fullPath = Paths.get(basePath, relativePath);
//
//        log.info("[FileHandlerUtil:saveCompressedData] Saving compressed file: {} to {}", uniqueFileName, fullPath);
//
//        try {
//            Files.createDirectories(fullPath.getParent());
//            Files.writeString(fullPath, compressedData);
//
//            log.info("[FileHandlerUtil:saveCompressedData] Compressed file saved at: {}", fullPath);
//
//            return new FileSaveResponse(uniqueFileName, relativePath, FileType.DOCUMENT, null);
//        } catch (IOException e) {
//            log.error("[FileHandlerUtil:saveCompressedData] Error saving compressed file: {}", e.getMessage());
//            throw new RuntimeException("Failed to save compressed file", e);
//        }
//    }
//}
package com.pasang.projectarchiver.utils.file;

import com.pasang.projectarchiver.config.file.FileConfig;
import com.pasang.projectarchiver.utils.file.dto.FileSaveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileHandlerUtil {

    private final FileConfig fileConfig;

    public FileSaveResponse saveFile(MultipartFile file, String additionalPath) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Invalid file");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String baseName = getBaseName(originalFilename);
        String extension = getFileExtension(originalFilename);
        String cleanedName = cleanFileName(baseName);
        String uniqueFileName = cleanedName + "-" + System.currentTimeMillis() + "." + extension;

        String relativePath = (additionalPath != null && !additionalPath.isBlank())
                ? additionalPath + "/" + uniqueFileName
                : uniqueFileName;
        String basePath = fileConfig.getFilePath();
        Path fullPath = Paths.get(basePath, relativePath);

        log.info("[FileHandlerUtil:saveFile] Saving file: {} to {}", uniqueFileName, fullPath);

        String fileDimension = null;
        if (isImageFile(extension)) {
            fileDimension = getFileDimension(file);
        }

        try {
            Files.createDirectories(fullPath.getParent());
            file.transferTo(fullPath.toFile());

            FileType fileType = determineFileType(extension);
            log.info("[FileHandlerUtil:saveFile] File saved at: {}", fullPath);

            return new FileSaveResponse(uniqueFileName, relativePath, fileType, fileDimension);
        } catch (IOException e) {
            log.error("[FileHandlerUtil:saveFile] Error saving file: {}", e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public FileSaveResponse saveCompressedData(byte[] compressedData, String fileName, String additionalPath) {
        if (compressedData == null || compressedData.length == 0) {
            throw new IllegalArgumentException("Invalid compressed data");
        }

        String cleanedName = cleanFileName(fileName);
        String extension = getFileExtension(fileName);
        String uniqueFileName = cleanedName + "-" + System.currentTimeMillis() + (extension.isEmpty() ? ".zip" : "." + extension);

        String relativePath = (additionalPath != null && !additionalPath.isBlank())
                ? additionalPath + "/" + uniqueFileName
                : uniqueFileName;
        String basePath = fileConfig.getFilePath();
        Path fullPath = Paths.get(basePath, relativePath);

        log.info("[FileHandlerUtil:saveCompressedData] Saving compressed file: {} to {}", uniqueFileName, fullPath);

        try {
            Files.createDirectories(fullPath.getParent());
            Files.write(fullPath, compressedData); // Save raw bytes

            log.info("[FileHandlerUtil:saveCompressedData] Compressed file saved at: {}", fullPath);

            return new FileSaveResponse(uniqueFileName, relativePath, FileType.DOCUMENT, null);
        } catch (IOException e) {
            log.error("[FileHandlerUtil:saveCompressedData] Error saving compressed file: {}", e.getMessage());
            throw new RuntimeException("Failed to save compressed file", e);
        }
    }

    public FileType determineFileType(String extension) {
        log.info("[FileHandlerUtil:determineFileType] File extension: {}", extension);
        return switch (extension.toLowerCase()) {
            case "png", "jpg", "jpeg", "gif", "bmp" -> FileType.IMAGE;
            case "pdf", "doc", "docx", "txt", "xls", "xlsx", "ppt", "pptx" -> FileType.DOCUMENT;
            case "mp4", "avi", "mkv", "mov" -> FileType.VIDEO;
            case "mp3", "wav", "aac" -> FileType.AUDIO;
            case "zip" -> FileType.DOCUMENT;
            default -> FileType.UNKNOWN;
        };
    }

    public String getFileDimension(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image.getWidth() + "x" + image.getHeight();
        } catch (IOException e) {
            log.error("[FileHandlerUtil:getFileDimension] Error getting file dimension: {}", e.getMessage());
            return null;
        }
    }

    private boolean isImageFile(String extension) {
        return switch (extension.toLowerCase()) {
            case "png", "jpg", "jpeg", "gif", "bmp" -> true;
            default -> false;
        };
    }

    private String cleanFileName(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\-]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

    private String getFileExtension(String fileName) {
        return fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1)
                : "";
    }

    private String getBaseName(String fileName) {
        return fileName.contains(".")
                ? fileName.substring(0, fileName.lastIndexOf('.'))
                : fileName;
    }

    public FileSaveResponse saveCompressedData(String compressedData, String fileName, String additionalPath) {
        if (compressedData == null || compressedData.isBlank()) {
            throw new IllegalArgumentException("Invalid compressed data");
        }

        String cleanedName = cleanFileName(fileName);
        String uniqueFileName = cleanedName + "-" + System.currentTimeMillis() + ".compressed";

        String relativePath = (additionalPath != null && !additionalPath.isBlank())
                ? additionalPath + "/" + uniqueFileName
                : uniqueFileName;
        String basePath = fileConfig.getFilePath();
        Path fullPath = Paths.get(basePath, relativePath);

        log.info("[FileHandlerUtil:saveCompressedData] Saving compressed file: {} to {}", uniqueFileName, fullPath);

        try {
            Files.createDirectories(fullPath.getParent());
            Files.writeString(fullPath, compressedData);

            log.info("[FileHandlerUtil:saveCompressedData] Compressed file saved at: {}", fullPath);

            return new FileSaveResponse(uniqueFileName, relativePath, FileType.DOCUMENT, null);
        } catch (IOException e) {
            log.error("[FileHandlerUtil:saveCompressedData] Error saving compressed file: {}", e.getMessage());
            throw new RuntimeException("Failed to save compressed file", e);
        }
    }
}