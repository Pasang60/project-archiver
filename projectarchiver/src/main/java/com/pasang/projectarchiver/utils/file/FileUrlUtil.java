package com.pasang.projectarchiver.utils.file;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FileUrlUtil {

    private static String staticBaseUrl;

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.base-url:}")
    private String baseUrl;

    @PostConstruct
    private void setBaseUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            staticBaseUrl = "http://localhost:" + serverPort + "/";
            log.info("Base URL set to localhost: {}", staticBaseUrl);
        } else {
            staticBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            log.info("Base URL set from property: {}", staticBaseUrl);
        }
    }

    public static URI getFileUri(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Relative path cannot be null or empty");
        }

        // Normalize and encode the path
        String cleanPath = relativePath.replace("\\", "/").replaceFirst("^/+", "");
        String encodedPath = URLEncoder.encode(cleanPath, StandardCharsets.UTF_8)
                .replace("%2F", "/");

        String urlPath = staticBaseUrl + "files/" + encodedPath;
        log.debug("Generated URL: {}", urlPath);
        return URI.create(urlPath);
    }
}
