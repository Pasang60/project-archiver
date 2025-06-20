package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;

public interface FileService {
    FileResponse compressAndSaveFile(FileRequest fileRequest);
}
