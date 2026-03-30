# 数据库迁移脚本 - 其他核心限界上下文 (Supporting & Generic Contexts)

> Flyway迁移文件：V1.3.0__supporting_contexts.sql
> 执行时间：10-15秒
> 影响表数：7个

---

```sql
-- ============ V1.3.0: 支撑与通用限界上下文初始化 ============

-- ===== 3. 临床决策支持限界上下文 (Clinical Decision Support BC) =====

-- 3.1 AI解读表 - 核心聚合根
CREATE TABLE cf_ai_interpret (
    id                    BIGINT          NOT NULL PRIMARY KEY,      -- 解读ID
    image_id              BIGINT          NOT NULL,
    exam_id               BIGINT          NOT NULL,
    patient_id            BIGINT          NOT NULL,

    -- 分析元数据
    modality              VARCHAR(10)     NOT NULL,
    analysis_started_at   DATETIME2       NOT NULL,
    analysis_completed_at DATETIME2       NULL,
    duration_ms           INT             NULL,

    -- 模型结果
    model_results         NVARCHAR(MAX)   NOT NULL,                  -- JSON: [{modelId, modelName, findings[], confidence}]
    ensemble_result       NVARCHAR(MAX)   NULL,                      -- JSON: {fusedFindings, weights, strategy}

    -- 置信度
    overall_confidence    DECIMAL(5,2)    NULL,                      -- 0-100，融合结果的总体置信度
    confidence_level      VARCHAR(10)     NULL,                      -- HIGH/MEDIUM/LOW
    uncertainty_bounds    NVARCHAR(MAX)   NULL,                      -- JSON: {lower, upper, percentile: 90}

    -- 医生反馈
    doctor_feedback       NVARCHAR(MAX)   NULL,                      -- JSON: {feedbackType: accept/reject/modify, comments, timestamp}
    feedback_at           DATETIME2       NULL,

    -- 用于模型优化的标签
    ground_truth          NVARCHAR(MAX)   NULL,                      -- JSON: 金标准诊断（用于模型评估）

    -- 版本与审计
    version               INT             NOT NULL DEFAULT 1,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL DEFAULT 0,
    updated_by            BIGINT          NOT NULL DEFAULT 0,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_interpret_image FOREIGN KEY (image_id) REFERENCES cf_image(id),
    CONSTRAINT fk_interpret_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT fk_interpret_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id)
);

-- 索引
CREATE INDEX idx_interpret_exam_id ON cf_ai_interpret(exam_id) WHERE del_flag = '0';
CREATE INDEX idx_interpret_image_id ON cf_ai_interpret(image_id) WHERE del_flag = '0';
CREATE INDEX idx_interpret_confidence ON cf_ai_interpret(overall_confidence);
CREATE INDEX idx_interpret_completed_at ON cf_ai_interpret(analysis_completed_at);

-- ===== 4. 存储管理限界上下文 (Storage Management BC) =====

-- 4.1 存储层级迁移记录表
CREATE TABLE cf_storage_migration (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    image_id              BIGINT          NOT NULL,

    -- 迁移信息
    from_tier             VARCHAR(10)     NULL,                      -- hot/warm/cold
    to_tier               VARCHAR(10)     NOT NULL,
    from_path             VARCHAR(500)    NULL,
    to_path               VARCHAR(500)    NOT NULL,

    -- 存储统计
    file_size_bytes       BIGINT          NOT NULL,
    compress_ratio        DECIMAL(5,4)    NULL,                      -- 0-1，压缩比

    -- 迁移触发信息
    trigger_reason        VARCHAR(100)    NOT NULL,                  -- scheduled/watermark/access_preheat/capacity_forecast
    trigger_timestamp     DATETIME2       NOT NULL,

    -- 迁移过程
    status                VARCHAR(20)     NOT NULL DEFAULT 'in_progress',  -- in_progress/completed/failed/rollback
    duration_ms           INT             NULL,

    error_message         NVARCHAR(500)   NULL,                      -- 失败原因

    -- 审计
    triggered_by_agent    VARCHAR(100)    NOT NULL DEFAULT 'StorageSchedulerAgent',
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    completed_at          DATETIME2       NULL,
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_migration_image FOREIGN KEY (image_id) REFERENCES cf_image(id),
    CONSTRAINT ck_tier CHECK (to_tier IN ('hot', 'warm', 'cold')),
    CONSTRAINT ck_migration_status CHECK (status IN ('in_progress', 'completed', 'failed', 'rollback'))
);

-- 索引（关键的迁移查询优化）
CREATE INDEX idx_migration_image_id ON cf_storage_migration(image_id);
CREATE INDEX idx_migration_status ON cf_storage_migration(status);
CREATE INDEX idx_migration_created_at ON cf_storage_migration(created_at);
CREATE INDEX idx_migration_to_tier ON cf_storage_migration(to_tier, status);

-- 4.2 存储使用统计表（汇总统计）
CREATE TABLE cf_storage_statistics (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    statistic_date        DATE            NOT NULL UNIQUE,

    hot_tier_usage_bytes  BIGINT          NOT NULL DEFAULT 0,
    warm_tier_usage_bytes BIGINT          NOT NULL DEFAULT 0,
    cold_tier_usage_bytes BIGINT          NOT NULL DEFAULT 0,
    total_usage_bytes     BIGINT          NOT NULL DEFAULT 0,

    image_count_hot       INT             NOT NULL DEFAULT 0,
    image_count_warm      INT             NOT NULL DEFAULT 0,
    image_count_cold      INT             NOT NULL DEFAULT 0,

    migration_count       INT             NOT NULL DEFAULT 0,

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_stats_date ON cf_storage_statistics(statistic_date);

-- ===== 5. 工作流编排限界上下文 (Workflow Orchestration BC) =====

-- 5.1 工作流状态表
CREATE TABLE cf_workflow_state (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    exam_id               BIGINT          NOT NULL,
    workflow_id           VARCHAR(64)     NOT NULL UNIQUE,           -- 工作流实例ID (UUID)

    -- 状态管理
    current_state         VARCHAR(50)     NOT NULL,                  -- pending/in_progress/completed等
    previous_state        VARCHAR(50)     NULL,
    state_entered_at      DATETIME2       NOT NULL DEFAULT GETDATE(),

    -- 控制信息
    triggered_by_event    VARCHAR(100)    NOT NULL,                  -- 触发该状态的事件类型
    triggered_by_agent    VARCHAR(100)    NOT NULL,                  -- 处理该状态的Agent

    -- 工作流上下文（快照）
    context_data          NVARCHAR(MAX)   NULL,                      -- JSON: 工作流上下文快照

    -- SLA管理
    sla_type              VARCHAR(20)     NOT NULL,                  -- emergency/normal/complex/checkup
    sla_deadline          DATETIME2       NULL,
    sla_warned            BIT             NOT NULL DEFAULT 0,        -- 是否已发送SLA预警

    -- 重试机制
    retry_count           INT             NOT NULL DEFAULT 0,
    max_retries           INT             NOT NULL DEFAULT 3,

    -- 版本
    version               INT             NOT NULL DEFAULT 1,

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_workflow_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id)
);

-- 索引
CREATE UNIQUE INDEX uidx_workflow_id ON cf_workflow_state(workflow_id);
CREATE INDEX idx_workflow_exam_id ON cf_workflow_state(exam_id);
CREATE INDEX idx_workflow_current_state ON cf_workflow_state(current_state);
CREATE INDEX idx_workflow_sla_deadline ON cf_workflow_state(sla_deadline) WHERE sla_deadline IS NOT NULL;

-- 5.2 工作流事件日志表（追踪每个事件）
CREATE TABLE cf_workflow_event_log (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    workflow_id           VARCHAR(64)     NOT NULL,
    exam_id               BIGINT          NOT NULL,

    -- 事件信息
    event_type            VARCHAR(100)    NOT NULL,
    event_timestamp       DATETIME2       NOT NULL,
    event_data            NVARCHAR(MAX)   NULL,                      -- JSON

    -- 关联信息
    correlation_id        VARCHAR(64)     NULL,
    trace_id              VARCHAR(64)     NULL,                      -- 分布式追踪ID

    -- 处理状态
    status                VARCHAR(20)     NOT NULL DEFAULT 'pending',  -- pending/processing/completed/failed
    processed_at          DATETIME2       NULL,
    error_message         NVARCHAR(500)   NULL,

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_event_workflow FOREIGN KEY (workflow_id) REFERENCES cf_workflow_state(workflow_id),
    CONSTRAINT fk_event_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id)
);

-- 索引
CREATE INDEX idx_event_workflow_id ON cf_workflow_event_log(workflow_id);
CREATE INDEX idx_event_type ON cf_workflow_event_log(event_type);
CREATE INDEX idx_event_timestamp ON cf_workflow_event_log(event_timestamp);

-- ===== 6. 安全审计限界上下文 (Security Audit BC) =====

-- 6.1 审计日志表
CREATE TABLE cf_audit_log (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    audit_id              VARCHAR(64)     NOT NULL UNIQUE,           -- 审计事件唯一ID (UUID)

    -- 操作者信息
    user_id               BIGINT          NULL,
    user_name             VARCHAR(100)    NULL,
    user_role             VARCHAR(100)    NULL,

    -- 资源信息
    patient_id            BIGINT          NULL,
    exam_id               BIGINT          NULL,
    image_id              BIGINT          NULL,
    report_id             BIGINT          NULL,
    resource_type         VARCHAR(50)     NOT NULL,                  -- IMAGE/REPORT/PATIENT/USER/SYSTEM
    resource_id           VARCHAR(100)    NULL,

    -- 操作信息
    action_type           VARCHAR(50)     NOT NULL,                  -- VIEW/DOWNLOAD/UPLOAD/SHARE/DELETE/LOGIN/LOGOUT/MODIFY
    action_detail         NVARCHAR(MAX)   NULL,                      -- JSON: 操作详情

    -- 网络信息
    ip_address            VARCHAR(50)     NOT NULL,
    user_agent            NVARCHAR(500)   NULL,

    -- 风险评估
    risk_level            VARCHAR(10)     NOT NULL DEFAULT 'LOW',    -- LOW/MEDIUM/HIGH/CRITICAL
    risk_rules_triggered  NVARCHAR(500)   NULL,                      -- JSON: [AUD-001, AUD-003, ...]
    is_blocked            BIT             NOT NULL DEFAULT 0,        -- 是否被阻断

    -- 详细信息
    detail                NVARCHAR(MAX)   NULL,                      -- JSON: {requestId, responseTime, status}

    -- 时间戳
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE()

    -- 审计日志永不删除
    -- 约束：
    -- CONSTRAINT fk_audit_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    -- CONSTRAINT fk_audit_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    -- CONSTRAINT fk_audit_image FOREIGN KEY (image_id) REFERENCES cf_image(id)
    -- 这些FK注释以避免级联删除导致审计记录丢失
);

-- 索引（审计查询优化，永不删除所以不用del_flag）
CREATE UNIQUE INDEX uidx_audit_id ON cf_audit_log(audit_id);
CREATE INDEX idx_audit_user_id ON cf_audit_log(user_id);
CREATE INDEX idx_audit_patient_id ON cf_audit_log(patient_id);
CREATE INDEX idx_audit_exam_id ON cf_audit_log(exam_id);
CREATE INDEX idx_audit_action_type ON cf_audit_log(action_type);
CREATE INDEX idx_audit_risk_level ON cf_audit_log(risk_level) WHERE risk_level IN ('HIGH', 'CRITICAL');
CREATE INDEX idx_audit_created_at ON cf_audit_log(created_at);

-- 6.2 异常行为检测规则表
CREATE TABLE cf_security_rule (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    rule_id               VARCHAR(20)     NOT NULL UNIQUE,           -- AUD-001, AUD-002等
    rule_name             VARCHAR(100)    NOT NULL,
    description           NVARCHAR(255)   NULL,

    -- 规则定义
    condition_expression  NVARCHAR(MAX)   NOT NULL,                  -- 规则条件（自定义DSL或SQL）
    action_on_trigger     VARCHAR(100)    NOT NULL,                  -- alert/block/quarantine等
    severity              VARCHAR(20)     NOT NULL,                  -- INFO/WARNING/CRITICAL

    is_enabled            BIT             NOT NULL DEFAULT 1,
    version               INT             NOT NULL DEFAULT 1,

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE()
);

-- ===== 7. 权限管理限界上下文 (Permission Management BC) - 已由RuoYi提供 =====

-- 使用现有的 sys_user, sys_role, sys_permission 表
-- 此处仅定义扩展字段（如DICOM访问权限）

CREATE TABLE IF NOT EXISTS cf_user_permission_extension (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    user_id               BIGINT          NOT NULL,

    -- DICOM特定权限
    can_access_dicom_scp  BIT             NOT NULL DEFAULT 0,        -- 能否接收DICOM
    can_view_all_images   BIT             NOT NULL DEFAULT 0,        -- 能否查看所有影像
    can_edit_report       BIT             NOT NULL DEFAULT 0,        -- 能否编辑报告
    can_approve_report    BIT             NOT NULL DEFAULT 0,        -- 能否审核报告
    can_view_ai_results   BIT             NOT NULL DEFAULT 0,        -- 能否查看AI结果

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_ext_user_id ON cf_user_permission_extension(user_id);

-- ===== 8. 系统监控限界上下文 (System Monitoring BC) =====

-- 系统性能指标表
CREATE TABLE cf_system_metrics (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    metric_timestamp      DATETIME2       NOT NULL,

    -- 基础设施指标
    cpu_usage_percent     DECIMAL(5,2)    NULL,
    memory_usage_percent  DECIMAL(5,2)    NULL,
    disk_usage_percent    DECIMAL(5,2)    NULL,

    -- 应用指标
    api_response_time_p95 INT             NULL,                      -- 毫秒
    api_response_time_p99 INT             NULL,
    error_rate_percent    DECIMAL(5,2)    NULL,

    -- DICOM指标
    dicom_ingest_rate     INT             NULL,                      -- 帧/秒
    dicom_success_rate    DECIMAL(5,2)    NULL,

    -- 存储指标
    hot_tier_usage_percent DECIMAL(5,2)   NULL,
    warm_tier_usage_percent DECIMAL(5,2)  NULL,

    -- 报告指标
    pending_reports_count INT             NULL,
    overdue_reports_count INT             NULL,

    created_at            DATETIME2       NOT NULL DEFAULT GETDATE()
);

CREATE INDEX idx_metrics_timestamp ON cf_system_metrics(metric_timestamp);

-- ============ V1.3.0: 支撑与通用限界上下文初始化 (完成) ============
```

