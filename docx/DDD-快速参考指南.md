# D-Site PACS DDD 快速参考指南

> 这是对完整DDD设计文档的补充，便于快速查阅

---

## 1️⃣ 限界上下文速查表

| BC名称 | 职责 | 核心聚合 | 关键事件 | 外部依赖 |
|--------|------|---------|---------|---------|
| **患者信息BC** | 患者主数据管理 | Patient | PatientCreated, AllergyRecorded | - |
| **影像管理BC** | DICOM接收、存储、查询 | DicomImage, ImageSeries | ImageReceived, QCScoreRecorded, StorageTierMigrated | 患者信息BC |
| **诊断报告BC** | 报告生成、审核、发布 | DiagnosticReport | ReportDraftCreated, ReportSubmittedForApproval, ReportPublished | 患者信息BC, 影像管理BC, 临床决策BC |
| **临床决策BC** | AI诊断辅助 | AIInterpretation | AIAnalysisCompleted, AIFeedbackRecorded | 影像管理BC |
| **存储管理BC** | 存储分级、迁移、容量规划 | StorageMigration | StorageTierMigrated | 影像管理BC |
| **权限管理BC** | RBAC权限控制 | Permission, Role | PermissionGranted, RoleCreated | - |
| **安全审计BC** | 访问日志、合规审计 | AuditLog | AccessLogged, AnomalyDetected | 所有BC |
| **工作流编排BC** | 检查全流程状态管理 | Workflow | WorkflowStateChanged, SLAWarning | 所有BC |

---

## 2️⃣ 核心聚合根设计速查

### DicomImage（影像聚合）
```
聚合根ID：ImageId (I20260325001)
├─ 身份：sopInstanceUid (DICOM全球唯一标识)
├─ 关键属性：
│  ├─ filePath (存储路径)
│  ├─ qcScore (QC评分 0-100)
│  ├─ storageTier (hot/warm/cold)
│  └─ isKeyImage (是否关键影像)
├─ 内部实体：QCIssue[]（QC问题）
└─ 值对象：StorageTier, QCScore, FilePath

业务规则：
  • 同一SOPInstanceUID不能重复接收（去重）
  • QC完成后评分不可再修改
  • 影像存储层级自动迁移
  • 关键影像优先传输
```

### DiagnosticReport（报告聚合）
```
聚合根ID：ReportId (R20260325001)
├─ 关键属性：
│  ├─ status (draft/pending/approved/published)
│  ├─ findings (所见)
│  ├─ impression (诊断意见)
│  └─ recommendation (建议)
├─ 内部实体：ApprovalRecord[]（审核记录）
├─ 内部实体：ReportVersion[]（版本历史）
└─ 值对象：ReportTemplate, ApprovalHistory

业务规则：
  • 只有草稿状态能编辑内容
  • 待审核报告不能直接发布
  • 已发布报告修改需重新审核（新版本）
  • 审核拒绝后回到草稿状态
```

### AIInterpretation（AI解读聚合）
```
聚合根ID：InterpretationId
├─ 关键属性：
│  ├─ modelResults[]（各模型输出）
│  ├─ ensembleResult（融合结果）
│  ├─ confidence（置信度 0-100）
│  └─ doctorFeedback（医生反馈）
├─ 内部实体：ModelResult[]
└─ 值对象：EnsembleResult, Confidence, Finding

业务规则：
  • 一次分析只能记录一次结果
  • 置信度<60% 不自动填充报告
  • 医生反馈后可用于模型优化
```

### Patient（患者聚合）
```
聚合根ID：PatientId
├─ 关键属性：
│  ├─ patientNo（患者号）
│  ├─ demographics（人口统计）
│  ├─ medicalHistory（病历）
│  └─ allergies[]（过敏史）
├─ 内部实体：Allergy[], MedicationRecord[]
└─ 值对象：PatientDemographics, ContactInfo

业务规则：
  • 患者号全局唯一
  • 过敏史记录后不可删除，仅可标记已处理
  • 患者合并时保留所有历史记录
```

---

## 3️⃣ 领域事件流程图

### 检查完整生命周期事件流

