# 数据库迁移脚本 - 影像管理限界上下文 (Image Management BC)

> Flyway迁移文件：V1.1.0__image_management_context.sql
> 执行时间：5-10秒
> 影响表数：4个

---

## 1. 检查（Study）聚合表

```sql
-- ============ V1.1.0: 影像管理限界上下文初始化 ============

-- 1.1 检查（Study）表
CREATE TABLE cf_examination (
    id                    BIGINT          NOT NULL PRIMARY KEY,      -- 检查ID
    patient_id            BIGINT          NOT NULL,

    -- 检查基本信息
    study_instance_uid    VARCHAR(128)    NOT NULL UNIQUE,           -- DICOM标准UID
    study_date            DATE            NOT NULL,
    study_time            TIME            NOT NULL,
    exam_name             VARCHAR(255)    NOT NULL,
    modality              VARCHAR(10)     NOT NULL,                  -- CT/MRI/DR/CR/US/PET/NM

    -- 申请信息
    request_id            VARCHAR(100)    NULL,
    referring_doctor      VARCHAR(100)    NULL,
    requesting_department VARCHAR(100)    NULL,

    -- 检查部位（解剖部位）
    body_part             VARCHAR(100)    NOT NULL,

    -- 状态流转
    exam_status           VARCHAR(50)     NOT NULL DEFAULT 'pending', -- pending/scheduled/in_progress/images_received/qc_passed/ai_analysis_completed/waiting_for_report/report_pending_approval/report_approved/completed/cancelled

    -- SLA管理
    sla_type              VARCHAR(20)     NOT NULL DEFAULT 'normal',  -- emergency/normal/complex/checkup
    sla_deadline          DATETIME2       NULL,
    qc_passed_at          DATETIME2       NULL,                       -- QC通过时间
    report_due_at         DATETIME2       NULL,                       -- 报告应完成时间

    -- AI分析状态
    ai_status             VARCHAR(30)     NULL,                       -- pending/running/completed/skipped/failed

    -- 工作流追踪
    workflow_id           VARCHAR(64)     NULL,                       -- 对应WorkflowOrchestrationAgent实例

    -- 版本与审计
    version               INT             NOT NULL DEFAULT 1,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL DEFAULT 0,
    updated_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    -- 约束
    CONSTRAINT fk_exam_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    CONSTRAINT ck_exam_status CHECK (exam_status IN ('pending', 'scheduled', 'in_progress', 'images_received', 'qc_passed', 'ai_analysis_completed', 'waiting_for_report', 'report_pending_approval', 'report_approved', 'completed', 'cancelled')),
    CONSTRAINT ck_sla_type CHECK (sla_type IN ('emergency', 'normal', 'complex', 'checkup'))
);

-- 索引
CREATE INDEX idx_exam_patient_id ON cf_examination(patient_id) WHERE del_flag = '0';
CREATE UNIQUE INDEX uidx_exam_study_uid ON cf_examination(study_instance_uid) WHERE del_flag = '0';
CREATE INDEX idx_exam_status ON cf_examination(exam_status);
CREATE INDEX idx_exam_sla_deadline ON cf_examination(sla_deadline) WHERE sla_deadline IS NOT NULL AND del_flag = '0';
CREATE INDEX idx_exam_created_at ON cf_examination(created_at);

-- 1.2 序列（Series）表
CREATE TABLE cf_series (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    exam_id               BIGINT          NOT NULL,

    -- DICOM标识
    series_instance_uid   VARCHAR(128)    NOT NULL UNIQUE,
    series_number         INT             NOT NULL,
    series_description    VARCHAR(255)    NULL,

    -- 序列属性
    modality              VARCHAR(10)     NOT NULL,
    laterality            CHAR(2)         NULL,                      -- L/R/B（左/右/双）
    body_part_examined    VARCHAR(100)    NULL,

    -- 影像统计
    instance_count        INT             NOT NULL DEFAULT 0,        -- 该序列包含的影像数
    total_file_size_bytes BIGINT          NOT NULL DEFAULT 0,

    -- 版本与审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_series_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id)
);

-- 索引
CREATE INDEX idx_series_exam_id ON cf_series(exam_id) WHERE del_flag = '0';
CREATE UNIQUE INDEX uidx_series_uid ON cf_series(series_instance_uid) WHERE del_flag = '0';

-- 1.3 影像（Image）表 - 最核心的聚合根
CREATE TABLE cf_image (
    id                    BIGINT          NOT NULL PRIMARY KEY,      -- 影像ID (I20260325001)
    exam_id               BIGINT          NOT NULL,
    series_id             BIGINT          NOT NULL,
    patient_id            BIGINT          NOT NULL,

    -- DICOM标识
    sop_instance_uid      VARCHAR(128)    NOT NULL UNIQUE,           -- 全球唯一标识（去重用）
    series_instance_uid   VARCHAR(128)    NOT NULL,
    study_instance_uid    VARCHAR(128)    NOT NULL,

    -- 影像属性
    modality              VARCHAR(10)     NOT NULL,
    rows                  INT             NOT NULL,                  -- 像素行数
    columns               INT             NOT NULL,                  -- 像素列数
    bits_allocated        INT             NULL,                      -- 分配位数
    bits_stored           INT             NULL,                      -- 存储位数

    -- 文件管理
    file_path             VARCHAR(500)    NOT NULL,                  -- 存储路径（相对或MinIO Key）
    file_size_bytes       BIGINT          NOT NULL,
    file_hash             VARCHAR(64)     NULL,                      -- MD5或SHA256校验值
    received_timestamp    DATETIME2       NOT NULL,

    -- QC质控信息
    qc_score              DECIMAL(5,2)    NULL,                      -- 0-100
    qc_status             VARCHAR(20)     NULL,                      -- pending/passed/failed/manual_review
    qc_issues             NVARCHAR(MAX)   NULL,                      -- JSON: [{category, type, severity, description}]
    qc_evaluated_by       BIGINT          NULL,
    qc_evaluated_at       DATETIME2       NULL,

    -- 存储分级
    storage_tier          VARCHAR(10)     NOT NULL DEFAULT 'hot',    -- hot/warm/cold
    last_accessed_at      DATETIME2       NULL,                      -- 最后访问时间（迁移判断依据）

    -- 标记
    is_key_image          BIT             NOT NULL DEFAULT 0,        -- 是否为关键影像（优先传输）
    is_desensitized       BIT             NOT NULL DEFAULT 0,        -- 是否已脱敏

    -- 缩略图与预览
    thumbnail_path        VARCHAR(500)    NULL,                      -- 缩略图路径
    preview_path          VARCHAR(500)    NULL,                      -- 预览图路径

    -- 版本与审计
    version               INT             NOT NULL DEFAULT 1,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL DEFAULT 0,
    updated_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    -- 约束
    CONSTRAINT fk_image_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT fk_image_series FOREIGN KEY (series_id) REFERENCES cf_series(id),
    CONSTRAINT fk_image_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    CONSTRAINT ck_storage_tier CHECK (storage_tier IN ('hot', 'warm', 'cold')),
    CONSTRAINT ck_qc_status CHECK (qc_status IS NULL OR qc_status IN ('pending', 'passed', 'failed', 'manual_review'))
);

-- 关键索引（QC和存储迁移的查询优化）
CREATE UNIQUE INDEX uidx_image_sop_uid ON cf_image(sop_instance_uid) WHERE del_flag = '0';
CREATE INDEX idx_image_exam_id ON cf_image(exam_id) WHERE del_flag = '0';
CREATE INDEX idx_image_series_id ON cf_image(series_id) WHERE del_flag = '0';
CREATE INDEX idx_image_qc_status ON cf_image(qc_status) WHERE del_flag = '0';
CREATE INDEX idx_image_storage_tier ON cf_image(storage_tier);
CREATE INDEX idx_image_last_accessed ON cf_image(last_accessed_at) WHERE storage_tier != 'hot';
CREATE INDEX idx_image_created_at ON cf_image(created_at);

-- 分区（可选，如果超大表可启用按月分区）
-- PARTITION BY RANGE (YEAR(created_at), MONTH(created_at))

-- 1.4 QC问题详情表
CREATE TABLE cf_qc_issue (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    image_id              BIGINT          NOT NULL,
    exam_id               BIGINT          NOT NULL,

    -- 问题分类
    issue_category        VARCHAR(30)     NOT NULL,                  -- technical/completeness/consistency/compliance
    issue_type            VARCHAR(100)    NOT NULL,                  -- exposure_abnormal/tag_missing/patient_mismatch等
    severity              VARCHAR(20)     NOT NULL,                  -- fatal/major/minor/warning
    description           NVARCHAR(500)   NOT NULL,

    -- 建议处理
    suggested_action      NVARCHAR(200)   NULL,                      -- 例：重照、补充采集等

    -- 处理状态
    resolution_status     VARCHAR(20)     NOT NULL DEFAULT 'open',   -- open/resolved/ignored/waived
    resolved_by           BIGINT          NULL,
    resolved_at           DATETIME2       NULL,
    resolution_notes      NVARCHAR(255)   NULL,

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_qc_image FOREIGN KEY (image_id) REFERENCES cf_image(id),
    CONSTRAINT fk_qc_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT ck_issue_category CHECK (issue_category IN ('technical', 'completeness', 'consistency', 'compliance')),
    CONSTRAINT ck_severity CHECK (severity IN ('fatal', 'major', 'minor', 'warning')),
    CONSTRAINT ck_resolution CHECK (resolution_status IN ('open', 'resolved', 'ignored', 'waived'))
);

-- 索引
CREATE INDEX idx_qc_image_id ON cf_qc_issue(image_id) WHERE del_flag = '0';
CREATE INDEX idx_qc_exam_id ON cf_qc_issue(exam_id) WHERE del_flag = '0';
CREATE INDEX idx_qc_severity ON cf_qc_issue(severity, resolution_status) WHERE del_flag = '0';

-- ============ V1.1.0: 影像管理限界上下文初始化 (完成) ============
```

