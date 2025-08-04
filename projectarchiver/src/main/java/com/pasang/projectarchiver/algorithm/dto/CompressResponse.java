package com.pasang.projectarchiver.algorithm.dto;

import com.pasang.projectarchiver.algorithm.entity.Files;
import com.pasang.projectarchiver.algorithm.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompressResponse {
    private Long archiveId;
    private String fileName;
    private String description;
    private String originalFileExtension;
    private String mimeType;
    private String compressedBy;
    private LocalDateTime compressedAt;
    private String compressedAlgorithm;
    private String originalFileSize;
    private String compressedFileSize;
    private Long compression;
    private Status status;

    public CompressResponse(Files files) {
        this.archiveId = files.getId();
        this.fileName = files.getFileName();
        this.description = files.getDescription();
        this.originalFileExtension = files.getOriginalFileExtension();
        this.mimeType = files.getMimeType();
        this.compressedBy = files.getUser().getFirstName() + " " + files.getUser().getLastName();
        this.compressedAt = files.getLastModifiedDate();
        this.compressedAlgorithm = files.getCompressionAlgorithm();
        this.originalFileSize = files.getOriginalFileSize();
        this.compressedFileSize = files.getCompressedFileSize();
        this.compression = calculateCompressionPercentage(
                files.getOriginalFileSize(),
                files.getCompressedFileSize()
        );
        this.status = files.getStatus();
    }

    private Long calculateCompressionPercentage(String originalSize, String compressedSize) {
        long original = Long.parseLong(originalSize.split(" ")[0]); // Extract numeric part
        long compressed = Long.parseLong(compressedSize.split(" ")[0]); // Extract numeric part
        return ((original - compressed) * 100) / original;
    }
}