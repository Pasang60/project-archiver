package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface FileService {
    FileResponse compressAndSaveFile(FileRequest fileRequest);

    ResponseEntity<ByteArrayResource> downloadDecompressedFile(Long fileId);

    Long getArchivedFilesCount();
}
