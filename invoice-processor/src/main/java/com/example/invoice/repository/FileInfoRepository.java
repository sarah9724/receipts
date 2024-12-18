package com.example.invoice.repository;

import com.example.invoice.model.FileInfo;
import com.example.invoice.model.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    List<FileInfo> findByStatus(FileStatus status);
    List<FileInfo> findByFileName(String fileName);
} 