package com.example.invoice.repository;

import com.example.invoice.model.InvoiceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceDataRepository extends JpaRepository<InvoiceData, Long> {
    // 根据发票代码和发票号码查找发票
    Optional<InvoiceData> findByInvoiceCodeAndInvoiceNumber(String invoiceCode, String invoiceNumber);
    
    // 根据文件ID查找发票
    Optional<InvoiceData> findByFileId(Long fileId);
    
    // 检查发票是否已存在
    boolean existsByInvoiceCodeAndInvoiceNumber(String invoiceCode, String invoiceNumber);
} 