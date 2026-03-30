# D-Site V1.0 云胶片管理系统 产品需求文档 (PRD)

## 文档信息

| 项目 | 内容 |
|------|------|
| 产品名称 | D-Site 云胶片管理系统 |
| 版本号 | V1.0 |
| 文档状态 | 正式发布 |
| 创建日期 | 2026-03-04 |
| 技术框架 | RuoYi 若依开发框架 |

---

## 1. 产品概述

### 1.1 产品定位

D-Site 云胶片管理系统是一套基于Web的医学影像管理平台，旨在为医疗机构提供完整的医学影像存储、查看、诊断报告生成及分享的一体化解决方案。系统采用B/S架构，支持医学影像的云端化管理和跨平台访问。

### 1.2 产品目标

- 实现医学影像的数字化集中管理
- 提供高效的在线阅片体验
- 支持诊断报告的在线生成与审核
- 实现患者影像的安全分享功能
- 提升医疗机构信息化水平和工作效率

### 1.3 目标用户

| 用户角色 | 描述 | 主要使用场景 |
|----------|------|--------------|
| 管理员 | 系统运维人员 | 用户管理、权限配置、系统设置 |
| 放射科医生 | 影像诊断医生 | 阅片、标注、诊断报告撰写 |
| 临床医生 | 门诊/住院医生 | 查看患者影像、出具检查申请 |
| 患者 | 受检人员 | 查看个人检查报告、影像分享 |

---

## 2. 技术架构

### 2.1 技术选型

| 层级 | 技术栈 |
|------|--------|
| 前端 | Vue 2 + Element UI |
| 后端 | Java 21 + Spring Boot + Spring Security |
| 数据访问 | MyBatis Plus |
| 认证授权 | JWT |
| 数据库 | SQL Server 2019 |
| 开发框架 | RuoYi 若依 |
| AI引擎 | OpenAI / Anthropic / 阿里云通义千问 / 百度文心一言

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────────┐
│                    前端层 (Vue2 + Element UI)            │
├─────────────────────────────────────────────────────────┤
│                    后端层 (Spring Boot)                  │
│  ┌──────────┬──────────┬──────────┬──────────┐         │
│  │患者管理  │检查管理  │影像管理  │报告管理  │         │
│  │          │          │          │          │         │
│  │分享管理  │系统管理  │DICOM处理 │AI解读    │         │
│  └──────────┴──────────┴──────────┴──────────┘         │
├─────────────────────────────────────────────────────────┤
│                    数据层 (SQL Server 2019)              │
├─────────────────────────────────────────────────────────┤
│                    存储层 (MinIO/本地存储)               │
├─────────────────────────────────────────────────────────┤
│              AI服务层 (LLM大模型)                        │
│    OpenAI / Claude / 通义千问 / 文心一言                 │
└─────────────────────────────────────────────────────────┘
```

---

## 3. 功能模块

### 3.1 患者管理模块

#### 3.1.1 功能描述
管理系统中的患者信息，支持患者的增删改查操作。

#### 3.1.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 患者列表 | 展示所有患者信息，支持分页查询 | P0 |
| 患者新增 | 添加新患者基本信息 | P0 |
| 患者编辑 | 修改患者基本信息 | P0 |
| 患者删除 | 删除患者记录 | P1 |
| 患者搜索 | 按姓名、身份证号、手机号搜索 | P0 |
| 患者详情 | 查看患者完整信息及历史检查 | P1 |

#### 3.1.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| patient_id | VARCHAR | 患者唯一标识 |
| patient_name | VARCHAR | 患者姓名 |
| gender | VARCHAR | 性别 |
| birth_date | DATE | 出生日期 |
| id_card | VARCHAR | 身份证号 |
| phone | VARCHAR | 联系电话 |
| address | VARCHAR | 住址 |
| allergy_history | TEXT | 过敏史 |
| medical_history | TEXT | 病史 |

---

### 3.2 检查管理模块

#### 3.2.1 功能描述
管理患者的检查单，跟踪检查状态的全流程。

#### 3.2.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检查单列表 | 展示所有检查单，支持多条件筛选 | P0 |
| 检查单新增 | 创建新的检查单 | P0 |
| 检查单编辑 | 修改检查单信息 | P0 |
| 检查单删除 | 删除检查单 | P1 |
| 检查状态管理 | 跟踪状态：待检查→检查中→已完成→已出报告 | P0 |
| 检查类型管理 | X光/CT/MRI/超声/内镜等检查类型 | P1 |
| 检查历史 | 查看患者历史检查记录 | P1 |

#### 3.2.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| exam_id | VARCHAR | 检查单唯一标识 |
| patient_id | VARCHAR | 关联患者ID |
| exam_type | VARCHAR | 检查类型 |
| exam_reason | TEXT | 检查原因/临床诊断 |
| exam_status | VARCHAR | 检查状态 |
| apply_doctor | VARCHAR | 申请医生 |
| exam_date | DATE | 检查日期 |
| report_date | DATE | 出报告日期 |

#### 3.2.4 检查状态流转

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  待检查   │───→│  检查中  │───→│ 已完成   │───→│ 已出报告  │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
```

---

### 3.3 影像管理模块

#### 3.3.1 功能描述
管理医学影像数据，支持DICOM格式影像的处理和存储。

#### 3.3.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 影像列表 | 展示检查关联的所有影像 | P0 |
| 影像上传 | 支持DICOM文件批量上传 | P0 |
| 影像浏览 | 在线查看医学影像 | P0 |
| 影像下载 | 下载原始影像文件 | P1 |
| 影像删除 | 删除指定影像 | P1 |
| 缩略图生成 | 自动生成影像缩略图 | P0 |
| 关键影像标记 | 标记重要影像 | P1 |
| 影像检索 | 按检查号、日期检索影像 | P1 |

