package com.pasang.projectarchiver.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String fileName;
//    private String encodedData;
//    private String huffmanTree;
    private URI file;
    private Long originalFileSize;
    private Long compressedFileSize;

    public FileResponse(String fileName, int originalSize, int compressedSize, String fileDownloadUri) {
        this.fileName = fileName;
        this.originalFileSize = (long) originalSize;
        this.compressedFileSize = (long) compressedSize;
        this.file = URI.create(fileDownloadUri);
    }


//    public FileResponse(String fileName, int originalSize, int compressedSize, String encodedData, String huffmanTree) {
//        this.fileName = fileName;
//        this.originalFileSize = (long) originalSize;
//        this.compressedFileSize = (long) compressedSize;
//        this.encodedData = encodedData;
//        this.huffmanTree = huffmanTree;
//    }
}
