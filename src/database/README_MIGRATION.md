# 数据库迁移管理指南

> 基于DDD限界上下文的数据库迁移完整手册

---

## 📋 目录

1. [概述](#概述)
2. [迁移架构](#迁移架构)
3. [执行步骤](#执行步骤)
4. [迁移文件清单](#迁移文件清单)
5. [版本控制](#版本控制)
6. [故障排查](#故障排查)
7. [回滚策略](#回滚策略)
8. [性能优化](#性能优化)

---

## 概述

### 什么是限界上下文迁移？

本迁移脚本包遵循DDD（领域驱动设计）的限界上下文（Bounded Context）划分方式：

```
DDD限界上下文  →  数据库表组  →  独立迁移脚本

患者信息BC     →  cf_patient*    →  V1.0.1
影像管理BC     →  cf_image*      →  V1.1.0
诊断报告BC     →  cf_diagnosis*  →  V1.2.0
其他BC         →  cf_*           →  V1.3.0
验证脚本       →  -              →  V1.4.0
```

### 核心特点

| 特点 | 说明 |
|-----|------|
| **独立性** | 每个BC对应一个迁移文件，独立执行 |
| **追踪性** | 每个BC都有专属的Undo（回滚）脚本 |
| **可验证** | 包含完整的迁移前后验证脚本 |
| **向前兼容** | 支持增量迁移，不破坏现有数据 |
| **可审计** | 所有操作有时间戳和操作人追踪 |

---

## 迁移架构

### Flyway迁移工具集成

```
项目启动
    ↓
Flyway检查版本
    ↓
执行待处理迁移 (V*.sql)
    ↓
记录执行历史到 flyway_schema_history
    ↓
应用启动成功
```

### 版本命名规则

```
V<大版本>.<BC版本>.<顺序>__<BC名称>.sql

示例：
  V1.0.1__patient_information_context.sql
  V1.1.0__image_management_context.sql
  V1.2.0__diagnostic_report_context.sql
  V1.3.0__supporting_contexts.sql
  V1.4.0__migration_verification.sql

约定：
  • V1.x.x = 初始版本（2026-03年）
  • 第二位 = 限界上下文编号 (0=患者, 1=影像, 2=报告, 3=其他, 4=验证)
  • 第三位 = 该BC内的顺序 (0=DDL, 1=初始数据, 2=索引优化等)
```

### 迁移依赖关系

```
患者信息BC (V1.0.1)
    ↓ (被引用)
影像管理BC (V1.1.0) ← 需要 cf_patient 存在
    ↓ (被引用)
诊断报告BC (V1.2.0) ← 需要 cf_examination 和 cf_patient 存在
    ↓ (被引用)
支撑BC (V1.3.0) ← 需要多个BC存在
    ↓
验证脚本 (V1.4.0) ← 验证所有表和约束
```

---

## 执行步骤

### 前置条件

✓ SQL Server 2019 或更高版本
✓ 数据库已创建（例如：`medical_pacs`）
✓ 当前用户具有 `db_owner` 权限
✓ 网络连接正常

### Step 1: 配置Flyway

#### 方式A：Spring Boot自动化（推荐）

**pom.xml 配置**：
```xml
<!-- Flyway Maven插件 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>

<!-- SQL Server JDBC -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.4.0.jre11</version>
</dependency>
```

**application.yml 配置**：
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=medical_pacs;encrypt=true;trustServerCertificate=true
    username: sa
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver

  flyway:
    enabled: true
    locations: classpath:db/migration                      # 迁移脚本位置
    baseline-on-migrate: false                             # 首次迁移不创建基线
    validate-on-migrate: true                              # 校验迁移脚本
    repair-on-migrate: false                               # 不自动修复
    out-of-order: false                                    # 禁止乱序执行
    placeholder-replacement: true
    placeholders:
      default_user_id: 1
      app_version: 1.0.0
```

**执行命令**：
```bash
# 1. 验证迁移脚本
mvn flyway:validate

# 2. 查看迁移计划
mvn flyway:info

# 3. 执行迁移
mvn flyway:migrate

# 4. 修复失败的迁移（谨慎使用）
mvn flyway:repair
```

#### 方式B：命令行工具

```bash
# 下载Flyway CLI
wget https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/9.22.3/flyway-commandline-9.22.3-windows-x64.zip

# 解压并配置 conf/flyway.conf
flyway_url=jdbc:sqlserver://localhost:1433;databaseName=medical_pacs
flyway_user=sa
flyway_password=Password123!
flyway_locations=filesystem:./sql/migrations

# 执行迁移
flyway migrate
```

#### 方式C：手动执行（开发环境）

```powershell
# SQL Server Management Studio 或 sqlcmd
sqlcmd -S localhost -U sa -P "Password123!" -d medical_pacs -i "V1.0.1__patient_information_context.sql"

# 或使用Azure Data Studio
# File → Open → 选择迁移脚本 → Execute
```

### Step 2: 迁移前检查

```sql
-- 在SSMS中执行
USE medical_pacs;
GO

-- 列出所有已存在的cf_*表
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME LIKE 'cf_%'
ORDER BY TABLE_NAME;
GO

-- 检查Flyway历史表
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;
GO
```

### Step 3: 执行迁移

```bash
# 方式1：Maven
mvn clean flyway:migrate

# 方式2：Gradle
gradle flywayMigrate

# 方式3：Spring Boot启动时自动执行
mvn spring-boot:run

# 方式4：Docker (如果使用容器)
docker run --rm -v $(pwd)/migrations:/flyway/sql -v $(pwd)/conf:/flyway/conf flyway/flyway migrate
```

### Step 4: 迁移后验证

```sql
-- 执行验证脚本
USE medical_pacs;
GO

-- 检查所有表
SELECT COUNT(*) AS table_count FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME LIKE 'cf_%';
GO
-- 预期结果：21

-- 检查约束
SELECT COUNT(*) AS constraint_count FROM sys.foreign_keys
WHERE OBJECT_NAME(parent_object_id) LIKE 'cf_%';
GO
-- 预期结果：>10

-- 检查索引
SELECT COUNT(*) AS index_count FROM sys.indexes
WHERE OBJECT_NAME(object_id) LIKE 'cf_%' AND index_id > 0;
GO
-- 预期结果：>30

-- 查看迁移历史
SELECT version, description, success, installed_on FROM flyway_schema_history ORDER BY installed_rank DESC;
GO
```

---

## 迁移文件清单

### 文件组织结构

```
src/database/migrations/
├── V1.0.1__patient_information_context.sql      (患者信息BC)
│   ├── 表: cf_patient, cf_patient_allergy, cf_patient_medication, cf_patient_merge_log
│   ├── 大小: ~12KB
│   └── 执行时间: 3-5秒
│
├── V1.1.0__image_management_context.sql         (影像管理BC)
│   ├── 表: cf_examination, cf_series, cf_image, cf_qc_issue
│   ├── 大小: ~18KB
│   └── 执行时间: 5-10秒
│
├── V1.2.0__diagnostic_report_context.sql        (诊断报告BC)
│   ├── 表: cf_report_template, cf_diagnosis_report, cf_report_approval, cf_report_version, cf_report_draft_backup
│   ├── 大小: ~15KB
│   └── 执行时间: 5-8秒
│
├── V1.3.0__supporting_contexts.sql              (支撑BC)
│   ├── 表: cf_ai_interpret, cf_storage_migration, cf_storage_statistics, cf_workflow_state, cf_workflow_event_log, cf_audit_log, cf_security_rule, cf_system_metrics
│   ├── 大小: ~25KB
│   └── 执行时间: 10-15秒
│
├── V1.4.0__migration_verification.sql           (验证脚本)
│   ├── 验证所有表、约束、索引的完整性
│   ├── 大小: ~12KB
│   └── 执行时间: 2-3秒
│
└── Undo/
    ├── U1.0.1__patient_information_context.sql
    ├── U1.1.0__image_management_context.sql
    ├── U1.2.0__diagnostic_report_context.sql
    └── U1.3.0__supporting_contexts.sql
```

### 各BC详细信息

| BC名称 | 文件 | 表数 | 字段数 | 索引数 | 大小 |
|-------|------|------|--------|--------|------|
| 患者信息 | V1.0.1 | 4 | ~35 | 7 | 12KB |
| 影像管理 | V1.1.0 | 4 | ~48 | 12 | 18KB |
| 诊断报告 | V1.2.0 | 5 | ~38 | 8 | 15KB |
| 支撑BC | V1.3.0 | 8 | ~52 | 15 | 25KB |
| 验证脚本 | V1.4.0 | - | - | - | 12KB |
| **总计** | - | **21** | **~173** | **>42** | **~82KB** |

---

## 版本控制

### Flyway版本号说明

| 版本号 | 含义 | 例子 |
|-------|------|------|
| V1 | 主版本（对应产品发布版本） | V1.0, V1.1等 |
| .0 | BC版本（0=患者, 1=影像, 2=报告, 3=其他, 4=验证） | V1.0.1, V1.1.0 |
| .1 | 迁移序列（同BC内的顺序） | V1.0.1, V1.0.2等 |

### 升级迁移

```
生产环境升级路径：

V1.0.1 (患者BC - DDL)
  ↓ (自动应用)
V1.1.0 (影像BC - DDL)
  ↓
V1.2.0 (报告BC - DDL)
  ↓
V1.3.0 (支撑BC - DDL)
  ↓
V1.4.0 (验证脚本)
  ↓
应用启动成功 ✓

如需添加新迁移：
  V1.5.0__optimization_indexes.sql     (优化阶段)
  V1.6.0__add_new_feature.sql          (新功能)
  V2.0.0__major_refactoring.sql        (大版本升级)
```

---

## 故障排查

### 常见问题

#### 1. "找不到flyway_schema_history表"

```sql
-- 原因：首次执行Flyway，需要初始化

-- 解决方案：设置baseline（仅首次）
mvn flyway:baseline
```

#### 2. "约束冲突：外键引用不存在的表"

```
原因：迁移文件执行顺序错误或表未创建

解决方案：
1. 查看迁移历史：SELECT * FROM flyway_schema_history;
2. 检查执行顺序是否正确
3. 确保父表在子表之前创建
4. 使用 flyway:repair 修复失败状态
```

#### 3. "数据库权限不足"

```
原因：当前用户权限不够

解决方案：
USE master;
ALTER AUTHORIZATION ON DATABASE::medical_pacs TO sa;
GRANT CONTROL ON DATABASE::medical_pacs TO [domain\username];
```

#### 4. "迁移脚本语法错误"

```
原因：SQL语法在该SQL Server版本不支持

解决方案：
1. 在SSMS中测试脚本
2. 检查数据库兼容性级别
   SELECT compatibility_level FROM sys.databases WHERE name = 'medical_pacs';
3. 使用兼容的SQL语法
```

#### 5. "字符编码问题"

```
原因：迁移脚本使用了错误的编码

解决方案：
1. 保存迁移脚本为UTF-8编码（无BOM）
2. 在SSMS中指定编码：File → Save With Encoding
3. 或在SQL文件头部指定：
   -- Encoding: UTF-8
```

### 诊断脚本

```sql
-- 检查迁移状态
SELECT
    version,
    description,
    type,
    installed_on,
    execution_time,
    success
FROM flyway_schema_history
ORDER BY installed_rank;

-- 检查失败的迁移
SELECT * FROM flyway_schema_history
WHERE success = 0;

-- 检查数据库完整性
DBCC CHECKDB (medical_pacs);

-- 检查约束状态
SELECT * FROM sys.foreign_keys
WHERE is_disabled = 1;
```

---

## 回滚策略

### 回滚方式

#### 方式1：使用Undo脚本（推荐）

```bash
# 手动执行回滚脚本
sqlcmd -S localhost -U sa -P "Password123!" -d medical_pacs -i "U1.3.0__supporting_contexts.sql"

# 或在SSMS中执行
-- File → Open → U1.3.0__supporting_contexts.sql → Execute

# 清理Flyway历史
DELETE FROM flyway_schema_history WHERE version >= '1.3.0';
```

#### 方式2：数据库备份恢复

```powershell
# 重新启动数据库服务
Restart-Service -Name MSSQL$SQLEXPRESS

# 从备份恢复
RESTORE DATABASE medical_pacs FROM DISK = 'C:\backups\medical_pacs_backup.bak'
WITH REPLACE;

# 重新应用迁移
mvn flyway:baseline
mvn flyway:migrate --to=1.2.0
```

#### 方式3：Flyway撤销（需要企业版）

```bash
# Flyway企业版支持undo
flyway undo

# 社区版不支持undo，需要手动处理
```

### 回滚清单

| BC | 回滚脚本 | 需要清理的表 | 备注 |
|----|---------|-----------|------|
| 患者信息 | U1.0.1 | cf_patient* | 4个表 |
| 影像管理 | U1.1.0 | cf_examination, cf_series, cf_image, cf_qc_issue | 4个表 |
| 诊断报告 | U1.2.0 | cf_diagnosis_report* | 5个表 |
| 支撑BC | U1.3.0 | cf_ai_interpret* + cf_audit_log 等 | 8个表 |

### 回滚注意事项

⚠️ **重要**：回滚会删除表中的所有数据！

```sql
-- 回滚前强烈建议备份
BACKUP DATABASE medical_pacs
TO DISK = 'C:\backups\medical_pacs_before_rollback.bak';

-- 回滚后验证
SELECT COUNT(*) FROM cf_patient;  -- 应为0
SELECT COUNT(*) FROM flyway_schema_history;  -- 应该少了回滚的版本
```

---

## 性能优化

### 迁移性能优化

```sql
-- 1. 禁用约束检查（加速迁移）
ALTER TABLE cf_image NOCHECK CONSTRAINT ALL;

-- 2. 禁用索引
ALTER INDEX ALL ON cf_image DISABLE;

-- 3. 执行迁移...

-- 4. 重新启用索引
ALTER INDEX ALL ON cf_image REBUILD;

-- 5. 启用约束检查
ALTER TABLE cf_image WITH CHECK CHECK CONSTRAINT ALL;

-- 6. 更新统计信息
UPDATE STATISTICS cf_image;
```

### 生产环境迁移计划

```
时间窗口：凌晨02:00-04:00（访问量最低）

步骤：
1. 20:00 - 全库备份
2. 02:00 - 停止所有应用连接
3. 02:05 - 执行迁移（预计10-15分钟）
4. 02:20 - 验证迁移（5分钟）
5. 02:25 - 恢复应用连接
6. 02:30 - 监控应用日志（15分钟）
7. 08:00 - 业务验收

回滚计划：如迁移失败，立即从20:00备份恢复
```

### 索引维护

```sql
-- 迁移后重建碎片索引
ALTER INDEX ALL ON cf_image REBUILD;
ALTER INDEX ALL ON cf_examination REBUILD;

-- 更新表统计
sp_updatestats;

-- 检查索引效率
SELECT
    OBJECT_NAME(ips.object_id) AS table_name,
    i.name AS index_name,
    ips.avg_fragmentation_in_percent
FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'LIMITED') ips
JOIN sys.indexes i ON ips.object_id = i.object_id AND ips.index_id = i.index_id
WHERE ips.avg_fragmentation_in_percent > 10
ORDER BY ips.avg_fragmentation_in_percent DESC;
```

---

## 监控和维护

### 迁移监控

```sql
-- 定期检查迁移状态
SELECT
    COUNT(*) AS total_migrations,
    SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END) AS successful,
    SUM(CASE WHEN success = 0 THEN 1 ELSE 0 END) AS failed
FROM flyway_schema_history;

-- 最新迁移信息
SELECT TOP 5
    version, description, installed_on, execution_time
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

### 定期清理

```sql
-- 清理过期的迁移记录（仅在完全确认无误后）
-- DELETE FROM flyway_schema_history WHERE version < '1.0';

-- 定期备份flyway_schema_history
BACKUP TABLE flyway_schema_history;
```

---

## 最佳实践

### ✓ 推荐做法

1. **版本控制** - 迁移脚本纳入Git，每次提交都要标记版本
2. **测试迁移** - 在开发/测试环境先执行，再上线到生产
3. **文档记录** - 每个迁移都要在任务系统中记录
4. **备份策略** - 迁移前必须全库备份
5. **监控告警** - 迁移后监控所有关键指标

### ✗ 不推荐做法

1. ❌ 修改已执行的迁移脚本（Flyway会校验失败）
2. ❌ 跳过中间版本直接升级（可能导致依赖缺失）
3. ❌ 在生产环境测试迁移脚本
4. ❌ 使用自动重名复（REPLACE）删除生产表
5. ❌ 忘记执行验证脚本就认为迁移完成

---

## 支持与反馈

- 🐛 发现Bug: 提交Issue到项目仓库
- 💬 有问题: 查看本文档的故障排查部分
- 📝 反馈建议: 联系DBA团队

---

*本指南持续更新，最后修订日期：2026-03-26*