#### 3.3.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| image_id | VARCHAR | 影像唯一标识 |
| exam_id | VARCHAR | 关联检查ID |
| dicom_uid | VARCHAR | DICOM UID |
| image_path | VARCHAR | 存储路径 |
| thumbnail_path | VARCHAR | 缩略图路径 |
| series_number | INT | 系列号 |
| image_number | INT | 图像号 |
| modality | VARCHAR | 模态(CT/MR/US等) |
| is_key_image | INT | 是否关键影像(0/1) |

---

### 3.4 诊断报告模块

#### 3.4.1 功能描述
支持医生在线撰写、审核诊断报告，支持PDF导出。

#### 3.4.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 报告列表 | 展示所有诊断报告 | P0 |
| 报告新增 | 创建诊断报告 | P0 |
| 报告编辑 | 修改报告内容 | P0 |
| 报告预览 | 在线预览报告内容 | P0 |
| 报告导出 | 导出PDF格式报告 | P0 |
| 报告审核 | 审核流程(撰写→审核→通过) | P1 |
| 报告打印 | 打印诊断报告 | P1 |
| 报告模板 | 常用报告模板管理 | P2 |

#### 3.4.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| report_id | VARCHAR | 报告唯一标识 |
| exam_id | VARCHAR | 关联检查ID |
| patient_id | VARCHAR | 关联患者ID |
| clinical_data | TEXT | 临床资料 |
| exam_findings | TEXT | 检查所见 |
| diagnosis | TEXT | 诊断意见 |
| report_status | VARCHAR | 报告状态 |
| create_doctor | VARCHAR | 撰写医生 |
| review_doctor | VARCHAR | 审核医生 |
| create_time | DATETIME | 创建时间 |
| review_time | DATETIME | 审核时间 |

#### 3.4.4 报告状态流转

```
┌────────────┐    ┌────────────┐    ┌────────────┐
│   草稿      │───→│   待审核    │───→│   已通过    │
└────────────┘    └────────────┘    └────────────┘
                         │
                         │ 驳回
                         ↓
                   ┌────────────┐
                   │   已驳回    │───→ 返回草稿状态
                   └────────────┘
```

**状态说明**:
- **草稿**: 医生撰写中，可随时修改
- **待审核**: 提交审核，等待上级医生审核
- **已驳回**: 审核不通过，需修改后重新提交
- **已通过**: 审核通过，可发布给患者

---

### 3.5 分享管理模块

#### 3.5.1 功能描述
支持患者影像的外部分享，通过链接和提取码实现安全分享。

#### 3.5.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 创建分享 | 生成带有提取码的分享链接 | P0 |
| 分享浏览 | 外部用户通过链接查看影像 | P0 |
| 分享下载 | 允许下载影像文件 | P1 |
| 分享有效期 | 设置链接有效期(1-30天) | P0 |
| 分享统计 | 查看次数、下载次数统计 | P1 |
| 取消分享 | 手动终止分享链接 | P0 |
| 分享记录 | 查看历史分享列表 | P1 |

**业务规则**:
- 提取码为6位数字，错误次数限制为5次，超过后锁定1小时
- 分享链接支持多次访问，但下载次数可配置（默认不限制）
- 单次分享最多可选择50张影像
- 分享链接过期后自动失效，无法再次访问
- 管理员可强制取消任何分享链接

#### 3.5.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| share_id | VARCHAR | 分享唯一标识 |
| share_no | VARCHAR | 分享编号(URL使用) |
| exam_id | VARCHAR | 关联检查ID |
| access_code | VARCHAR | 提取码(6位数字) |
| expire_date | DATETIME | 过期时间 |
| view_count | INT | 查看次数 |
| download_count | INT | 下载次数 |
| allow_download | INT | 是否允许下载(0/1) |
| max_download_count | INT | 最大下载次数(0表示不限制) |
| failed_attempts | INT | 提取码错误次数 |
| locked_until | DATETIME | 锁定截止时间 |
| create_time | DATETIME | 创建时间 |

**说明**: 业务实体使用 VARCHAR 类型的雪花ID（带类型前缀如 S1928374650127），便于日志追踪和问题排查；系统管理实体使用 BIGINT 自增ID，保持若依框架原有设计风格。

---

### 3.6 系统管理模块

#### 3.6.1 功能描述
系统基础管理和运维功能，包括用户、角色、部门、菜单、字典、日志、参数等管理。

**术语说明**:
- **部门/科室**: 对应 `sys_dept` 表，表示医院的组织架构（如放射科、内科等）
- **检查单**: 对应 `cf_examination` 表，表示单次检查申请记录

#### 3.6.2 用户管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 用户列表 | 展示所有系统用户，支持分页查询 | P0 |
| 用户新增 | 添加新用户 | P0 |
| 用户编辑 | 修改用户信息 | P0 |
| 用户删除 | 删除用户 | P0 |
| 重置密码 | 重置用户登录密码 | P1 |
| 修改密码 | 用户修改自己密码 | P0 |
| 用户状态 | 启用/禁用用户账号 | P1 |
| 分配角色 | 为用户分配角色 | P0 |
| 分配部门 | 分配用户所属部门 | P0 |

#### 3.6.3 角色管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 角色列表 | 展示所有角色 | P0 |
| 角色新增 | 创建新角色 | P0 |
| 角色编辑 | 修改角色信息 | P0 |
| 角色删除 | 删除角色 | P1 |
| 分配权限 | 配置角色菜单权限 | P0 |
| 数据权限 | 配置角色数据范围 | P1 |
| 角色状态 | 启用/禁用角色 | P1 |

