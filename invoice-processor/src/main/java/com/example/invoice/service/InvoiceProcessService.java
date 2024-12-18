package com.example.invoice.service;

import com.example.invoice.model.*;
import com.example.invoice.repository.*;
import com.example.invoice.util.PDFTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceProcessService {
    private final FileInfoRepository fileInfoRepository;
    private final InvoiceDataRepository invoiceDataRepository;
    private final ExcelService excelService;
    
    @Value("${file.storage.processed-dir:./processed}")
    private String processedDir;
    
    @Value("${file.storage.temp-dir:./temp}")
    private String tempDir;
    
    @Transactional
    public ProcessStatusResult processFile(Long fileId) {
        // 获取文件信息
        FileInfo fileInfo = fileInfoRepository.findById(fileId)
            .orElseThrow(() -> new RuntimeException("File not found: " + fileId));
            
        try {
            fileInfo.setStatus(FileStatus.PROCESSING);
            fileInfoRepository.save(fileInfo);
            
            // 提取发票数据
            File pdfFile = new File(fileInfo.getFilePath());
            PDFTextExtractor.ExtractResult extractResult = PDFTextExtractor.extractText(pdfFile);
            
            // 检查发票是否已存在
            if (invoiceDataRepository.existsByInvoiceCodeAndInvoiceNumber(
                    extractResult.getInvoiceCode(), 
                    extractResult.getInvoiceNumber())) {
                fileInfo.setStatus(FileStatus.DUPLICATE);
                fileInfoRepository.save(fileInfo);
                return new ProcessStatusResult(false, "发票已存在");
            }
            
            // 创建发票数据对象
            InvoiceData invoiceData = new InvoiceData();
            invoiceData.setFileId(fileId);
            invoiceData.setInvoiceCode(extractResult.getInvoiceCode());
            invoiceData.setInvoiceNumber(extractResult.getInvoiceNumber());
            invoiceData.setTotalAmount(extractResult.getTotalAmount());
            invoiceData.setTaxAmount(extractResult.getTaxAmount());
            invoiceData.setAmountWithoutTax(extractResult.getAmountWithoutTax());
            invoiceData.setInvoiceDate(extractResult.getInvoiceDate());
            invoiceData.setBuyerName(extractResult.getBuyerName());
            invoiceData.setBuyerTaxNumber(extractResult.getBuyerTaxNumber());
            invoiceData.setSellerName(extractResult.getSellerName());
            invoiceData.setSellerTaxNumber(extractResult.getSellerTaxNumber());
            invoiceData.setItemInfo(extractResult.getItemInfo());
            
            // 保存发票数据
            invoiceDataRepository.save(invoiceData);
            
            // 更新文件状态
            fileInfo.setStatus(FileStatus.PROCESSED);
            fileInfoRepository.save(fileInfo);
            
            return new ProcessStatusResult(true, "处理成功");
        } catch (Exception e) {
            log.error("处理文件失败: {}", e.getMessage(), e);
            fileInfo.setStatus(FileStatus.FAILED);
            fileInfoRepository.save(fileInfo);
            return new ProcessStatusResult(false, "处理失败: " + e.getMessage());
        }
    }

    public String createDownloadPackage() throws IOException {
        // 获取所有已处理的发票数据
        List<InvoiceData> invoices = invoiceDataRepository.findAll();
        
        // 生成Excel报表
        String excelFile = excelService.generateExcelReport(invoices, tempDir);
        
        // 创建ZIP文件
        String zipFileName = "发票处理结果_" + System.currentTimeMillis() + ".zip";
        Path zipPath = Paths.get(tempDir, zipFileName);
        
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            // 添加Excel文件
            addFileToZip(zipOut, excelFile, Paths.get(excelFile).getFileName().toString());
            
            // 添加处理后的PDF文件
            for (InvoiceData invoice : invoices) {
                if (invoice.getProcessedFilePath() != null) {
                    addFileToZip(zipOut, invoice.getProcessedFilePath(), 
                               Paths.get(invoice.getProcessedFilePath()).getFileName().toString());
                }
            }
        }
        
        return zipPath.toString();
    }
    
    private void addFileToZip(ZipOutputStream zipOut, String filePath, String fileName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        zipOut.write(bytes, 0, bytes.length);
        zipOut.closeEntry();
    }
} 