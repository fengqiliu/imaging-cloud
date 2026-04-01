# 云胶片管理系统

Cloud Film Management System - 医学影像云管理平台

## 项目简介

云胶片管理系统是一套基于 Web 的医学影像管理平台，为医疗机构提供医学影像存储、查看、诊断报告生成及分享的一体化解决方案。

### 核心功能

- **患者管理** - 患者信息增删改查、历史检查记录
- **检查管理** - 检查单管理、状态流转跟踪
- **影像管理** - DICOM 影像上传、解析、存储、在线阅片
- **诊断报告** - 报告撰写、PDF 导出
- **影像分享** - 分享链接生成、提取码验证
- **系统管理** - 用户、角色、权限、院区、字典、日志管理

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 21 / Spring Boot 3.2.1 / Spring Security / JWT |
| 数据访问 | MyBatis Plus 3.5.5 |
| 数据库 | SQL Server 2019 |
| DICOM 处理 | dcm4che 5.33.0 |
| PDF 生成 | iText 7.2.5 |
| 对象存储 | MinIO (可选) / 本地存储 |
| 前端 | Vue 2 / Element UI |
| API 文档 | SpringDoc OpenAPI 3 |

## 项目结构

```
src/
├── medical-admin/           # 应用入口模块
├── medical-framework/       # 基础框架配置
├── medical-common/          # 通用工具类
├── medical-system/          # 业务模块
│   ├── system/             # 系统管理 (用户/角色/权限)
│   └── cloudfilm/          # 云胶片业务 (患者/检查/影像/报告)
└── database/               # 数据库脚本
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- SQL Server 2019+

### 数据库初始化

```bash
# 使用 sqlcmd 执行初始化脚本
sqlcmd -S localhost -U sa -P Password123! -i src/database/init_db.sql
```

### 构建与运行

```bash
cd src

# 构建项目
mvn clean install

# 运行应用
cd medical-admin
mvn spring-boot:run
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 后端 API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API 文档 | http://localhost:8080/doc.html |
| Druid 监控 | http://localhost:8080/druid |

### 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| radiologist | admin123 | 放射科医生 |
| clinician | admin123 | 临床医生 |

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=medical_cloud_film
    username: sa
    password: ${DB_PASSWORD:Password123!}
```

### 存储配置

```yaml
# 本地存储 (默认)
storage:
  local:
    path: ./uploads
    domain: http://localhost:8080

# MinIO 存储
minio:
  url: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucketName: medical-film
  enabled: false
```

### JWT 配置

```yaml
jwt:
  secret: dsite-medical-cloud-film-secret-key-2024
  expire: 120          # Token 有效期 (分钟)
  refresh-expire: 7d   # 刷新 Token 有效期
```

## API 接口

### 业务模块

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `/auth` | 登录、登出、刷新 Token |
| 患者 | `/cloudfilm/patient` | 患者管理 |
| 检查 | `/cloudfilm/examination` | 检查单管理 |
| 影像 | `/cloudfilm/image` | 影像上传、查看 |
| 报告 | `/cloudfilm/report` | 诊断报告管理 |
| 分享 | `/cloudfilm/share` | 影像分享管理 |

### 系统模块

| 模块 | 路径 | 说明 |
|------|------|------|
| 用户 | `/system/user` | 用户管理 |
| 角色 | `/system/role` | 角色管理 |
| 院区 | `/system/dept` | 院区管理 |
| 菜单 | `/system/menu` | 菜单管理 |
| 字典 | `/system/dict` | 字典管理 |
| 参数 | `/system/config` | 系统参数 |
| 日志 | `/monitor/operlog` | 操作日志 |

## 用户角色

| 角色 | 权限范围 |
|------|----------|
| 管理员 | 系统运维、用户管理、权限配置 |
| 放射科医生 | 阅片、报告撰写、影像上传 |
| 临床医生 | 申请检查、查看影像报告 |
| 患者 | 查看个人报告、分享影像 |

## 开发指南

详见 [CLAUDE.md](./CLAUDE.md)

## 版本规划

### V1.0 (MVP)

- [x] 患者管理
- [x] 检查管理
- [x] 影像管理 (DICOM 支持)
- [x] 诊断报告
- [x] 影像分享
- [x] 系统管理

### 后续版本

- AI 报告解读
- 报告审核流程
- 移动端支持
- HIS/PACS 对接

## 许可证

Private - All Rights Reserved