#### 3.6.4 部门管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 部门列表 | 展示所有部门信息 | P0 |
| 部门新增 | 添加新部门 | P0 |
| 部门编辑 | 修改部门信息 | P0 |
| 部门删除 | 删除部门 | P1 |
| 部门配置 | 部门相关业务配置 | P1 |

#### 3.6.5 菜单管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 菜单列表 | 树形展示所有菜单 | P0 |
| 菜单新增 | 添加菜单/按钮 | P0 |
| 菜单编辑 | 修改菜单信息 | P0 |
| 菜单删除 | 删除菜单 | P1 |
| 菜单排序 | 调整菜单顺序 | P1 |
| 菜单图标 | 配置菜单图标 | P1 |

#### 3.6.6 字典管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 字典类型 | 字典类型列表 | P0 |
| 字典类型新增 | 添加字典类型 | P0 |
| 字典类型编辑 | 修改字典类型 | P0 |
| 字典类型删除 | 删除字典类型 | P1 |
| 字典数据 | 字典数据列表 | P0 |
| 字典数据新增 | 添加字典数据 | P0 |
| 字典数据编辑 | 修改字典数据 | P0 |
| 字典数据删除 | 删除字典数据 | P1 |

#### 3.6.7 日志管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 操作日志 | 记录用户操作行为 | P0 |
| 登录日志 | 记录用户登录信息 | P0 |
| 日志查询 | 按条件查询日志 | P0 |
| 日志删除 | 删除历史日志 | P1 |
| 日志导出 | 导出日志报表 | P2 |

#### 3.6.8 参数管理

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 参数列表 | 展示所有系统参数 | P0 |
| 参数新增 | 添加新参数 | P0 |
| 参数编辑 | 修改参数值 | P0 |
| 参数删除 | 删除参数 | P1 |
| 参数分类 | 参数分组管理 | P2 |

#### 3.6.9 数据字段 - 用户表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| user_id | BIGINT | 用户ID |
| dept_id | BIGINT | 院区ID |
| user_name | VARCHAR | 用户名称 |
| nick_name | VARCHAR | 用户昵称 |
| user_type | VARCHAR | 用户类型 |
| email | VARCHAR | 用户邮箱 |
| phone | VARCHAR | 手机号码 |
| sex | CHAR | 用户性别 |
| avatar | VARCHAR | 头像地址 |
| password | VARCHAR | 密码 |
| status | CHAR | 帐号状态(0正常/1停用) |
| login_ip | VARCHAR | 最后登录IP |
| login_date | DATETIME | 最后登录时间 |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.6.10 数据字段 - 角色表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| role_id | BIGINT | 角色ID |
| role_name | VARCHAR | 角色名称 |
| role_key | VARCHAR | 角色权限字符串 |
| role_sort | INT | 显示顺序 |
| data_scope | VARCHAR | 数据范围 |
| menu_check_strictly | INT | 菜单树选择项是否关联 |
| dept_check_strictly | INT | 部门树选择项是否关联 |
| status | CHAR | 角色状态(0正常/1停用) |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.6.9 数据字段 - 部门表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| dept_id | BIGINT | 部门ID |
| parent_id | BIGINT | 父部门ID |
| ancestors | VARCHAR | 祖级列表 |
| dept_name | VARCHAR | 部门名称 |
| order_num | INT | 显示顺序 |
| leader | VARCHAR | 负责人 |
| phone | VARCHAR | 联系电话 |
| email | VARCHAR | 邮箱 |
| status | CHAR | 部门状态(0正常/1停用) |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.6.12 数据字段 - 菜单表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| menu_id | BIGINT | 菜单ID |
| menu_name | VARCHAR | 菜单名称 |
| parent_id | BIGINT | 父菜单ID |
| order_num | INT | 显示顺序 |
| path | VARCHAR | 路由地址 |
| component | VARCHAR | 组件路径 |
| menu_type | CHAR | 菜单类型(M目录/C菜单/F按钮) |
| visible | CHAR | 菜单状态(0显示/1隐藏) |
| status | CHAR | 菜单状态(0正常/1停用) |
| perms | VARCHAR | 权限标识 |
| icon | VARCHAR | 菜单图标 |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.6.13 数据字段 - 字典类型表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| dict_id | BIGINT | 字典主键 |
| dict_name | VARCHAR | 字典名称 |
| dict_type | VARCHAR | 字典类型 |
| status | CHAR | 状态(0正常/1停用) |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR | 备注 |

#### 3.6.14 数据字段 - 字典数据表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| dict_code | BIGINT | 字典编码 |
| dict_sort | INT | 字典排序 |
| dict_label | VARCHAR | 字典标签 |
| dict_value | VARCHAR | 字典键值 |
| dict_type | VARCHAR | 字典类型 |
| css_class | VARCHAR | 样式属性 |
| list_class | VARCHAR | 表格回显样式 |
| is_default | CHAR | 是否默认(Y是/N否) |
| status | CHAR | 状态(0正常/1停用) |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR | 备注 |

---

### 3.7 API接口管理模块

#### 3.7.1 功能描述
基于OpenAPI 3.0规范，实现系统API接口的统一管理、文档生成、在线测试及调用监控。支持Swagger UI在线文档展示，便于开发人员快速查阅和测试接口。

#### 3.7.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| API文档展示 | Swagger UI在线文档 | P0 |
| API分组管理 | 按模块/业务分组API | P0 |
| API详情 | 查看接口详细说明 | P0 |
| 在线测试 | 在线调用API进行测试 | P0 |
| API导出 | 导出OpenAPI JSON/YAML | P1 |
| API统计 | 接口调用次数统计 | P1 |
| API版本管理 | 多版本API文档管理 | P2 |
| API导入 | 导入现有OpenAPI定义 | P2 |

