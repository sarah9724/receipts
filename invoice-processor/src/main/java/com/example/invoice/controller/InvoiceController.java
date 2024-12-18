package com.example.invoice.controller;

import com.example.invoice.model.*;
import com.example.invoice.service.FileService;
import com.example.invoice.service.InvoiceProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    
    private final FileService fileService;
    private final InvoiceProcessService invoiceProcessService;
    
    @PostMapping("/upload")
    public ApiResponse<List<FileUploadResult>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<FileUploadResult> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                FileInfo fileInfo = fileService.saveFile(file);
                log.info("File saved with ID: {}", fileInfo.getId());
                
                FileUploadResult result = new FileUploadResult();
                result.setFileId(fileInfo.getId());
                result.setFileName(fileInfo.getFileName());
                result.setStatus(fileInfo.getStatus().name());
                results.add(result);
                
                // 异步处理发票
                invoiceProcessService.processFile(fileInfo.getId());
            } catch (Exception e) {
                log.error("文件上传失败: " + file.getOriginalFilename(), e);
                FileUploadResult result = new FileUploadResult();
                result.setFileName(file.getOriginalFilename());
                result.setStatus(FileStatus.FAILED.name());
                result.setError(e.getMessage());
                results.add(result);
            }
        }
        
        return ApiResponse.success(results);
    }
    
    @GetMapping("/{fileId}/status")
    public ApiResponse<ProcessStatusResult> getProcessStatus(@PathVariable Long fileId) {
        try {
            ProcessStatusResult result = invoiceProcessService.processFile(fileId);
            return ApiResponse.success(result);
        } catch (RuntimeException e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
    
    @GetMapping("/download-package")
    public ResponseEntity<Resource> downloadPackage() throws IOException {
        String packagePath = invoiceProcessService.createDownloadPackage();
        Path path = Paths.get(packagePath);
        Resource resource = new UrlResource(path.toUri());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
            .body(resource);
    }
} 