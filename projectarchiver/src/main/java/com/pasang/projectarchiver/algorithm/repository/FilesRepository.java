package com.pasang.projectarchiver.algorithm.repository;

import com.pasang.projectarchiver.algorithm.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<Files,Long> {
    Optional<Files> findFilesByIdAndUserId(Long id, Long userId);

    Long countByCompressedFileSizeNotNull();
}