#### 3.7.3 OpenAPI集成

| 功能 | 说明 |
|------|------|
| Swagger 2.0 | 兼容旧版本规范 |
| OpenAPI 3.0 | 当前主版本 |
| ReDoc | 文档展示主题 |
| API认证 | 支持JWT/API Key/OAuth2 |

#### 3.7.4 数据字段 - API分组表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| group_id | BIGINT | 分组ID |
| group_name | VARCHAR | 分组名称 |
| group_key | VARCHAR | 分组标识 |
| description | VARCHAR | 分组描述 |
| order_num | INT | 显示顺序 |
| status | CHAR | 状态(0正常/1停用) |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.7.5 数据字段 - API文档表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| api_id | BIGINT | API ID |
| group_id | BIGINT | 分组ID |
| api_name | VARCHAR | 接口名称 |
| api_path | VARCHAR | 请求路径 |
| api_method | VARCHAR | 请求方法(GET/POST/PUT/DELETE) |
| api_version | VARCHAR | 版本号(v1/v2) |
| summary | VARCHAR | 接口摘要 |
| description | TEXT | 详细说明 |
| tags | VARCHAR | 标签(逗号分隔) |
| deprecated | CHAR | 是否废弃(0否/1是) |
| operation_id | VARCHAR | 操作ID |
| create_by | VARCHAR | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR | 更新者 |
| update_time | DATETIME | 更新时间 |

#### 3.7.6 数据字段 - API参数表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| param_id | BIGINT | 参数ID |
| api_id | BIGINT | API ID |
| param_name | VARCHAR | 参数名称 |
| param_in | VARCHAR | 参数位置(path/query/header/body) |
| param_type | VARCHAR | 参数类型 |
| required | CHAR | 是否必填(0否/1是) |
| description | VARCHAR | 参数说明 |
| default_value | VARCHAR | 默认值 |
| schema_ref | VARCHAR | Schema引用 |

#### 3.7.7 数据字段 - API响应表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| response_id | BIGINT | 响应ID |
| api_id | BIGINT | API ID |
| status_code | INT | HTTP状态码 |
| description | VARCHAR | 响应描述 |
| schema_ref | VARCHAR | Schema引用 |
| example | TEXT | 响应示例 |

---

### 3.8 AI解读模块

#### 3.8.1 功能描述
基于LLM大语言模型，将专业的医学诊断报告转化为浅显易懂的文字说明，帮助患者理解自己的检查结果。

**重要声明**:
- AI解读仅作为健康科普和辅助理解工具，不作为正式诊断依据
- 所有AI解读内容必须经过执业医师审核后才能展示给患者
- 系统将在显著位置展示医疗免责声明
- 患者首次使用需明确同意《AI解读服务协议》

**医疗免责声明**:
> ⚠️ 本AI解读仅供参考，不能替代医生的专业诊断。如有疑问或不适，请及时就医咨询专业医生。本系统不对AI解读内容的准确性、完整性承担医疗责任。

#### 3.8.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| AI解读生成 | 一键生成检查报告的通俗解读 | P0 |
| 解读历史 | 查看历史生成的AI解读记录 | P1 |
| 解读刷新 | 重新生成AI解读内容 | P1 |
| 解读复制 | 一键复制解读内容 | P1 |
| 分享解读 | 将AI解读一并分享给患者 | P1 |
| 医生审核 | 医生审核AI解读内容 | P0 |
| 患者同意 | 患者同意AI解读服务协议 | P0 |

#### 3.8.3 数据字段

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| ai_interpret_id | VARCHAR | AI解读唯一标识 |
| report_id | VARCHAR | 关联报告ID |
| exam_id | VARCHAR | 关联检查ID |
| patient_id | VARCHAR | 关联患者ID |
| ai_content | TEXT | AI解读内容(通俗易懂版) |
| original_diagnosis | TEXT | 原始诊断内容 |
| model_provider | VARCHAR | LLM提供商 |
| model_name | VARCHAR | 模型名称 |
| token_usage | INT | 消耗Token数 |
| review_status | VARCHAR | 审核状态(pending/approved/rejected) |
| review_doctor | VARCHAR | 审核医生 |
| review_time | DATETIME | 审核时间 |
| patient_agreed | INT | 患者是否同意协议(0/1) |
| create_time | DATETIME | 生成时间 |

#### 3.8.4 AI解读流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 选择检查报告 │───→│ 调用LLM API │───→│ 医生审核    │───→│ 患者查看    │
└─────────────┘    │ 构造Prompt  │    │ 审核通过    │    │ 同意协议    │
                   └─────────────┘    └─────────────┘    └─────────────┘
                          │                   │
                          ↓                   ↓
                   ┌─────────────┐    ┌─────────────┐
                   │ 格式化输出  │    │ 驳回重新生成│
                   │ 通俗解读内容│    └─────────────┘
                   └─────────────┘
