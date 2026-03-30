# D-Site 云胶片管理系统 DDD 领域驱动设计分析

> **版本**: V1.0
> **日期**: 2026-03-26
> **适用系统**: D-Site V1.0 云胶片管理系统
> **方法论**: 领域驱动设计（Domain-Driven Design）

---

## 目录

1. [DDD概述与系统全景](#1-ddd概述与系统全景)
2. [核心域划分](#2-核心域划分)
3. [子域分析与限界上下文](#3-子域分析与限界上下文)
4. [聚合设计](#4-聚合设计)
5. [领域模型详设](#5-领域模型详设)
6. [领域事件](#6-领域事件)
7. [领域服务](#7-领域服务)
8. [通用语言](#8-通用语言)
9. [流程建模](#9-流程建模)
10. [数据建模](#10-数据建模)
11. [限界上下文集成](#11-限界上下文集成)

---

## 1. DDD概述与系统全景

### 1.1 医疗影像系统的DDD视图

```
┌────────────────────────────────────────────────────────────────────────────┐
│                    D-Site 云胶片管理系统 全景                               │
│                                                                            │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐              │
│  │    核心域1      │  │    核心域2      │  │    核心域3      │              │
│  │  影像管理域     │  │  诊断报告域     │  │  临床决策域     │              │
│  │  (Image Mgmt)  │  │ (Report Mgmt)  │  │ (Clinical)     │              │
│  └────────────────┘  └────────────────┘  └────────────────┘              │
│         │                    │                    │                       │
│         └────────────────────┼────────────────────┘                       │
│                              │                                            │
│  ┌────────────────┐  ┌──────▼───────┐  ┌────────────────┐                │
│  │   支撑域1      │  │  支撑域2      │  │   支撑域3      │                │
│  │ 患者信息管理   │  │  存储管理      │  │  安全审计      │                │
│  │ (Patient Mgmt) │  │ (Storage)    │  │ (Audit)       │                │
│  └────────────────┘  └───────────────┘  └────────────────┘                │
│                                                                            │
│  ┌────────────────┐  ┌───────────────┐  ┌────────────────┐                │
│  │   通用域1      │  │  通用域2      │  │   通用域3      │                │
│  │  权限管理      │  │  系统监控      │  │  工作流编排    │                │
│  │ (Permission)  │  │ (Monitoring)  │  │ (Orchestration)│               │
│  └────────────────┘  └───────────────┘  └────────────────┘                │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 DDD战略设计关键问题

| 问题 | 回答 | 含义 |
|-----|------|------|
| **核心价值** | 医学影像的采集、存储、诊断、分享 | 医疗机构的业务生命线 |
| **关键决策** | 检查状态流转、诊断意见生成、权限授予 | 需要深度的领域建模 |
| **竞争优势** | AI辅助诊断、分级存储、高可用性 | 影响核心业务的模块设计 |
| **复杂度来源** | DICOM处理、多Agent协调、合规审计 | 需要限界上下文隔离 |

---

## 2. 核心域划分

### 2.1 核心域 vs 支撑域 vs 通用域

```
按照对业务价值的贡献程度分类：

┌─────────────────────────────────────────────────────────────┐
│                      核心域（Core Domain）                   │
│                                                             │
│  如果移除这些域，系统失去医疗价值，医院业务停滞             │
│                                                             │
│  1. 影像管理域（Image Management Context）                 │
│     └─ DICOM接收、存储、查询、分发                           │
│  2. 诊断报告域（Diagnostic Report Context）                │
│     └─ 报告生成、审核、发布、版本管理                        │
│  3. 临床决策支持域（Clinical Decision Support Context）    │
│     └─ AI诊断辅助、病灶标注、诊断建议                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  支撑域（Supporting Domain）                  │
│                                                             │
│  这些域支撑核心域运作，但不是差异化竞争力                   │
│  可购买现成产品或外包开发                                  │
│                                                             │
│  1. 患者信息管理域（Patient Information Context）          │
│     └─ 患者基本信息、病历、用药史                            │
│  2. 存储管理域（Storage Management Context）              │
│     └─ 分级存储、生命周期管理、容量规划                      │
│  3. 安全审计域（Security Audit Context）                  │
│     └─ 访问日志、权限检查、合规报告                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                 通用域（Generic Domain）                     │
│                                                             │
│  业界通用能力，可采用开源框架或第三方产品                   │
│                                                             │
│  1. 权限管理域（Permission Management Context）            │
│     └─ RBAC、权限校验、角色分配                              │
│  2. 系统监控域（System Monitoring Context）                │
│     └─ 性能指标、告警、日志聚合                              │
│  3. 工作流编排域（Workflow Orchestration Context）        │
│     └─ 流程定义、状态机、任务调度                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 域的特性矩阵

| 域名称 | 分类 | 复杂度 | 变化频率 | 投入优先级 | 外包可行性 |
|-------|------|--------|---------|----------|----------|
| **影像管理** | 核心 | ★★★★★ | 低 | P0 | 否 |
| **诊断报告** | 核心 | ★★★★★ | 低 | P0 | 否 |
| **临床决策** | 核心 | ★★★★☆ | 中 | P0 | 否 |
| **患者信息** | 支撑 | ★★★☆☆ | 低 | P1 | 部分（需改造） |
| **存储管理** | 支撑 | ★★★☆☆ | 中 | P1 | 可（MinIO） |
| **安全审计** | 支撑 | ★★★☆☆ | 中 | P1 | 否（合规） |
| **权限管理** | 通用 | ★★☆☆☆ | 低 | P2 | 可（RuoYi） |
| **系统监控** | 通用 | ★★☆☆☆ | 中 | P2 | 可（Prometheus） |
| **工作流编排** | 通用 | ★★★☆☆ | 低 | P2 | 可（Temporal） |

---

## 3. 子域分析与限界上下文

### 3.1 限界上下文全景图

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                          限界上下文（Bounded Context）                         │
│                                                                              │
│  ┌─────────────────────────┐                  ┌──────────────────────────┐  │
│  │   患者信息BC              │                  │   影像管理BC              │  │
│  │ (Patient Information)    │                  │  (Image Management)      │  │
│  │                          │                  │                          │  │
│  │ • PatientNo             │◄─── 患者ID ─────┤ • DICOM File           │  │
│  │ • PatientName           │    (聚合ID)      │ • SeriesInstanceUID    │  │
│  │ • MedicalHistory        │                  │ • ImageFile            │  │
│  │ • Allergies             │                  │ • QualityScore         │  │
│  └─────────────────────────┘                  │ • StorageTier          │  │
│           ▲                                    └────────┬─────────────────┘  │
│           │                                             │                    │
│           │                                     QC通过事件                    │
│           │                                             │                    │
│  ┌────────┴─────────────┐                       ┌──────▼──────────────────┐  │
│  │                      │                       │                         │  │
│  │  诊断报告BC           │    报告初稿 ────────▶ │   临床决策BC             │  │
│  │ (Report Management)   │                       │  (Clinical Decision)    │  │
│  │                       │                       │                         │  │
│  │ • Report ID           │◀────── AI分析结果 ──┤ • AIFinding            │  │
│  │ • ReportStatus        │                       │ • Confidence           │  │
│  │ • Findings            │                       │ • RecommendedAction    │  │
│  │ • Conclusion          │                       │ • EvidenceReference    │  │
│  │ • ApprovalHistory     │                       │ • DoctorFeedback       │  │
│  └────────┬─────────────┘                       └───────┬────────────────┘  │
│           │                                             │                    │
│           │◀────────── 报告发布事件 ──────────────────┘                     │
│           │                                                                  │
│  ┌────────▼──────────────┐        ┌───────────────────────────────────────┐│
│  │                       │        │                                       ││
│  │  分享管理BC            │        │   影像质控BC                         ││
│  │  (Share Management)    │        │  (Image QC)                         ││
│  │                        │        │                                     ││
│  │ • ShareLink           │        │ • QCScore                          ││
│  │ • AccessCode          │        │ • QCStatus                         ││
│  │ • ExpiryTime          │        │ • QCIssues                         ││
│  │ • Desensitization     │        │ • RectificationNotice              ││
│  └────────────────────────┘        └───────────────────────────────────────┘│
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                      存储与归档BC (Storage & Archive)                   │ │
│  │  • StorageTier • FilePath • CompressRatio • MigrationLog             │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 核心限界上下文详析

#### 3.2.1 影像管理限界上下文（Image Management BC）

**职责**：
- 接收和存储DICOM影像
- 管理影像元数据
- 提供影像查询和分发接口

**通用语言**：
- Study（检查）：一次完整的影像检查
- Series（序列）：一个检查下的多个影像序列
- Image（影像）：单个DICOM文件
- Modality（模态）：CT、MRI、DR等影像类型
- SOPInstanceUID：全球唯一的影像标识符

**关键聚合根**：
- `DicomImage` - 影像聚合
- `ImageSeries` - 序列聚合
- `StudyImages` - 检查影像集合

**入站接口**：
- DICOM C-STORE SCP
- STOW-RS HTTP接口
- 本地文件导入

**出站事件**：
- ImageReceived（影像接收）
- ImageStored（影像存储）
- ImageQCStarted（质控开始）

---

#### 3.2.2 诊断报告限界上下文（Diagnostic Report BC）

**职责**：
- 管理报告的整个生命周期
- 支持报告的起草、修改、审核、发布
- 维护报告版本历史

**通用语言**：
- Report（报告）：一份完整的诊断报告
- ReportStatus（报告状态）：草稿、待审核、已发布、已归档
- Findings（所见）：医生对影像的描述
- Impression（诊断意见）：医学诊断结论
- Approval（审核）：报告的上级医生审核

**关键聚合根**：
- `DiagnosticReport` - 诊断报告聚合
- `ReportApprovalFlow` - 审核流程

**入站接口**：
- 报告创建请求
- 报告修改请求
- 审核决策

**出站事件**：
- ReportDraftCreated（报告草稿创建）
- ReportSubmittedForApproval（报告提交审核）
- ReportApproved（报告审核通过）
- ReportPublished（报告发布）

---

#### 3.2.3 临床决策支持限界上下文（Clinical Decision Support BC）

**职责**：
- 提供AI诊断辅助
- 融合多个AI模型结果
- 量化诊断置信度
- 追踪医生反馈

**通用语言**：
- Finding（发现）：AI检测到的病灶或异常
- Confidence（置信度）：AI对该发现的确定程度
- Ensemble（融合）：多模型结果的加权融合
- DoctorFeedback（医生反馈）：医生对AI结果的评价

**关键聚合根**：
- `AIInterpretation` - AI诊断解读聚合
- `ModelEnsemble` - 模型融合策略

**入站接口**：
- 影像接收完成事件
- 报告草稿创建请求

**出站事件**：
- AIAnalysisStarted（AI分析开始）
- AIAnalysisCompleted（AI分析完成）
- FindingIdentified（病灶识别）

---

#### 3.2.4 患者信息限界上下文（Patient Information BC）

**职责**：
- 维护患者主数据
- 管理患者与影像的关系
- 提供患者历史查询

**通用语言**：
- Patient（患者）：患者主体
- MedicalRecord（病历）：患者的医学记录
- ExaminationHistory（检查历史）：患者的所有检查记录

**关键聚合根**：
- `Patient` - 患者聚合
- `PatientMedicalHistory` - 患者病历聚合

---

### 3.3 限界上下文间的关系

```
通信模式矩阵：

┌──────────────────┬─────────────────┬──────────────────┬──────────────┐
│ 上下文A          │ 上下文B          │ 通信方式           │ 数据流向     │
├──────────────────┼─────────────────┼──────────────────┼──────────────┤
│ 患者信息BC       │ 影像管理BC      │ 事件驱动（异步）   │ 患者ID       │
│ 患者信息BC       │ 诊断报告BC      │ 事件驱动（异步）   │ 患者ID       │
│ 影像管理BC       │ 临床决策BC      │ 事件驱动（异步）   │ 影像ID集合   │
│ 临床决策BC       │ 诊断报告BC      │ 事件驱动（异步）   │ AI发现结果   │
│ 诊断报告BC       │ 分享管理BC      │ 同步API或事件      │ 报告ID       │
│ 影像管理BC       │ 分享管理BC      │ 同步API            │ 影像ID       │
│ 影像管理BC       │ 存储管理BC      │ 事件驱动（异步）   │ 存储层级     │
│ 权限管理BC       │ 所有BC          │ 同步API            │ 权限校验结果 │
└──────────────────┴─────────────────┴──────────────────┴──────────────┘
```

---

## 4. 聚合设计

### 4.1 聚合设计原则

```
DDD中的聚合设计核心要点：

1. 聚合根是唯一的入口
   ├── 只能通过聚合根修改聚合内部的对象
   ├── 其他聚合只能通过聚合根ID引用
   └── 不允许直接引用聚合内的其他实体

2. 聚合边界
   ├── 包含直接修改一致性的对象
   ├── 不包含只读或弱关联的对象
   └── 遵循业务规则和一致性要求

3. 聚合设计尺寸
   ├── 小聚合：易于测试、易于理解、便于并发
   ├── 大聚合：强一致性、强约束、复杂业务规则
   └── 平衡：大多数情况选择小聚合
```

### 4.2 核心聚合设计

#### 4.2.1 DicomImage（DICOM影像聚合）

```java
// 核心聚合根
public class DicomImage extends AggregateRoot {
    // 聚合根ID
    private ImageId imageId;

    // 影像基本属性
    private String sopInstanceUid;           // 全球唯一标识
    private SeriesId seriesId;               // 所属序列（外部聚合ID）
    private StudyId studyId;                 // 所属检查（外部聚合ID）
    private String modality;                 // 影像模态(CT/MRI/DR)
    private int rows;                        // 像素行数
    private int columns;                     // 像素列数

    // 影像文件信息（值对象）
    private FilePath filePath;               // 文件路径
    private FileSize fileSize;               // 文件大小
    private Timestamp receivedTime;          // 接收时间

    // 存储分级（值对象）
    private StorageTier currentTier;         // 当前所在层级(hot/warm/cold)
    private Timestamp lastAccessedAt;        // 最后访问时间

    // 质控信息（值对象）
    private QCScore qcScore;                 // QC评分
    private QCStatus qcStatus;               // QC状态
    private List<QCIssue> qcIssues;          // QC问题清单

    // 缩略图
    private ThumbnailPath thumbnailPath;     // 缩略图路径

    // 标记
    private boolean isKeyImage;              // 是否为关键影像
    private boolean isDesensitized;          // 是否已脱敏

    // 业务方法
    public void recordQCScore(QCScore score, List<QCIssue> issues) {
        // 业务规则：QC已完成的影像不能再修改QC评分
        if (qcStatus == QCStatus.COMPLETED) {
            throw new BusinessException("Cannot modify QC score of completed image");
        }
        this.qcScore = score;
        this.qcIssues = issues;
        this.qcStatus = QCStatus.COMPLETED;

        // 发布领域事件
        publish(new QCScoreRecorded(imageId, score));
    }

    public void migrateStorageTier(StorageTier newTier) {
        if (newTier == currentTier) {
            return;
        }
        StorageTier oldTier = this.currentTier;
        this.currentTier = newTier;
        publish(new StorageTierMigrated(imageId, oldTier, newTier));
    }

    public void markAsKeyImage() {
        this.isKeyImage = true;
        publish(new ImageMarkedAsKey(imageId));
    }
}
```

**聚合内实体和值对象**：
```
DicomImage (聚合根)
  ├─ FilePath (值对象)
  │   ├─ path: String
  │   └─ storage: StorageType
  ├─ FileSize (值对象)
  │   ├─ bytes: long
  │   └─ formattedSize: String
  ├─ QCScore (值对象)
  │   ├─ score: decimal(0-100)
  │   ├─ timestamp: LocalDateTime
  │   └─ evaluator: UserId
  ├─ QCIssue (实体)
  │   ├─ issueId: IssueId
  │   ├─ type: String
  │   ├─ severity: Severity
  │   └─ description: String
  └─ StorageTier (值对象)
      ├─ tier: Enum(hot/warm/cold)
      ├─ lastMigratedAt: LocalDateTime
      └─ compressionRatio: decimal
```

#### 4.2.2 DiagnosticReport（诊断报告聚合）

```java
public class DiagnosticReport extends AggregateRoot {
    // 聚合根ID
    private ReportId reportId;

    // 关联信息
    private ExamId examId;                   // 所属检查（外部聚合ID）
    private PatientId patientId;             // 所属患者（外部聚合ID）
    private DoctorId authorId;               // 报告作者

    // 报告内容（值对象）
    private ReportTemplate template;         // 报告模板
    private ReportFindings findings;         // 检查所见
    private ReportImpression impression;     // 诊断意见
    private ReportRecommendation recommendation; // 建议

    // 状态管理
    private ReportStatus status;             // 报告状态
    private ApprovalHistory approvalHistory; // 审核历史

    // 版本控制
    private int version;                     // 版本号
    private List<ReportVersion> versions;    // 版本历史

    // 输出格式
    private PdfDocument pdfDocument;         // 生成的PDF

    // 业务方法
    public void submitForApproval(DoctorId supervisor) {
        if (status != ReportStatus.DRAFT) {
            throw new BusinessException("Only draft report can be submitted");
        }
        this.status = ReportStatus.PENDING_APPROVAL;
        this.approvalHistory.addSubmission(supervisor, Timestamp.now());
        publish(new ReportSubmittedForApproval(reportId, examId, supervisor));
    }

    public void approve(DoctorId approver, String comments) {
        if (status != ReportStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only pending report can be approved");
        }
        this.status = ReportStatus.APPROVED;
        this.approvalHistory.recordApproval(approver, comments, Timestamp.now());
        publish(new ReportApproved(reportId, examId, approver));
    }

    public void reject(DoctorId rejector, String reason) {
        if (status != ReportStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only pending report can be rejected");
        }
        this.status = ReportStatus.DRAFT;
        this.approvalHistory.recordRejection(rejector, reason, Timestamp.now());
        publish(new ReportRejected(reportId, examId, rejector, reason));
    }

    public void publish() {
        if (status != ReportStatus.APPROVED) {
            throw new BusinessException("Only approved report can be published");
        }
        this.status = ReportStatus.PUBLISHED;
        this.approvalHistory.recordPublished(Timestamp.now());
        publish(new ReportPublished(reportId, examId));
    }

    public void amendReport(ReportFindings newFindings, ReportImpression newImpression) {
        // 业务规则：已发布的报告修改需要新的审核
        if (status == ReportStatus.PUBLISHED) {
            this.version++;
            this.versions.add(new ReportVersion(this.version - 1, this.findings, this.impression));
            this.findings = newFindings;
            this.impression = newImpression;
            this.status = ReportStatus.PENDING_APPROVAL;
            publish(new ReportAmended(reportId, this.version));
        } else if (status == ReportStatus.DRAFT) {
            this.findings = newFindings;
            this.impression = newImpression;
        }
    }
}
```

**聚合内结构**：
```
DiagnosticReport (聚合根)
  ├─ ReportTemplate (值对象)
  │   ├─ templateId: TemplateId
  │   ├─ templateName: String
  │   └─ sections: List<Section>
  ├─ ReportFindings (值对象)
  │   ├─ content: String
  │   └─ createdAt: LocalDateTime
  ├─ ReportImpression (值对象)
  │   ├─ content: String
  │   ├─ icdCodes: List<ICDCode>
  │   └─ createdAt: LocalDateTime
  ├─ ApprovalHistory (实体)
  │   ├─ submissions: List<Submission>
  │   ├─ approvals: List<Approval>
  │   └─ rejections: List<Rejection>
  └─ ReportVersion (实体)
      ├─ versionNumber: int
      ├─ snapshot: ReportSnapshot
      └─ createdAt: LocalDateTime
```

#### 4.2.3 AIInterpretation（AI诊断解读聚合）

```java
public class AIInterpretation extends AggregateRoot {
    // 聚合根ID
    private InterpretationId interpretationId;

    // 关联信息
    private ImageId imageId;                 // 关联影像
    private ExamId examId;                   // 关联检查

    // 分析元数据
    private Modality imageModality;          // 影像模态
    private AnalysisTimestamp startedAt;     // 分析开始时间
    private AnalysisTimestamp completedAt;   // 分析完成时间

    // 模型结果（值对象）
    private List<ModelResult> modelResults;  // 各模型结果
    private EnsembleResult ensembleResult;   // 融合结果

    // 置信度和不确定度
    private Confidence confidence;           // 置信度(0-100)
    private UncertaintyBounds bounds;        // 不确定度区间

    // 医生反馈
    private DoctorFeedback doctorFeedback;   // 医生的接受/拒绝/修改意见

    // 业务方法
    public void recordAnalysisResult(List<ModelResult> results) {
        // 业务规则：一次分析只能记录一次结果
        if (ensembleResult != null) {
            throw new BusinessException("Analysis result already recorded");
        }

        this.modelResults = results;
        this.ensembleResult = new EnsembleResult(results);
        this.confidence = calculateConfidence(ensembleResult);
        this.bounds = calculateUncertaintyBounds(modelResults);
        this.completedAt = AnalysisTimestamp.now();

        publish(new AIAnalysisCompleted(interpretationId, examId, ensembleResult));
    }

    public void recordDoctorFeedback(DoctorId doctor, FeedbackType type, String comments) {
        this.doctorFeedback = new DoctorFeedback(doctor, type, comments, Timestamp.now());
        publish(new AIFeedbackRecorded(interpretationId, type));
    }

    // 内部计算方法
    private Confidence calculateConfidence(EnsembleResult result) {
        // 根据多个模型的加权投票计算置信度
        return new Confidence(result.calculateWeightedProbability());
    }

    private UncertaintyBounds calculateUncertaintyBounds(List<ModelResult> results) {
        // 计算90%置信区间
        return new UncertaintyBounds(results);
    }
}
```

**聚合内结构**：
```
AIInterpretation (聚合根)
  ├─ ModelResult (实体)
  │   ├─ modelId: ModelId
  │   ├─ modelName: String
  │   ├─ version: String
  │   ├─ findings: List<Finding>
  │   └─ confidence: decimal
  ├─ EnsembleResult (值对象)
  │   ├─ fusedFindings: List<Finding>
  │   ├─ weights: Map<ModelId, decimal>
  │   ├─ votingStrategy: String
  │   └─ createdAt: LocalDateTime
  ├─ Finding (值对象)
  │   ├─ findingId: FindingId
  │   ├─ description: String
  │   ├─ location: AnatomicalLocation
  │   ├─ severity: Severity
  │   └─ confidence: decimal
  ├─ Confidence (值对象)
  │   ├─ value: decimal(0-100)
  │   ├─ level: Enum(HIGH/MEDIUM/LOW)
  │   └─ interpretation: String
  └─ DoctorFeedback (值对象)
      ├─ doctorId: DoctorId
      ├─ feedbackType: Enum(ACCEPT/REJECT/MODIFY)
      ├─ comments: String
      └─ timestamp: LocalDateTime
```

#### 4.2.4 Patient（患者聚合）

```java
public class Patient extends AggregateRoot {
    // 聚合根ID
    private PatientId patientId;

    // 基本信息（值对象）
    private PatientDemographics demographics;  // 患者人口学特征
    private PatientIdentifiers identifiers;    // 患者标识符集

    // 医学信息
    private MedicalHistory medicalHistory;     // 病历
    private List<Allergy> allergies;           // 过敏史
    private List<Medication> medications;      // 用药史

    // 联系方式
    private ContactInfo contactInfo;           // 联系方式

    // 业务方法
    public void recordAllergy(Allergy allergy) {
        if (allergies.contains(allergy)) {
            throw new BusinessException("Allergy already recorded");
        }
        this.allergies.add(allergy);
        publish(new AllergyRecorded(patientId, allergy));
    }

    public void updateMedicalHistory(MedicalHistoryEntry entry) {
        this.medicalHistory.addEntry(entry);
        publish(new MedicalHistoryUpdated(patientId, entry));
    }
}
```

---

## 5. 领域模型详设

### 5.1 核心值对象

```
值对象 = 不可变、无标识、可比较、内聚的数据对象

┌─────────────────────────────────────────────────────────────┐
│ 值对象设计规范                                               │
├─────────────────────────────────────────────────────────────┤
│ 1. 不可变性  → 创建后无法修改，所有属性为final                │
│ 2. 可交换    → 相同属性值的对象可以互相替换                  │
│ 3. 无副作用  → 方法调用不产生业务影响                        │
│ 4. 可序列化  → 便于传输和持久化                              │
└─────────────────────────────────────────────────────────────┘

常见值对象示例：
```

```java
// 1. 简单值对象 - QC评分
public final class QCScore {
    private final BigDecimal score;  // 0-100
    private final LocalDateTime evaluatedAt;
    private final UserId evaluatedBy;

    public QCScore(BigDecimal score, UserId evaluatedBy) {
        if (score.compareTo(ZERO) < 0 || score.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        this.score = score;
        this.evaluatedAt = LocalDateTime.now();
        this.evaluatedBy = evaluatedBy;
    }

    public boolean isPassed() {
        return score.compareTo(new BigDecimal("70")) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QCScore qcScore = (QCScore) o;
        return score.equals(qcScore.score) &&
               evaluatedAt.equals(qcScore.evaluatedAt) &&
               evaluatedBy.equals(qcScore.evaluatedBy);
    }
}

// 2. 复合值对象 - 存储层级
public final class StorageTier {
    public enum Tier { HOT, WARM, COLD }

    private final Tier tier;
    private final LocalDateTime lastMigratedAt;
    private final BigDecimal compressionRatio;  // 0-1

    public StorageTier(Tier tier, LocalDateTime lastMigratedAt, BigDecimal compressionRatio) {
        this.tier = tier;
        this.lastMigratedAt = lastMigratedAt;
        this.compressionRatio = compressionRatio;
    }

    public StorageTier migrateTo(Tier newTier) {
        return new StorageTier(newTier, LocalDateTime.now(), compressionRatio);
    }
}

// 3. 标识符值对象 - ImageId
public final class ImageId {
    private final String value;  // 例如：I20260325001

    public ImageId(String value) {
        if (!value.matches("^I\\d{14}$")) {
            throw new IllegalArgumentException("Invalid ImageId format");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

// 4. 集合值对象 - ApprovalHistory
public final class ApprovalHistory {
    private final List<ApprovalRecord> records;

    public ApprovalHistory() {
        this.records = new ArrayList<>();
    }

    public void recordSubmission(DoctorId supervisor, LocalDateTime timestamp) {
        records.add(new ApprovalRecord(ApprovalAction.SUBMITTED, supervisor, timestamp));
    }

    public boolean hasPendingApproval() {
        return records.stream()
            .filter(r -> r.action == ApprovalAction.SUBMITTED)
            .anyMatch(r -> r.approvalResult == null);
    }
}

// 5. 测量值对象 - Confidence
public final class Confidence {
    private final BigDecimal value;  // 0-100
    private final ConfidenceLevel level;

    public enum ConfidenceLevel { HIGH, MEDIUM, LOW }

    public Confidence(BigDecimal value) {
        this.value = value;
        this.level = determineLevel(value);
    }

    private ConfidenceLevel determineLevel(BigDecimal value) {
        if (value.compareTo(new BigDecimal("85")) >= 0) return ConfidenceLevel.HIGH;
        if (value.compareTo(new BigDecimal("60")) >= 0) return ConfidenceLevel.MEDIUM;
        return ConfidenceLevel.LOW;
    }

    public boolean isHighConfidence() {
        return level == ConfidenceLevel.HIGH;
    }
}
```

### 5.2 实体与聚合根

```
实体 vs 值对象对比：

┌──────────────────────┬─────────────────────────┬─────────────────────┐
│ 特性                 │ 值对象                   │ 实体                 │
├──────────────────────┼─────────────────────────┼─────────────────────┤
│ 标识                 │ 无标识，由属性定义      │ 有唯一标识ID       │
│ 生命周期             │ 短暂，快速创建销毁      │ 长期存在            │
│ 可变性               │ 不可变                  │ 可变                │
│ 数据库映射           │ 嵌入式或JSON            │ 专属表或列          │
│ 引用方式             │ 值传递                  │ 引用传递            │
│ 相等性比较           │ 基于属性值              │ 基于ID              │
└──────────────────────┴─────────────────────────┴─────────────────────┘

实体示例：
```

```java
// 审核记录实体 - 聚合内实体
public class ApprovalRecord extends Entity {
    private ApprovalRecordId recordId;          // 实体标识
    private ApprovalAction action;              // 操作类型：SUBMITTED/APPROVED/REJECTED
    private DoctorId actor;                     // 执行人
    private LocalDateTime timestamp;            // 操作时间
    private String comments;                    // 备注

    // 实体相等性判断
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApprovalRecord that = (ApprovalRecord) o;
        return recordId.equals(that.recordId);  // 仅基于ID比较
    }
}

// QC问题实体 - 聚合内实体
public class QCIssue extends Entity {
    private QCIssueId issueId;
    private QCIssueCategoryEnum category;       // technical/completeness/consistency/compliance
    private String issueType;                   // 具体问题类型
    private IssueSeverity severity;             // 严重程度
    private String description;
    private String suggestedAction;
}
```

---

## 6. 领域事件

### 6.1 领域事件设计

```
领域事件 = 发生在领域中的重要的、已完成的事情

事件的特点：
• 过去式命名：ImageReceived（已接收）而非ReceiveImage（接收）
• 不可变：创建后不能修改
• 包含上下文：聚合ID、时间戳、操作人等
• 支持追踪：分布式追踪ID、关联ID
```

### 6.2 核心领域事件定义

```java
// 基础事件类
public abstract class DomainEvent {
    private final String eventId;              // 事件唯一ID
    private final LocalDateTime occurredAt;    // 事件发生时间
    private final String correlationId;        // 关联ID（检查ID或患者ID）
    private final int version = 1;

    public DomainEvent(String correlationId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.correlationId = correlationId;
    }
}

// ============ 影像管理域事件 ============

// 1. 影像接收事件
public class ImageReceived extends DomainEvent {
    private final ImageId imageId;
    private final ExamId examId;
    private final SeriesId seriesId;
    private final String sopInstanceUid;
    private final String modality;
    private final long fileSizeBytes;
    private final String filePath;

    public ImageReceived(ImageId imageId, ExamId examId, SeriesId seriesId,
                        String sopInstanceUid, String modality, long fileSizeBytes,
                        String filePath) {
        super(examId.getValue());
        this.imageId = imageId;
        this.examId = examId;
        this.seriesId = seriesId;
        this.sopInstanceUid = sopInstanceUid;
        this.modality = modality;
        this.fileSizeBytes = fileSizeBytes;
        this.filePath = filePath;
    }
}

// 2. QC评分记录事件
public class QCScoreRecorded extends DomainEvent {
    private final ImageId imageId;
    private final BigDecimal score;
    private final String qcStatus;             // passed/failed
    private final List<String> issues;

    public QCScoreRecorded(ImageId imageId, BigDecimal score, String qcStatus, List<String> issues) {
        super(imageId.getValue());
        this.imageId = imageId;
        this.score = score;
        this.qcStatus = qcStatus;
        this.issues = issues;
    }
}

// 3. 存储层级迁移事件
public class StorageTierMigrated extends DomainEvent {
    private final ImageId imageId;
    private final String fromTier;
    private final String toTier;
    private final long durationMillis;
    private final BigDecimal compressionRatio;

    public StorageTierMigrated(ImageId imageId, String fromTier, String toTier,
                              long durationMillis, BigDecimal compressionRatio) {
        super(imageId.getValue());
        this.imageId = imageId;
        this.fromTier = fromTier;
        this.toTier = toTier;
        this.durationMillis = durationMillis;
        this.compressionRatio = compressionRatio;
    }
}

// ============ 诊断报告域事件 ============

// 4. 报告提交审核事件
public class ReportSubmittedForApproval extends DomainEvent {
    private final ReportId reportId;
    private final ExamId examId;
    private final DoctorId authorId;
    private final DoctorId supervisorId;

    public ReportSubmittedForApproval(ReportId reportId, ExamId examId,
                                     DoctorId authorId, DoctorId supervisorId) {
        super(examId.getValue());
        this.reportId = reportId;
        this.examId = examId;
        this.authorId = authorId;
        this.supervisorId = supervisorId;
    }
}

// 5. 报告审核通过事件
public class ReportApproved extends DomainEvent {
    private final ReportId reportId;
    private final ExamId examId;
    private final DoctorId approverId;
    private final LocalDateTime approvedAt;

    public ReportApproved(ReportId reportId, ExamId examId, DoctorId approverId) {
        super(examId.getValue());
        this.reportId = reportId;
        this.examId = examId;
        this.approverId = approverId;
        this.approvedAt = LocalDateTime.now();
    }
}

// 6. 报告发布事件
public class ReportPublished extends DomainEvent {
    private final ReportId reportId;
    private final ExamId examId;
    private final String pdfPath;
    private final LocalDateTime publishedAt;

    public ReportPublished(ReportId reportId, ExamId examId, String pdfPath) {
        super(examId.getValue());
        this.reportId = reportId;
        this.examId = examId;
        this.pdfPath = pdfPath;
        this.publishedAt = LocalDateTime.now();
    }
}

// ============ 临床决策域事件 ============

// 7. AI分析完成事件
public class AIAnalysisCompleted extends DomainEvent {
    private final InterpretationId interpretationId;
    private final ImageId imageId;
    private final ExamId examId;
    private final List<Finding> findings;
    private final BigDecimal confidence;
    private final long durationMillis;        // 分析耗时

    public AIAnalysisCompleted(InterpretationId interpretationId, ImageId imageId,
                              ExamId examId, List<Finding> findings,
                              BigDecimal confidence, long durationMillis) {
        super(examId.getValue());
        this.interpretationId = interpretationId;
        this.imageId = imageId;
        this.examId = examId;
        this.findings = findings;
        this.confidence = confidence;
        this.durationMillis = durationMillis;
    }
}

// 8. 医生反馈事件
public class AIFeedbackRecorded extends DomainEvent {
    private final InterpretationId interpretationId;
    private final DoctorId doctorId;
    private final String feedbackType;        // accept/reject/modify
    private final String comments;

    public AIFeedbackRecorded(InterpretationId interpretationId, DoctorId doctorId,
                             String feedbackType, String comments) {
        super(interpretationId.getValue());
        this.interpretationId = interpretationId;
        this.doctorId = doctorId;
        this.feedbackType = feedbackType;
        this.comments = comments;
    }
}

// ============ 患者信息域事件 ============

// 9. 过敏史记录事件
public class AllergyRecorded extends DomainEvent {
    private final PatientId patientId;
    private final String allergenName;
    private final String severity;            // mild/moderate/severe
    private final String reaction;

    public AllergyRecorded(PatientId patientId, String allergenName,
                          String severity, String reaction) {
        super(patientId.getValue());
        this.patientId = patientId;
        this.allergenName = allergenName;
        this.severity = severity;
        this.reaction = reaction;
    }
}
```

### 6.3 事件发布与订阅模式

```
事件处理流程：

┌───────────────────────────────────────────────────────────────┐
│                                                               │
│  1. 聚合根内发起业务操作                                      │
│     └─ image.recordQCScore(score)                            │
│                                                               │
│  2. 业务规则验证通过，状态变更                                │
│     └─ this.qcStatus = QCStatus.COMPLETED                    │
│                                                               │
│  3. 发布领域事件                                              │
│     └─ publish(new QCScoreRecorded(...))                     │
│                                                               │
│  4. 应用服务保存聚合根                                        │
│     └─ repository.save(image)                                │
│                                                               │
│  5. 事件发布器异步发送事件                                    │
│     ├─ 消息队列（Kafka/RabbitMQ）                            │
│     ├─ 数据库事件表                                          │
│     └─ 本地事件监听器                                        │
│                                                               │
│  6. 订阅者处理事件（异步）                                    │
│     ├─ ImageQCCompletedHandler                              │
│     ├─ WorkflowOrchestrationHandler                         │
│     └─ NotificationHandler                                   │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

---

## 7. 领域服务

### 7.1 领域服务设计原则

```
什么时候使用领域服务？

1. 操作跨越多个聚合
   ├─ 不能由单个聚合完成的业务操作
   ├─ 例：转诊操作 = 更新患者信息 + 更新检查状态 + 发送通知

2. 查询复杂业务规则
   ├─ 例：计算患者的最佳诊断方案（需要查询历史、AI建议、医学知识库）

3. 需要外部资源
   ├─ 例：上传文件到对象存储、调用AI推理服务

4. 事务边界管理
   ├─ 例：Saga分布式事务协调

避免使用领域服务：
✗ 简单的单聚合操作（应在聚合内实现）
✗ 技术性的操作（应使用应用服务）
✗ 缺乏业务含义的操作
```

### 7.2 核心领域服务

```java
// ============ 影像管理领域服务 ============

/**
 * DICOM处理服务
 * 职责：DICOM文件解析、患者匹配、数据抽取
 */
public interface DicomProcessingService {
    /**
     * 处理接收到的DICOM文件
     *
     * 业务规则：
     * 1. 校验DICOM格式和标签完整性
     * 2. 执行患者匹配或患者创建
     * 3. 提取关键元数据
     * 4. 检测重复（基于SOPInstanceUID）
     * 5. 创建DicomImage聚合
     */
    ProcessedDicom processDicomFile(File dicomFile) throws DicomProcessingException;
}

public class DicomProcessingServiceImpl implements DicomProcessingService {
    private final DicomParser parser;
    private final PatientRepository patientRepository;
    private final ImageRepository imageRepository;
    private final DuplicateDetectionService duplicateDetector;

    @Override
    public ProcessedDicom processDicomFile(File dicomFile) throws DicomProcessingException {
        // Step 1：解析DICOM文件
        DicomDataset dataset = parser.parse(dicomFile);

        // Step 2：验证必要标签
        validateMandatoryTags(dataset);

        // Step 3：患者匹配或创建
        PatientIdentifiers identifiers = extractPatientIdentifiers(dataset);
        Patient patient = patientRepository.findByIdentifiers(identifiers)
            .orElseGet(() -> createNewPatient(identifiers));

        // Step 4：检测重复
        String sopInstanceUid = dataset.getString(DicomTag.SOPInstanceUID);
        if (duplicateDetector.isDuplicate(sopInstanceUid)) {
            throw new DuplicateImageException("Image already exists: " + sopInstanceUid);
        }

        // Step 5：创建Image聚合
        DicomImage image = DicomImage.create(
            patient.getPatientId(),
            dataset.getString(DicomTag.StudyInstanceUID),
            dataset.getString(DicomTag.SeriesInstanceUID),
            sopInstanceUid,
            // ... 其他属性
        );

        return new ProcessedDicom(image, patient, dataset);
    }
}

/**
 * 质控执行服务
 * 职责：执行QC评估规则、判定是否通过、记录问题
 */
public interface ImageQualityControlService {
    /**
     * 执行影像质控
     *
     * 业务规则：
     * 1. 检查四个维度：技术质量、完整性、一致性、合规性
     * 2. 计算加权评分
     * 3. 生成QC问题清单
     * 4. 判定是否通过（评分≥70）
     */
    QCResult performQualityControl(DicomImage image) throws QCException;
}

public class ImageQualityControlServiceImpl implements ImageQualityControlService {
    private final TechnicalQualityEvaluator technicalEvaluator;
    private final CompletenessEvaluator completenessEvaluator;
    private final ConsistencyEvaluator consistencyEvaluator;
    private final ComplianceEvaluator complianceEvaluator;

    @Override
    public QCResult performQualityControl(DicomImage image) {
        // 评估四个维度
        TechnicalQualityScore techScore = technicalEvaluator.evaluate(image);    // 40%权重
        CompletenessScore completeScore = completenessEvaluator.evaluate(image); // 30%权重
        ConsistencyScore consistScore = consistencyEvaluator.evaluate(image);    // 20%权重
        ComplianceScore complianceScore = complianceEvaluator.evaluate(image);   // 10%权重

        // 计算加权总分
        BigDecimal totalScore = techScore.getValue().multiply(new BigDecimal("0.4"))
            .add(completeScore.getValue().multiply(new BigDecimal("0.3")))
            .add(consistScore.getValue().multiply(new BigDecimal("0.2")))
            .add(complianceScore.getValue().multiply(new BigDecimal("0.1")));

        // 生成问题清单
        List<QCIssue> issues = generateQCIssues(
            techScore, completeScore, consistScore, complianceScore
        );

        // 判定是否通过
        boolean passed = totalScore.compareTo(new BigDecimal("70")) >= 0;

        return new QCResult(totalScore, passed, issues);
    }
}

// ============ 诊断报告领域服务 ============

/**
 * 报告生成服务
 * 职责：根据AI结果和历史数据生成报告初稿
 */
public interface ReportGenerationService {
    /**
     * 生成诊断报告初稿
     *
     * 业务规则：
     * 1. 匹配合适的报告模板（根据模态、部位、年龄）
     * 2. AI结果预填报告内容
     * 3. 查询历史检查进行对比
     * 4. 医学术语标准化
     */
    ReportDraft generateReportDraft(ExamId examId, AIInterpretation aiInterpretation);
}

public class ReportGenerationServiceImpl implements ReportGenerationService {
    private final ReportTemplateRepository templateRepository;
    private final AIInterpretationRepository aiRepository;
    private final ExaminationRepository examRepository;
    private final ReportContentGenerator contentGenerator;
    private final MedicalTerminologyNormalizer terminologyNormalizer;

    @Override
    public ReportDraft generateReportDraft(ExamId examId, AIInterpretation aiInterpretation) {
        // Step 1：获取检查信息
        Examination exam = examRepository.findById(examId)
            .orElseThrow(() -> new ExaminationNotFoundException(examId));

        // Step 2：选择报告模板
        ReportTemplate template = templateRepository.findOptimal(
            exam.getModality(),
            exam.getExaminationPart(),
            exam.getPatient().getAge()
        );

        // Step 3：从AI结果预填内容
        ReportFindings findings = contentGenerator.generateFindings(
            aiInterpretation.getEnsembleResult()
        );

        // Step 4：查询历史检查进行对比
        List<DiagnosticReport> historyReports = getPatientHistoryReports(
            exam.getPatientId(), 3
        );
        String comparativeAnalysis = generateComparativeAnalysis(
            findings, historyReports
        );

        // Step 5：术语标准化
        ReportImpression impression = new ReportImpression(
            terminologyNormalizer.normalize(findings.getContent())
        );

        return new ReportDraft(template, findings, impression, comparativeAnalysis);
    }
}

/**
 * 报告审核流程服务
 * 职责：管理报告审核的多级流程
 */
public interface ReportApprovalService {
    /**
     * 执行报告审核流程
     *
     * 业务规则：
     * 1. 一级审核：科室内高级医生
     * 2. 二级审核（可选）：科室主任（针对复杂或异常报告）
     * 3. 审核通过后可以发布
     * 4. 审核拒绝后必须返回作者修改
     */
    void submitForApproval(DiagnosticReport report, DoctorId supervisor);

    ApprovalResult approve(ReportId reportId, DoctorId approver, String comments);

    ApprovalResult reject(ReportId reportId, DoctorId rejector, String reason);
}

// ============ 临床决策领域服务 ============

/**
 * AI诊断融合服务
 * 职责：融合多个AI模型的结果，计算置信度
 */
public interface AIEnsembleService {
    /**
     * 融合多模型结果
     *
     * 业务规则：
     * 1. 根据影像模态选择应用的模型集合
     * 2. 对各模型结果进行空间对齐（NMS）
     * 3. 加权投票融合（权重基于模型历史AUC）
     * 4. 计算不确定度和置信区间
     * 5. 根据置信度判定是否需要医生确认
     */
    EnsembleResult ensembleResults(List<ModelResult> modelResults);
}

public class AIEnsembleServiceImpl implements AIEnsembleService {
    private final ModelWeightRepository modelWeights;
    private final SpatialAlignmentService alignmentService;
    private final UncertaintyCalculator uncertaintyCalc;

    @Override
    public EnsembleResult ensembleResults(List<ModelResult> modelResults) {
        // Step 1：空间对齐
        List<AlignedResult> aligned = modelResults.stream()
            .map(alignmentService::align)
            .collect(toList());

        // Step 2：加权投票
        Map<String, List<AlignedResult>> groupedByRegion = groupByAnatomicalRegion(aligned);
        List<Finding> fusedFindings = groupedByRegion.entrySet().stream()
            .map(entry -> weightedVote(entry.getValue()))
            .collect(toList());

        // Step 3：计算不确定度
        UncertaintyBounds bounds = uncertaintyCalc.calculate(modelResults);

        // Step 4：确定置信度等级
        Confidence confidence = determineConfidenceLevel(fusedFindings, bounds);

        return new EnsembleResult(fusedFindings, confidence, bounds);
    }
}

/**
 * 诊疗建议服务
 * 职责：基于诊断结果生成后续诊疗建议
 */
public interface ClinicalRecommendationService {
    /**
     * 生成诊疗建议
     *
     * 业务规则：
     * 1. 根据诊断结果查询临床指南（NCCN/EORTC等）
     * 2. 考虑患者的合并症、用药史、过敏史
     * 3. 生成分级建议（强烈推荐/条件推荐/可考虑）
     * 4. 引用相关文献
     */
    List<ClinicalRecommendation> generateRecommendations(
        DiagnosticReport report,
        Patient patient
    );
}
```

---

## 8. 通用语言

### 8.1 领域术语表

```
通用语言 = 被所有人理解并使用的共同词汇（包括医生、开发者、产品经理）

┌────────────────────────────────────────────────────────────────────┐
│  影像管理领域                                                       │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  • Study（检查）：一次完整的医学影像检查                            │
│    └─ 包含多个Series，代表一次诊疗事件                              │
│                                                                    │
│  • Series（序列）：检查中的一个影像序列                             │
│    └─ 例：CT胸部检查中的"平扫"、"增强"两个系列                      │
│                                                                    │
│  • Image（影像）：单个DICOM文件                                     │
│    └─ 最小的存储和处理单位                                         │
│                                                                    │
│  • Modality（模态）：影像类型                                       │
│    └─ CT、MRI、DR、CR、US、PET、NM 等标准DICOM模态                 │
│                                                                    │
│  • StudyInstanceUID / SeriesInstanceUID / SOPInstanceUID           │
│    └─ DICOM标准的全球唯一标识符（UID）                             │
│                                                                    │
│  • DICOM Tags（DICOM标签）：                                       │
│    ├─ (0010,0010) PatientName：患者姓名                           │
│    ├─ (0010,0030) PatientBirthDate：患者出生日期                  │
│    ├─ (0008,0060) Modality：影像模态                               │
│    └─ (0020,000D) StudyInstanceUID：检查UID                       │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│  诊断报告领域                                                       │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  • Report（报告）：完整的诊断报告                                    │
│    └─ 包含 Findings、Impression、Recommendation                    │
│                                                                    │
│  • Findings（检查所见）：医生对影像的客观描述                        │
│    └─ "左肺下叶见一枚约15mm的结节灶"                                │
│                                                                    │
│  • Impression（诊断意见）：医学诊断结论                              │
│    └─ "考虑左肺下叶恶性肿瘤，建议进一步检查"                        │
│                                                                    │
│  • Recommendation（建议）：后续诊疗建议                              │
│    └─ "建议进行PET-CT以评估肿瘤分期"                                │
│                                                                    │
│  • ReportStatus（报告状态）：                                       │
│    ├─ DRAFT：草稿（医生正在编辑）                                  │
│    ├─ PENDING_APPROVAL：待审核（等待上级审批）                      │
│    ├─ APPROVED：已审核（通过审批，待发布）                          │
│    ├─ PUBLISHED：已发布（患者/医生可见）                            │
│    └─ ARCHIVED：已归档                                              │
│                                                                    │
│  • Approval（审核）：报告的审核过程                                  │
│    ├─ Submitted（已提交）：作者提交等待审核                         │
│    ├─ Approved（已批准）：审核者批准                                │
│    └─ Rejected（已拒绝）：审核者拒绝，要求修改                      │
│                                                                    │
│  • Amendment（修改）：对已发布报告的修改                            │
│    └─ 需要重新审核，产生新版本                                     │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│  临床决策支持领域                                                   │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  • Finding（AI发现）：AI检测到的病灶或异常                          │
│    ├─ 位置：解剖位置                                                │
│    ├─ 属性：大小、形状、密度等                                      │
│    └─ 风险级别：良性/恶性概率                                       │
│                                                                    │
│  • Confidence（置信度）：AI对该发现的确定程度                        │
│    └─ 0-100%，分为 HIGH(>85%) / MEDIUM(60-85%) / LOW(<60%)        │
│                                                                    │
│  • Ensemble（融合）：多模型结果的加权融合                            │
│    ├─ 对齐各模型的输出                                              │
│    ├─ 按权重投票                                                    │
│    └─ 计算不确定度区间                                              │
│                                                                    │
│  • DoctorFeedback（医生反馈）：医生对AI结果的评价                    │
│    ├─ ACCEPT：接受AI建议                                            │
│    ├─ REJECT：拒绝AI建议                                            │
│    └─ MODIFY：修改AI建议                                            │
│                                                                    │
│  • CorrectnessRate（正确率）：AI在该类型影像上的历史准确率          │
│    └─ 用于计算模型权重                                              │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│  患者信息领域                                                       │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  • Patient（患者）：患者个体                                         │
│    └─ 医疗活动的中心对象                                            │
│                                                                    │
│  • PatientNo（患者号）：患者在医院的唯一标识                        │
│    └─ 例如：12345678 或 HN20260325001                              │
│                                                                    │
│  • MedicalRecord（病历）：患者的完整医学记录                        │
│    └─ 包括既往史、家族史、用药史、过敏史等                          │
│                                                                    │
│  • Allergy（过敏史）：患者的过敏信息                                │
│    └─ 重要的临床决策参考                                            │
│                                                                    │
│  • ExaminationHistory（检查历史）：患者的所有检查记录                │
│    └─ 用于对比分析和长期评估                                       │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

### 8.2 通用语言使用指南

```
统一命名规范（确保所有代码、文档、讨论中术语一致）：

❌ 错误的做法：
  • 代码中：image / dcm / imageFile / dicomImage 混用
  • 文档中："影像" / "image" / "DICOM文件" 混用
  • 讨论中：医生说"序列"，开发者听成"文件"

✅ 正确的做法：
  • 统一使用：DicomImage（类名）、image（变量名）、影像/DICOM影像（文档中文名）
  • Series / Series（代码） / 序列（中文文档）
  • Pattern：英文编码中使用通用语言的英文版本
         中文文档中使用通用语言的中文版本，并在首次出现时标注英文
```

---

## 9. 流程建模

### 9.1 检查生命周期流程

```
检查的完整生命周期（从申请到归档）

  ┌─────────────────────────────────────────────────────────────────────────┐
  │                          Examination Life Cycle                          │
  │                                                                         │
  │  1️⃣ 申请阶段 (Request Phase)                                          │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 临床医生提出检查申请                                            │   │
  │  │ • 创建Examination聚合，初始状态 = REQUESTED                      │   │
  │  │ • 触发事件：ExaminationRequested                                │   │
  │  │ • SLA类型判定：EMERGENCY/NORMAL/COMPLEX/CHECKUP                 │   │
  │  │ • 设置SLA截止时间                                              │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  2️⃣ 排队阶段 (Queuing Phase)                                         │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 放射技师接收检查                                                │   │
  │  │ • 根据设备和人力安排检查时间                                      │   │
  │  │ • Examination.status = SCHEDULED                               │   │
  │  │ • 通知患者准备信息                                              │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  3️⃣ 检查执行阶段 (Examination Phase)                                 │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 放射技师执行检查（操作DICOM设备）                              │   │
  │  │ • Examination.status = IN_PROGRESS                             │   │
  │  │ • DICOM设备持续推送影像至PACS                                   │   │
  │  │ • 触发事件：ExaminationStarted                                 │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  4️⃣ 影像接收阶段 (Image Ingestion Phase)                             │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ DicomIngestAgent处理流程：                                      │   │
  │  │ • 接收DICOM C-STORE消息                                        │   │
  │  │ • 创建DicomImage聚合 (status=received)                        │   │
  │  │ • 存储至HOT层（本地SSD）                                        │   │
  │  │ • 发布事件：ImageReceived                                      │   │
  │  │ • 触发事件：ExaminationImagesReceived                          │   │
  │  │ 并发处理：100帧/秒，支持断点续传                                │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  5️⃣ 质控阶段 (QC Phase) ◀─── ImageQCAgent                           │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 接收 ImageReceived 事件                                       │   │
  │  │ • 执行四维度评估：技术质量/完整性/一致性/合规性                  │   │
  │  │ • 计算QC评分（0-100）                                          │   │
  │  │ ┌─ 评分≥70 → QC通过 ────────────────────────────────────────┐  │   │
  │  │ │ • 设置 image.qcStatus = PASSED                          │  │   │
  │  │ │ • 发布事件：QCPassedEvent                                │  │   │
  │  │ │ • Examination.qcPassedAt = now()                       │  │   │
  │  │ │ • 自动进入下一阶段（AI分析或人工阅片）                    │  │   │
  │  │ │                                                         │  │   │
  │  │ └─ 评分<70 → QC不通过 ────────────────────────────────────┤  │   │
  │  │   • 设置 image.qcStatus = FAILED                         │  │   │
  │  │   • 创建QCIssue记录（问题清单）                            │  │   │
  │  │   • 发布事件：QCFailedEvent                               │  │   │
  │  │   • Examination.status = QC_FAILED                       │  │   │
  │  │   • 通知放射技师 → 重照流程                               │  │   │
  │  └─────────────────────────────────────────────────────────────┘  │   │
  │                              ▼                                          │
  │  6️⃣ AI分析阶段 (AI Analysis Phase) ◀─── AIDiagnosisAgent            │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 接收 QCPassedEvent                                           │   │
  │  │ • 根据Modality路由到对应AI模型                                  │   │
  │  │   ├─ CT → 肺结节检测 + 骨折识别 + 颅内出血 (30s)                │   │
  │  │   ├─ MRI → 脑肿瘤分割 + 脊柱分析 (60s)                          │   │
  │  │   ├─ DR → 胸片分析 (10s)                                       │   │
  │  │   └─ US → 器官测量 (15s)                                       │   │
  │  │ • 融合多模型结果（Ensemble）                                    │   │
  │  │ • 计算置信度和不确定度                                          │   │
  │  │ • 创建AIInterpretation聚合                                      │   │
  │  │ • 发布事件：AIAnalysisCompleted                               │   │
  │  │ • Examination.aiStatus = COMPLETED                           │   │
  │  │ 降级处理：GPU不可用 → CPU推理或跳过AI分析                       │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  7️⃣ 报告生成阶段 (Report Generation Phase) ◀─── ReportGenerationAgent │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 接收 AIAnalysisCompleted 事件                                 │   │
  │  │ • 创建DiagnosticReport聚合 (status=DRAFT)                       │   │
  │  │ • 自动步骤：                                                    │   │
  │  │   ├─ 匹配报告模板（模态/部位/年龄）                              │   │
  │  │   ├─ AI结果预填 "检查所见"                                      │   │
  │  │   ├─ 查询历史检查进行对比分析                                    │   │
  │  │   └─ 医学术语标准化（ICD编码）                                  │   │
  │  │ • 医生编辑：完善"诊断意见"和"建议"                               │   │
  │  │ • 发布事件：ReportDraftCreated                                │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  8️⃣ 报告审核阶段 (Approval Phase) ◀─── ReportApprovalService        │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 医生编辑完成，提交审核                                         │   │
  │  │ • DiagnosticReport.status = PENDING_APPROVAL                   │   │
  │  │ • 发布事件：ReportSubmittedForApproval                         │   │
  │  │ • 通知上级医生                                                  │   │
  │  │ ┌─ 审核通过 ─────────────────────────────────────────────────┐  │   │
  │  │ │ • DiagnosticReport.status = APPROVED                    │  │   │
  │  │ │ • 发布事件：ReportApproved                              │  │   │
  │  │ │ • Examination.status = REPORT_APPROVED                 │  │   │
  │  │ │                                                        │  │   │
  │  │ └─ 审核拒绝 ─────────────────────────────────────────────────┤  │   │
  │  │   • DiagnosticReport.status = DRAFT（返回作者修改）      │  │   │
  │  │   • 发布事件：ReportRejected                             │  │   │
  │  │   • 通知作者，说明拒绝原因                                │  │   │
  │  └─────────────────────────────────────────────────────────────┘  │   │
  │                              ▼                                          │
  │  9️⃣ 报告发布阶段 (Publication Phase)                               │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ • 已审核报告生成PDF（包含医生签名）                              │   │
  │  │ • DiagnosticReport.status = PUBLISHED                          │   │
  │  │ • Examination.status = COMPLETED                               │   │
  │  │ • 发布事件：ReportPublished                                   │   │
  │  │ • 通知患者报告可查看                                            │   │
  │  │ • 发送自动通知（短信/邮件）                                      │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                              ▼                                          │
  │  🔟 存储归档阶段 (Storage & Archive Phase) ◀─── StorageSchedulerAgent │
  │  ┌──────────────────────────────────────────────────────────────────┐   │
  │  │ 分级存储策略：                                                   │   │
  │  │ • HOT层（0-30天）：SSD，访问延迟<100ms                           │   │
  │  │ • WARM层（30天-1年）：对象存储，访问延迟<1s                       │   │
  │  │ • COLD层（1年以上）：磁带库，访问延迟<5min                        │   │
  │  │ 自动迁移触发：                                                  │   │
  │  │ • 定时扫描（每日02:00）                                         │   │
  │  │ • 存储水位告警（>80%）                                          │   │
  │  │ • 用户访问时预热COLD层                                          │   │
  │  └──────────────────────────────────────────────────────────────────┘   │
  │                                                                         │
  └─────────────────────────────────────────────────────────────────────────┘
```

### 9.2 事件驱动的流程编排

```
使用 WorkflowOrchestrationAgent 和事件驱动进行状态管理：

EventFlowDiagram:

  Examination Request
        │
        ▼
  [REQUESTED]
        │
        ├─ ExaminationScheduled Event
        │       ▼
        │   [SCHEDULED]
        │       │
        │       ├─ ExaminationStarted Event
        │       │       ▼
        │       │   [IN_PROGRESS]
        │       │       │
        │       │       ├─ ImagesReceived Event
        │       │       │       ▼
        │       │       │   [IMAGES_RECEIVED]
        │       │       │       │
        │       │       │       ├─ (ImageQCAgent评分)
        │       │       │       │
        │       │       │       ├─ QCPassedEvent
        │       │       │       │       ▼
        │       │       │       │   [QC_PASSED]
        │       │       │       │       │
        │       │       │       │       ├─ (AIDiagnosisAgent分析)
        │       │       │       │       │
        │       │       │       │       ├─ AIAnalysisCompleted Event
        │       │       │       │       │       ▼
        │       │       │       │       │   [AI_ANALYSIS_COMPLETED]
        │       │       │       │       │       │
        │       │       │       │       │       ├─ (ReportGenerationAgent生成)
        │       │       │       │       │       │
        │       │       │       │       │       ├─ ReportDraftCreated Event
        │       │       │       │       │       │       ▼
        │       │       │       │       │       │   [WAITING_FOR_REPORT]
        │       │       │       │       │       │       │
        │       │       │       │       │       │       ├─ (医生编辑提交)
        │       │       │       │       │       │       │
        │       │       │       │       │       │       ├─ ReportSubmittedForApproval Event
        │       │       │       │       │       │       │       ▼
        │       │       │       │       │       │       │   [REPORT_PENDING_APPROVAL]
        │       │       │       │       │       │       │       │
        │       │       │       │       │       │       │       ├─ ReportApproved Event
        │       │       │       │       │       │       │       │       ▼
        │       │       │       │       │       │       │       │   [REPORT_APPROVED]
        │       │       │       │       │       │       │       │       │
        │       │       │       │       │       │       │       │       ├─ ReportPublished Event
        │       │       │       │       │       │       │       │       │       ▼
        │       │       │       │       │       │       │       │       │   [COMPLETED]
        │       │       │       │       │       │       │       │
        │       │       │       │       │       │       │       └─ ReportRejected Event
        │       │       │       │       │       │       │               ▼
        │       │       │       │       │       │       │         [WAITING_FOR_REPORT]
        │       │       │       │       │       │       │              (循环)
        │       │       │       │
        │       │       │       └─ QCFailedEvent
        │       │       │               ▼
        │       │       │           [QC_FAILED]
        │       │       │               │
        │       │       │               ├─ 通知技师重照
        │       │       │               │
        │       │       │               └─ 等待重新审视或重照
        │       │
        └─── SLA监控（WorkflowOrchestrationAgent）
             ├─ 急诊：30分钟内完成报告 → P0告警
             ├─ 普通：24小时内完成报告 → P1告警
             └─ 复杂：48小时内完成报告 → P1告警
```

---

## 10. 数据建模

### 10.1 聚合与存储映射

```
聚合根 → 数据库表映射规则：

原则1：每个聚合根一张主表
  ├─ DicomImage → cf_image
  ├─ DiagnosticReport → cf_diagnosis_report
  ├─ AIInterpretation → cf_ai_interpret
  └─ Patient → cf_patient

原则2：聚合内的实体 → 关联表或JSON字段
  ├─ 选项A：专表（如果实体会被频繁查询或独立管理）
  │   └─ ApprovalRecord → cf_report_approval（专表）
  │
  └─ 选项B：JSON字段（如果实体是只读或快照）
      └─ QCIssue → cf_image.qc_issues（JSON字段）

原则3：值对象 → 字段或JSON嵌入
  ├─ QCScore → cf_image.qc_score（单一字段）
  ├─ FilePath → cf_image.file_path（单一字段）
  └─ StorageTier → cf_image.storage_tier（enum字段）

原则4：聚合间引用 → 仅保存ID，不保存对象
  ├─ DicomImage 中的 examId：值为 cf_examination.id，不嵌入对象
  ├─ DiagnosticReport 中的 patientId：值为 cf_patient.id
  └─ 跨聚合查询需通过应用层协调或数据库JOIN
```

### 10.2 完整数据库设计

```sql
-- ============ 影像管理数据库表 ============

-- 1. 影像基础表 (cf_image)
CREATE TABLE cf_image (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    exam_id               BIGINT          NOT NULL,           -- 外部聚合：Examination
    series_id             BIGINT          NOT NULL,           -- Series聚合的ID
    patient_id            BIGINT          NOT NULL,           -- 外部聚合：Patient

    -- 影像标识
    sop_instance_uid      VARCHAR(128)    NOT NULL UNIQUE,    -- DICOM标准UID
    series_instance_uid   VARCHAR(128)    NOT NULL,
    study_instance_uid    VARCHAR(128)    NOT NULL,

    -- 影像属性
    modality              VARCHAR(10)     NOT NULL,           -- CT/MRI/DR/CR/US
    rows                  INT             NOT NULL,
    columns               INT             NOT NULL,
    received_timestamp    DATETIME2       NOT NULL,

    -- 文件管理
    file_path             VARCHAR(500)    NOT NULL,           -- 相对路径或MinIO Key
    file_size_bytes       BIGINT          NOT NULL,

    -- QC质控字段
    qc_score              DECIMAL(5,2)    NULL,               -- 0-100
    qc_status             VARCHAR(20)     NULL,               -- pending/passed/failed/manual_review
    qc_issues             NVARCHAR(MAX)   NULL,               -- JSON格式 [{"type":"...","severity":"..."}]
    qc_evaluated_by       BIGINT          NULL,               -- QC评估人ID
    qc_evaluated_at       DATETIME2       NULL,

    -- 存储分级
    storage_tier          VARCHAR(10)     NOT NULL DEFAULT 'hot',  -- hot/warm/cold
    last_accessed_at      DATETIME2       NULL,

    -- 标记
    is_key_image          BIT             NOT NULL DEFAULT 0,
    is_desensitized       BIT             NOT NULL DEFAULT 0,

    -- 缩略图
    thumbnail_path        VARCHAR(500)    NULL,

    -- 版本与审计
    version               INT             NOT NULL DEFAULT 1,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    created_by            BIGINT          NOT NULL,
    updated_by            BIGINT          NOT NULL,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    -- 索引
    CONSTRAINT fk_image_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT fk_image_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id)
);

CREATE INDEX idx_image_exam_id ON cf_image(exam_id);
CREATE INDEX idx_image_sop_uid ON cf_image(sop_instance_uid);
CREATE INDEX idx_image_qc_status ON cf_image(qc_status);
CREATE INDEX idx_image_storage_tier ON cf_image(storage_tier);
CREATE UNIQUE INDEX uidx_image_sop ON cf_image(sop_instance_uid);

-- 2. Series聚合表
CREATE TABLE cf_series (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    exam_id               BIGINT          NOT NULL,
    series_instance_uid   VARCHAR(128)    NOT NULL UNIQUE,
    series_number        INT             NOT NULL,
    series_description    VARCHAR(255)    NULL,
    modality              VARCHAR(10)     NOT NULL,
    instance_count        INT             NOT NULL DEFAULT 0,
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),

    CONSTRAINT fk_series_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id)
);

-- ============ 诊断报告数据库表 ============

-- 3. 诊断报告主表
CREATE TABLE cf_diagnosis_report (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    exam_id               BIGINT          NOT NULL UNIQUE,    -- 一个检查对应一份报告
    patient_id            BIGINT          NOT NULL,

    -- 报告内容
    template_id           BIGINT          NULL,
    findings              NVARCHAR(MAX)   NULL,               -- 检查所见（富文本）
    impression            NVARCHAR(MAX)   NULL,               -- 诊断意见
    recommendation        NVARCHAR(MAX)   NULL,               -- 建议

    -- 状态与版本
    status                VARCHAR(30)     NOT NULL,           -- draft/pending_approval/approved/published/archived
    version               INT             NOT NULL DEFAULT 1,

    -- 作者与审核
    author_id             BIGINT          NOT NULL,
    first_author_id       BIGINT          NULL,               -- 一作（若有多作者）

    -- 输出
    pdf_path              VARCHAR(500)    NULL,
    pdf_generated_at      DATETIME2       NULL,

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    published_at          DATETIME2       NULL,
    archived_at           DATETIME2       NULL,
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_report_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id),
    CONSTRAINT fk_report_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id),
    CONSTRAINT fk_report_author FOREIGN KEY (author_id) REFERENCES sys_user(user_id)
);

CREATE INDEX idx_report_exam_id ON cf_diagnosis_report(exam_id);
CREATE INDEX idx_report_status ON cf_diagnosis_report(status);
CREATE INDEX idx_report_published_at ON cf_diagnosis_report(published_at);

-- 4. 报告审核历史表
CREATE TABLE cf_report_approval (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    report_id             BIGINT          NOT NULL,

    -- 审核记录
    action                VARCHAR(30)     NOT NULL,           -- submitted/approved/rejected
    actor_id              BIGINT          NOT NULL,
    comments              NVARCHAR(500)   NULL,

    -- 时间戳
    action_at             DATETIME2       NOT NULL DEFAULT GETDATE(),

    -- 审核结果（仅对approved/rejected）
    is_approved           BIT             NULL,
    approval_reason       NVARCHAR(500)   NULL,

    CONSTRAINT fk_approval_report FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(id)
);

CREATE INDEX idx_approval_report_id ON cf_report_approval(report_id);
CREATE INDEX idx_approval_action ON cf_report_approval(action);

-- 5. 报告版本历史表
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
    modified_at           DATETIME2       NOT NULL DEFAULT GETDATE(),
    modification_reason   NVARCHAR(500)   NULL,

    CONSTRAINT fk_version_report FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(id),
    CONSTRAINT uq_version UNIQUE (report_id, version)
);

-- ============ 临床决策数据库表 ============

-- 6. AI解读表
CREATE TABLE cf_ai_interpret (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    image_id              BIGINT          NOT NULL,
    exam_id               BIGINT          NOT NULL,

    -- 分析元数据
    modality              VARCHAR(10)     NOT NULL,
    started_at            DATETIME2       NOT NULL,
    completed_at          DATETIME2       NULL,
    duration_ms           INT             NULL,

    -- 模型结果
    model_results         NVARCHAR(MAX)   NOT NULL,           -- JSON: [{modelId, findings, confidence}]
    ensemble_result       NVARCHAR(MAX)   NULL,               -- JSON: {fusedFindings, weights}

    -- 置信度
    confidence            DECIMAL(5,2)    NULL,               -- 0-100
    confidence_level      VARCHAR(10)     NULL,               -- HIGH/MEDIUM/LOW
    uncertainty_bounds    NVARCHAR(MAX)   NULL,               -- JSON: {lower, upper, percentile}

    -- 医生反馈
    doctor_feedback       NVARCHAR(MAX)   NULL,               -- JSON: {feedbackType, comments, timestamp}

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_interpret_image FOREIGN KEY (image_id) REFERENCES cf_image(id),
    CONSTRAINT fk_interpret_exam FOREIGN KEY (exam_id) REFERENCES cf_examination(id)
);

CREATE INDEX idx_interpret_exam_id ON cf_ai_interpret(exam_id);
CREATE INDEX idx_interpret_confidence ON cf_ai_interpret(confidence);

-- ============ 患者信息数据库表 ============

-- 7. 患者主表
CREATE TABLE cf_patient (
    id                    BIGINT          NOT NULL PRIMARY KEY,

    -- 患者标识
    patient_no            VARCHAR(50)     NOT NULL UNIQUE,    -- 医院患者号
    patient_name          VARCHAR(100)    NOT NULL,
    patient_id_number     VARCHAR(20)     NULL,               -- 身份证号（加密存储）

    -- 基本信息
    gender                CHAR(1)         NOT NULL,           -- M/F/U
    birth_date            DATE            NOT NULL,

    -- 联系方式
    phone                 VARCHAR(20)     NULL,
    email                 VARCHAR(100)    NULL,

    -- 病历相关
    medical_history       NVARCHAR(MAX)   NULL,               -- JSON格式病历
    allergy_history       NVARCHAR(MAX)   NULL,               -- JSON格式过敏史
    medication_history    NVARCHAR(MAX)   NULL,               -- JSON格式用药史

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    updated_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT uq_patient_id UNIQUE (patient_id_number, del_flag)
);

CREATE INDEX idx_patient_no ON cf_patient(patient_no);
CREATE INDEX idx_patient_name ON cf_patient(patient_name);

-- 8. 过敏信息表
CREATE TABLE cf_patient_allergy (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    patient_id            BIGINT          NOT NULL,

    allergen_name         VARCHAR(100)    NOT NULL,
    severity              VARCHAR(20)     NOT NULL,           -- mild/moderate/severe
    reaction              VARCHAR(255)    NULL,

    recorded_by           BIGINT          NOT NULL,
    recorded_at           DATETIME2       NOT NULL DEFAULT GETDATE(),
    del_flag              CHAR(1)         NOT NULL DEFAULT '0',

    CONSTRAINT fk_allergy_patient FOREIGN KEY (patient_id) REFERENCES cf_patient(id)
);

-- ============ 存储管理数据库表 ============

-- 9. 存储层级迁移记录表
CREATE TABLE cf_storage_migration (
    id                    BIGINT          NOT NULL PRIMARY KEY,
    image_id              BIGINT          NOT NULL,

    -- 迁移信息
    from_tier             VARCHAR(10)     NULL,               -- hot/warm/cold
    to_tier               VARCHAR(10)     NOT NULL,
    from_path             VARCHAR(500)    NULL,
    to_path               VARCHAR(500)    NOT NULL,

    -- 存储信息
    file_size_bytes       BIGINT          NOT NULL,
    compress_ratio        DECIMAL(5,4)    NULL,               -- 压缩比

    -- 迁移过程
    trigger_reason        VARCHAR(100)    NOT NULL,           -- scheduled/watermark/access/capacity
    duration_ms           INT             NULL,
    status                VARCHAR(20)     NOT NULL DEFAULT 'in_progress',  -- completed/failed

    -- 审计
    created_at            DATETIME2       NOT NULL DEFAULT GETDATE(),
    completed_at          DATETIME2       NULL,

    CONSTRAINT fk_migration_image FOREIGN KEY (image_id) REFERENCES cf_image(id)
);

CREATE INDEX idx_migration_image_id ON cf_storage_migration(image_id);
CREATE INDEX idx_migration_status ON cf_storage_migration(status);
```

---

## 11. 限界上下文集成

### 11.1 上下文间通信协议

```
限界上下文间的集成方案（Anti-Corruption Layer）：

  上下文A ──┐                      ┌──┐ 上下文B
            │                      │  │
            │ 使用自己的通用语言    │  │ 使用自己的通用语言
            │                      │  │
            └──────┬───────────────┘  │
                   │                   │
                   ▼                   │
         ┌──────────────────┐          │
         │  Anti-Corruption │          │
         │  Layer Adapter   │          │
         │                  │          │
         │ 翻译语言 ←────────┴──────────┘
         │ 转换模型
         │ 处理版本差异
         └──────────────────┘
                   ▲
                   │
         使用统一的通信协议
         (Domain Event / API)
```

### 11.2 具体集成案例

#### Case 1: 影像管理 → 诊断报告集成

```java
/**
 * 影像管理 BC 内部事件
 */
public class ImageReceived extends DomainEvent {
    private ImageId imageId;
    private ExamId examId;
    private String modality;
    // ... 其他影像相关字段
}

/**
 * Anti-Corruption Layer: 事件翻译器
 */
public class ImageReceivedToReportEventTranslator {
    public void handle(ImageReceived event) {
        // 翻译为诊断报告BC能理解的数据结构
        ExamReadyForReporting reportEvent = new ExamReadyForReporting(
            event.getExamId(),
            event.getModality(),
            extractReportRelatedInfo(event)
        );

        // 发布至诊断报告BC
        reportEventPublisher.publish(reportEvent);
    }
}

/**
 * 诊断报告 BC 订阅事件
 */
@Service
public class ReportInitializationService {
    @EventListener
    public void onExamReadyForReporting(ExamReadyForReporting event) {
        // 创建报告初稿
        DiagnosticReport report = DiagnosticReport.createDraft(
            event.getExamId(),
            event.getReportableContent()
        );
        reportRepository.save(report);
    }
}
```

#### Case 2: 临床决策 → 诊断报告集成

```java
/**
 * 临床决策 BC 内部事件
 */
public class AIAnalysisCompleted extends DomainEvent {
    private InterpretationId interpretationId;
    private List<Finding> findings;
    private BigDecimal confidence;
    // ...
}

/**
 * Anti-Corruption Layer: 数据转换
 */
public class AIResultToReportContentAdapter {

    public ReportContent convert(AIAnalysisCompleted aiEvent) {
        // 将AI的technical findings转换为医学语言
        String medicalFinding = translateToMedicalLanguage(
            aiEvent.getFindings()
        );

        // 评估置信度是否足够自动填充
        boolean autoFillEligible = aiEvent.getConfidence()
            .compareTo(new BigDecimal("85")) >= 0;

        return new ReportContent(
            medicalFinding,
            autoFillEligible,
            aiEvent.getInterpretationId()
        );
    }
}

/**
 * 诊断报告 BC 订阅AI事件
 */
@Service
public class AIReportIntegrationService {
    private final AIResultToReportContentAdapter adapter;
    private final ReportRepository reportRepository;

    @EventListener
    public void onAIAnalysisCompleted(AIAnalysisCompleted event) {
        // 转换AI结果
        ReportContent content = adapter.convert(event);

        // 查找对应的报告
        DiagnosticReport report = reportRepository.findByExamId(
            event.getExamId()
        ).orElseThrow();

        // 自动或建议填充内容
        if (content.isAutoFillEligible()) {
            report.autoFillFindingsFromAI(content);
        } else {
            report.suggestFindingsFromAI(content);
        }

        reportRepository.save(report);
    }
}
```

#### Case 3: 跨界上下文 REST API调用

```java
/**
 * 患者信息BC提供的API（供其他BC调用）
 */
@RestController
@RequestMapping("/patient-info/api")
public class PatientInformationController {

    @GetMapping("/{patientId}")
    public PatientView getPatientInfo(@PathVariable Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException(patientId));

        // 将Patient聚合转换为View对象（仅暴露必要信息）
        return PatientView.from(patient);
    }
}

/**
 * 诊断报告BC使用Patient数据
 */
@Service
public class ReportGenerationService {
    private final RestTemplate restTemplate;

    public ReportDraft generateReportDraft(ExamId examId, Examination exam) {
        // 调用患者信息BC的API获取患者信息
        PatientView patientView = restTemplate.getForObject(
            "/patient-info/api/{patientId}",
            PatientView.class,
            exam.getPatientId()
        );

        // 使用患者信息生成报告
        return ReportDraft.builder()
            .patientAge(patientView.getAge())
            .patientGender(patientView.getGender())
            .medicalHistory(patientView.getMedicalHistory())
            .allergies(patientView.getAllergies())
            .build();
    }
}
```

---

## 总结

### DDD在本系统中的应用价值

| 方面 | 价值 |
|-----|------|
| **需求理解** | 医生、开发者、产品经理使用统一语言讨论需求，减少歧义 |
| **架构清晰** | 限界上下文划分清楚，各BC独立开发、测试、部署 |
| **代码质量** | 聚合设计强制实施业务规则，代码更容易维护 |
| **扩展性** | 新的医学算法可以通过新的Agent或新的BC集成，不影响现有代码 |
| **合规性** | 领域事件完整追踪所有数据变更，便于审计和合规验证 |
| **交接文档** | DDD的模型文档天生就是最好的系统设计文档 |

---

*本文档由 D-Site 研发团队与 Claude Code 协作生成*
*基于DDD方法论，适用于医疗影像PACS系统的架构设计和开发指导*