```
[申请]
  ↓
ExaminationRequested
  ↓
[排队]
  ↓
ExaminationScheduled
  ↓
[执行]
  ↓
ExaminationStarted
  ↓
ImagesReceived（100+张影像）
  ↓
  ├─ ImageQCAgent 评分
  ├─ QCPassedEvent → [QC通过]
  │   ↓
  │   ├─ AIDiagnosisAgent 分析
  │   ├─ AIAnalysisCompleted
  │   ↓
  │   ├─ ReportGenerationAgent 生成初稿
  │   ├─ ReportDraftCreated
  │   ↓
  │   ├─ 医生编辑
  │   ├─ ReportSubmittedForApproval
  │   ↓
  │   ├─ 上级审核
  │   ├─ ReportApproved
  │   ↓
  │   ├─ ReportPublished
  │   ↓
  │   └─ [完成] ExaminationCompleted
  │
  └─ QCFailedEvent → [QC不通过]
      ↓
      → 通知技师重照
      → 等待重新采集
```

### 事件触发的Agent动作

```
事件                              →  处理Agent              →  输出事件
─────────────────────────────────────────────────────────────────────
ImageReceived                    →  ImageQCAgent          →  QCPassedEvent
QCPassedEvent                    →  AIDiagnosisAgent      →  AIAnalysisCompleted
AIAnalysisCompleted              →  ReportGenerationAgent →  ReportDraftCreated
ReportDraftCreated               →  ReportApprovalService →  ReportApproved
ReportApproved                   →  ReportPublishService  →  ReportPublished
ReportPublished + QCPassedEvent  →  StorageSchedulerAgent →  StorageTierMigrated
*所有事件                         →  SecurityAuditAgent    →  AccessLogged
*所有关键事件                     →  WorkflowAgent         →  WorkflowStateChanged
```

---

## 4️⃣ 聚合间的通信方式

### 同步调用（少用）
```
ReportGenerationService.generateReportDraft()
  ├─ 同步查询：Patient 数据（患者BC的API）
  └─ 同步查询：AIInterpretation 数据（临床决策BC的API）

风险：如果被调用方故障，调用方也故障
```

### 异步事件驱动（推荐）
```
1. DicomImage 聚合 发布 ImageReceived 事件
   ↓
2. 事件发布器 → Kafka Topic: image-events
   ↓
3. ImageQCAgent 消费事件，执行QC
   ↓
4. ImageQCAgent 聚合 发布 QCPassedEvent
   ↓
5. AIDiagnosisAgent 订阅 QCPassedEvent，执行AI分析
   ↓
6. ...（链式反应，高度解耦）

优势：各BC独立演进，故障隔离
```

### 共享数据库（仅限实时查询）
```
DiagnosticReport 需要查询患者年龄计算SLA
  └─ 直接查询 cf_patient 表（同一数据库）
  └─ 但这是反DDD的，应该改为：
     • 检查创建时携带患者年龄（冗余但有意义）
     • 或通过API异步获取

在有足够理由时可以打破隔离（如性能），但要明确说明
```

---

## 5️⃣ 应用服务 vs 领域服务区分

### 应用服务（Application Service）
```
位置：application 层

职责：
  • 编排多个聚合/领域服务
  • 处理数据库事务边界
  • 权限校验和审计
  • DTO转换

示例：
  public ReportDraftResponse generateReportDraft(GenerateReportDraftRequest req) {
      // 权限校验（应用服务职责）
      checkPermission(req.getDoctorId(), "cloudfilm:report:add");

      // 聚合查询和调用（应用服务职责）
      Examination exam = examinationRepository.findById(req.getExamId());
      AIInterpretation aiInterpret = aiRepository.findByExamId(req.getExamId());

      // 调用领域服务（应用服务职责）
      ReportDraft draft = reportGenService.generateReportDraft(exam, aiInterpret);

      // 持久化（应用服务职责）
      DiagnosticReport report = new DiagnosticReport(draft);
      reportRepository.save(report);

      // 发布事件（应用服务职责）
      eventPublisher.publish(report.getDomainEvents());

      return ReportDraftResponse.from(report);
  }
```

