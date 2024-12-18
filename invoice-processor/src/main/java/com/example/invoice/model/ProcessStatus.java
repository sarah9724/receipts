package com.example.invoice.model;

import lombok.Getter;

@Getter
public enum ProcessStatus {
    PROCESSING("处理中"),
    SUCCESS("处理成功"),
    FAILED("处理失败");
    
    private final String description;
    
    ProcessStatus(String description) {
        this.description = description;
    }
} 