```

#### 3.8.5 Prompt设计原则

- 使用通俗易懂的语言，避免专业医学术语
- 检查结果正常时，给予积极正面的反馈
- 检查结果异常时，建议进一步就医而非直接诊断
- 提供健康建议和生活注意事项
- 明确告知仅为辅助参考，不替代医生诊断

#### 3.8.6 AI解读示例

**原始诊断**: 右肺上叶见磨玻璃密度结节，直径约8mm，建议定期复查

**AI解读**:
> 您的右上肺部发现了一个小结节，大小约8毫米（相当于一颗黄豆大小）。
>
> 从影像学来看，这个结节呈现"磨玻璃"密度，医生建议定期复查观察变化。
>
> 📌 **您需要知道的：**
> - 绝大多数肺部小结节都是良性的，不必过度担忧
> - 建议3-6个月后复查CT，观察结节是否有变化
> - 戒烟很重要，避免接触二手烟和空气污染
> - 保持规律作息，增强身体免疫力
>
> ⚠️ **温馨提示：** 此解读仅供参考，具体诊疗请遵循主治医生建议。如有不适，请及时就医。

#### 3.8.7 数据字段 - LLM配置表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| config_id | INT | 配置ID |
| provider | VARCHAR | LLM提供商 |
| model_name | VARCHAR | 模型名称 |
| api_key | VARCHAR | API密钥(AES-256-GCM加密存储) |
| api_url | VARCHAR | API地址 |
| encryption_key_id | VARCHAR | 加密密钥ID(引用KMS) |
| is_enabled | INT | 是否启用(0/1) |
| is_default | INT | 是否默认(0/1) |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**安全说明**: API密钥使用AES-256-GCM算法加密存储，加密密钥通过密钥管理服务(KMS)管理，应用程序仅持有密钥ID，不直接存储加密密钥。

#### 3.8.8 数据字段 - Prompt模板表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| template_id | INT | 模板ID |
| template_name | VARCHAR | 模板名称 |
| template_content | TEXT | Prompt模板内容 |
| template_type | VARCHAR | 模板类型(report/general) |
| is_default | INT | 是否默认(0/1) |
| create_time | DATETIME | 创建时间 |

---

### 3.9 运营统计模块

#### 3.9.1 功能描述
对云胶片系统的运营数据进行统计分析，包括用户操作行为、报告数量、影像数量及胶片使用趋势等，为运营决策提供数据支持。

#### 3.9.2 功能列表

| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 操作日志统计 | 统计用户操作记录数据量 | P0 |
| 报告数量统计 | 统计每日报告数量 | P0 |
| 影像数量统计 | 统计每日影像数量 | P0 |
| 胶片使用趋势 | 图表展示月度使用趋势 | P0 |
| 设备类型分析 | 按CT/MRI/X光等分类统计 | P0 |
| 数据导出 | 导出统计报表(Excel/PDF) | P1 |
| 自定义时间 | 自定义统计时间范围 | P0 |
| 图表可视化 | 折线图/柱状图/饼图展示 | P0 |

#### 3.9.3 统计维度说明

##### 3.9.3.1 用户操作统计

| 操作类型 | 说明 |
|----------|------|
| 查看报告 | 患者/医生查看诊断报告 |
| 调阅影像 | 在线查看医学影像 |
| 下载影像 | 下载原始DICOM影像 |
| 查看图文报告 | 查看图文结合报告 |
| 下载图文报告 | 下载PDF/图片格式报告 |
| 分享检查 | 创建影像分享链接 |
| AI解读 | 生成AI智能解读 |

##### 3.9.3.2 设备类型分类

| 设备类型 | 说明 |
|----------|------|
| CR | 计算机X线摄影 |
| DR | 数字X线摄影 |
| CT | 电子计算机断层扫描 |
| MRI | 磁共振成像 |
| US | 超声检查 |
| DX | 乳腺钼靶 |
| XA | 血管造影 |
| RF | 消化道造影 |
| SC | 超声心动图 |
| OT | 其他类型设备 |

##### 3.9.3.3 胶片使用率计算

```
胶片使用率 = (已使用胶片数 / 分配胶片总数) × 100%
已使用胶片数 = 查看报告次数 + 调阅影像次数 + 下载次数
```

#### 3.9.4 数据字段 - 操作日志表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| oper_id | BIGINT | 日志ID |
| title | VARCHAR | 模块标题 |
| business_type | INT | 业务类型 |
| method | VARCHAR | 方法名称 |
| request_method | VARCHAR | 请求方式 |
| operator_type | INT | 操作类型(0患者/1医生/2管理员) |
| oper_name | VARCHAR | 操作人员 |
| dept_name | VARCHAR | 部门名称 |
| oper_url | VARCHAR | 请求URL |
| oper_ip | VARCHAR | 主机地址 |
| oper_location | VARCHAR | 操作地点 |
| oper_param | VARCHAR | 请求参数 |
| json_result | VARCHAR | 返回结果 |
| status | INT | 操作状态(0正常/1异常) |
| error_msg | TEXT | 错误消息 |
| oper_time | DATETIME | 操作时间 |
| cost_time | BIGINT | 消耗时间(毫秒) |

#### 3.9.5 数据字段 - 统计汇总表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| stat_id | BIGINT | 统计ID |
| stat_date | DATE | 统计日期 |
| stat_type | VARCHAR | 统计类型(report/image/oper) |
| device_type | VARCHAR | 设备类型 |
| count | INT | 数量 |
| create_time | DATETIME | 创建时间 |

#### 3.9.6 数据字段 - 胶片使用记录表

| 字段名 | 字段类型 | 说明 |
|--------|----------|------|
| record_id | BIGINT | 记录ID |
| patient_id | VARCHAR | 患者ID |
| exam_id | VARCHAR | 检查ID |
| operate_type | VARCHAR | 操作类型 |
| operate_source | VARCHAR | 操作来源(App/Web/小程序) |
| device_type | VARCHAR | 设备类型 |
| ip_address | VARCHAR | IP地址 |
| location | VARCHAR | 地理位置 |
| create_time | DATETIME | 操作时间 |

#### 3.9.7 统计报表设计

##### 3.9.7.1 用户操作统计报表

| 统计项 | 指标 |
|--------|------|
| 报告查看次数 | 今日/本周/本月/本年 |
| 影像调阅次数 | 今日/本周/本月/本年 |
| 影像下载次数 | 今日/本周/本月/本年 |
| 报告下载次数 | 今日/本周/本月/本年 |
| 分享次数 | 今日/本周/本月/本年 |
| AI解读次数 | 今日/本周/本月/本年 |

##### 3.9.7.2 报告数量统计报表

| 统计维度 | 分类方式 |
|----------|----------|
| 按日期 | 每日/每周/每月/每年 |
| 按设备类型 | CT/MRI/DR/US等 |
| 按检查类型 | X光/CT/MRI/超声等 |
| 按院区 | 各院区分开统计 |

##### 3.9.7.3 影像数量统计报表

| 统计维度 | 分类方式 |
|----------|----------|
| 按日期 | 每日/每周/每月/每年 |
| 按设备类型 | CT/MRI/DR/US等 |
| 按序列 | 每个检查的序列数 |
| 按图像数 | 每个检查的图像数 |

##### 3.9.7.4 胶片使用趋势图

| 图表类型 | 展示内容 |
|----------|----------|
| 折线图 | 每月使用量趋势 |
| 柱状图 | 每日使用量对比 |
| 饼图 | 各类操作占比 |
| 面积图 | 累计使用量趋势 |

#### 3.9.8 接口设计

##### 3.9.8.1 操作统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 操作统计 | GET | /system/stat/oper | 获取操作统计数据 |
| 操作趋势 | GET | /system/stat/oper/trend | 获取操作趋势数据 |
| 操作排名 | GET | /system/stat/oper/rank | 获取操作排行榜 |

##### 3.9.8.2 报告统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 报告统计 | GET | /system/stat/report | 获取报告统计数据 |
| 报告趋势 | GET | /system/stat/report/trend | 获取报告趋势数据 |
| 报告设备分布 | GET | /system/stat/report/device | 按设备类型统计 |

##### 3.9.8.3 影像统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 影像统计 | GET | /system/stat/image | 获取影像统计数据 |
| 影像趋势 | GET | /system/stat/image/trend | 获取影像趋势数据 |
| 影像设备分布 | GET | /system/stat/image/device | 按设备类型统计 |

##### 3.9.8.4 使用率统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 使用率概览 | GET | /system/stat/usage | 获取使用率概览 |
| 使用率趋势 | GET | /system/stat/usage/trend | 获取使用率趋势 |
| 使用率导出 | GET | /system/stat/usage/export | 导出使用率报表 |

---

## 4. 业务流程

### 4.1 核心业务流程

#### 4.1.1 检查与报告流程

```
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│ 患者登记 │───→│ 开检查单 │───→│ 影像采集 │───→│ 阅片诊断 │───→│ 出报告  │
└─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘
     │              │              │              │              │
     ▼              ▼              ▼              ▼              ▼
  创建患者      检查单状态      上传DICOM      撰写诊断      PDF导出
  信息         更新为"待检查"   影像文件       报告          报告
                                                                  │
                                                                  ▼
                                                           ┌─────────────┐
                                                      ┌────│  AI解读生成  │
                                                      │    └─────────────┘
                                                      │          │
                                                      ▼          ▼
                                                   ┌────────┐ ┌────────┐
                                                   │通俗易懂 │ │分享给  │
                                                   │解读内容 │ │患者    │
                                                   └────────┘ └────────┘
