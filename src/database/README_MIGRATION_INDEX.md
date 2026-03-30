# 数据库迁移文件索引

> 所有DDD限界上下文迁移脚本的完整索引和导航

---

## 📂 文件组织

```
src/database/
├── migrations/                                    # Flyway迁移脚本目录
│   ├── V1.0.1__patient_information_context.sql   # 患者信息BC
│   ├── V1.1.0__image_management_context.sql      # 影像管理BC
│   ├── V1.2.0__diagnostic_report_context.sql     # 诊断报告BC
│   ├── V1.3.0__supporting_contexts.sql           # 支撑BC（AI/存储/工作流/审计等）
│   ├── V1.4.0__migration_verification.sql        # 验证脚本
│   │
│   └── undo/                                      # 回滚脚本（可选）
│       ├── U1.0.1__patient_information_context.sql
│       ├── U1.1.0__image_management_context.sql
│       ├── U1.2.0__diagnostic_report_context.sql
│       └── U1.3.0__supporting_contexts.sql
│
├── init_db.sql                                     # 完整的数据库初始化脚本（现有）
├── README_MIGRATION.md                             # 完整的迁移管理指南
└── QUICK_START_MIGRATION.md                        # 快速启动指南
```

---

## 🗂️ 限界上下文迁移索引

### 1️⃣ 患者信息限界上下文 (V1.0.1)

**文件**：`V1.0.1__patient_information_context.sql`

**创建的表**（4个）：
- `cf_patient` - 患者聚合根
- `cf_patient_allergy` - 过敏信息
- `cf_patient_medication` - 用药历史
- `cf_patient_merge_log` - 患者合并追踪

**关键字段**：
- `patient_no` (患者号，唯一)
- `patient_name` (姓名)
- `gender` (性别)
- `birth_date` (出生日期)
- `medical_history` (JSON格式病历)

**索引数**：7个
**约束**：主键、唯一键、检查约束

**执行时间**：3-5秒
**大小**：~12KB

**回滚脚本**：`U1.0.1__patient_information_context.sql`

---

### 2️⃣ 影像管理限界上下文 (V1.1.0)

**文件**：`V1.1.0__image_management_context.sql`

**创建的表**（4个）：
- `cf_examination` - 检查聚合根
- `cf_series` - 序列表
- `cf_image` - 影像聚合根（最核心）
- `cf_qc_issue` - 质控问题

**关键字段**：
- `study_instance_uid` (检查UID，全球唯一)
- `sop_instance_uid` (影像UID，全球唯一)
- `modality` (CT/MRI/DR等)
- `qc_score` (质控评分)
- `storage_tier` (hot/warm/cold分级)

**索引数**：12个（包括QC和存储迁移优化）
**约束**：主键、外键、唯一键、检查约束

**执行时间**：5-10秒
**大小**：~18KB

**回滚脚本**：`U1.1.0__image_management_context.sql`

---

### 3️⃣ 诊断报告限界上下文 (V1.2.0)

**文件**：`V1.2.0__diagnostic_report_context.sql`

**创建的表**（5个）：
- `cf_report_template` - 报告模板
- `cf_diagnosis_report` - 报告聚合根
- `cf_report_approval` - 审核历史（聚合内实体）
- `cf_report_version` - 版本历史（聚合内实体）
- `cf_report_draft_backup` - 草稿自动备份

**关键字段**：
- `exam_id` (关联检查，唯一)
- `status` (draft/pending/approved/published/archived)
- `findings` (检查所见)
- `impression` (诊断意见)
- `version` (版本号)

**索引数**：8个
**约束**：主键、外键、唯一键、检查约束

**执行时间**：5-8秒
**大小**：~15KB

**回滚脚本**：`U1.2.0__diagnostic_report_context.sql`

---

### 4️⃣ 支撑与通用限界上下文 (V1.3.0)

**文件**：`V1.3.0__supporting_contexts.sql`

