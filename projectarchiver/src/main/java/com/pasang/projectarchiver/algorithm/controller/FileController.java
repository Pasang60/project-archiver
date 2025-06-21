package com.pasang.projectarchiver.algorithm.controller;

import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.service.FileService;
import com.pasang.projectarchiver.global.BaseController;
import com.pasang.projectarchiver.global.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/algorithm")
public class FileController extends BaseController {
    private final FileService fileService;

    @PostMapping("/compress")
    public ResponseEntity<GlobalApiResponse> compressAndSaveFile(@ModelAttribute FileRequest fileRequest) {
    return successResponse(
            fileService.compressAndSaveFile(fileRequest),
            "File compressed and saved successfully"
        );
    }

//    @Operation(summary = "Download decompressed/original file")
//    @GetMapping("/download/{fileId}")
//    public ResponseEntity<ByteArrayResource> downloadOriginalFile(@PathVariable Long fileId) {
//        return fileService.downloadDecompressedFile(fileId);
//    }

    @Operation(summary = "Download decompressed/original file")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadOriginalFile(@PathVariable Long fileId) {
        return fileService.downloadDecompressedFile(fileId);
    }

    @Operation(summary = "Get the count of Archived Files")
    @GetMapping("/archived-files/count")
    public ResponseEntity<GlobalApiResponse> getArchivedFilesCount() {
        return successResponse(fileService.getArchivedFilesCount(), "Count of archived files retrieved successfully");
    }



}