```

#### 4.1.2 影像分享流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 选择检查影像 │───→│ 设置有效期  │───→│ 生成分享链接│
└─────────────┘    │ 提取码      │    └─────────────┘
                   │ 下载权限    │          │
                   └─────────────┘          ▼
                                      ┌─────────────┐
                                      │ 患者获取链接│
                                      │ 输入提取码  │
                                      └─────────────┘
                                              │
                                              ▼
                                      ┌─────────────┐
                                      │影像│ 在线查看
                                      │ 或下载      │
                                      └─────────────┘
```

---

## 5. 接口设计

### 5.1 患者管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 患者列表 | GET | /cloudfilm/patient/list | 分页查询患者 |
| 患者详情 | GET | /cloudfilm/patient/{id} | 获取患者详情 |
| 新增患者 | POST | /cloudfilm/patient | 创建患者 |
| 修改患者 | PUT | /cloudfilm/patient | 更新患者 |
| 删除患者 | DELETE | /cloudfilm/patient/{ids} | 删除患者 |

### 5.2 检查管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 检查单列表 | GET | /cloudfilm/examination/list | 分页查询检查单 |
| 检查单详情 | GET | /cloudfilm/examination/{id} | 获取检查单详情 |
| 新增检查单 | POST | /cloudfilm/examination | 创建检查单 |
| 修改检查单 | PUT | /cloudfilm/examination | 更新检查单 |
| 删除检查单 | DELETE | /cloudfilm/examination/{ids} | 删除检查单 |

### 5.3 影像管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 影像列表 | GET | /cloudfilm/image/list | 分页查询影像 |
| 检查影像 | GET | /cloudfilm/image/exam/{examId} | 获取检查关联影像 |
| 上传影像 | POST | /cloudfilm/image | 上传影像文件 |
| 删除影像 | DELETE | /cloudfilm/image/{ids} | 删除影像 |

### 5.4 诊断报告接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 报告列表 | GET | /cloudfilm/report/list | 分页查询报告 |
| 报告详情 | GET | /cloudfilm/report/{id} | 获取报告详情 |
| 创建报告 | POST | /cloudfilm/report | 创建报告 |
| 修改报告 | PUT | /cloudfilm/report | 更新报告 |
| 审核报告 | PUT | /cloudfilm/report/review/{id} | 审核报告 |

### 5.5 分享管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建分享 | POST | /cloudfilm/share | 创建分享 |
| 分享信息 | GET | /cloudfilm/share/info/{shareNo} | 获取分享信息 |
| 验证提取码 | POST | /cloudfilm/share/verify | 验证提取码 |
| 取消分享 | DELETE | /cloudfilm/share/{shareNo} | 取消分享 |

