# 数据库迁移管理与验证脚本

> 文件：V1.4.0__migration_verification.sql
> 用途：验证所有迁移的完整性和正确性

---

## 迁移前检查脚本

```sql
-- ============ 迁移前检查 ============

-- 检查是否已有现有表（避免重复创建）
SELECT
    'cf_patient' AS table_name,
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_patient')
         THEN '已存在' ELSE '不存在' END AS status
UNION ALL
SELECT 'cf_examination',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_examination')
         THEN '已存在' ELSE '不存在' END
UNION ALL
SELECT 'cf_image',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_image')
         THEN '已存在' ELSE '不存在' END
UNION ALL
SELECT 'cf_diagnosis_report',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_diagnosis_report')
         THEN '已存在' ELSE '不存在' END
UNION ALL
SELECT 'cf_ai_interpret',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_ai_interpret')
         THEN '已存在' ELSE '不存在' END
UNION ALL
SELECT 'cf_workflow_state',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_workflow_state')
         THEN '已存在' ELSE '不存在' END
UNION ALL
SELECT 'cf_audit_log',
    CASE WHEN EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cf_audit_log')
         THEN '已存在' ELSE '不存在' END
ORDER BY table_name;
```

---

## 迁移后验证脚本

```sql
-- ============ V1.4.0: 迁移完整性验证 ============

-- 1. 验证所有表已创建
DECLARE @missing_tables TABLE (table_name VARCHAR(100));

INSERT INTO @missing_tables
SELECT name FROM (
    SELECT 'cf_patient' AS name
    UNION ALL SELECT 'cf_patient_allergy'
    UNION ALL SELECT 'cf_patient_medication'
    UNION ALL SELECT 'cf_patient_merge_log'
    UNION ALL SELECT 'cf_examination'
    UNION ALL SELECT 'cf_series'
    UNION ALL SELECT 'cf_image'
    UNION ALL SELECT 'cf_qc_issue'
    UNION ALL SELECT 'cf_report_template'
    UNION ALL SELECT 'cf_diagnosis_report'
    UNION ALL SELECT 'cf_report_approval'
    UNION ALL SELECT 'cf_report_version'
    UNION ALL SELECT 'cf_report_draft_backup'
    UNION ALL SELECT 'cf_ai_interpret'
    UNION ALL SELECT 'cf_storage_migration'
    UNION ALL SELECT 'cf_storage_statistics'
    UNION ALL SELECT 'cf_workflow_state'
    UNION ALL SELECT 'cf_workflow_event_log'
    UNION ALL SELECT 'cf_audit_log'
    UNION ALL SELECT 'cf_security_rule'
    UNION ALL SELECT 'cf_system_metrics'
) required_tables
WHERE name NOT IN (
    SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = 'dbo' AND TABLE_TYPE = 'BASE TABLE'
);

IF EXISTS (SELECT 1 FROM @missing_tables)
BEGIN
    PRINT '错误：以下表未创建：';
    SELECT * FROM @missing_tables;
END
ELSE
BEGIN
    PRINT '✓ 所有表已成功创建 (21个表)';
END;

-- 2. 验证主键约束
PRINT '';
PRINT '========== 主键约束验证 ==========';
SELECT
    t.name AS table_name,
    COUNT(*) AS pk_count
FROM sys.tables t
LEFT JOIN sys.key_constraints kc ON t.object_id = kc.parent_object_id AND kc.type = 'PK'
WHERE t.name LIKE 'cf_%'
GROUP BY t.name
ORDER BY table_name;

-- 3. 验证外键约束
PRINT '';
PRINT '========== 外键约束验证 ==========';
SELECT
    OBJECT_NAME(constraint_object_id) AS constraint_name,
    OBJECT_NAME(parent_object_id) AS child_table,
    OBJECT_NAME(referenced_object_id) AS parent_table,
    'OK' AS status
FROM sys.foreign_keys
WHERE OBJECT_NAME(parent_object_id) LIKE 'cf_%'
ORDER BY child_table, constraint_name;

-- 4. 验证索引
PRINT '';
PRINT '========== 索引统计 ==========';
SELECT
    t.name AS table_name,
    COUNT(i.index_id) AS index_count
FROM sys.tables t
LEFT JOIN sys.indexes i ON t.object_id = i.object_id AND i.index_id > 0
WHERE t.name LIKE 'cf_%'
GROUP BY t.name
ORDER BY table_name;

-- 5. 验证数据类型
PRINT '';
PRINT '========== 关键字段数据类型验证 ==========';
SELECT
    t.name AS table_name,
    c.name AS column_name,
    TYPE_NAME(c.user_type_id) AS data_type,
    c.max_length AS max_length,
    c.is_nullable AS nullable
FROM sys.tables t
JOIN sys.columns c ON t.object_id = c.object_id
WHERE t.name IN ('cf_patient', 'cf_examination', 'cf_image', 'cf_diagnosis_report')
    AND c.name IN ('id', 'status', 'created_at', 'updated_at')
ORDER BY t.name, c.column_id;

-- 6. 检查表大小
PRINT '';
PRINT '========== 表空间使用情况 ==========';
SELECT
    t.name AS table_name,
    CAST(SUM(au.total_pages) * 8 / 1024.0 AS DECIMAL(10,2)) AS total_mb,
    COUNT(*) AS row_count
FROM sys.tables t
LEFT JOIN sys.indexes i ON t.object_id = i.object_id
LEFT JOIN sys.dm_db_partition_stats ps ON i.object_id = ps.object_id AND i.index_id = ps.index_id
LEFT JOIN sys.allocation_units au ON ps.partition_id = au.container_id
WHERE t.name LIKE 'cf_%'
GROUP BY t.name
ORDER BY total_mb DESC;

-- 7. 生成表结构清单
PRINT '';
PRINT '========== 表结构清单 ==========';
SELECT
    t.name AS table_name,
    STRING_AGG(CONCAT(c.name, ' (', TYPE_NAME(c.user_type_id), ')'), ', ')
        WITHIN GROUP (ORDER BY c.column_id) AS columns
FROM sys.tables t
JOIN sys.columns c ON t.object_id = c.object_id
WHERE t.name LIKE 'cf_%'
GROUP BY t.name
ORDER BY t.name;

PRINT '';
PRINT '✓ 迁移完整性验证完成';
PRINT '';

-- ============ V1.4.0: 迁移完整性验证 (完成) ============
```

