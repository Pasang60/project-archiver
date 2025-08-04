package com.pasang.projectarchiver.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolderRequest {

        private String fileName;
        private String description;
        private List<MultipartFile> file;
    }
