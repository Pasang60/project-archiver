package com.pasang.projectarchiver.algorithm.entity;

import com.pasang.projectarchiver.algorithm.util.HuffmanUtil;
import com.pasang.projectarchiver.global.Auditable;
import com.pasang.projectarchiver.users.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String file;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private String encodedData;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String huffmanTree;


    @Column(nullable = false)
    private Long originalFileSize;

    @Column(nullable = false)
    private Long compressedFileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // Relationship with Users

}