### 领域服务（Domain Service）
```
位置：domain 层

触发场景：
  • 跨越多个聚合的业务操作
  • 需要外部资源的操作
  • 复杂业务规则的计算

示例：
  // ✓ 这应该是领域服务
  public ReportDraft generateReportDraft(
      Examination exam,
      AIInterpretation aiInterpret
  ) {
      // Step 1：模板选择（领域知识）
      ReportTemplate template = selectOptimalTemplate(
          exam.getModality(),
          exam.getExaminationPart(),
          exam.getPatientAge()
      );

      // Step 2：AI结果预填（领域知识）
      ReportFindings findings = aiInterpret.getEnsembleResult()
          .toReportFindings();

      // Step 3：术语标准化（领域知识+外部资源）
      findings = medicalTerminologyNormalizer.normalize(findings);

      return new ReportDraft(template, findings);
  }

  // ✗ 这不应该是领域服务
  public void sendEmailNotification(DoctorId doctor, String message) {
      // 这是技术性操作，应该用基础设施服务或应用服务处理
  }
```

---

## 6️⃣ 数据库表命名与聚合映射

```
聚合与表的对应关系：

核心聚合 → 主表
┌─────────────────────────────────────────────────────┐
│ DicomImage          → cf_image                        │
│ ImageSeries         → cf_series                       │
│ Examination         → cf_examination                  │
│ Patient             → cf_patient                      │
│ DiagnosticReport    → cf_diagnosis_report             │
│ AIInterpretation    → cf_ai_interpret                 │
│ StorageMigration    → cf_storage_migration            │
│ Workflow            → cf_workflow_state               │
└─────────────────────────────────────────────────────┘

聚合内实体 → 关联表 或 JSON字段
┌─────────────────────────────────────────────────────┐
│ ApprovalRecord      → cf_report_approval（关联表）   │
│ ReportVersion       → cf_report_version（关联表）    │
│ QCIssue             → cf_image.qc_issues（JSON）     │
│ ModelResult         → cf_ai_interpret.model_results（JSON）│
│ Allergy             → cf_patient_allergy（关联表）   │
└─────────────────────────────────────────────────────┘

值对象 → 字段 或 JSON
┌─────────────────────────────────────────────────────┐
│ QCScore             → cf_image.qc_score              │
│ FilePath            → cf_image.file_path             │
│ StorageTier         → cf_image.storage_tier          │
│ Confidence          → cf_ai_interpret.confidence     │
│ PatientDemographics → cf_patient的多个字段            │
└─────────────────────────────────────────────────────┘
```

---

## 7️⃣ 检查清单：DDD实施要点

### 业务建模检查
- [ ] 识别了所有核心域/支撑域/通用域
- [ ] 核心域的聚合设计遵循了一致性边界原则
- [ ] 聚合大小合理（不过大也不过小）
- [ ] 聚合间仅通过ID引用

### 领域模型检查
- [ ] 聚合根有明确的业务规则
- [ ] 值对象不可变且有业务含义
- [ ] 实体有明确的标识和生命周期
- [ ] 没有贫血模型（所有业务逻辑都在聚合内）

### 领域事件检查
- [ ] 事件命名用过去式（已发生）
- [ ] 事件包含足够的上下文信息
- [ ] 事件中包含聚合ID（便于追踪）
- [ ] 支持分布式追踪ID和关联ID

### 通用语言检查
- [ ] 所有代码、文档、讨论中术语一致
- [ ] 医学术语、IT术语明确区分
- [ ] 术语在首次出现时有英文标注
- [ ] 聚合根、实体、值对象的命名清晰

### 限界上下文检查
- [ ] 各BC有明确的职责边界
- [ ] BC间通信方式明确（事件/API/共享表）
- [ ] Anti-Corruption Layer处理了BC间的语义差异
- [ ] 没有BC间的直接对象引用

---

## 8️⃣ 常见错误及纠正

