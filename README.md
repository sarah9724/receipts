# 发票管理工具

## 项目简介
这是一个基于Spring Boot和Vue3的发票管理工具，支持发票文件的上传、处理和下载功能。用户可以通过Web界面上传发票文件，系统会在后台进行处理，并提供处理后的文件下载。

## 技术栈
### 后端
- Spring Boot 3.2.3
- Spring Data JPA
- MySQL
- Maven

### 前端
- Vue 3
- TypeScript
- Element Plus
- Vite

## 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## 项目结构

E:\receipts
├─invoice-processor (后端项目)
│ ├─src
│ │ └─main
│ │ ├─java
│ │ │ └─com
│ │ │ └─example
│ │ │ └─invoice
│ │ │ ├─controller // 控制器层
│ │ │ ├─service // 服务层
│ │ │ ├─model // 数据模型
│ │ │ ├─repository // 数据访问层
│ │ │ └─config // 配置类
│ │ └─resources // 配置文件
│ └─pom.xml
└─invoice-ui (前端项目)
├─src
│ ├─components // 组件
│ ├─views // 页面
│ ├─router // 路由
│ ├─store // 状态管理
│ └─assets // 静态资源
└─package.json


## 快速开始

### 后端启动
1. 配置数据库
sql
CREATE DATABASE invoice_db;


2. 修改配置文件
编辑 `invoice-processor/src/main/resources/application.yml`，配置数据库连接信息。

3. 启动后端服务

bash
cd invoice-processor
mvn spring-boot:run


### 前端启动
1. 安装依赖

bash
cd invoice-ui
npm install


2. 启动开发服务器

bash
npm run dev


## API文档

### 文件上传
- 接口：`POST /api/files/upload`
- 说明：上传发票文件
- 参数：
  - file: MultipartFile（文件对象）
- 返回：文件ID

### 文件下载
- 接口：`GET /api/files/download/{fileId}`
- 说明：下载处理后的文件
- 参数：
  - fileId: String（文件ID）
- 返回：文件流

## 开发指南

### 添加新功能
1. 在对应的package下创建相关类
2. 遵循现有的代码结构和命名规范
3. 添加必要的注释和文档

### 代码规范
- 遵循Java代码规范
- 使用统一的代码格式化工具
- 添加适当的注释
- 使用有意义的变量和方法名

## 注意事项
1. 确保MySQL服务已启动
2. 文件上传大小限制为10MB
3. 支持的文件类型：PDF, JPG, PNG
4. 临时文件会定期清理

## 常见问题
1. 数据库连接失败
   - 检查MySQL服务是否启动
   - 验证数据库连接信息是否正确

2. 文件上传失败
   - 检查文件大小是否超限
   - 确认文件类型是否支持

## 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 发起 Pull Request

## 版本历史
- v0.1.0 - 基础功能实现
  - 文件上传下载
  - 基础处理功能

## 联系方式
如有问题，请联系项目维护者。

## 许可证
MIT License