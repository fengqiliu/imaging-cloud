# 数据库迁移快速启动指南

> 5分钟快速上手，10分钟完整迁移

---

## 🚀 快速启动（开发环境）

### Step 1: 准备数据库（2分钟）

```sql
-- 在SQL Server Management Studio中执行
USE master;
GO

-- 创建数据库（如果不存在）
IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = 'medical_pacs')
BEGIN
    CREATE DATABASE medical_pacs
    COLLATE Chinese_PRC_CI_AS;
END
GO

-- 创建用户和授权
CREATE LOGIN [pacs_user] WITH PASSWORD = 'PacsPass@123!';
GO

CREATE USER [pacs_user] FOR LOGIN [pacs_user];
GO

GRANT CONTROL ON DATABASE::medical_pacs TO [pacs_user];
GO

-- 验证
SELECT name, state_desc FROM sys.databases WHERE name = 'medical_pacs';
```

### Step 2: 配置应用（3分钟）

**application.yml**：
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=medical_pacs;encrypt=true;trustServerCertificate=true
    username: pacs_user
    password: PacsPass@123!
  flyway:
    enabled: true
    locations: classpath:db/migration
```

**pom.xml**：
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>9.22.3</version>
</dependency>
```

### Step 3: 执行迁移（5分钟）

```bash
# 方式1：Maven命令
mvn clean flyway:migrate

# 方式2：Spring Boot启动（自动执行）
mvn spring-boot:run

# 输出示例：
# [INFO] Successfully validated 5 migrations (execution time 00:00.156s)
# [INFO] Current version of schema "dbo": 1.4.0
# [INFO] Schema "dbo" is up to date. No migration necessary.
```

### Step 4: 验证结果（2分钟）

```sql
-- 在SSMS中执行
USE medical_pacs;
GO

-- 查看创建的表
SELECT COUNT(*) AS table_count
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME LIKE 'cf_%';
-- 预期结果：21

-- 查看迁移历史
SELECT version, description, installed_on
FROM flyway_schema_history
ORDER BY installed_rank;
-- 预期结果：5行（V1.0.1 - V1.4.0）
```

✅ **完成！** 你的数据库已准备好开发

---

## 📊 迁移文件概览

```
src/database/migrations/
│
├─ V1.0.1__patient_information_context.sql
│  └─ 患者、过敏、用药、合并记录 (4个表)
│
├─ V1.1.0__image_management_context.sql
│  └─ 检查、序列、影像、QC问题 (4个表)
│
├─ V1.2.0__diagnostic_report_context.sql
│  └─ 报告、模板、审批、版本、草稿 (5个表)
│
├─ V1.3.0__supporting_contexts.sql
│  └─ AI、存储、工作流、审计、规则、监控 (8个表)
│
└─ V1.4.0__migration_verification.sql
   └─ 验证脚本（不创建表）
```

---

## 🔍 常见操作

### 查看迁移状态

```bash
# Maven方式
mvn flyway:info

# 输出示例：
# Version  | Description                        | Installed On        | State
# ---------|------------------------------------|--------------------|--------
# 1.0.1    | patient information context        | 2026-03-26 10:00:00 | Success
# 1.1.0    | image management context           | 2026-03-26 10:00:05 | Success
# ...
```

### 数据库查询示例

```sql
-- 查询患者列表
SELECT TOP 10 patient_no, patient_name, gender, birth_date
FROM cf_patient
WHERE del_flag = '0'
ORDER BY created_at DESC;

-- 查询最新检查
SELECT TOP 10 id, study_instance_uid, exam_name, exam_status
FROM cf_examination
WHERE del_flag = '0'
ORDER BY created_at DESC;

-- 查询报告统计
SELECT status, COUNT(*) AS count
FROM cf_diagnosis_report
WHERE del_flag = '0'
GROUP BY status;

-- 查询影像存储分布
SELECT storage_tier, COUNT(*) AS count,
       SUM(file_size_bytes) / 1024 / 1024 / 1024 AS size_gb
FROM cf_image
WHERE del_flag = '0'
GROUP BY storage_tier;
```

---

## ⚠️ 迁移检查清单

### 迁移前

- [ ] 备份现有数据库
  ```bash
  # SQL Server备份
  BACKUP DATABASE medical_pacs
  TO DISK = 'C:\backups\medical_pacs_backup.bak';
  ```

- [ ] 确认数据库连接正常
  ```bash
  # 测试连接
  sqlcmd -S localhost -U sa -P "Password123!" -d medical_pacs -Q "SELECT 1"
  ```

- [ ] 检查磁盘空间（至少需要1GB）
  ```sql
  EXEC xp_fixeddrives;  -- 查看各分区可用空间
  ```

- [ ] 停止应用服务
  ```bash
  # 确保没有应用连接到数据库
  # 可选：KILL所有连接
  ```

### 迁移中

- [ ] 执行迁移命令
  ```bash
  mvn clean flyway:migrate
  ```

- [ ] 监控执行进度
  - 查看控制台输出
  - 检查SQL Server日志
  ```sql
  SELECT * FROM sys.event_log
  WHERE session_id = @@SPID
  ORDER BY object_id DESC;
  ```

- [ ] 检查是否有错误
  ```sql
  SELECT * FROM flyway_schema_history
  WHERE success = 0;
  ```

### 迁移后

