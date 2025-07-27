package com.pasang.projectarchiver.algorithm.dto;

import com.pasang.projectarchiver.algorithm.entity.Files;
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
    private String compressedBy;
    private LocalDateTime compressedAt;
    private String originalFileSize;
    private String compressedFileSize;
    private Long compression;

    public CompressResponse(Files files) {
        this.archiveId = files.getId();
        this.fileName = files.getFileName();
        this.compressedBy = files.getUser().getFirstName() + " " + files.getUser().getLastName();
        this.compressedAt = files.getLastModifiedDate();
        this.originalFileSize = files.getOriginalFileSize();
        this.compressedFileSize = files.getCompressedFileSize();
        this.compression = calculateCompressionPercentage(
                files.getOriginalFileSize(),
                files.getCompressedFileSize()
        );
    }

    private Long calculateCompressionPercentage(String originalSize, String compressedSize) {
        long original = Long.parseLong(originalSize.split(" ")[0]); // Extract numeric part
        long compressed = Long.parseLong(compressedSize.split(" ")[0]); // Extract numeric part
        return ((original - compressed) * 100) / original;
    }
}