### 5.6 AI解读接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 生成解读 | POST | /cloudfilm/ai/interpret | 生成AI解读 |
| 解读详情 | GET | /cloudfilm/ai/interpret/{reportId} | 获取解读详情 |
| 解读历史 | GET | /cloudfilm/ai/interpret/history/{patientId} | 获取解读历史 |
| 刷新解读 | PUT | /cloudfilm/ai/interpret/refresh/{reportId} | 重新生成解读 |
| LLM配置 | GET/POST | /cloudfilm/ai/config | LLM模型配置 |

### 5.7 LLM集成说明

#### 5.7.1 支持的LLM提供商

| 提供商 | 模型 | 说明 |
|--------|------|------|
| OpenAI | GPT-4o / GPT-4o-mini | 需API Key |
| Anthropic | Claude-3.5-Sonnet | 需API Key |
| 阿里云 | Qwen-Max | 需API Key |
| 百度智能云 | ERNIE-4.0-8K | 需API Key |
| 本地部署 | LLaMA / Qwen | 支持私有化部署 |

#### 5.7.2 系统Prompt模板

```
您是一位经验丰富的家庭医生，需要将医学检查报告用通俗易懂的语言解释给患者。
要求：
1. 使用简单直白的语言，避免专业术语（必须使用时需解释）
2. 检查结果正常时，给予积极反馈
3. 异常结果不直接诊断，而是建议进一步就医
4. 提供实用的健康建议
5. 明确告知仅为参考，不替代医生诊断
6. 保持温暖、鼓励的语气
7. 结构清晰，适当使用Emoji
```

### 5.8 系统管理接口

#### 5.7.1 用户管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户列表 | GET | /system/user/list | 分页查询用户 |
| 用户详情 | GET | /system/user/{userId} | 获取用户详情 |
| 新增用户 | POST | /system/user | 创建用户 |
| 修改用户 | PUT | /system/user | 更新用户 |
| 删除用户 | DELETE | /system/user/{userIds} | 删除用户 |
| 重置密码 | PUT | /system/user/resetPwd | 重置用户密码 |
| 修改密码 | PUT | /system/user/profile/updatePwd | 修改个人密码 |
| 分配角色 | PUT | /system/user/authRole | 分配用户角色 |
| 导出用户 | GET | /system/user/export | 导出用户数据 |

#### 5.7.2 角色管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 角色列表 | GET | /system/role/list | 查询角色列表 |
| 角色详情 | GET | /system/role/{roleId} | 获取角色详情 |
| 新增角色 | POST | /system/role | 创建角色 |
| 修改角色 | PUT | /system/role | 更新角色 |
| 删除角色 | DELETE | /system/role/{roleIds} | 删除角色 |
| 分配权限 | PUT | /system/role/authDataScope | 分配数据权限 |
| 导出角色 | GET | /system/role/export | 导出角色数据 |

#### 5.7.3 院区管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 院区列表 | GET | /system/dept/list | 查询院区列表 |
| 院区详情 | GET | /system/dept/{deptId} | 获取院区详情 |
| 新增院区 | POST | /system/dept | 创建院区 |
| 修改院区 | PUT | /system/dept | 更新院区 |
| 删除院区 | DELETE | /system/dept/{deptId} | 删除院区 |
| 院区树 | GET | /system/dept/treeselect | 获取院区树 |

#### 5.7.4 菜单管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 菜单列表 | GET | /system/menu/list | 查询菜单列表 |
| 菜单详情 | GET | /system/menu/{menuId} | 获取菜单详情 |
| 新增菜单 | POST | /system/menu | 创建菜单 |
| 修改菜单 | PUT | /system/menu | 更新菜单 |
| 删除菜单 | DELETE | /system/menu/{menuId} | 删除菜单 |
| 菜单树 | GET | /system/menu/treeselect | 获取菜单树 |
| 角色菜单 | GET | /system/menu/roleMenuTreeselect | 获取角色菜单树 |

#### 5.7.5 字典管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 字典类型列表 | GET | /system/dict/type/list | 查询字典类型 |
| 字典类型详情 | GET | /system/dict/type/{dictId} | 获取字典类型 |
| 新增字典类型 | POST | /system/dict/type | 创建字典类型 |
| 修改字典类型 | PUT | /system/dict/type | 更新字典类型 |
| 删除字典类型 | DELETE | /system/dict/type/{dictIds} | 删除字典类型 |
| 字典数据列表 | GET | /system/dict/data/list | 查询字典数据 |
| 字典数据详情 | GET | /system/dict/data/{dictCode} | 获取字典数据 |
| 新增字典数据 | POST | /system/dict/data | 创建字典数据 |
| 修改字典数据 | PUT | /system/dict/data | 更新字典数据 |
| 删除字典数据 | DELETE | /system/dict/data/{dictCodes} | 删除字典数据 |

#### 5.7.6 日志管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 操作日志列表 | GET | /monitor/operlog/list | 查询操作日志 |
| 操作日志详情 | GET | /monitor/operlog/{operId} | 获取日志详情 |
| 删除操作日志 | DELETE | /monitor/operlog/{operIds} | 删除日志 |
| 清空操作日志 | DELETE | /monitor/operlog/clean | 清空日志 |
| 登录日志列表 | GET | /monitor/logininfor/list | 查询登录日志 |
| 删除登录日志 | DELETE | /monitor/logininfor/{infoIds} | 删除登录日志 |
| 清空登录日志 | DELETE | /monitor/logininfor/clean | 清空登录日志 |