---

## 回滚脚本

```sql
-- ============ U1.1.0: 影像管理限界上下文回滚 ============

DROP TABLE IF EXISTS cf_qc_issue;
DROP TABLE IF EXISTS cf_image;
DROP TABLE IF EXISTS cf_series;
DROP TABLE IF EXISTS cf_examination;

-- ============ U1.1.0: 影像管理限界上下文回滚 (完成) ============
```

---

## 初始化数据（可选）

```sql
-- ============ V1.1.1: 影像管理测试数据 ============

-- 插入测试检查
INSERT INTO cf_examination (
    id, patient_id, study_instance_uid, study_date, study_time, exam_name,
    modality, body_part, exam_status, created_by, updated_by
) VALUES
    (100001, 1000001, '1.2.840.10008.5.1.4.1.1.2.1', '2026-03-25', '10:30:00', '胸部CT平扫',
     'CT', '胸部', 'pending', 1, 1),
    (100002, 1000002, '1.2.840.10008.5.1.4.1.1.2.2', '2026-03-25', '11:00:00', '腹部MRI扫描',
     'MRI', '腹部', 'images_received', 1, 1),
    (100003, 1000003, '1.2.840.10008.5.1.4.1.1.2.3', '2026-03-25', '11:30:00', '胸部X光拍片',
     'DR', '胸部', 'qc_passed', 1, 1);

-- 插入测试序列
INSERT INTO cf_series (
    id, exam_id, series_instance_uid, series_number, series_description, modality, body_part_examined
) VALUES
    (200001, 100001, '1.2.840.10008.5.1.4.1.1.2.1.1', 1, '胸部平扫序列', 'CT', '胸部'),
    (200002, 100001, '1.2.840.10008.5.1.4.1.1.2.1.2', 2, '胸部增强序列', 'CT', '胸部'),
    (200003, 100002, '1.2.840.10008.5.1.4.1.1.2.2.1', 1, 'T2加权像', 'MRI', '腹部');

-- 插入测试影像
INSERT INTO cf_image (
    id, exam_id, series_id, patient_id, sop_instance_uid, series_instance_uid, study_instance_uid,
    modality, rows, columns, file_path, file_size_bytes, received_timestamp,
    qc_status, storage_tier, created_by, updated_by
) VALUES
    (300001, 100001, 200001, 1000001, '1.2.840.10008.5.1.4.1.1.2.1.1.1', '1.2.840.10008.5.1.4.1.1.2.1.1', '1.2.840.10008.5.1.4.1.1.2.1',
     'CT', 512, 512, '/images/2026-03-25/image_001.dcm', 1048576, GETDATE(),
     'pending', 'hot', 1, 1),
    (300002, 100001, 200001, 1000001, '1.2.840.10008.5.1.4.1.1.2.1.1.2', '1.2.840.10008.5.1.4.1.1.2.1.1', '1.2.840.10008.5.1.4.1.1.2.1',
     'CT', 512, 512, '/images/2026-03-25/image_002.dcm', 1048576, GETDATE(),
     'passed', 'hot', 1, 1),
    (300003, 100002, 200003, 1000002, '1.2.840.10008.5.1.4.1.1.2.2.1.1', '1.2.840.10008.5.1.4.1.1.2.2.1', '1.2.840.10008.5.1.4.1.1.2.2',
     'MRI', 256, 256, '/images/2026-03-25/mri_001.dcm', 2097152, GETDATE(),
     'passed', 'hot', 1, 1);

-- ============ V1.1.1: 影像管理测试数据 (完成) ============
```
