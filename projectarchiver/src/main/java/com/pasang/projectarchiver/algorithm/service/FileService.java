package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.CompressResponse;
import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FileService {
    FileResponse compressAndSaveFile(FileRequest fileRequest);

    ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId);

    Long getArchivedFilesCount();

    Long getUserArchivedFilesCount();

    List<CompressResponse> getAllArchivedFiles();

    List<CompressResponse> getUserArchivedFiles();
}
