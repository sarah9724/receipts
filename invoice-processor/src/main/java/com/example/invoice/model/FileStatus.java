package com.example.invoice.model;

import lombok.Getter;

@Getter
public enum FileStatus {
    PENDING,    // 待处理
    PROCESSING, // 处理中
    PROCESSED,  // 处理完成
    FAILED,     // 处理失败
    DUPLICATE   // 重复文件
} 