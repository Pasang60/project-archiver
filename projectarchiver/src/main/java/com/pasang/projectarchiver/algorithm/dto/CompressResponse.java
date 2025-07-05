package com.pasang.projectarchiver.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompressResponse {
    private Long archiveId;
    private String fileName;
    private String originalFileSize;
    private String compressedFileSize;
    private Long compression;

}
