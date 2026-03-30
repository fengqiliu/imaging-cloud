# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

D-Site V1.0 Cloud Film Management System (云胶片管理系统) - A medical imaging management platform for cloud-based medical image storage, viewing, diagnostic reporting, and sharing.

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21 / Spring Boot 3.2.1 / Spring Security / JWT |
| Data Access | MyBatis Plus 3.5.5 |
| Database | SQL Server 2019 |
| DICOM Processing | dcm4che 5.33.0 |
| PDF Generation | iText 7.2.5 |
| Storage | MinIO (optional) / Local filesystem |
| Frontend | Vue 2 / Element UI |

## Common Commands

### Backend

```bash
cd src

# Build entire project
mvn clean install

# Build single module with dependencies
mvn clean install -pl medical-admin -am

# Skip tests during build
mvn clean install -DskipTests

# Run all tests
mvn test

# Run a single test class (when tests exist)
mvn test -pl medical-admin -am -Dtest=CfPatientServiceTest

# Run application
cd medical-admin
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run serve

# Build production assets
npm run build

# Run lint
npm run lint
```

### Development Endpoints

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs (ReDoc) | http://localhost:8080/doc.html |
| Druid Console | http://localhost:8080/druid (admin/123456) |

### Database Initialization

```bash
# Run init script in SQL Server Management Studio or via sqlcmd
sqlcmd -S localhost -U sa -P Password123! -i src/database/init_db.sql
```

### Database Migrations

Migrations are in `src/database/migrations/` with naming convention `V{version}__{description}.sql`:

| File | Description |
|------|-------------|
| `V1.0.1__patient_information_context.sql` | Patient management tables |
| `V1.1.0__image_management_context.sql` | Image/DICOM management |
| `V1.2.0__diagnostic_report_context.sql` | Report tables |
| `V1.3.0__supporting_contexts.sql` | Dictionaries, configs, logs |
| `V1.4.0__migration_verification.sql` | Data verification |

Run migrations in order or use Flyway/Liquibase for automated migration.

## Architecture

### Module Dependencies

```
medical-admin (Spring Boot entry point; application bootstrap and global controllers)
  └── medical-system (business controllers and services)
        └── medical-framework (security, JWT, MyBatis Plus, shared infrastructure)
              └── medical-common (base domain classes, exceptions, utilities)
```

Repository layout:
- `src/` contains the Java/Spring Boot multi-module backend
- `frontend/` contains the Vue 2 + Element UI frontend app
- `prototype/` contains static HTML prototypes for UI reference, not the production frontend

### Package Structure

```
com.dsite.medical
├── admin/           # medical-admin: app entry, auth, static resource and viewer controllers
├── common/          # medical-common: shared domain objects, exceptions, utilities
├── framework/       # medical-framework: config, security, infrastructure wiring
├── system/          # medical-system: RBAC management (sys_* tables)
└── cloudfilm/       # medical-system: cloud film business (cf_* tables)
    ├── domain/entity/
    ├── mapper/
    ├── service/
    ├── controller/
    ├── dicom/       # DICOM parsing and processing
    ├── storage/     # Local/MinIO storage implementations
    └── report/      # PDF report generation
```

Note: `system/` and `cloudfilm/` packages live in the `medical-system` Maven module, not under `framework/`.

### Entity Naming Conventions

| Table Prefix | Module | Entity Prefix | Example |
|--------------|--------|---------------|---------|
| `sys_` | System management | `Sys` | SysUser, SysRole, SysDept |
| `cf_` | Cloud film business | `Cf` | CfPatient, CfExamination, CfImage |

### Key Business Entities

| Entity | Table | Description |
|--------|-------|-------------|
| CfPatient | cf_patient | Patient information |
| CfExamination | cf_examination | Examination orders with status flow |
| CfImage | cf_image | Medical images with DICOM metadata |
| CfDiagnosisReport | cf_diagnosis_report | Diagnostic reports |
| CfImageShare | cf_image_share | Share links with access codes |

### Controller Pattern

```java
@Tag(name = "模块名称")
@RestController
@RequestMapping("/cloudfilm/patient")
@RequiredArgsConstructor
public class CfPatientController {
    private final ICfPatientService patientService;

    @Operation(summary = "列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:list')")
    @GetMapping("/list")
    public PageResult<CfPatient> list(CfPatient query, PageQuery pageQuery) { ... }
}
```

### Service Pattern

