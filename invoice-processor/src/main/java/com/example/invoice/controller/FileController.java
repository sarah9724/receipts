package com.example.invoice.controller;

import com.example.invoice.model.FileInfo;
import com.example.invoice.model.ProcessStatusResult;
import com.example.invoice.service.FileService;
import com.example.invoice.service.InvoiceProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final InvoiceProcessService invoiceProcessService;

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileInfo fileInfo = fileService.saveFile(file);
            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{fileId}/process")
    public ResponseEntity<ProcessStatusResult> processFile(@PathVariable Long fileId) {
        try {
            ProcessStatusResult result = invoiceProcessService.processFile(fileId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件处理失败", e);
            return ResponseEntity.badRequest().body(new ProcessStatusResult(false, e.getMessage()));
        }
    }
}