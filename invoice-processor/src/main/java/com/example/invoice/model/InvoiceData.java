package com.example.invoice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoice_data")
public class InvoiceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_code", length = 20)
    private String invoiceCode;      // 发票代码

    @Column(name = "invoice_number", length = 20)
    private String invoiceNumber;    // 发票号码

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;  // 价税合计

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;    // 税额

    @Column(name = "amount_without_tax", precision = 10, scale = 2)
    private BigDecimal amountWithoutTax; // 不含税金额

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;   // 开票日期

    @Column(name = "buyer_name", length = 200)
    private String buyerName;        // 购买方名称

    @Column(name = "buyer_tax_number", length = 50)
    private String buyerTaxNumber;   // 购买方税号

    @Column(name = "seller_name", length = 200)
    private String sellerName;       // 销售方名称

    @Column(name = "seller_tax_number", length = 50)
    private String sellerTaxNumber;  // 销售方税号

    @Column(name = "item_info", length = 1000)
    private String itemInfo;         // 商品信息

    @Column(name = "file_id")
    private Long fileId;             // 关联的文件ID

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "processed_file_path")
    private String processedFilePath;    // 处理后的文件路径
    
    @Column(name = "original_file_name")
    private String originalFileName;     // 原始文件名
    
    @Column(name = "new_file_name")
    private String newFileName;          // 新文件名（重命名后）

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 