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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get the count of Archived Files")
    @GetMapping("/archived-files/count")
    public ResponseEntity<GlobalApiResponse> getArchivedFilesCount() {
        return successResponse(fileService.getArchivedFilesCount(), "Count of archived files retrieved successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get the count of Archived Files for users")
    @GetMapping("/user/archived-count")
    public ResponseEntity<GlobalApiResponse> getUserArchivedFilesCount() {
        return successResponse(fileService.getUserArchivedFilesCount(), "Count of archived files for user retrieved successfully");
    }

    @PostMapping("/deflate/compress")
    @Operation(summary = "Compress file using Deflate algorithm")
    public ResponseEntity<GlobalApiResponse> compressFileUsingDeflate(@ModelAttribute FileRequest fileRequest) {
        return successResponse(
                fileService.compressAndSaveFile(fileRequest),
                "File compressed using Deflate algorithm and saved successfully"
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Get all archived files for the user",
            description = "Fetch all archived files for the admin")
    @GetMapping("/getAll")
    public ResponseEntity<GlobalApiResponse> getAllArchivedFiles(Pageable pageable) {
        return successResponse(fileService.getAllArchivedFiles(pageable), "All archived files fetched successfully");
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Get all archived files for the user",
            description = "Fetch all archived files for the currently logged-in user")
    @GetMapping("/user/getAll")
    public ResponseEntity<GlobalApiResponse> getUserArchivedFiles(Pageable pageable) {
        return successResponse(fileService.getUserArchivedFiles(pageable), "All archived files for user fetched successfully");
    }




}