**创建的表**（8个）：

#### 临床决策支持BC：
- `cf_ai_interpret` - AI诊断解读聚合根

#### 存储管理BC：
- `cf_storage_migration` - 存储迁移记录
- `cf_storage_statistics` - 存储统计汇总

#### 工作流编排BC：
- `cf_workflow_state` - 工作流状态聚合根
- `cf_workflow_event_log` - 工作流事件日志

#### 安全审计BC：
- `cf_audit_log` - 审计日志（永不删除）
- `cf_security_rule` - 安全规则（AUD-001等）

#### 权限管理BC扩展：
- `cf_user_permission_extension` - DICOM权限扩展

#### 系统监控BC：
- `cf_system_metrics` - 系统性能指标

**关键字段汇总**：
- `cf_ai_interpret`: `overall_confidence`, `ensemble_result`, `model_results`
- `cf_storage_migration`: `from_tier`, `to_tier`, `trigger_reason`, `status`
- `cf_workflow_state`: `current_state`, `sla_deadline`, `sla_warned`
- `cf_audit_log`: `risk_level`, `action_type`, `resource_type` （永不删除）
- `cf_security_rule`: `rule_id`, `condition_expression`, `action_on_trigger`

**索引数**：15个
**约束**：主键、外键、唯一键、检查约束

**执行时间**：10-15秒
**大小**：~25KB

**回滚脚本**：`U1.3.0__supporting_contexts.sql`

---

### 5️⃣ 验证脚本 (V1.4.0)

**文件**：`V1.4.0__migration_verification.sql`

**功能**（不创建表）：
- 验证所有表已创建
- 验证所有主键约束
- 验证所有外键约束
- 验证所有索引
- 统计表空间使用
- 初始化数据验证
- 性能基准建立

**包含的检查**：
```sql
✓ 21个表全部创建检查
✓ 主键约束验证
✓ 外键约束验证
✓ 索引完整性检查
✓ 数据类型验证
✓ 表空间统计
✓ 初始数据验证
✓ 碎片检查和优化建议
```

**执行时间**：2-3秒
**大小**：~12KB

**无回滚脚本**（仅验证，无数据变更）

---

## 📊 完整统计表

| 限界上下文 | 版本 | 表数 | 字段数 | 索引数 | 大小 | 执行时间 | 状态 |
|----------|------|------|--------|--------|------|---------|------|
| 患者信息 | V1.0.1 | 4 | ~35 | 7 | 12KB | 3-5s | ✓ 完成 |
| 影像管理 | V1.1.0 | 4 | ~48 | 12 | 18KB | 5-10s | ✓ 完成 |
| 诊断报告 | V1.2.0 | 5 | ~38 | 8 | 15KB | 5-8s | ✓ 完成 |
| 支撑BC | V1.3.0 | 8 | ~52 | 15 | 25KB | 10-15s | ✓ 完成 |
| 验证脚本 | V1.4.0 | - | - | - | 12KB | 2-3s | ✓ 完成 |
| **总计** | - | **21** | **~173** | **>42** | **~82KB** | **25-41s** | ✓ |

---

## 🔗 相关文档导航

| 文档 | 位置 | 用途 |
|-----|------|------|
| DDD完整设计 | `docx/DDD-领域驱动设计分析.md` | 理解系统的DDD模型设计 |
| DDD快速参考 | `docx/DDD-快速参考指南.md` | 日常开发中查询DDD术语和模式 |
| 迁移完整指南 | `README_MIGRATION.md` | 详细的迁移执行和管理说明 |
| 快速启动指南 | `QUICK_START_MIGRATION.md` | 5分钟快速启动迁移 |
| **本文档** | `README_MIGRATION_INDEX.md` | 迁移文件索引和导航 |

---

## 🚀 快速导航

### 我想...

