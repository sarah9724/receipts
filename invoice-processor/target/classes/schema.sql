DROP TABLE IF EXISTS invoice_data;
DROP TABLE IF EXISTS file_info;

-- 文件信息表
CREATE TABLE file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    status VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 发票数据表
CREATE TABLE invoice_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_code VARCHAR(20),
    invoice_number VARCHAR(20),
    total_amount DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    amount_without_tax DECIMAL(10,2),
    invoice_date DATE,
    buyer_name VARCHAR(200),
    buyer_tax_number VARCHAR(50),
    seller_name VARCHAR(200),
    seller_tax_number VARCHAR(50),
    item_info VARCHAR(1000),
    file_id BIGINT,
    created_at TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES file_info(id)
); 