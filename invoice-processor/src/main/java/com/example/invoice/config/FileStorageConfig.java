package com.example.invoice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageConfig {
    private String uploadDir = "./uploads";
    private String processedDir = "./processed";
    private String tempDir = "./temp";
    private String duplicateDir = "./duplicate";
    private long maxFileSize = 10 * 1024 * 1024; // 10MB
} 