| 错误做法 | 正确做法 | 原因 |
|---------|--------|------|
| 聚合内有循环引用 | 重新划分聚合边界 | 单向依赖，便于推理 |
| 多个聚合共享同一ID | 各聚合有独立ID，通过ID引用 | 聚合要独立，通过事件协调 |
| 直接修改聚合内的实体 | 通过聚合根的业务方法修改 | 保证业务规则在聚合内 |
| 领域服务处理技术性操作 | 用应用服务或基础设施服务 | 领域服务只处理业务规则 |
| 同步RPC进行限界上下文通信 | 异步事件驱动 | 减少耦合，支持异构系统 |
| 为不存在的功能创建聚合 | 功能驱动的聚合设计 | 避免Over-design |
| 值对象有大量计算逻辑 | 抽取为领域服务或策略对象 | 值对象保持简洁 |
| 事件含有业务敏感信息 | 事件只含有ID和基本信息 | 避免数据泄露 |

---

## 9️⃣ 与现有代码的映射

### 现有代码缺陷对应的DDD解决方案

| 现有代码问题 | DDD解决方案 | 实施步骤 |
|------------|----------|--------|
| `CfImage` 实体贫血，无业务方法 | 将业务逻辑转移到 `DicomImage` 聚合内 | 1. 定义聚合根2. 实现业务方法3. 发布事件 |
| 直接修改 `cf_image.qc_score` | 通过 `recordQCScore()` 聚合方法 | 1. 添加业务规则校验2. 发布QC事件 |
| `ReportService` 处理太多职责 | 拆分为 `ReportGenerationService` + `ReportApprovalService` | 1. 按职责拆分2. 定义清晰的API |
| 存储和业务逻辑混在一起 | 应用服务处理存储，领域服务处理业务 | 1. 建立清晰的分层2. Repository接口 |

---

## 🔟 参考代码骨架

### 聚合根实现模板

```java
public abstract class AggregateRoot {
    private List<DomainEvent> domainEvents = new ArrayList<>();

    // 发布领域事件
    protected void publish(DomainEvent event) {
        domainEvents.add(event);
    }

    // 清空已发布的事件
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}

public class DicomImage extends AggregateRoot {
    private ImageId imageId;
    // ... 其他字段

    public void recordQCScore(QCScore score, List<QCIssue> issues) {
        // 业务规则检查
        if (qcStatus == QCStatus.COMPLETED) {
            throw new BusinessException("Cannot modify completed QC");
        }

        // 状态变更
        this.qcScore = score;
        this.qcStatus = QCStatus.COMPLETED;

        // 发布事件（让外部世界知道发生了什么）
        publish(new QCScoreRecorded(imageId, score, issues));
    }
}
```

### 领域服务实现模板

```java
public interface QualityControlService {
    QCResult performQualityControl(DicomImage image);
}

@Service
public class ImageQualityControlServiceImpl implements QualityControlService {
    // 注入各个评估器
    private final TechnicalQualityEvaluator techEval;
    private final CompletenessEvaluator completeEval;

    @Override
    public QCResult performQualityControl(DicomImage image) {
        // 执行业务逻辑
        TechnicalQualityScore techScore = techEval.evaluate(image);
        CompletenessScore completeScore = completeEval.evaluate(image);

        // 计算综合评分（领域知识）
        BigDecimal totalScore = calculateWeightedScore(
            techScore, completeScore
        );

        return new QCResult(totalScore, generateIssues(image));
    }
}
```

### 应用服务实现模板

```java
@Service
@RequiredArgsConstructor
public class RecordQCScoreApplicationService {
    private final ImageRepository imageRepository;
    private final QualityControlService qcService;
    private final DomainEventPublisher eventPublisher;
    private final SecurityService securityService;

    public RecordQCScoreResponse recordQCScore(RecordQCScoreRequest request) {
        // 权限检查（应用服务职责）
        securityService.checkPermission(request.getUserId(), "qc:record");

        // 查询聚合
        DicomImage image = imageRepository.findById(request.getImageId())
            .orElseThrow(() -> new ImageNotFoundException(request.getImageId()));

        // 执行领域服务
        QCResult qcResult = qcService.performQualityControl(image);

        // 调用聚合的业务方法
        image.recordQCScore(qcResult.getScore(), qcResult.getIssues());

        // 持久化
        imageRepository.save(image);

        // 发布事件
        eventPublisher.publishAll(image.getDomainEvents());
        image.clearDomainEvents();

        return RecordQCScoreResponse.from(image);
    }
}
```

---

*快速参考完成 - 详见完整DDD设计文档*