---

## 回滚脚本

```sql
-- ============ U1.3.0: 支撑与通用限界上下文回滚 ============

DROP TABLE IF EXISTS cf_system_metrics;
DROP TABLE IF EXISTS cf_user_permission_extension;
DROP TABLE IF EXISTS cf_security_rule;
DROP TABLE IF EXISTS cf_audit_log;
DROP TABLE IF EXISTS cf_workflow_event_log;
DROP TABLE IF EXISTS cf_workflow_state;
DROP TABLE IF EXISTS cf_storage_statistics;
DROP TABLE IF EXISTS cf_storage_migration;
DROP TABLE IF EXISTS cf_ai_interpret;

-- ============ U1.3.0: 支撑与通用限界上下文回滚 (完成) ============
```

---

## 初始化数据

```sql
-- ============ V1.3.1: 支撑与通用限界上下文测试数据 ============

-- 插入安全规则
INSERT INTO cf_security_rule (id, rule_id, rule_name, description, condition_expression, action_on_trigger, severity, is_enabled)
VALUES
    (5000001, 'AUD-001', '批量下载异常检测', '单用户单分钟下载超过50张', 'downloads_per_min > 50', 'block', 'CRITICAL', 1),
    (5000002, 'AUD-002', '异常访问时段检测', '非工作时间(22-06)访问超100次', 'off_hours_access_count > 100', 'alert', 'WARNING', 1),
    (5000003, 'AUD-003', '权限越界检测', '访问未授权患者影像', 'unauthorized_access_attempt', 'block', 'CRITICAL', 1),
    (5000004, 'AUD-006', '暴力破解防护', '同一分享码输入错误超5次', 'failed_attempts > 5', 'block', 'HIGH', 1);

-- 插入工作流初始状态
INSERT INTO cf_workflow_state (id, exam_id, workflow_id, current_state, triggered_by_event, triggered_by_agent, sla_type, created_by)
VALUES
    (4000001, 100001, '550e8400-e29b-41d4-a716-446655440001', 'pending', 'ExaminationRequested', 'WorkflowOrchestrationAgent', 'normal', 1),
    (4000002, 100002, '550e8400-e29b-41d4-a716-446655440002', 'images_received', 'ImagesReceived', 'DicomIngestAgent', 'normal', 1),
    (4000003, 100003, '550e8400-e29b-41d4-a716-446655440003', 'report_approved', 'ReportApproved', 'ReportApprovalService', 'normal', 1);

-- ============ V1.3.1: 支撑与通用限界上下文测试数据 (完成) ============
```
