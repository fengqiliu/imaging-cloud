# 数据库迁移脚本 - 患者信息限界上下文 (Patient Information BC)

> Flyway迁移文件：V1.0.1__patient_information_context.sql
> 执行时间：3-5秒
> 影响表数：2个

---

## 1. 患者主表

```sql
-- ============ V1.0.1: 患者信息限界上下文初始化 ============

-- 1.1 患者主表 (cf_patient)
CREATE TABLE cf_patient (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    patient_no            VARCHAR(50)     NOT NULL UNIQUE,
    patient_name          VARCHAR(100)    NOT NULL,
    patient_id_number     VARCHAR(20)     NULL,              -- 身份证号（加密存储）

    -- 基本属性
    gender                CHAR(1)         NOT NULL,          -- M/F/U
    birth_date            DATE            NOT NULL,
    age                   INT             GENERATED ALWAYS AS (YEAR(GETDATE()) - YEAR(birth_date)) STORED,

    -- 联系方式
    phone                 VARCHAR(20)     NULL,
    email                 VARCHAR(100)    NULL,
    address               NVARCHAR(255)   NULL,

    -- 病历相关（JSON格式便于扩展）
    medical_history       NVARCHAR(MAX)   NULL,              -- JSON: [{date, diagnosis, description}]
    allergy_history       NVARCHAR(MAX)   NULL,              -- JSON: [{allergen, severity, reaction}]
    medication_history    NVARCHAR(MAX)   NULL,              -- JSON: [{medication, dosage, frequency}]
    family_history        NVARCHAR(MAX)   NULL,              -- JSON: [{relation, condition}]

    -- 状态
    status                VARCHAR(20)     NOT NULL DEFAULT 'active',  -- active/inactive/merged
    is_vip                BIT             NOT NULL DEFAULT 0,

    -- 版本与审计
    version               INT             NOT NULL DEFAULT 1,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL DEFAULT 0,
    updated_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    -- 约束
    CONSTRAINT ck_gender CHECK (gender IN ('M', 'F', 'U')),
    CONSTRAINT ck_status CHECK (status IN ('active', 'inactive', 'merged'))
);

-- 索引
CREATE UNIQUE INDEX uidx_patient_no ON cf_patient(patient_no) WHERE del_flag = '0';
CREATE UNIQUE INDEX uidx_patient_id_number ON cf_patient(patient_id_number) WHERE del_flag = '0' AND patient_id_number IS NOT NULL;
CREATE INDEX idx_patient_name ON cf_patient(patient_name) WHERE del_flag = '0';
CREATE INDEX idx_patient_status ON cf_patient(status);
CREATE INDEX idx_patient_created_at ON cf_patient(created_at);

-- 权限注释
EXEC sp_addextendedproperty @name = N'MS_Description',
    @value = N'患者主表 - 患者信息BC的聚合根',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE', @level1name = N'cf_patient';

-- 1.2 过敏信息表 (cf_patient_allergy)
CREATE TABLE cf_patient_allergy (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    patient_id            BIGINT          NOT NULL,

    allergen_name         VARCHAR(100)    NOT NULL,
    severity              VARCHAR(20)     NOT NULL,          -- mild/moderate/severe
    reaction              VARCHAR(255)    NULL,              -- 过敏反应描述

    recorded_by           BIGINT          NOT NULL,
    recorded_at           DATETIME2       NOT NULL DEFAULT GETDATE(),

    -- 状态（过敏史不删除，仅标记处理状态）
    status                VARCHAR(20)     NOT NULL DEFAULT 'active',  -- active/resolved/archived
    resolved_at           DATETIME2       NULL,
    resolved_by           BIGINT          NULL,

    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_allergy_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    CONSTRAINT ck_severity CHECK (severity IN ('mild', 'moderate', 'severe')),
    CONSTRAINT ck_allergy_status CHECK (status IN ('active', 'resolved', 'archived'))
);

-- 索引
CREATE INDEX idx_allergy_patient_id ON cf_patient_allergy(patient_id) WHERE del_flag = '0';
CREATE INDEX idx_allergy_severity ON cf_patient_allergy(severity);
CREATE INDEX idx_allergy_recorded_at ON cf_patient_allergy(recorded_at);

-- 1.3 用药史表 (cf_patient_medication)
CREATE TABLE cf_patient_medication (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    patient_id            BIGINT          NOT NULL,

    medication_name       VARCHAR(100)    NOT NULL,
    dosage                VARCHAR(50)     NOT NULL,          -- 例：500mg
    frequency             VARCHAR(50)     NOT NULL,          -- 例：twice a day
    indication            VARCHAR(255)    NULL,              -- 适应症

    start_date            DATE            NOT NULL,
    end_date              DATE            NULL,              -- NULL表示在用

    recorded_by           BIGINT          NOT NULL,
    recorded_at           DATETIME2       NOT NULL DEFAULT GETDATE(),

    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_medication_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id)
);

-- 索引
CREATE INDEX idx_medication_patient_id ON cf_patient_medication(patient_id) WHERE del_flag = '0';
CREATE INDEX idx_medication_end_date ON cf_patient_medication(end_date);

-- 1.4 患者合并记录表 (cf_patient_merge_log)
-- 用于追踪患者去重合并
CREATE TABLE cf_patient_merge_log (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    source_patient_id     BIGINT          NOT NULL,          -- 被合并的患者
    target_patient_id     BIGINT          NOT NULL,          -- 保留的患者

    merge_reason          VARCHAR(255)    NOT NULL,
    merge_evidence        NVARCHAR(MAX)   NULL,              -- JSON：匹配证据

    merged_by             BIGINT          NOT NULL,
    merged_at             DATETIME2       NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_merge_source FOREIGN KEY (source_patient_id) REFERENCES cf_patient(id),
    CONSTRAINT fk_merge_target FOREIGN KEY (target_patient_id) REFERENCES cf_patient(id)
);

-- 索引
CREATE INDEX idx_merge_target ON cf_patient_merge_log(target_patient_id);
CREATE INDEX idx_merge_merged_at ON cf_patient_merge_log(merged_at);

-- ============ V1.0.1: 患者信息限界上下文初始化 (完成) ============
```