#### 5.7.7 参数管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 参数列表 | GET | /system/config/list | 查询参数列表 |
| 参数详情 | GET | /system/config/{configId} | 获取参数详情 |
| 新增参数 | POST | /system/config | 创建参数 |
| 修改参数 | PUT | /system/config | 更新参数 |
| 删除参数 | DELETE | /system/config/{configIds} | 删除参数 |

### 5.9 API接口管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| Swagger资源 | GET | /swagger-resources | 获取Swagger资源 |
| API文档 | GET | /v3/api-docs | 获取OpenAPI 3.0文档 |
| API分组列表 | GET | /system/api/group/list | 获取API分组列表 |
| API分组新增 | POST | /system/api/group | 创建API分组 |
| API分组修改 | PUT | /system/api/group | 更新API分组 |
| API分组删除 | DELETE | /system/api/group/{groupId} | 删除API分组 |
| API列表 | GET | /system/api/list | 获取API列表 |
| API详情 | GET | /system/api/{apiId} | 获取API详情 |
| API新增 | POST | /system/api | 创建API |
| API修改 | PUT | /system/api | 更新API |
| API删除 | DELETE | /system/api/{apiId} | 删除API |
| API统计 | GET | /system/api/statistics | API调用统计 |
| 导出API | GET | /system/api/export | 导出API定义 |

### 5.10 OpenAPI集成接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| OpenAPI JSON | GET | /v3/api-docs/{group} | 获取指定分组API |
| Swagger UI | GET | /swagger-ui.html | Swagger UI首页 |
| ReDoc | GET | /doc.html | ReDoc文档页 |
| API聚合文档 | GET | /swagger-resources | 聚合API资源 |

### 5.11 运营统计接口

#### 5.11.1 操作统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 操作统计概览 | GET | /system/stat/oper/overview | 获取操作统计概览 |
| 操作明细 | GET | /system/stat/oper/list | 分页查询操作记录 |
| 操作趋势 | GET | /system/stat/oper/trend | 获取趋势数据(折线图) |
| 操作排行 | GET | /system/stat/oper/rank | 获取排行榜 |
| 操作导出 | GET | /system/stat/oper/export | 导出操作统计 |

#### 5.11.2 报告统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 报告统计概览 | GET | /system/stat/report/overview | 获取报告统计概览 |
| 报告趋势 | GET | /system/stat/report/trend | 获取报告趋势数据 |
| 报告设备分布 | GET | /system/stat/report/device | 按设备类型分布 |
| 报告导出 | GET | /system/stat/report/export | 导出报告统计 |

#### 5.11.3 影像统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 影像统计概览 | GET | /system/stat/image/overview | 获取影像统计概览 |
| 影像趋势 | GET | /system/stat/image/trend | 获取影像趋势数据 |
| 影像设备分布 | GET | /system/stat/image/device | 按设备类型分布 |
| 影像导出 | GET | /system/stat/image/export | 导出影像统计 |

#### 5.11.4 使用率统计接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 使用率概览 | GET | /system/stat/usage/overview | 获取使用率概览 |
| 使用率趋势 | GET | /system/stat/usage/trend | 获取月度趋势(图表) |
| 使用率排行 | GET | /system/stat/usage/rank | 使用率排行榜 |
| 使用率导出 | GET | /system/stat/usage/export | 导出使用率报表 |

---

## 6. 安全设计

### 6.1 认证授权

- **认证方式**: JWT Token
- **授权模型**: RBAC (基于角色的访问控制)
- **密码加密**: MD5/SHA256
- **Session管理**: Redis存储

### 6.2 安全措施

| 措施 | 说明 |
|------|------|
| 接口鉴权 | 所有API需携带有效Token |
| 权限控制 | 按钮级别权限控制 |
| SQL注入 | MyBatis参数绑定防止 |
| XSS防护 | 输入过滤与转义 |
| CORS配置 | 跨域请求控制 |
| 操作日志 | 记录关键操作行为 |

### 6.3 数据安全

- 分享链接一次性验证机制
- 提取码错误次数限制
- 分享链接过期自动失效
- 敏感数据传输HTTPS加密

---

## 7. 性能要求

| 指标 | 要求 |
|------|------|
| 系统响应时间 | < 3秒 |
| 影像加载时间 | < 5秒 |
| 并发用户数 | ≥ 100 |
| 系统可用性 | ≥ 99.5% |
| 文件上传限制 | 单个文件 ≤ 500MB |

---

## 8. 部署要求

### 8.1 环境要求

| 环境 | 配置 |
|------|------|
| JDK | 21 |
| 数据库 | SQL Server 2019 |
| Web服务器 | Nginx |
| 存储 | MinIO 或本地存储 |
| 缓存 | Redis (可选) |

### 8.2 端口规划

| 服务 | 端口 |
|------|------|
| 后端服务 | 8080 |
| 前端服务 | 80 |
| API文档 | 8080/doc.html |

---

## 9. 版本规划

### V1.0 版本功能范围

- [x] 患者管理
- [x] 检查管理
- [x] 影像管理 (DICOM支持)
- [x] 诊断报告
- [x] 影像分享
- [x] 系统管理 (基础)
- [x] 用户权限
- [x] 报告AI解读

### 后续版本规划 (V1.1+)

- AI辅助诊断集成
- 移动端支持
- 报告模板库
- 影像归档管理
- 与HIS/PACS系统对接

---

## 10. 附录

### 10.1 术语表

| 术语 | 说明 |
|------|------|
| DICOM | 医学数字影像和通信标准 |
| 云胶片 | 基于云计算的医学影像服务 |
| PACS | 医学影像存档与传输系统 |
| RIS | 放射科信息系统 |
| UID | 唯一标识符 |

### 10.2 参考资料

- RuoYi-Vue3 框架文档
- DICOM 3.0 标准
- HL7 FHIR 标准
