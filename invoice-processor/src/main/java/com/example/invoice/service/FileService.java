package com.example.invoice.service;

import com.example.invoice.model.FileInfo;
import com.example.invoice.model.FileStatus;
import com.example.invoice.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileInfoRepository fileInfoRepository;
    @Value("${file.storage.upload-dir:./uploads}")
    private String uploadDir;

    public FileInfo saveFile(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成新的文件名
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + getFileExtension(originalFilename);
        Path filePath = uploadPath.resolve(newFilename);

        // 保存文件
        file.transferTo(filePath.toFile());

        // 创建文件信息
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(originalFilename);
        fileInfo.setFilePath(filePath.toString());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setStatus(FileStatus.PENDING);

        return fileInfoRepository.save(fileInfo);
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
}