- **快速启动迁移** → 阅读 `QUICK_START_MIGRATION.md` 的"快速启动"章节
- **理解迁移结构** → 本文档中的"完整统计表"和"限界上下文迁移索引"
- **了解某个BC的表结构** → 对应的BC迁移脚本（V1.x.x__*.sql）
- **解决迁移问题** → `README_MIGRATION.md` 的"故障排查"章节
- **实施回滚** → `README_MIGRATION.md` 的"回滚策略"章节
- **了解DDD概念** → `docx/DDD-领域驱动设计分析.md`
- **查询关键数据** → 本文档中的"数据库初始化"部分

---

## 💾 初始化数据

### V1.0.1 - 患者信息BC 初始数据

```sql
-- 3个测试患者
INSERT INTO cf_patient (id, patient_no, patient_name, gender, birth_date) VALUES
    (1000001, 'HN20260325001', '张三', 'M', '1990-01-01'),
    (1000002, 'HN20260325002', '李四', 'F', '1995-01-01'),
    (1000003, 'HN20260325003', '王五', 'M', '1985-01-01');

-- 3条过敏记录
INSERT INTO cf_patient_allergy (id, patient_id, allergen_name, severity) VALUES
    (2000001, 1000001, '青霉素', 'severe'),
    (2000002, 1000001, '海鲜', 'moderate'),
    (2000003, 1000002, '鸡蛋', 'mild');
```

### V1.1.0 - 影像管理BC 初始数据

```sql
-- 3个测试检查
INSERT INTO cf_examination (id, patient_id, study_instance_uid, exam_name, modality, body_part) VALUES
    (100001, 1000001, '1.2.840.10008.5.1.4.1.1.2.1', '胸部CT平扫', 'CT', '胸部'),
    (100002, 1000002, '1.2.840.10008.5.1.4.1.1.2.2', '腹部MRI扫描', 'MRI', '腹部'),
    (100003, 1000003, '1.2.840.10008.5.1.4.1.1.2.3', '胸部X光拍片', 'DR', '胸部');

-- 3条影像记录
INSERT INTO cf_image (id, exam_id, patient_id, sop_instance_uid, modality, file_path) VALUES
    (300001, 100001, 1000001, '1.2.840.10008.5.1.4.1.1.2.1.1', 'CT', '/images/2026-03-25/img_001.dcm'),
    (300002, 100002, 1000002, '1.2.840.10008.5.1.4.1.1.2.2.1', 'MRI', '/images/2026-03-25/img_002.dcm'),
    (300003, 100003, 1000003, '1.2.840.10008.5.1.4.1.1.2.3.1', 'DR', '/images/2026-03-25/img_003.dcm');
```

### V1.2.0 - 诊断报告BC 初始数据

```sql
-- 3个报告模板
INSERT INTO cf_report_template (id, template_name, template_code, applicable_modality) VALUES
    (1000001, '胸部CT报告模板', 'CHEST_CT_001', 'CT'),
    (1000002, '腹部MRI报告模板', 'ABDOMEN_MRI_001', 'MRI'),
    (1000003, '胸部X光报告模板', 'CHEST_DR_001', 'DR');

-- 3个报告记录
INSERT INTO cf_diagnosis_report (id, exam_id, patient_id, status, author_id) VALUES
    (2000001, 100001, 1000001, 'draft', 1),
    (2000002, 100002, 1000002, 'pending_approval', 1),
    (2000003, 100003, 1000003, 'published', 1);
```

### V1.3.0 - 支撑BC 初始数据

```sql
-- 4个安全规则
INSERT INTO cf_security_rule (id, rule_id, rule_name, action_on_trigger, severity) VALUES
    (5000001, 'AUD-001', '批量下载异常检测', 'block', 'CRITICAL'),
    (5000002, 'AUD-002', '异常访问时段检测', 'alert', 'WARNING'),
    (5000003, 'AUD-003', '权限越界检测', 'block', 'CRITICAL'),
    (5000004, 'AUD-006', '暴力破解防护', 'block', 'HIGH');

-- 3个工作流实例
INSERT INTO cf_workflow_state (id, exam_id, workflow_id, current_state, sla_type) VALUES
    (4000001, 100001, 'uuid-001', 'pending', 'normal'),
    (4000002, 100002, 'uuid-002', 'images_received', 'normal'),
    (4000003, 100003, 'uuid-003', 'report_approved', 'normal');
```

