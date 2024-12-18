package com.example.invoice.service;

import com.example.invoice.model.InvoiceData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ExcelService {
    
    public String generateExcelReport(List<InvoiceData> invoices, String outputDir) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("发票信息");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"发票代码", "发票号码", "开票日期", "购买方名称", "购买方税号", 
                              "销售方名称", "销售方税号", "金额", "税额", "价税合计"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            int rowNum = 1;
            for (InvoiceData invoice : invoices) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(invoice.getInvoiceCode());
                row.createCell(1).setCellValue(invoice.getInvoiceNumber());
                row.createCell(2).setCellValue(invoice.getInvoiceDate().toString());
                row.createCell(3).setCellValue(invoice.getBuyerName());
                row.createCell(4).setCellValue(invoice.getBuyerTaxNumber());
                row.createCell(5).setCellValue(invoice.getSellerName());
                row.createCell(6).setCellValue(invoice.getSellerTaxNumber());
                row.createCell(7).setCellValue(invoice.getAmountWithoutTax().doubleValue());
                row.createCell(8).setCellValue(invoice.getTaxAmount().doubleValue());
                row.createCell(9).setCellValue(invoice.getTotalAmount().doubleValue());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 保存文件
            String fileName = "发票汇总_" + System.currentTimeMillis() + ".xlsx";
            Path filePath = Paths.get(outputDir, fileName);
            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                workbook.write(outputStream);
            }
            
            return filePath.toString();
        }
    }
} 