package com.pasang.projectarchiver.algorithm.service;

import com.pasang.projectarchiver.algorithm.dto.FileRequest;
import com.pasang.projectarchiver.algorithm.dto.FileResponse;
import com.pasang.projectarchiver.algorithm.entity.Files;
import com.pasang.projectarchiver.algorithm.repository.FilesRepository;
import com.pasang.projectarchiver.algorithm.util.HuffmanUtil;
import com.pasang.projectarchiver.users.entity.Users;
import com.pasang.projectarchiver.users.repository.UsersRepository;
import com.pasang.projectarchiver.utils.logged_in_user.LoggedInUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FilesRepository filesRepository;
    private final LoggedInUser loggedInUser;
    private final UsersRepository usersRepository;

    @Override
    public FileResponse compressAndSaveFile(FileRequest fileRequest) {
        try {
            // 1. Read file
            String originalText = new String(fileRequest.getFile().getBytes());

            // 2. Huffman compress
            HuffmanUtil.Result result = HuffmanUtil.compress(originalText);

            // 3. Save entity
            Files files = new Files();
            files.setFileName(fileRequest.getFileName());
            files.setEncodedData(result.encodedData); // Save the binary string directly
            files.setHuffmanTree(result.huffmanTree);
            files.setOriginalFileSize((long) result.originalSize);
            files.setCompressedFileSize((long) result.compressedSize);

            // TODO: Replace this with actual logged-in user
            Long userId = loggedInUser.getLoggedInUser().getId();
            Users user = new Users();
            if (userId != null) {
                user = usersRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
            }

            files.setUser(user);

            filesRepository.save(files);

            return new FileResponse(fileRequest.getFileName(), result.originalSize, result.compressedSize, result.encodedData, result.huffmanTree);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress and save file", e);
        }
    }

}
