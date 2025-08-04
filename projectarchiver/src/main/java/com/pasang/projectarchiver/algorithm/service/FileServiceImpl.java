////package com.pasang.projectarchiver.algorithm.service;
////
////import com.pasang.projectarchiver.algorithm.dto.CompressResponse;
////import com.pasang.projectarchiver.algorithm.dto.FileRequest;
////import com.pasang.projectarchiver.algorithm.dto.FileResponse;
////import com.pasang.projectarchiver.algorithm.entity.Files;
////import com.pasang.projectarchiver.algorithm.entity.Status;
////import com.pasang.projectarchiver.algorithm.repository.FilesRepository;
////import com.pasang.projectarchiver.algorithm.util.HuffmanUtil;
////import com.pasang.projectarchiver.users.entity.Users;
////import com.pasang.projectarchiver.users.repository.UsersRepository;
////import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
////import com.pasang.projectarchiver.utils.file.dto.FileSaveResponse;
////import com.pasang.projectarchiver.utils.logged_in_user.LoggedInUser;
////import lombok.RequiredArgsConstructor;
////import lombok.extern.slf4j.Slf4j;
////import org.springframework.core.io.ByteArrayResource;
////import org.springframework.http.*;
////import org.springframework.stereotype.Service;
////
////import java.io.*;
////import java.net.URLConnection;
////import java.nio.charset.StandardCharsets;
////import java.util.List;
////
////@Service
////@Slf4j
////@RequiredArgsConstructor
////public class FileServiceImpl implements FileService {
////    private final FilesRepository filesRepository;
////    private final FileHandlerUtil fileHandlerUtil;
////    private final LoggedInUser loggedInUser;
////    private final UsersRepository usersRepository;
////
////
////    private String formatFileSize(long sizeInBytes) {
////        if (sizeInBytes < 1024) {
////            return sizeInBytes + " Bytes";
////        } else if (sizeInBytes < 1024 * 1024) {
////            return (sizeInBytes / 1024) + " KB";
////        } else if (sizeInBytes < 1024 * 1024 * 1024) {
////            return (sizeInBytes / (1024 * 1024)) + " MB";
////        } else {
////            return (sizeInBytes / (1024 * 1024 * 1024)) + " GB";
////        }
////    }
////    @Override
////    public FileResponse compressAndSaveFile(FileRequest fileRequest) {
////        try {
////            // 1. Read file
////            String originalText = new String(fileRequest.getFile().getBytes());
////
////            // 2. Huffman compress
////            HuffmanUtil.Result result = HuffmanUtil.compress(originalText);
////
////            // 3. Save compressed data using FileHandlerUtil
////            FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
////                    result.encodedData,
////                    fileRequest.getFileName(),
////                    "compressedFiles"
////            );
////            log.info("Compressed file saved at: {}", fileSaveResponse.getFileDownloadUri());
////
////            // 4. Extract file extension and MIME type
////            String fileName = fileRequest.getFileName();
////            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1); // Extract extension
////            String mimeType = URLConnection.guessContentTypeFromName(fileName); // Guess MIME type
////
////            // 4. Save metadata in the database
////            Files files = new Files();
////            files.setFileName(fileName);
////            files.setDescription(fileRequest.getDescription());
////            files.setOriginalFileExtension(fileExtension);
////            files.setMimeType(mimeType);
////            files.setFile(fileSaveResponse.getFileDownloadUri());
////            files.setEncodedData(result.encodedData);
////            files.setHuffmanTree(result.huffmanTree);
////
////            // Format file sizes
////            files.setOriginalFileSize(formatFileSize(fileRequest.getFile().getBytes().length));
////            files.setCompressedFileSize(formatFileSize(result.compressedSize));
////
////            files.setCompressionAlgorithm("Huffman Compression");
////
////            Long userId = loggedInUser.getLoggedInUser().getId();
////            Users user = usersRepository.findById(userId)
////                    .orElseThrow(() -> new RuntimeException("User not found"));
////            files.setStatus(Status.COMPLETED);
////            files.setUser(user);
////
////            filesRepository.save(files);
////
////            return new FileResponse(fileRequest.getFileName(), result.originalSize, result.compressedSize,
////                                    fileSaveResponse.getFileDownloadUri());
////
////        } catch (IOException e) {
////            throw new RuntimeException("Failed to compress and save file", e);
////        }
////    }
////
//////    @Override
//////    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
//////        Files file = filesRepository.findById(fileId)
//////                .orElseThrow(() -> new RuntimeException("File not found"));
//////
//////        String encodedData = file.getEncodedData();
//////        String huffmanTree = file.getHuffmanTree();
//////
//////        String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
//////        byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
//////        ByteArrayResource resource = new ByteArrayResource(bytes);
//////
//////        return ResponseEntity.ok()
//////                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"original_" + file.getFileName() + ".txt\"")
//////                .contentLength(bytes.length)
//////                .contentType(MediaType.TEXT_PLAIN)
//////                .body(resource);
//////    }
////
////    @Override
////    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
////        Files file = filesRepository.findById(fileId)
////                .orElseThrow(() -> new RuntimeException("File not found"));
////
////        String encodedData = file.getEncodedData();
////        String huffmanTree = file.getHuffmanTree();
////
////        // Decompress the content
////        String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
////
////        // Convert to bytes
////        byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
////        ByteArrayResource resource = new ByteArrayResource(bytes);
////
////        // Return with original file name
////        return ResponseEntity.ok()
////                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
////                .contentLength(bytes.length)
////                .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                .body(resource);
////    }
////
////    @Override
////    public Long getArchivedFilesCount() {
////        log.info("Fetching count of archived files");
////        Long count = filesRepository.countByCompressedFileSizeNotNull();
////        log.info("Count of archived files: {}", count);
////            return count;
////
////    }
////
////    @Override
////    public Long getUserArchivedFilesCount() {
////        log.info("Fetching count of archived files for user: {}", loggedInUser.getLoggedInUser().getId());
////        Long userId = loggedInUser.getLoggedInUser().getId();
////        Long count = filesRepository.countByUserId(userId);
////        log.info("Count of archived files for user {}: {}", userId, count);
////        return count;
////    }
////
////    @Override
////    public List<CompressResponse> getAllArchivedFiles() {
////        log.info("Fetching all archived files");
////
////        List<Files> archivedFiles = filesRepository.findByCompressedFileSizeNotNull();
////
////        return archivedFiles.stream()
////                .map(CompressResponse::new)
////                .toList();
////    }
////
////    @Override
////    public List<CompressResponse> getUserArchivedFiles() {
////        log.info("Fetching archived files for user: {}", loggedInUser.getLoggedInUser().getId());
////        Long userId = loggedInUser.getLoggedInUser().getId();
////        List<Files> archivedFiles = filesRepository.findByUserIdAndCompressedFileSizeNotNullAndStatus(userId, Status.COMPLETED);
////        return archivedFiles.stream()
////                .map(CompressResponse::new)
////                .toList();
////
////    }
////
////
////
////
////}
//
//
//
//
//package com.pasang.projectarchiver.algorithm.service;
//
//import com.pasang.projectarchiver.algorithm.dto.CompressResponse;
//import com.pasang.projectarchiver.algorithm.dto.FileRequest;
//import com.pasang.projectarchiver.algorithm.dto.FileResponse;
//import com.pasang.projectarchiver.algorithm.entity.Files;
//import com.pasang.projectarchiver.algorithm.entity.Status;
//import com.pasang.projectarchiver.algorithm.repository.FilesRepository;
//import com.pasang.projectarchiver.algorithm.util.HuffmanUtil;
//import com.pasang.projectarchiver.users.entity.Users;
//import com.pasang.projectarchiver.users.repository.UsersRepository;
//import com.pasang.projectarchiver.utils.file.FileHandlerUtil;
//import com.pasang.projectarchiver.utils.file.dto.FileSaveResponse;
//import com.pasang.projectarchiver.utils.logged_in_user.LoggedInUser;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.*;
//import java.net.URLConnection;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;
//import java.util.zip.ZipOutputStream;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class FileServiceImpl implements FileService {
//    private final FilesRepository filesRepository;
//    private final FileHandlerUtil fileHandlerUtil;
//    private final LoggedInUser loggedInUser;
//    private final UsersRepository usersRepository;
//
//    private String formatFileSize(long sizeInBytes) {
//        if (sizeInBytes < 1024) {
//            return sizeInBytes + " Bytes";
//        } else if (sizeInBytes < 1024 * 1024) {
//            return (sizeInBytes / 1024) + " KB";
//        } else if (sizeInBytes < 1024 * 1024 * 1024) {
//            return (sizeInBytes / (1024 * 1024)) + " MB";
//        } else {
//            return (sizeInBytes / (1024 * 1024 * 1024)) + " GB";
//        }
//    }
//
////    @Override
////    public FileResponse compressAndSaveFile(FileRequest fileRequest) {
////        try {
////            if (fileRequest.getFile().size() == 1) {
////                // Handle single file with Huffman compression
////                String originalText = new String(fileRequest.getFile().get(0).getBytes());
////                HuffmanUtil.Result result = HuffmanUtil.compress(originalText);
////
////                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
////                        result.encodedData,
////                        fileRequest.getFileName(),
////                        "compressedFiles"
////                );
////                log.info("Compressed file saved at: {}", fileSaveResponse.getFileDownloadUri());
////
////                String fileName = fileRequest.getFileName();
////                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
////                String mimeType = URLConnection.guessContentTypeFromName(fileName);
////
////                Files files = new Files();
////                files.setFileName(fileName);
////                files.setDescription(fileRequest.getDescription());
////                files.setOriginalFileExtension(fileExtension);
////                files.setMimeType(mimeType);
////                files.setFile(fileSaveResponse.getFileDownloadUri());
////                files.setEncodedData(result.encodedData);
////                files.setHuffmanTree(result.huffmanTree);
////                files.setOriginalFileSize(formatFileSize(fileRequest.getFile().get(0).getSize()));
////                files.setCompressedFileSize(formatFileSize(result.compressedSize));
////                files.setCompressionAlgorithm("Huffman Compression");
////
////                Long userId = loggedInUser.getLoggedInUser().getId();
////                Users user = usersRepository.findById(userId)
////                        .orElseThrow(() -> new RuntimeException("User not found"));
////                files.setStatus(Status.COMPLETED);
////                files.setUser(user);
////
////                filesRepository.save(files);
////
////                return new FileResponse(fileName, result.originalSize, result.compressedSize, fileSaveResponse.getFileDownloadUri());
////            } else {
////                // Handle multiple files with ZIP Deflate compression
////                ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                long totalOriginalSize = 0;
////
////                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
////                    for (MultipartFile file : fileRequest.getFile()) {
////                        String entryName = file.getOriginalFilename();
////                        if (entryName == null || entryName.isEmpty()) {
////                            continue;
////                        }
////                        totalOriginalSize += file.getSize();
////                        ZipEntry zipEntry = new ZipEntry(entryName);
////                        zipEntry.setTime(System.currentTimeMillis());
////                        zos.putNextEntry(zipEntry);
////                        zos.write(file.getBytes());
////                        zos.closeEntry();
////                    }
////                }
////
////                byte[] zipBytes = baos.toByteArray();
////                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
////                        new String(zipBytes, StandardCharsets.ISO_8859_1),
////                        fileRequest.getFileName(),
////                        "compressedFiles"
////                );
////                log.info("Compressed ZIP saved at: {}", fileSaveResponse.getFileDownloadUri());
////
////                String fileName = fileRequest.getFileName();
////                String fileExtension = fileName.endsWith(".zip") ? "zip" : fileName.substring(fileName.lastIndexOf('.') + 1);
////                String mimeType = "application/zip";
////
////                Files files = new Files();
////                files.setFileName(fileName);
////                files.setDescription(fileRequest.getDescription());
////                files.setOriginalFileExtension(fileExtension);
////                files.setMimeType(mimeType);
////                files.setFile(fileSaveResponse.getFileDownloadUri());
////                files.setEncodedData(null); // No Huffman encoding for ZIP
////                files.setHuffmanTree(null);
////                files.setOriginalFileSize(formatFileSize(totalOriginalSize));
////                files.setCompressedFileSize(formatFileSize(zipBytes.length));
////                files.setCompressionAlgorithm("ZIP Deflate");
////                files.setCompressedFile(zipBytes);
////
////                Long userId = loggedInUser.getLoggedInUser().getId();
////                Users user = usersRepository.findById(userId)
////                        .orElseThrow(() -> new RuntimeException("User not found"));
////                files.setStatus(Status.COMPLETED);
////                files.setUser(user);
////
////                filesRepository.save(files);
////
////                return new FileResponse(fileName, (int) totalOriginalSize, zipBytes.length, fileSaveResponse.getFileDownloadUri());
////            }
////        } catch (IOException e) {
////            throw new RuntimeException("Failed to compress and save file", e);
////        }
////    }
//
//
//    @Override
//    public FileResponse compressAndSaveFile(FileRequest fileRequest) {
//        try {
//            if (fileRequest.getFile() == null || fileRequest.getFile().isEmpty()) {
//                throw new IllegalArgumentException("No files provided for compression");
//            }
//
//            if (fileRequest.getFile().size() == 1) {
//                // Handle single file with Huffman compression
//                MultipartFile file = fileRequest.getFile().get(0);
//                if (file.isEmpty()) {
//                    throw new IllegalArgumentException("Uploaded file is empty");
//                }
//                String originalText = new String(file.getBytes(), StandardCharsets.UTF_8);
//                HuffmanUtil.Result result = HuffmanUtil.compress(originalText);
//
//                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
//                        Arrays.toString(result.encodedData.getBytes(StandardCharsets.UTF_8)),
//                        fileRequest.getFileName(),
//                        "compressedFiles"
//                );
//                log.info("Compressed file saved at: {}", fileSaveResponse.getFileDownloadUri());
//
//                String fileName = fileRequest.getFileName();
//                String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
//                String mimeType = URLConnection.guessContentTypeFromName(fileName);
//
//                Files files = new Files();
//                files.setFileName(fileName);
//                files.setDescription(fileRequest.getDescription());
//                files.setOriginalFileExtension(fileExtension);
//                files.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
//                files.setFile(fileSaveResponse.getFileDownloadUri());
//                files.setEncodedData(result.encodedData);
//                files.setHuffmanTree(result.huffmanTree);
//                files.setOriginalFileSize(formatFileSize(file.getSize()));
//                files.setCompressedFileSize(formatFileSize(result.compressedSize));
//                files.setCompressionAlgorithm("Huffman Compression");
//
//                Long userId = loggedInUser.getLoggedInUser().getId();
//                Users user = usersRepository.findById(userId)
//                        .orElseThrow(() -> new RuntimeException("User not found"));
//                files.setStatus(Status.COMPLETED);
//                files.setUser(user);
//
//                filesRepository.save(files);
//
//                return new FileResponse(fileName, result.originalSize, result.compressedSize, fileSaveResponse.getFileDownloadUri());
//            } else {
//                // Handle multiple files with ZIP Deflate compression
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                long totalOriginalSize = 0;
//
//                try (ZipOutputStream zos = new ZipOutputStream(baos)) {
//                    for (MultipartFile file : fileRequest.getFile()) {
//                        String entryName = file.getOriginalFilename();
//                        if (entryName == null || entryName.isEmpty()) {
//                            log.warn("Skipping file with null or empty name");
//                            continue;
//                        }
//                        totalOriginalSize += file.getSize();
//                        ZipEntry zipEntry = new ZipEntry(entryName);
//                        zipEntry.setTime(System.currentTimeMillis());
//                        zos.putNextEntry(zipEntry);
//                        zos.write(file.getBytes());
//                        zos.closeEntry();
//                    }
//                }
//
//                byte[] zipBytes = baos.toByteArray();
//                FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
//                        zipBytes, // Save raw bytes
//                        fileRequest.getFileName().endsWith(".zip") ? fileRequest.getFileName() : fileRequest.getFileName() + ".zip",
//                        "compressedFiles"
//                );
//                log.info("Compressed ZIP saved at: {}", fileSaveResponse.getFileDownloadUri());
//
//                String fileName = fileRequest.getFileName();
//                String fileExtension = fileName.endsWith(".zip") ? "zip" : fileName.substring(fileName.lastIndexOf('.') + 1);
//                String mimeType = "application/zip";
//
//                Files files = new Files();
//                files.setFileName(fileName.endsWith(".zip") ? fileName : fileName + ".zip");
//                files.setDescription(fileRequest.getDescription());
//                files.setOriginalFileExtension(fileExtension);
//                files.setMimeType(mimeType);
//                files.setFile(fileSaveResponse.getFileDownloadUri());
//                files.setEncodedData(null);
//                files.setHuffmanTree(null);
//                files.setOriginalFileSize(formatFileSize(totalOriginalSize));
//                files.setCompressedFileSize(formatFileSize(zipBytes.length));
//                files.setCompressionAlgorithm("ZIP Deflate");
//                files.setCompressedFile(zipBytes);
//
//                Long userId = loggedInUser.getLoggedInUser().getId();
//                Users user = usersRepository.findById(userId)
//                        .orElseThrow(() -> new RuntimeException("User not found"));
//                files.setStatus(Status.COMPLETED);
//                files.setUser(user);
//
//                filesRepository.save(files);
//
//                return new FileResponse(files.getFileName(), (int) totalOriginalSize, zipBytes.length, fileSaveResponse.getFileDownloadUri());
//            }
//        } catch (IOException e) {
//            log.error("Failed to compress and save file: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to compress and save file: " + e.getMessage(), e);
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid input: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
//
////    @Override
////    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
////        Files file = filesRepository.findById(fileId)
////                .orElseThrow(() -> new RuntimeException("File not found"));
////
////        if ("Huffman Compression".equals(file.getCompressionAlgorithm())) {
////            String encodedData = file.getEncodedData();
////            String huffmanTree = file.getHuffmanTree();
////            String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
////            byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
////            ByteArrayResource resource = new ByteArrayResource(bytes);
////
////            return ResponseEntity.ok()
////                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
////                    .contentLength(bytes.length)
////                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                    .body(resource);
////        } else {
////            // For ZIP files, return the compressed file as-is
////            byte[] zipBytes = file.getCompressedFile();
////            ByteArrayResource resource = new ByteArrayResource(zipBytes);
////
////            return ResponseEntity.ok()
////                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
////                    .contentLength(zipBytes.length)
////                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                    .body(resource);
////        }
////    }
//
//    @Override
//    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
//        Files file = filesRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        if ("Huffman Compression".equals(file.getCompressionAlgorithm())) {
//            String encodedData = file.getEncodedData();
//            String huffmanTree = file.getHuffmanTree();
//            if (encodedData == null || huffmanTree == null) {
//                throw new RuntimeException("Invalid compression data for file ID: " + fileId);
//            }
//            String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
//            byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
//            ByteArrayResource resource = new ByteArrayResource(bytes);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//                    .contentLength(bytes.length)
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } else {
//            byte[] zipBytes = file.getCompressedFile();
//            if (zipBytes == null || zipBytes.length == 0) {
//                throw new RuntimeException("No compressed data found for file ID: " + fileId);
//            }
//            ByteArrayResource resource = new ByteArrayResource(zipBytes);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//                    .contentLength(zipBytes.length)
//                    .contentType(MediaType.parseMediaType("application/zip"))
//                    .body(resource);
//        }
//    }
//
//
//    @Override
//    public ResponseEntity<ByteArrayResource> downloadDecompressedZip(Long fileId) {
//        Files file = filesRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        if (!"ZIP Deflate".equals(file.getCompressionAlgorithm())) {
//            throw new RuntimeException("File is not a ZIP archive");
//        }
//
//        byte[] zipBytes = file.getCompressedFile();
//        if (zipBytes == null || zipBytes.length == 0) {
//            throw new RuntimeException("No compressed data found for file ID: " + fileId);
//        }
//
//        ByteArrayResource resource = new ByteArrayResource(zipBytes);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
//                .contentLength(zipBytes.length)
//                .contentType(MediaType.parseMediaType("application/zip"))
//                .body(resource);
//    }
//
////    @Override
////    public ResponseEntity<ByteArrayResource> downloadDecompressedZip(Long fileId) {
////        Files file = filesRepository.findById(fileId)
////                .orElseThrow(() -> new RuntimeException("File not found"));
////
////        if (!"ZIP Deflate".equals(file.getCompressionAlgorithm())) {
////            throw new RuntimeException("File is not a ZIP archive");
////        }
////
////        byte[] zipBytes = file.getCompressedFile();
////        ByteArrayOutputStream baos = new ByteArrayOutputStream();
////        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
////            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
////                ZipEntry entry;
////                while ((entry = zis.getNextEntry()) != null) {
////                    ByteArrayOutputStream entryBaos = new ByteArrayOutputStream();
////                    byte[] buffer = new byte[1024];
////                    int len;
////                    while ((len = zis.read(buffer)) > 0) {
////                        entryBaos.write(buffer, 0, len);
////                    }
////                    ZipEntry newEntry = new ZipEntry(entry.getName());
////                    zos.putNextEntry(newEntry);
////                    zos.write(entryBaos.toByteArray());
////                    zos.closeEntry();
////                }
////            }
////        } catch (IOException e) {
////            throw new RuntimeException("Failed to decompress ZIP", e);
////        }
////
////        byte[] unzippedBytes = baos.toByteArray();
////        ByteArrayResource resource = new ByteArrayResource(unzippedBytes);
////
////        return ResponseEntity.ok()
////                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
////                .contentLength(unzippedBytes.length)
////                .contentType(MediaType.APPLICATION_OCTET_STREAM)
////                .body(resource);
////    }
//
//    @Override
//    public Long getArchivedFilesCount() {
//        log.info("Fetching count of archived files");
//        Long count = filesRepository.countByCompressedFileSizeNotNull();
//        log.info("Count of archived files: {}", count);
//        return count;
//    }
//
//    @Override
//    public Long getUserArchivedFilesCount() {
//        log.info("Fetching count of archived files for user: {}", loggedInUser.getLoggedInUser().getId());
//        Long userId = loggedInUser.getLoggedInUser().getId();
//        Long count = filesRepository.countByUserId(userId);
//        log.info("Count of archived files for user {}: {}", userId, count);
//        return count;
//    }
//
//    @Override
//    public List<CompressResponse> getAllArchivedFiles() {
//        log.info("Fetching all archived files");
//        List<Files> archivedFiles = filesRepository.findByCompressedFileSizeNotNull();
//        return archivedFiles.stream()
//                .map(CompressResponse::new)
//                .toList();
//    }
//
//    @Override
//    public List<CompressResponse> getUserArchivedFiles() {
//        log.info("Fetching archived files for user: {}", loggedInUser.getLoggedInUser().getId());
//        Long userId = loggedInUser.getLoggedInUser().getId();
//        List<Files> archivedFiles = filesRepository.findByUserIdAndCompressedFileSizeNotNullAndStatus(userId, Status.COMPLETED);
//        return archivedFiles.stream()
//                .map(CompressResponse::new)
//                .toList();
//    }
//}


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