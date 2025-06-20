package com.pasang.projectarchiver.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String fileName;
    private String encodedData;
    private String huffmanTree; // Serialized Huffman tree for decoding
    private Long originalFileSize;
    private Long compressedFileSize;


    public FileResponse(String fileName, int originalSize, int compressedSize, String encodedData, String huffmanTree) {
        this.fileName = fileName;
        this.originalFileSize = (long) originalSize;
        this.compressedFileSize = (long) compressedSize;
        this.encodedData = encodedData;
        this.huffmanTree = huffmanTree;
    }
}