Services use interface + implementation:
- Interface: `ICfPatientService` extends `IService<CfPatient>`
- Implementation: `CfPatientServiceImpl` with `@Service`

### User Roles

| Role | Chinese | Permissions |
|------|---------|-------------|
| Admin | 管理员 | System admin, user/role/dept management |
| Radiologist | 放射科医生 | Upload images, view DICOM, write/edit reports, export PDF |
| Clinician | 临床医生 | Create exam orders, view patient history, view images/reports |
| Patient | 患者 | View own reports, view own images, create/share image links |

### ID Generation

Business entities use snowflake IDs with type-specific prefixes:

| Prefix | Entity | Example |
|--------|--------|---------|
| `P` | CfPatient | P1928374650123 |
| `E` | CfExamination | E1928374650124 |
| `I` | CfImage | I1928374650125 |
| `R` | CfDiagnosisReport | R1928374650126 |
| `S` | CfImageShare | S1928374650127 |

System entities (SysUser, SysRole, etc.) use `IdType.AUTO` (database auto-increment).

### Default Test Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Super Admin |
| radiologist | admin123 | Radiologist |
| clinician | admin123 | Clinician |

### Examination Status Flow

```
待检查(PENDING) → 检查中(READING) → 已完成(COMPLETED) → 已出报告(REPORTED)
```

### Permission Naming

Format: `{module}:{entity}:{action}`
- `cloudfilm:patient:list`
- `cloudfilm:patient:query`
- `cloudfilm:patient:add`
- `cloudfilm:patient:edit`
- `cloudfilm:patient:remove`

## Configuration

Key settings in `medical-admin/src/main/resources/application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Application port |
| `jwt.expire` | 120 | Token expiry (minutes) |
| `jwt.refresh-expire` | 7d | Refresh token expiry |
| `storage.local.path` | ./uploads | Local storage path |
| `minio.enabled` | false | Use MinIO for storage |
| `spring.servlet.multipart.max-file-size` | 500MB | Max upload size |

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | Password123! | Database password |

## API Paths

| Module | Base Path |
|--------|-----------|
| Authentication | `/auth` |
| System Management | `/system/user`, `/system/role`, `/system/dept`, `/system/menu`, `/system/dict`, `/system/config` |
| Cloud Film | `/cloudfilm/patient`, `/cloudfilm/examination`, `/cloudfilm/image`, `/cloudfilm/report`, `/cloudfilm/share` |

## Development Notes

- Java 21 is required (uses modern Java features)
- Primary key generation: Business entities use `IdType.INPUT` with snowflake IDs, system entities use `IdType.AUTO`
- Logical delete: Entities have `del_flag` field with `@TableLogic` annotation
- All API responses use `AjaxResult` for single results, `PageResult<T>` for paginated lists
- Permission checks use `@PreAuthorize("@ss.hasPermi('...')")`
- Backend code lives under `src/`; frontend application code lives under `frontend/`
- `prototype/` is a static UI reference and should not be treated as the source of truth for the Vue app
- No backend test classes were found under `src/**/src/test/` during repository scan, so automated test coverage may be limited or not yet added

## UI Prototype

High-fidelity prototypes are in `prototype/` for frontend development reference:

```
prototype/
├── css/common.css          # Shared styles (CSS variables)
├── login.html              # Login page
├── index.html              # Combined prototype (all pages)
├── pages/
│   ├── cloudfilm/          # Business module pages
│   │   ├── dashboard.html, patient.html, examination.html
│   │   ├── appointment.html, queue.html, image.html
│   │   ├── viewer.html     # DICOM viewer
│   │   ├── report.html, report-review.html
│   │   ├── print.html, share.html
│   │   └── patient-portal.html  # Mobile patient view
│   ├── system/              # Admin pages
│   │   └── user.html, role.html, dept.html, dict.html, log.html
│   └── extended/            # Extended features
│       └── ai.html, statistics.html, settings.html
└── external-viewer.html    # External share access page
```

Each page is self-contained with shared CSS via `href="../../css/common.css"`.

## Documentation

| File | Description |
|------|-------------|
| `docx/架构图-UML和C4模型.md` | Architecture diagrams (C4 model) |
| `docx/DDD-快速参考指南.md` | DDD patterns quick reference |
| `docx/DDD-领域驱动设计分析.md` | DDD domain analysis |
| `diagram/` | PNG architecture diagrams |
| `prototype/` | UI prototypes (see above) |
