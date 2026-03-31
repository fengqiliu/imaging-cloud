# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

D-Site V1.0 Cloud Film Management System (云胶片管理系统) - A medical imaging management platform for cloud-based medical image storage, viewing, diagnostic reporting, and sharing.

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21 / Spring Boot 3.2.1 / Spring Security / JWT (jjwt 0.12.3) |
| Data Access | MyBatis Plus 3.5.5 / Druid connection pool |
| Database | SQL Server 2019 |
| DICOM Processing | dcm4che 5.33.0 |
| PDF Generation | iText 7.2.5 |
| Storage | MinIO 8.5.9 (optional) / Local filesystem |
| Frontend | Vue 2.7.14 / Element UI 2.15.14 / Vuex 3 / Vue Router 3 |

## Common Commands

### Backend

```bash
cd src

# Build entire project
mvn clean install -DskipTests

# Build single module with all dependencies
mvn clean install -pl medical-admin -am -DskipTests

# Run application
cd medical-admin
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> **Note:** No test classes exist in the codebase yet. `mvn test` will succeed vacuously.

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server (runs on port 3000, proxies API to localhost:8080)
npm run serve

# Build production assets
npm run build

# Run lint
npm run lint
```

### Development Endpoints

| Service | URL |
|---------|-----|
| Frontend Dev Server | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| API Docs (knife4j) | http://localhost:8080/doc.html |
| Druid Console | http://localhost:8080/druid (admin/123456) |

### Database Initialization

```bash
# Full init (tables + seed data)
sqlcmd -S localhost -U sa -P Password123! -i src/database/init_db.sql

# Incremental migrations (run in order)
# src/database/migrations/V1.0.1__patient_information_context.sql
# src/database/migrations/V1.1.0__image_management_context.sql
# src/database/migrations/V1.2.0__diagnostic_report_context.sql
# src/database/migrations/V1.3.0__supporting_contexts.sql
# src/database/migrations/V1.4.0__migration_verification.sql
```

## Architecture

### Module Dependency Chain

```
medical-admin       → Spring Boot entry point; auth controllers, DICOM viewer endpoint, static resources
  └── medical-system  → All business logic: cloudfilm + RBAC system controllers/services
        └── medical-framework  → Security, JWT, MyBatis Plus config, shared infrastructure
              └── medical-common  → Base entities, exceptions, AjaxResult, utilities
```

### Package Structure

Both `system/` and `cloudfilm/` live inside the `medical-system` Maven module:

```
com.dsite.medical
├── admin/        # App entry, AuthController, DicomViewerController, StaticResourceController
├── common/       # AjaxResult, PageQuery, base exceptions, utils
├── framework/    # SecurityConfig, JwtToken, JwtAuthenticationFilter, WebConfig, MyBatis Plus wiring
├── system/       # RBAC: SysUser, SysRole, SysDept, SysMenu, SysDict, SysConfig, SysOperLog
└── cloudfilm/    # Business: patient, examination, image, report, share
    ├── dicom/    # DicomParser (dcm4che), DicomStorageService
    ├── storage/  # StorageService interface → LocalStorageService / MinioStorageService
    └── report/   # PDF generation with iText
```

### Frontend Structure

```
frontend/src/
├── api/          # axios modules: auth.js, patient.js, examination.js, image.js,
│                 #   report.js, share.js, system.js
├── store/        # Vuex: modules/auth.js (JWT + user state), modules/app.js (UI state)
├── router/       # Single flat route file; all routes defined here
├── views/        # Page components organized by module
│   ├── cloudfilm/  # patient, examination, image, report, share, viewer
│   ├── system/     # user, role, dept, dict, log
│   ├── ai/         # AiAnalysis.vue — AI-assisted diagnosis module
│   └── statistics/ # Statistics.vue
└── components/   # Shared components
```

`vue.config.js` proxies `/api`, `/auth`, `/cloudfilm`, `/system`, `/monitor`, `/uploads` to `http://localhost:8080`.

### Key Business Entities

| Entity | Table | Description |
|--------|-------|-------------|
| CfPatient | cf_patient | Patient information |
| CfExamination | cf_examination | Examination orders with status flow |
| CfImage | cf_image | Medical images with DICOM metadata |
| CfDiagnosisReport | cf_diagnosis_report | Diagnostic reports |
| CfImageShare | cf_image_share | Share links with access codes |

### Controller / Service Pattern

```java
@Tag(name = "模块名称")
@RestController
@RequestMapping("/cloudfilm/patient")
@RequiredArgsConstructor
public class CfPatientController {
    private final ICfPatientService patientService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public AjaxResult list(CfPatient query, PageQuery pageQuery) { ... }
}
```

Services use interface + implementation: `ICfPatientService extends IService<CfPatient>` → `CfPatientServiceImpl`.

All API responses use `AjaxResult` (in `medical-common`). `PageQuery` carries pagination parameters via MyBatis Plus's `IPage`.

> **Note:** `@PreAuthorize("@ss.hasPermi('...')")` annotations appear in the codebase but the `PermissionService` (`@ss` bean) is not yet implemented. Permission checks are currently unenforced.

### ID Generation

Business entities (`cf_*`) use `IdType.INPUT` with snowflake IDs and type-specific prefixes (`P`, `E`, `I`, `R`, `S`). System entities (`sys_*`) use `IdType.AUTO` (database auto-increment).

### Examination Status Flow

```
待检查(PENDING) → 检查中(READING) → 已完成(COMPLETED) → 已出报告(REPORTED)
```

### Logical Delete

All entities have a `del_flag` field with `@TableLogic` — MyBatis Plus handles soft-delete automatically.

### Default Test Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Super Admin |
| radiologist | admin123 | Radiologist |
| clinician | admin123 | Clinician |

## Configuration

Key settings in `medical-admin/src/main/resources/application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Application port |
| `jwt.expire` | 120 | Token expiry (minutes) |
| `jwt.refresh-expire` | 7d | Refresh token expiry |
| `storage.local.path` | ./uploads | Local storage path |
| `minio.enabled` | false | Enable MinIO; falls back to local |
| `spring.servlet.multipart.max-file-size` | 500MB | Max upload size |

Environment variable `DB_PASSWORD` (default: `Password123!`) is used for the database connection.

## API Paths

| Module | Base Path |
|--------|-----------|
| Authentication | `/auth` |
| System Management | `/system/user`, `/system/role`, `/system/dept`, `/system/menu`, `/system/dict`, `/system/config` |
| Cloud Film | `/cloudfilm/patient`, `/cloudfilm/examination`, `/cloudfilm/image`, `/cloudfilm/report`, `/cloudfilm/share` |

## UI Prototype

Static HTML prototypes in `prototype/` are design references only — not the production frontend. Each `.html` file is self-contained and uses `prototype/css/common.css` for shared styles.

## Documentation

| File | Description |
|------|-------------|
| `docx/架构图-UML和C4模型.md` | Architecture diagrams (C4 model) |
| `docx/DDD-快速参考指南.md` | DDD patterns quick reference |
| `docx/DDD-领域驱动设计分析.md` | DDD domain analysis |
| `diagram/` | PNG architecture diagrams |