---

## 回滚脚本（Undo）

```sql
-- ============ U1.0.1: 患者信息限界上下文回滚 ============

DROP TABLE IF EXISTS cf_patient_merge_log;
DROP TABLE IF EXISTS cf_patient_medication;
DROP TABLE IF EXISTS cf_patient_allergy;
DROP TABLE IF EXISTS cf_patient;

-- ============ U1.0.1: 患者信息限界上下文回滚 (完成) ============
```

---

## 初始化数据（可选）

```sql
-- ============ V1.0.2: 患者信息测试数据 ============

-- 插入测试患者
INSERT INTO cf_patient (
    id, patient_no, patient_name, patient_id_number, gender, birth_date,
    phone, email, status, created_by, updated_by
) VALUES
    (1000001, 'HN20260325001', '张三', '110101199001011234', 'M', '1990-01-01', '13800138000', 'zs@test.com', 'active', 1, 1),
    (1000002, 'HN20260325002', '李四', '110101199501011234', 'F', '1995-01-01', '13800138001', 'ls@test.com', 'active', 1, 1),
    (1000003, 'HN20260325003', '王五', '110101198501011234', 'M', '1985-01-01', '13800138002', 'ww@test.com', 'active', 1, 1);

-- 插入过敏史
INSERT INTO cf_patient_allergy (id, patient_id, allergen_name, severity, reaction, recorded_by, recorded_at)
VALUES
    (2000001, 1000001, '青霉素', 'severe', '皮疹、呼吸困难', 1, GETDATE()),
    (2000002, 1000001, '海鲜', 'moderate', '腹泻、腹痛', 1, GETDATE()),
    (2000003, 1000002, '鸡蛋', 'mild', '瘙痒', 1, GETDATE());

-- ============ V1.0.2: 患者信息测试数据 (完成) ============
```
