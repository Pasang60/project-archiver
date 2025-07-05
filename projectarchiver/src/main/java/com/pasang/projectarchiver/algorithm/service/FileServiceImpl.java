package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.CompressResponse;
import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;
import com.pasang.projectarchiver.algorithm.entity.Files;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
            // 1. Read file
            String originalText = new String(fileRequest.getFile().getBytes());

            // 2. Huffman compress
            HuffmanUtil.Result result = HuffmanUtil.compress(originalText);

            // 3. Save compressed data using FileHandlerUtil
            FileSaveResponse fileSaveResponse = fileHandlerUtil.saveCompressedData(
                    result.encodedData,
                    fileRequest.getFileName(),
                    "compressedFiles"
            );
            log.info("Compressed file saved at: {}", fileSaveResponse.getFileDownloadUri());

            // 4. Save metadata in the database
            Files files = new Files();
            files.setFileName(fileRequest.getFileName());
            files.setFile(fileSaveResponse.getFileDownloadUri());
            files.setEncodedData(result.encodedData);
            files.setHuffmanTree(result.huffmanTree);

            // Format file sizes
            files.setOriginalFileSize(formatFileSize(fileRequest.getFile().getBytes().length));
            files.setCompressedFileSize(formatFileSize(result.compressedSize));

            Long userId = loggedInUser.getLoggedInUser().getId();
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            files.setUser(user);

            filesRepository.save(files);

            return new FileResponse(fileRequest.getFileName(), result.originalSize, result.compressedSize,
                                    fileSaveResponse.getFileDownloadUri());

//            return new FileResponse(fileRequest.getFileName(), result.originalSize, result.compressedSize, result.encodedData, result.huffmanTree);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress and save file", e);
        }
    }

//    @Override
//    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
//        Files file = filesRepository.findById(fileId)
//                .orElseThrow(() -> new RuntimeException("File not found"));
//
//        String encodedData = file.getEncodedData();
//        String huffmanTree = file.getHuffmanTree();
//
//        String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);
//        byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
//        ByteArrayResource resource = new ByteArrayResource(bytes);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"original_" + file.getFileName() + ".txt\"")
//                .contentLength(bytes.length)
//                .contentType(MediaType.TEXT_PLAIN)
//                .body(resource);
//    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String encodedData = file.getEncodedData();
        String huffmanTree = file.getHuffmanTree();

        // Decompress the content
        String originalContent = HuffmanUtil.decompress(encodedData, huffmanTree);

        // Convert to bytes
        byte[] bytes = originalContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        // Return with original file name
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentLength(bytes.length)
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
    public Page<CompressResponse> getAllArchivedFiles(Pageable pageable) {
        log.info("Fetching all archived files");

        Page<Files> archivedFiles = filesRepository.findByCompressedFileSizeNotNull(pageable);

        return archivedFiles.map(file -> new CompressResponse(
                file.getId(),
                file.getFileName(),
                file.getOriginalFileSize(),
                file.getCompressedFileSize(),
                calculateCompressionPercentage(file.getOriginalFileSize(), file.getCompressedFileSize())
        ));
    }

    @Override
    public Page<CompressResponse> getUserArchivedFiles(Pageable pageable) {
        log.info("Fetching archived files for user: {}", loggedInUser.getLoggedInUser().getId());
        Long userId = loggedInUser.getLoggedInUser().getId();
        Page<Files> archivedFiles = filesRepository.findByUserIdAndCompressedFileSizeNotNull(userId, pageable);
        return archivedFiles.map(file -> new CompressResponse(
                file.getId(),
                file.getFileName(),
                file.getOriginalFileSize(),
                file.getCompressedFileSize(),
                calculateCompressionPercentage(file.getOriginalFileSize(), file.getCompressedFileSize())
        ));
    }

    private Long calculateCompressionPercentage(String originalSize, String compressedSize) {
        long original = Long.parseLong(originalSize.split(" ")[0]);
        long compressed = Long.parseLong(compressedSize.split(" ")[0]);
        return ((original - compressed) * 100) / original;
    }


}