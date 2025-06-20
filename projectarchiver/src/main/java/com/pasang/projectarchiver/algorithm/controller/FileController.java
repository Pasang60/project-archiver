package com.pasang.projectarchiver.algorithm.controller;

import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.service.FileService;
import com.pasang.projectarchiver.global.BaseController;
import com.pasang.projectarchiver.global.GlobalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController extends BaseController {
    private final FileService fileService;

    @PostMapping("/compress")
    public ResponseEntity<GlobalApiResponse> compressAndSaveFile(@ModelAttribute FileRequest fileRequest) {
    return successResponse(
            fileService.compressAndSaveFile(fileRequest),
            "File compressed and saved successfully"
        );
    }
}