- [ ] 执行验证脚本
  ```bash
  # 自动验证（在V1.4.0中）
  # 或手动执行验证SQL
  ```

- [ ] 确认表和约束完整
  ```sql
  -- 验证表数量
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
  WHERE TABLE_NAME LIKE 'cf_%';  -- 应为21

  -- 验证外键约束
  SELECT COUNT(*) FROM sys.foreign_keys
  WHERE OBJECT_NAME(parent_object_id) LIKE 'cf_%';  -- 应为>10
  ```

- [ ] 检查初始数据
  ```sql
  SELECT COUNT(*) FROM cf_patient;          -- 应为3
  SELECT COUNT(*) FROM cf_examination;      -- 应为3
  SELECT COUNT(*) FROM cf_image;            -- 应为3
  ```

- [ ] 启动应用服务
  ```bash
  mvn spring-boot:run
  ```

- [ ] 业务验收（从应用UI确认）
  - [ ] 能否查询患者列表
  - [ ] 能否查询检查记录
  - [ ] 能否查看影像列表
  - [ ] 能否创建报告

- [ ] 性能基准测试
  ```sql
  -- 查询响应时间
  SET STATISTICS TIME ON;
  SELECT * FROM cf_patient WHERE patient_no = 'HN20260325001';
  SET STATISTICS TIME OFF;
  ```

---

## 🔄 回滚步骤（如需撤销）

```bash
# Step 1: 停止应用
# 确保没有应用连接

# Step 2: 执行回滚脚本
sqlcmd -S localhost -U sa -P "Password123!" -d medical_pacs -i "U1.3.0__supporting_contexts.sql"

# Step 3: 清理Flyway历史
sqlcmd -S localhost -U sa -P "Password123!" -d medical_pacs -Q "DELETE FROM flyway_schema_history WHERE version >= '1.3.0'"

# Step 4: 验证回滚
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;

# Step 5: 重新启动应用
mvn spring-boot:run
```

---

## 🆘 故障排查

### 问题1：flyway_schema_history表不存在

```sql
-- 解决：初始化Baseline
mvn flyway:baseline -Dflyway.baselineVersion=1.0.0
```

### 问题2：外键约束失败

```sql
-- 检查依赖表是否存在
SELECT * FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME IN ('cf_patient', 'cf_examination')
AND TABLE_SCHEMA = 'dbo';

-- 查看具体错误
SELECT * FROM flyway_schema_history
WHERE success = 0;
```

### 问题3：权限不足

```sql
-- 确保用户有足够权限
USE medical_pacs;
GRANT CONTROL ON DATABASE::medical_pacs TO [pacs_user];
```

### 问题4：脚本编码错误

```
解决：使用UTF-8编码（无BOM）保存迁移脚本
```

---

## 📈 迁移统计

| 指标 | 值 |
|-----|-----|
| 总迁移文件 | 5个 |
| 总表数 | 21个 |
| 总字段数 | ~173个 |
| 总索引数 | >42个 |
| 总大小 | ~82KB |
| 预计执行时间 | 10-15秒 |
| 迁移后数据库大小 | 10-20MB |

---

## 🎓 进阶话题

### 添加新的迁移脚本

当需要添加新功能时：

```sql
-- 创建文件：V1.5.0__add_new_feature.sql
-- ============ V1.5.0: 新功能添加 ============

-- 添加新表
CREATE TABLE cf_new_table (
    id BIGINT NOT NULL PRIMARY KEY,
    ...
);

-- 添加新列到现有表
ALTER TABLE cf_image ADD new_column VARCHAR(100) NULL;

-- 创建新索引
CREATE INDEX idx_new ON cf_image(new_column);

-- ============ V1.5.0: 新功能添加 (完成) ============
```

### 生产环境迁移计划

```
时间表：
├─ 晚上20:00 - 全库备份
├─ 凌晨02:00 - 停止应用
├─ 凌晨02:05 - 执行迁移
├─ 凌晨02:20 - 验证迁移
├─ 凌晨02:30 - 启动应用
└─ 上午08:00 - 业务验收

风险控制：
├─ 备份恢复点：20:00
├─ 回滚方案：提前准备Undo脚本
├─ 灰度发布：先在测试环境，再生产
└─ 监控告警：迁移全程有人值班
```

---

## 📞 获取帮助

| 问题类型 | 解决方案 |
|---------|--------|
| 脚本错误 | 查看README_MIGRATION.md故障排查章节 |
| 性能问题 | 检查索引、更新统计信息、参考性能优化章节 |
| 备份恢复 | 使用之前的备份文件恢复数据库 |
| 其他问题 | 提交Issue到项目仓库或联系DBA |

---

## ✅ 成功标志

迁移完成后，你应该看到：

```
✓ 5个迁移脚本全部执行成功
✓ flyway_schema_history表中有5条记录
✓ 21个表全部创建完成
✓ 所有外键约束生效
✓ 初始测试数据已插入
✓ 应用可以正常启动
✓ UI能够查询数据
```

---

**🎉 恭喜！你已经成功完成了基于DDD的数据库迁移！**

现在你可以开始开发应用了。下一步：
1. 在各个BC中实现聚合根
2. 编写应用服务
3. 实现领域服务
4. 集成事件驱动

详见 `DDD-领域驱动设计分析.md` 文档。

---

*最后更新：2026-03-26*
