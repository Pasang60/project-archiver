package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.CompressResponse;
import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;
import com.pasang.projectarchiver.algorithm.entity.Files;
import com.pasang.projectarchiver.algorithm.entity.Status;
import com.pasang.projectarchiver.algorithm.repository.FilesRepository;
import com.pasang.projectarchiver.algorithm.util.HuffmanUtil;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
import com.pasang.projectarchiver.utils.file.dto.FileSaveResponse;
import com.pasang.projectarchiver.utils.logged_in_user.LoggedInUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final FileHandlerUtil fileHandlerUtil;
    private final LoggedInUser loggedInUser;
    private final UsersRepository usersRepository;

    private String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " Bytes";
        } else if (sizeInBytes < 1024 * 1024) {
            return (sizeInBytes / 1024) + " KB";
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return (sizeInBytes / (1024 * 1024)) + " MB";
        } else {
            return (sizeInBytes / (1024 * 1024 * 1024)) + " GB";
        }
    }

    @Override
    public FileResponse compressAndSaveFile(FileRequest fileRequest) {
        try {
            if (fileRequest.getFile() == null || fileRequest.getFile().isEmpty()) {
                throw new IllegalArgumentException("No files provided for compression");
            }

            if (fileRequest.getFile().size() == 1) {
                // Handle single file with Huffman compression
                MultipartFile file = fileRequest.getFile().get(0);
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("Uploaded file is empty");
                }
                String originalText = new String(file.getBytes(), StandardCharsets.UTF_8);
                HuffmanUtil.Result result = HuffmanUtil.compress(originalText);

                // Save Huffman encoded data and tree as strings
                String combinedData = result.encodedData + "\n" + result.huffmanTree;
                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
                        combinedData,
                        fileRequest.getFileName(),
                        "compressedFiles"
                );
                log.info("Compressed file saved at: {}, length: {}", fileSaveResponse.getFileDownloadUri(), combinedData.length());

                String fileName = fileRequest.getFileName();
                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
                String mimeType = URLConnection.guessContentTypeFromName(fileName);

                Files files = new Files();
                files.setFileName(fileName);
                files.setDescription(fileRequest.getDescription());
                files.setOriginalFileExtension(fileExtension);
                files.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
                files.setFile(fileSaveResponse.getFileDownloadUri());
                files.setEncodedData(result.encodedData);
                files.setHuffmanTree(result.huffmanTree);
                files.setOriginalFileSize(formatFileSize(file.getSize()));
                files.setCompressedFileSize(formatFileSize(result.compressedSize));
                files.setCompressionAlgorithm("Huffman Compression");

                Long userId = loggedInUser.getLoggedInUser().getId();
                Users user = usersRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                files.setStatus(Status.COMPLETED);
                files.setUser(user);

                filesRepository.save(files);

                return new FileResponse(fileName, result.originalSize, result.compressedSize, fileSaveResponse.getFileDownloadUri());
            } else {
                // Handle multiple files with ZIP Deflate compression
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                long totalOriginalSize = 0;
                int fileCount = 0;

                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                    for (MultipartFile file : fileRequest.getFile()) {
                        String entryName = file.getOriginalFilename();
                        if (entryName == null || entryName.isEmpty()) {
                            log.warn("Skipping file with null or empty name");
                            continue;
                        }
                        log.info("Processing file: {}, size: {}", entryName, file.getSize());
                        totalOriginalSize += file.getSize();
                        ZipEntry zipEntry = new ZipEntry(entryName); // Use original filename as entry
                        zipEntry.setTime(System.currentTimeMillis());
                        zos.putNextEntry(zipEntry);
                        try {
                            zos.write(file.getBytes());
                        } catch (IOException e) {
                            log.error("Failed to write file {} to ZIP: {}", entryName, e.getMessage());
                            continue;
                        }
                        zos.closeEntry();
                        fileCount++;
                    }
                }

                if (fileCount == 0) {
                    throw new IllegalArgumentException("No valid files provided for ZIP compression");
                }

                byte[] zipBytes = baos.toByteArray();
                log.info("ZIP byte length: {}, sample: {}", zipBytes.length, Arrays.toString(Arrays.copyOf(zipBytes, Math.min(10, zipBytes.length))));

                // Validate ZIP file
                try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        log.info("ZIP entry created: {}", entry.getName());
                    }
                } catch (IOException e) {
                    log.error("Invalid ZIP file created: {}", e.getMessage());
                    throw new RuntimeException("Failed to create valid ZIP file", e);
                }

                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
                        zipBytes,
                        fileRequest.getFileName().endsWith(".zip") ? fileRequest.getFileName() : fileRequest.getFileName() + ".zip",
                        "compressedFiles"
                );
                log.info("Compressed ZIP saved at: {}", fileSaveResponse.getFileDownloadUri());

                String fileName = fileRequest.getFileName();
                String fileExtension = fileName.endsWith(".zip") ? "zip" : fileName.substring(fileName.lastIndexOf('.') + 1);
                String mimeType = "application/zip";

                Files files = new Files();
                files.setFileName(fileName.endsWith(".zip") ? fileName : fileName + ".zip");
                files.setDescription(fileRequest.getDescription());
                files.setOriginalFileExtension(fileExtension);
                files.setMimeType(mimeType);
                files.setFile(fileSaveResponse.getFileDownloadUri());
                files.setEncodedData(null);
                files.setHuffmanTree(null);
                files.setOriginalFileSize(formatFileSize(totalOriginalSize));
                files.setCompressedFileSize(formatFileSize(zipBytes.length));
                files.setCompressionAlgorithm("ZIP Deflate");
                files.setCompressedFile(zipBytes);

                Long userId = loggedInUser.getLoggedInUser().getId();
                Users user = usersRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                files.setStatus(Status.COMPLETED);
                files.setUser(user);

                filesRepository.save(files);

                return new FileResponse(files.getFileName(), (int) totalOriginalSize, zipBytes.length, fileSaveResponse.getFileDownloadUri());
            }
        } catch (IOException e) {
            log.error("Failed to compress and save file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to compress and save file: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadHuffmanFile(Long fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!"Huffman Compression".equals(file.getCompressionAlgorithm())) {
            throw new RuntimeException("File is not compressed using Huffman Compression");
        }

        String encodedData = file.getEncodedData();
        String huffmanTree = file.getHuffmanTree();
        if (encodedData == null || huffmanTree == null) {
            throw new RuntimeException("Invalid Huffman data for file ID: " + fileId);
        }

        String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
        byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentLength(bytes.length)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadZipFile(Long fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!"ZIP Deflate".equals(file.getCompressionAlgorithm())) {
            throw new RuntimeException("File is not compressed using ZIP Deflate");
        }

        byte[] zipBytes = file.getCompressedFile();
        if (zipBytes == null || zipBytes.length == 0) {
            throw new RuntimeException("No ZIP data found for file ID: " + fileId);
        }

        ByteArrayResource resource = new ByteArrayResource(zipBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentLength(zipBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    @Override
    public Long getArchivedFilesCount() {
        log.info("Fetching count of archived files");
        Long count = filesRepository.countByCompressedFileSizeNotNull();
        log.info("Count of archived files: {}", count);
        return count;
    }

    @Override
    public Long getUserArchivedFilesCount() {
        log.info("Fetching count of archived files for user: {}", loggedInUser.getLoggedInUser().getId());
        Long userId = loggedInUser.getLoggedInUser().getId();
        Long count = filesRepository.countByUserId(userId);
        log.info("Count of archived files for user {}: {}", userId, count);
        return count;
    }

    @Override
    public List<CompressResponse> getAllArchivedFiles() {
        log.info("Fetching all archived files");
        List<Files> archivedFiles = filesRepository.findByCompressedFileSizeNotNull();
        return archivedFiles.stream()
                .map(CompressResponse::new)
                .toList();
    }

    @Override
    public List<CompressResponse> getUserArchivedFiles() {
        log.info("Fetching archived files for user: {}", loggedInUser.getLoggedInUser().getId());
        Long userId = loggedInUser.getLoggedInUser().getId();
        List<Files> archivedFiles = filesRepository.findByUserIdAndCompressedFileSizeNotNullAndStatus(userId, Status.COMPLETED);
        return archivedFiles.stream()
                .map(CompressResponse::new)
                .toList();
    }
}