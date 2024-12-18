package com.example.invoice.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PDFTextExtractor {
    
    @Data
    public static class ExtractResult {
        private String invoiceCode;      // 发票代码
        private String invoiceNumber;    // 发票号码
        private BigDecimal totalAmount;  // 价税合计
        private BigDecimal taxAmount;    // 税额
        private BigDecimal amountWithoutTax; // 不含税金额
        private LocalDate invoiceDate;   // 开票日期
        private String buyerName;        // 购买方名称
        private String buyerTaxNumber;   // 购买方税号
        private String sellerName;       // 销售方名称
        private String sellerTaxNumber;  // 销售方税号
        private String itemInfo;         // 商品信息
        private String fullText;         // 完整文本
    }
    
    private static final Pattern INVOICE_CODE_PATTERN = Pattern.compile("发票代码:?\\s*(\\d{10,12})");
    private static final Pattern INVOICE_NUMBER_PATTERN = Pattern.compile("发票号码:?\\s*(\\d{8})");
    private static final Pattern TOTAL_AMOUNT_PATTERN = Pattern.compile("\\(小写\\)[￥¥]?\\s*(\\d+\\.?\\d*)");
    private static final Pattern TAX_AMOUNT_PATTERN = Pattern.compile("合\\s*计.*?[￥¥]\\s*\\d+\\.?\\d*\\s*[￥¥]\\s*(\\d+\\.?\\d*)");
    private static final Pattern AMOUNT_WITHOUT_TAX_PATTERN = Pattern.compile("合\\s*计.*?[￥¥]\\s*(\\d+\\.?\\d*)");
    private static final Pattern INVOICE_DATE_PATTERN = Pattern.compile("开票日期:?\\s*(\\d{4})\\s*年\\s*(\\d{1,2})\\s*月\\s*(\\d{1,2})\\s*日");

    // 购买方信息 - 取第一个匹配的名称和税号
    private static final Pattern BUYER_NAME_PATTERN = Pattern.compile("名\\s*称:?\\s*([^\\n]+?)(?=\\s*密|\\s*纳税人|\\s*[\\d>\\+\\-\\*/\\\\<]|\\s*地址|\\s*码)");
    private static final Pattern BUYER_TAX_NUMBER_PATTERN = Pattern.compile("纳税人识别号:?\\s*([^\\n\\+]+?)(?=\\s*[\\+\\-\\*/\\\\<]|\\s*地址|\\s*买|\\s*码)");

    // 销售方信息 - 从后向前匹配
    private static final Pattern SELLER_NAME_PATTERN = Pattern.compile("名\\s*称:?\\s*([^\\n]+?)(?=\\s*\\d|\\s*备|\\s*纳税人|\\s*销|\\s*售)");
    private static final Pattern SELLER_TAX_NUMBER_PATTERN = Pattern.compile("纳税人识别号:?\\s*([^\\n]+?)(?=\\s*地址|\\s*售|\\s*方|\\s*备)");

    // 商品信息提取 - 使用更简单的模式
    private static final Pattern ITEM_INFO_PATTERN = Pattern.compile("\\*([^\\n]+?)(?=\\s*合\\s*计)");

    public static ExtractResult extractText(File pdfFile) throws IOException {
        ExtractResult result = new ExtractResult();
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);  // 按位置排序
            String text = stripper.getText(document);
            result.setFullText(text);
            
            log.info("PDF文件路径: {}", pdfFile.getAbsolutePath());
            log.info("提取到的PDF文本内容: \n{}", text);
            
            // 提取各项信息
            String invoiceCode = extractPattern(text, INVOICE_CODE_PATTERN);
            log.info("发票代码: {}", invoiceCode);
            result.setInvoiceCode(invoiceCode);
            
            String invoiceNumber = extractPattern(text, INVOICE_NUMBER_PATTERN);
            log.info("发票号码: {}", invoiceNumber);
            result.setInvoiceNumber(invoiceNumber);
            
            BigDecimal totalAmount = extractAmount(text, TOTAL_AMOUNT_PATTERN);
            log.info("价税合计: {}", totalAmount);
            result.setTotalAmount(totalAmount);
            
            BigDecimal taxAmount = extractAmount(text, TAX_AMOUNT_PATTERN);
            log.info("税额: {}", taxAmount);
            result.setTaxAmount(taxAmount);
            
            BigDecimal amountWithoutTax = extractAmount(text, AMOUNT_WITHOUT_TAX_PATTERN);
            log.info("不含税金额: {}", amountWithoutTax);
            result.setAmountWithoutTax(amountWithoutTax);
            
            LocalDate invoiceDate = extractDate(text, INVOICE_DATE_PATTERN);
            log.info("开票日期: {}", invoiceDate);
            result.setInvoiceDate(invoiceDate);
            
            String buyerName = extractPattern(text, BUYER_NAME_PATTERN);
            log.info("购买方名称: {}", buyerName);
            result.setBuyerName(buyerName);
            
            String buyerTaxNumber = extractPattern(text, BUYER_TAX_NUMBER_PATTERN);
            log.info("购买方税号: {}", buyerTaxNumber);
            result.setBuyerTaxNumber(buyerTaxNumber);
            
            String sellerName = extractPattern(text, SELLER_NAME_PATTERN);
            log.info("销售方名称: {}", sellerName);
            result.setSellerName(sellerName);
            
            String sellerTaxNumber = extractPattern(text, SELLER_TAX_NUMBER_PATTERN);
            log.info("销售方税号: {}", sellerTaxNumber);
            result.setSellerTaxNumber(sellerTaxNumber);
            
            // 提取商品信息
            String itemInfo = extractItemInfo(text);
            log.info("商品信息: {}", itemInfo);
            result.setItemInfo(itemInfo);
        }
        
        return result;
    }
    
    private static String extractPattern(String text, Pattern pattern) {
        try {
            Matcher matcher = pattern.matcher(text);
            String value = null;
            
            if (pattern == SELLER_NAME_PATTERN || pattern == SELLER_TAX_NUMBER_PATTERN) {
                // 对于销售方信息，从后向前查找
                while (matcher.find()) {
                    value = matcher.group(1);  // 保存最后一次匹配结果
                }
            } else if (pattern == BUYER_NAME_PATTERN || pattern == BUYER_TAX_NUMBER_PATTERN) {
                // 对于购买方信息，取第一个匹配的结果
                if (matcher.find()) {
                    value = matcher.group(1);
                }
            } else {
                // 其他信息从前向后查找
                if (matcher.find()) {
                    value = matcher.group(1);
                }
            }
            
            if (value != null) {
                // 根据不同的模式使用不同的清理规则
                if (pattern == INVOICE_CODE_PATTERN || pattern == INVOICE_NUMBER_PATTERN) {
                    // 对于发票代码和发票号码，只保留数字
                    value = value.replaceAll("\\D+", "");
                } else if (pattern == BUYER_NAME_PATTERN || pattern == SELLER_NAME_PATTERN) {
                    // 对于名称，移除特殊字符和数字
                    value = value.replaceAll("[\\s　]*", "")  // 移除所有空白字符
                                .replaceAll("[\\*]+", "")     // 移除星号
                                .replaceAll("[\\d>\\+\\-\\*/\\\\<]+", "") // 移除数字和特殊字符
                                .replaceAll("\\d+$", "")      // 移除末尾的数字
                                .replaceAll("密$", "")        // 移除末尾的"密"字
                                .replaceAll("销售方$", "")    // 移除末尾的"销售方"
                                .replaceAll("备注?$", "")     // 移除末尾的"备注"
                                .replaceAll("购买方$", "")    // 移除末尾的"购买方"
                                .trim();
                } else if (pattern == BUYER_TAX_NUMBER_PATTERN || pattern == SELLER_TAX_NUMBER_PATTERN) {
                    // 对于税号，只保留字母和数字
                    value = value.replaceAll("[^a-zA-Z0-9]", "");
                } else {
                    // 其他情况，只移除空白字符
                    value = value.replaceAll("[\\s　]*", "");
                }
                return value.isEmpty() ? null : value;
            }
        } catch (Exception e) {
            log.warn("提取文本失败: {}", e.getMessage());
        }
        return null;
    }
    
    private static BigDecimal extractAmount(String text, Pattern pattern) {
        try {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String amount = matcher.group(1);
                // 只保留数字和小数点
                amount = amount.replaceAll("[^\\d.]", "");
                return new BigDecimal(amount);
            }
        } catch (Exception e) {
            log.warn("提取金额失败: {}", e.getMessage());
        }
        return null;
    }
    
    private static LocalDate extractDate(String text, Pattern pattern) {
        try {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                int year = Integer.parseInt(matcher.group(1).trim());
                int month = Integer.parseInt(matcher.group(2).trim());
                int day = Integer.parseInt(matcher.group(3).trim());
                return LocalDate.of(year, month, day);
            }
            log.warn("未找到日期文本");
            return null;
        } catch (Exception e) {
            log.error("解析日期时发生错误: {}", e.getMessage());
            return null;
        }
    }
    
    private static String extractItemInfo(String text) {
        try {
            Matcher matcher = ITEM_INFO_PATTERN.matcher(text);
            StringBuilder items = new StringBuilder();
            while (matcher.find()) {
                String item = matcher.group(1).trim()
                    .replaceAll("[\\*]+", "")  // 移除星号
                    .replaceAll("\\s+", " ")   // 合并空白字符
                    .replaceAll("(?<=\\d)\\s+(?=\\d)", ".")  // 数字之间的空格替换为小数点
                    .trim();
                if (!item.isEmpty()) {
                    if (items.length() > 0) {
                        items.append("; ");
                    }
                    items.append(item);
                }
            }
            return items.length() > 0 ? items.toString() : null;
        } catch (Exception e) {
            log.warn("提取商品信息失败: {}", e.getMessage());
            return null;
        }
    }
} 