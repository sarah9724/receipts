package com.example.invoice.repository;

import com.example.invoice.model.FileInfo;
import com.example.invoice.model.ProcessTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessTaskRepository extends JpaRepository<ProcessTask, Long> {
    Optional<ProcessTask> findByFileInfo(FileInfo fileInfo);
    Optional<ProcessTask> findByFileInfo_Id(String fileId);
} 