---

## 📈 迁移依赖关系图

```
V1.0.1 (患者信息BC)
    │
    ├─ 被 V1.1.0 引用 (cf_examination.patient_id FK cf_patient.id)
    │
    └─ 被 V1.1.0 引用 (cf_image.patient_id FK cf_patient.id)
        │
        ├─ 被 V1.2.0 引用 (cf_diagnosis_report.patient_id FK cf_patient.id)
        │
        └─ 被 V1.3.0 引用 (cf_ai_interpret.patient_id FK cf_patient.id)

V1.1.0 (影像管理BC)
    │
    ├─ 被 V1.2.0 引用 (cf_diagnosis_report.exam_id FK cf_examination.id)
    │
    └─ 被 V1.3.0 引用 (多个FK引用)

V1.2.0 (诊断报告BC)
    │
    └─ 被 V1.3.0 引用 (cf_workflow_event_log.exam_id FK cf_examination.id)

V1.3.0 (支撑BC)
    │
    └─ 被 V1.4.0 验证 (完整性检查)

V1.4.0 (验证脚本)
    │
    └─ 无后续依赖 (仅验证)
```

---

## ✅ 验证清单

### 迁移完整性检查

- [ ] V1.0.1 - 4个表 + 7个索引 ✓
- [ ] V1.1.0 - 4个表 + 12个索引 ✓
- [ ] V1.2.0 - 5个表 + 8个索引 ✓
- [ ] V1.3.0 - 8个表 + 15个索引 ✓
- [ ] V1.4.0 - 验证脚本执行成功 ✓

### 数据完整性检查

- [ ] 患者表：3条记录
- [ ] 检查表：3条记录
- [ ] 影像表：3条记录
- [ ] 报告表：3条记录
- [ ] 工作流：3条记录

### 约束检查

- [ ] 主键约束：>20个
- [ ] 外键约束：>10个
- [ ] 唯一键约束：>10个
- [ ] 检查约束：>5个

### 索引检查

- [ ] 表索引：>42个
- [ ] 覆盖索引：>10个
- [ ] 唯一索引：>8个

---

## 🔧 故障排查速查表

| 错误信息 | 原因 | 解决方案 | 文档位置 |
|---------|------|--------|--------|
| "table already exists" | 表已存在，重复迁移 | 清理旧表或使用不同的版本号 | README_MIGRATION.md |
| "FK constraint fails" | 外键约束冲突 | 检查依赖表是否创建 | README_MIGRATION.md |
| "permission denied" | 用户权限不足 | 赋予db_owner权限 | QUICK_START_MIGRATION.md |
| "flyway_schema_history not found" | 首次执行未初始化 | 执行flyway:baseline | README_MIGRATION.md |
| "脚本编码错误" | 文件编码不是UTF-8 | 用UTF-8编码重新保存 | QUICK_START_MIGRATION.md |

---

## 📞 获取帮助

需要帮助？检查以下资源：

1. **快速问题** → `QUICK_START_MIGRATION.md` 的"常见操作"和"故障排查"
2. **详细指南** → `README_MIGRATION.md` 的相应章节
3. **DDD概念** → `docx/DDD-领域驱动设计分析.md`
4. **代码示例** → 迁移脚本中的SQL示例

---

## 📝 版本历史

| 版本 | 日期 | 说明 |
|-----|------|------|
| V1.0 | 2026-03-26 | 初始版本，包含5个主要迁移脚本 |
| - | - | - |

---

**最后更新**：2026-03-26

**维护者**：D-Site 研发团队

**许可证**：内部使用