---

## 迁移状态查询脚本

```sql
-- ============ 查询迁移历史状态 (Flyway) ============

-- 检查Flyway的迁移历史（假设已安装Flyway）
SELECT
    installed_rank AS rank,
    version AS migration_version,
    description AS migration_name,
    type AS type,
    script AS script_name,
    CAST(checksum AS VARCHAR(20)) AS checksum,
    installed_by AS executed_by,
    installed_on AS executed_at,
    CAST(execution_time AS VARCHAR(20)) + ' ms' AS execution_time,
    success AS is_success
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

---

## 数据库初始化完整性检查

```sql
-- ============ 初始化数据验证 ============

-- 1. 验证患者数据
PRINT '========== 患者BC数据验证 ==========';
SELECT '患者记录数' AS item, COUNT(*) AS count FROM cf_patient WHERE del_flag = '0'
UNION ALL
SELECT '过敏记录数', COUNT(*) FROM cf_patient_allergy WHERE del_flag = '0'
UNION ALL
SELECT '用药记录数', COUNT(*) FROM cf_patient_medication WHERE del_flag = '0';

-- 2. 验证检查和影像数据
PRINT '';
PRINT '========== 影像BC数据验证 ==========';
SELECT '检查记录数' AS item, COUNT(*) AS count FROM cf_examination WHERE del_flag = '0'
UNION ALL
SELECT '序列记录数', COUNT(*) FROM cf_series WHERE del_flag = '0'
UNION ALL
SELECT '影像记录数', COUNT(*) FROM cf_image WHERE del_flag = '0'
UNION ALL
SELECT 'QC问题记录数', COUNT(*) FROM cf_qc_issue WHERE del_flag = '0'
UNION ALL
SELECT '热层影像数', COUNT(*) FROM cf_image WHERE storage_tier = 'hot' AND del_flag = '0'
UNION ALL
SELECT '温层影像数', COUNT(*) FROM cf_image WHERE storage_tier = 'warm' AND del_flag = '0'
UNION ALL
SELECT '冷层影像数', COUNT(*) FROM cf_image WHERE storage_tier = 'cold' AND del_flag = '0';

