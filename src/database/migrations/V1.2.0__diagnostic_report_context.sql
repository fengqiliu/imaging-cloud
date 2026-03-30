# 数据库迁移脚本 - 诊断报告限界上下文 (Diagnostic Report BC)

> Flyway迁移文件：V1.2.0__diagnostic_report_context.sql
> 执行时间：5-8秒
> 影响表数：4个

---

```sql
-- ============ V1.2.0: 诊断报告限界上下文初始化 ============

-- 2.1 报告模板表
CREATE TABLE cf_report_template (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    template_name         VARCHAR(100)    NOT NULL,
    template_code         VARCHAR(50)     NOT NULL UNIQUE,
    description           VARCHAR(255)    NULL,

    -- 模板应用范围
    applicable_modality   VARCHAR(100)    NOT NULL,                  -- CT,MRI,DR等，逗号分隔
    applicable_body_part  VARCHAR(100)    NOT NULL,                  -- 胸部,腹部等
    applicable_age_range  VARCHAR(50)     NULL,                      -- JSON: {minAge, maxAge}

    -- 模板结构
    template_content      NVARCHAR(MAX)   NOT NULL,                  -- HTML或Markdown格式的模板

    -- 状态
    is_active             BIT             NOT NULL DEFAULT 1,
    version               INT             NOT NULL DEFAULT 1,

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0'
);

CREATE INDEX idx_template_modality ON cf_report_template(applicable_modality) WHERE is_active = 1;
CREATE INDEX idx_template_body_part ON cf_report_template(applicable_body_part);

-- 2.2 诊断报告主表 - 核心聚合根
CREATE TABLE cf_diagnosis_report (
    id                    BIGINT          NOT NULL PRIMARY KEY,      -- 报告ID (R20260325001)
    exam_id               BIGINT          NOT NULL UNIQUE,           -- 一个检查对应一份报告
    patient_id            BIGINT          NOT NULL,
    template_id           BIGINT          NULL,

    -- 报告内容
    findings              NVARCHAR(MAX)   NULL,                      -- 检查所见（医生或AI填充）
    impression            NVARCHAR(MAX)   NULL,                      -- 诊断意见
    recommendation        NVARCHAR(MAX)   NULL,                      -- 临床建议

    -- ICD编码关联
    icd_codes             NVARCHAR(MAX)   NULL,                      -- JSON: [{code, description}]

    -- 状态流转
    status                VARCHAR(30)     NOT NULL DEFAULT 'draft',  -- draft/pending_approval/approved/published/archived

    -- 版本管理
    version               INT             NOT NULL DEFAULT 1,

    -- 作者信息
    author_id             BIGINT          NOT NULL,
    author_name           VARCHAR(100)    NULL,                      -- 冗余便于展示

    -- PDF输出
    pdf_path              VARCHAR(500)    NULL,                      -- PDF存储路径
    pdf_generated_at      DATETIME2       NULL,
    has_signature         BIT             NOT NULL DEFAULT 0,        -- 是否有电子签名

    -- 时间戳
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    published_at          DATETIME2       NULL,
    archived_at           DATETIME2       NULL,

    created_by            BIGINT          NOT NULL DEFAULT 0,
    updated_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    -- 约束
    CONSTRAINT fk_report_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT fk_report_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    CONSTRAINT fk_report_template FOREIGN KEY (template_id) REFERENCES cf_report_template(id),
    CONSTRAINT fk_report_author FOREIGN KEY (author_id) REFERENCES sys_user(user_id),
    CONSTRAINT ck_report_status CHECK (status IN ('draft', 'pending_approval', 'approved', 'published', 'archived'))
);

-- 索引
CREATE UNIQUE INDEX uidx_report_exam ON cf_diagnosis_report(exam_id) WHERE del_flag = '0';
CREATE INDEX idx_report_patient_id ON cf_diagnosis_report(patient_id) WHERE del_flag = '0';
CREATE INDEX idx_report_status ON cf_diagnosis_report(status);
CREATE INDEX idx_report_author_id ON cf_diagnosis_report(author_id);
CREATE INDEX idx_report_published_at ON cf_diagnosis_report(published_at) WHERE published_at IS NOT NULL;
CREATE INDEX idx_report_created_at ON cf_diagnosis_report(created_at);

-- 2.3 报告审核历史表 - 聚合内实体
CREATE TABLE cf_report_approval (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    report_id             BIGINT          NOT NULL,

    -- 审核操作
    action                VARCHAR(30)     NOT NULL,                  -- submitted/approved/rejected
    actor_id              BIGINT          NOT NULL,
    actor_name            VARCHAR(100)    NULL,
    comments              NVARCHAR(500)   NULL,

    -- 审核结果（仅对approved/rejected）
    approval_result       VARCHAR(20)     NULL,                      -- approved/rejected
    approval_reason       NVARCHAR(500)   NULL,

    -- 时间戳
    action_at             DATETIME2       NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_approval_report FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(id)
);

-- 索引
CREATE INDEX idx_approval_report_id ON cf_report_approval(report_id);
CREATE INDEX idx_approval_action ON cf_report_approval(action);
CREATE INDEX idx_approval_actor_id ON cf_report_approval(actor_id);
CREATE INDEX idx_approval_action_at ON cf_report_approval(action_at);

-- 2.4 报告版本历史表 - 聚合内实体
CREATE TABLE cf_report_version (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    report_id             BIGINT          NOT NULL,
    version               INT             NOT NULL,

    -- 内容快照
    findings              NVARCHAR(MAX)   NULL,
    impression            NVARCHAR(MAX)   NULL,
    recommendation        NVARCHAR(MAX)   NULL,

    -- 修改信息
    modified_by           BIGINT          NOT NULL,
    modified_by_name      VARCHAR(100)    NULL,
    modified_at           DATETIME2       NOT NULL DEFAULT GETDATE(),
    modification_reason   NVARCHAR(500)   NULL,
    modification_type     VARCHAR(30)     NOT NULL,                  -- auto_fill/manual_edit/amendment

    -- PDF快照
    pdf_path              VARCHAR(500)    NULL,

    CONSTRAINT fk_version_report FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(id),
    CONSTRAINT ck_modification_type CHECK (modification_type IN ('auto_fill', 'manual_edit', 'amendment')),
    CONSTRAINT uq_version UNIQUE (report_id, version)
);

-- 索引
CREATE INDEX idx_version_report_id ON cf_report_version(report_id);
CREATE INDEX idx_version_modified_by ON cf_report_version(modified_by);

-- 2.5 报告草稿备份表（可选，用于自动保存）
CREATE TABLE cf_report_draft_backup (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    report_id             BIGINT          NOT NULL,

    findings              NVARCHAR(MAX)   NULL,
    impression            NVARCHAR(MAX)   NULL,
    recommendation        NVARCHAR(MAX)   NULL,

    backup_at             DATETIME2       NOT NULL DEFAULT GETDATE(),
    sequence_number       INT             NOT NULL,

    CONSTRAINT fk_backup_report FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(id)
);

-- 索引
CREATE INDEX idx_backup_report_id ON cf_report_draft_backup(report_id);
CREATE INDEX idx_backup_backup_at ON cf_report_draft_backup(backup_at);

-- ============ V1.2.0: 诊断报告限界上下文初始化 (完成) ============
```

