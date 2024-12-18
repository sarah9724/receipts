package com.example.invoice.model;

import lombok.Data;

@Data
public class FileUploadResult {
    private Long fileId;
    private String fileName;
    private String status;
    private String error;
} 