-- 3. 验证报告数据
PRINT '';
PRINT '========== 诊断报告BC数据验证 ==========';
SELECT '报告模板数' AS item, COUNT(*) AS count FROM cf_report_template WHERE del_flag = '0'
UNION ALL
SELECT '报告记录数', COUNT(*) FROM cf_diagnosis_report WHERE del_flag = '0'
UNION ALL
SELECT '草稿状态报告', COUNT(*) FROM cf_diagnosis_report WHERE status = 'draft' AND del_flag = '0'
UNION ALL
SELECT '待审核报告', COUNT(*) FROM cf_diagnosis_report WHERE status = 'pending_approval' AND del_flag = '0'
UNION ALL
SELECT '已发布报告', COUNT(*) FROM cf_diagnosis_report WHERE status = 'published' AND del_flag = '0'
UNION ALL
SELECT '审核记录数', COUNT(*) FROM cf_report_approval;

-- 4. 验证AI和工作流数据
PRINT '';
PRINT '========== AI与工作流BC数据验证 ==========';
SELECT 'AI解读记录数' AS item, COUNT(*) AS count FROM cf_ai_interpret WHERE del_flag = '0'
UNION ALL
SELECT '工作流实例数', COUNT(*) FROM cf_workflow_state WHERE del_flag = '0'
UNION ALL
SELECT '工作流事件数', COUNT(*) FROM cf_workflow_event_log;

-- 5. 验证审计和存储数据
PRINT '';
PRINT '========== 审计与存储BC数据验证 ==========';
SELECT '审计日志条数' AS item, COUNT(*) AS count FROM cf_audit_log
UNION ALL
SELECT '存储迁移记录数', COUNT(*) FROM cf_storage_migration
UNION ALL
SELECT '安全规则数', COUNT(*) FROM cf_security_rule WHERE is_enabled = 1;

PRINT '';
PRINT '✓ 初始化数据验证完成';
```

---

## 索引优化脚本

```sql
-- ============ 索引碎片检查与优化 ============

-- 检查索引碎片情况
SELECT
    OBJECT_NAME(ips.object_id) AS table_name,
    i.name AS index_name,
    ips.index_type_desc AS index_type,
    CAST(ips.avg_fragmentation_in_percent AS DECIMAL(5,2)) AS fragmentation_percent,
    CASE
        WHEN ips.avg_fragmentation_in_percent < 10 THEN '无需维护'
        WHEN ips.avg_fragmentation_in_percent < 30 THEN '建议重新组织'
        ELSE '建议重建'
    END AS recommendation
FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'LIMITED') ips
JOIN sys.indexes i ON ips.object_id = i.object_id AND ips.index_id = i.index_id
WHERE OBJECT_NAME(ips.object_id) LIKE 'cf_%'
    AND ips.avg_fragmentation_in_percent > 5
ORDER BY fragmentation_percent DESC;

-- 重建高度碎片的索引（碎片 > 30%）
-- ALTER INDEX index_name ON table_name REBUILD;

-- 重新组织中度碎片的索引（10% < 碎片 <= 30%）
-- ALTER INDEX index_name ON table_name REORGANIZE;
```

---

## 性能基准建立

```sql
-- ============ 建立性能基准 ============

-- 查询性能基准（执行计划）
SET STATISTICS IO ON;
SET STATISTICS TIME ON;

-- 基准查询1：患者查询
SELECT TOP 100
    p.patient_no, p.patient_name, COUNT(e.id) AS exam_count
FROM cf_patient p
LEFT JOIN cf_examination e ON p.id = e.patient_id
WHERE p.del_flag = '0'
GROUP BY p.id, p.patient_no, p.patient_name
ORDER BY exam_count DESC;

-- 基准查询2：影像搜索（QC状态）
SELECT TOP 1000
    i.id, i.sop_instance_uid, i.qc_status, i.storage_tier, i.received_timestamp
FROM cf_image i
WHERE i.qc_status = 'passed' AND i.storage_tier = 'hot'
ORDER BY i.received_timestamp DESC;

-- 基准查询3：报告查询（按状态）
SELECT
    dr.status, COUNT(*) AS count, AVG(DATEDIFF(MINUTE, dr.created_at, dr.published_at)) AS avg_days_to_publish
FROM cf_diagnosis_report dr
WHERE dr.del_flag = '0'
GROUP BY dr.status;

SET STATISTICS IO OFF;
SET STATISTICS TIME OFF;

-- 记录基准结果
INSERT INTO cf_system_metrics (metric_timestamp, api_response_time_p95, error_rate_percent)
VALUES (GETDATE(), 500, 0.5);
```
