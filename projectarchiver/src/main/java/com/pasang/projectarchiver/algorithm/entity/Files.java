package com.pasang.projectarchiver.algorithm.entity;

import com.pasang.projectarchiver.global.Auditable;
import com.pasang.projectarchiver.users.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable = false)
    private String fileName;

    @Column(length = 500)
    private String description;


    private String originalFileExtension;
    private String mimeType;


    @Column(nullable = false)
    private String file;

    @Lob
    @Basic(fetch = FetchType.EAGER)
//    @Column(nullable = false)
    private String encodedData;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String huffmanTree;

//    @Column(nullable = false)
    private String originalFileSize;

//    @Column(nullable = false)
    private String compressedFileSize;

//    @Column(nullable = false)
    private String compressionAlgorithm;

    @Lob
    private byte[] compressedFile; // Compressed file data

    @Enumerated(EnumType.STRING)
    private Status status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // Relationship with Users

}
