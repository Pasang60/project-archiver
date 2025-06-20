package com.pasang.projectarchiver.algorithm.repository;

import com.pasang.projectarchiver.algorithm.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends JpaRepository<Files,Long> {
}