---

## 回滚脚本

```sql
-- ============ U1.2.0: 诊断报告限界上下文回滚 ============

DROP TABLE IF EXISTS cf_report_draft_backup;
DROP TABLE IF EXISTS cf_report_version;
DROP TABLE IF EXISTS cf_report_approval;
DROP TABLE IF EXISTS cf_diagnosis_report;
DROP TABLE IF EXISTS cf_report_template;

-- ============ U1.2.0: 诊断报告限界上下文回滚 (完成) ============
```

---

## 初始化数据

```sql
-- ============ V1.2.1: 诊断报告测试数据 ============

-- 插入报告模板
INSERT INTO cf_report_template (
    id, template_name, template_code, applicable_modality, applicable_body_part, is_active
) VALUES
    (1000001, '胸部CT报告模板', 'CHEST_CT_001', 'CT', '胸部', 1),
    (1000002, '腹部MRI报告模板', 'ABDOMEN_MRI_001', 'MRI', '腹部', 1),
    (1000003, '胸部X光报告模板', 'CHEST_DR_001', 'DR', '胸部', 1);

-- 插入测试报告
INSERT INTO cf_diagnosis_report (
    id, exam_id, patient_id, template_id, status, author_id, created_by, updated_by
) VALUES
    (2000001, 100001, 1000001, 1000001, 'draft', 1, 1, 1),
    (2000002, 100002, 1000002, 1000002, 'pending_approval', 1, 1, 1),
    (2000003, 100003, 1000003, 1000003, 'published', 1, 1, 1);

-- ============ V1.2.1: 诊断报告测试数据 (完成) ============
```
