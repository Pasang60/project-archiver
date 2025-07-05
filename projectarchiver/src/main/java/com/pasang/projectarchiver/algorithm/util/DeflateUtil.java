package com.pasang.projectarchiver.algorithm.util;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class DeflateUtil {

    // Compress a folder recursively into a zip file
    public static void compressFolderToZip(Path sourceDir, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry entry = new ZipEntry(sourceDir.relativize(path).toString().replace("\\", "/"));
                        try {
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException("Error compressing: " + path, e);
                        }
                    });
        }
    }

    // Compress a single file into a zip file
    public static void compressFileToZip(Path filePath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            ZipEntry entry = new ZipEntry(filePath.getFileName().toString());
            zos.putNextEntry(entry);
            Files.copy(filePath, zos);
            zos.closeEntry();
        }
    }

    // Decompress a zip file into target directory
    public static void decompressZipToFolder(Path zipPath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newFile = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    Files.createDirectories(newFile.getParent());
                    try (OutputStream os = Files.newOutputStream(newFile)) {
                        zis.transferTo(os);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

