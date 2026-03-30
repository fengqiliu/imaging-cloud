-- =============================================
-- D-Site 云胶片管理系统 数据库初始化脚本
-- 数据库: medical_cloud_film
-- 创建日期: 2026-03-04
-- =============================================

-- 创建数据库
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'medical_cloud_film')
BEGIN
    CREATE DATABASE medical_cloud_film;
END
GO

USE medical_cloud_film;
GO

-- =============================================
-- 1. 系统管理相关表
-- =============================================

-- 用户表
IF OBJECT_ID('sys_user', 'U') IS NOT NULL DROP TABLE sys_user;
CREATE TABLE sys_user (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dept_id BIGINT NULL,
    user_name VARCHAR(30) NOT NULL,
    nick_name VARCHAR(30) NOT NULL,
    user_type VARCHAR(2) DEFAULT '00',
    email VARCHAR(50) NULL,
    phone VARCHAR(11) NULL,
    sex CHAR(1) DEFAULT '0',
    avatar VARCHAR(100) NULL,
    password VARCHAR(100) NULL,
    status CHAR(1) DEFAULT '0',
    login_ip VARCHAR(128) NULL,
    login_date DATETIME NULL,
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL,
    del_flag CHAR(1) DEFAULT '0'
);
CREATE INDEX idx_user_name ON sys_user(user_name);
CREATE INDEX idx_dept_id ON sys_user(dept_id);
CREATE INDEX idx_status ON sys_user(status);

-- 角色表
IF OBJECT_ID('sys_role', 'U') IS NOT NULL DROP TABLE sys_role;
CREATE TABLE sys_role (
    role_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    role_name VARCHAR(30) NOT NULL,
    role_key VARCHAR(100) NOT NULL,
    role_sort INT NOT NULL,
    data_scope VARCHAR(100) DEFAULT '1',
    menu_check_strictly BIT DEFAULT 1,
    dept_check_strictly BIT DEFAULT 1,
    status CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL
);
CREATE INDEX idx_role_key ON sys_role(role_key);

-- 用户和角色关联表
IF OBJECT_ID('sys_user_role', 'U') IS NOT NULL DROP TABLE sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色和菜单关联表
IF OBJECT_ID('sys_role_menu', 'U') IS NOT NULL DROP TABLE sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

-- 院区表 (部门表)
IF OBJECT_ID('sys_dept', 'U') IS NOT NULL DROP TABLE sys_dept;
CREATE TABLE sys_dept (
    dept_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(50) DEFAULT '0',
    dept_name VARCHAR(30) NOT NULL,
    order_num INT DEFAULT 0,
    leader VARCHAR(20) NULL,
    phone VARCHAR(11) NULL,
    email VARCHAR(50) NULL,
    status CHAR(1) DEFAULT '0',
    del_flag CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL
);
CREATE INDEX idx_parent_id ON sys_dept(parent_id);

-- 菜单权限表
IF OBJECT_ID('sys_menu', 'U') IS NOT NULL DROP TABLE sys_menu;
CREATE TABLE sys_menu (
    menu_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    order_num INT DEFAULT 0,
    path VARCHAR(200) DEFAULT '',
    component VARCHAR(255) NULL,
    menu_type CHAR(1) DEFAULT '',
    visible CHAR(1) DEFAULT '0',
    status CHAR(1) DEFAULT '0',
    perms VARCHAR(100) NULL,
    icon VARCHAR(100) DEFAULT '#',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL
);
CREATE INDEX idx_parent_id_menu ON sys_menu(parent_id);

-- 字典类型表
IF OBJECT_ID('sys_dict_type', 'U') IS NOT NULL DROP TABLE sys_dict_type;
CREATE TABLE sys_dict_type (
    dict_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_name VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL
);
CREATE UNIQUE INDEX idx_dict_type ON sys_dict_type(dict_type);

-- 字典数据表
IF OBJECT_ID('sys_dict_data', 'U') IS NOT NULL DROP TABLE sys_dict_data;
CREATE TABLE sys_dict_data (
    dict_code BIGINT IDENTITY(1,1) PRIMARY KEY,
    dict_sort INT DEFAULT 0,
    dict_label VARCHAR(100) DEFAULT '',
    dict_value VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    css_class VARCHAR(100) NULL,
    list_class VARCHAR(100) NULL,
    is_default CHAR(1) DEFAULT 'N',
    status CHAR(1) DEFAULT '0',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL
);
CREATE INDEX idx_dict_type_data ON sys_dict_data(dict_type);

-- 参数配置表
IF OBJECT_ID('sys_config', 'U') IS NOT NULL DROP TABLE sys_config;
CREATE TABLE sys_config (
    config_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    config_name VARCHAR(100) DEFAULT '',
    config_key VARCHAR(100) DEFAULT '',
    config_value VARCHAR(500) DEFAULT '',
    config_type CHAR(1) DEFAULT 'N',
    create_by VARCHAR(64) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_by VARCHAR(64) NULL,
    update_time DATETIME NULL,
    remark VARCHAR(500) NULL
);
CREATE UNIQUE INDEX idx_config_key ON sys_config(config_key);

-- 系统操作日志表
IF OBJECT_ID('sys_oper_log', 'U') IS NOT NULL DROP TABLE sys_oper_log;
CREATE TABLE sys_oper_log (
    oper_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(50) DEFAULT '',
    business_type INT DEFAULT 0,
    method VARCHAR(100) DEFAULT '',
    request_method VARCHAR(10) DEFAULT '',
    operator_type INT DEFAULT 0,
    oper_name VARCHAR(50) NULL,
    dept_name VARCHAR(50) NULL,
    oper_url VARCHAR(255) DEFAULT '',
    oper_ip VARCHAR(128) DEFAULT '',
    oper_location VARCHAR(255) DEFAULT '',
    oper_param VARCHAR(2000) NULL,
    json_result VARCHAR(2000) NULL,
    status INT DEFAULT 0,
    error_msg VARCHAR(2000) NULL,
    oper_time DATETIME DEFAULT GETDATE(),
    cost_time BIGINT DEFAULT 0
);
CREATE INDEX idx_oper_time ON sys_oper_log(oper_time);

-- 系统访问日志表
IF OBJECT_ID('sys_login_log', 'U') IS NOT NULL DROP TABLE sys_login_log;
CREATE TABLE sys_login_log (
    info_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_name VARCHAR(50) DEFAULT '',
    ipaddr VARCHAR(128) DEFAULT '',
    login_location VARCHAR(255) DEFAULT '',
    browser VARCHAR(50) DEFAULT '',
    os VARCHAR(50) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    msg VARCHAR(255) DEFAULT '',
    login_time DATETIME DEFAULT GETDATE()
);
CREATE INDEX idx_login_time ON sys_login_log(login_time);

-- =============================================
-- 2. 业务相关表
-- =============================================

-- 患者表
IF OBJECT_ID('cf_patient', 'U') IS NOT NULL DROP TABLE cf_patient;
CREATE TABLE cf_patient (
    patient_id VARCHAR(32) PRIMARY KEY,
    patient_name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NULL,
    birth_date DATE NULL,
    id_card VARCHAR(18) NULL,
    phone VARCHAR(11) NULL,
    address VARCHAR(255) NULL,
    allergy_history TEXT NULL,
    medical_history TEXT NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME NULL,
    del_flag CHAR(1) DEFAULT '0'
);
CREATE INDEX idx_patient_name ON cf_patient(patient_name);
CREATE INDEX idx_id_card ON cf_patient(id_card);
CREATE INDEX idx_phone ON cf_patient(phone);

-- 检查单表
IF OBJECT_ID('cf_examination', 'U') IS NOT NULL DROP TABLE cf_examination;
CREATE TABLE cf_examination (
    exam_id VARCHAR(32) PRIMARY KEY,
    patient_id VARCHAR(32) NOT NULL,
    exam_type VARCHAR(50) NULL,
    exam_reason TEXT NULL,
    exam_status VARCHAR(20) DEFAULT 'pending',
    apply_doctor VARCHAR(50) NULL,
    exam_date DATE NULL,
    report_date DATE NULL,
    dept_id BIGINT NULL,
    create_time DATETIME DEFAULT GETDATE(),
    update_time DATETIME NULL,
    del_flag CHAR(1) DEFAULT '0',
    FOREIGN KEY (patient_id) REFERENCES cf_patient(patient_id)
);
CREATE INDEX idx_patient_exam ON cf_examination(patient_id);
CREATE INDEX idx_exam_status ON cf_examination(exam_status);
CREATE INDEX idx_exam_date ON cf_examination(exam_date);

-- 影像表
IF OBJECT_ID('cf_image', 'U') IS NOT NULL DROP TABLE cf_image;
CREATE TABLE cf_image (
    image_id VARCHAR(32) PRIMARY KEY,
    exam_id VARCHAR(32) NOT NULL,
    dicom_uid VARCHAR(64) NULL,
    image_path VARCHAR(500) NOT NULL,
    thumbnail_path VARCHAR(500) NULL,
    series_number INT DEFAULT 0,
    image_number INT DEFAULT 0,
    modality VARCHAR(20) NULL,
    is_key_image CHAR(1) DEFAULT '0',
    create_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (exam_id) REFERENCES cf_examination(exam_id)
);
CREATE INDEX idx_exam_image ON cf_image(exam_id);
CREATE INDEX idx_dicom_uid ON cf_image(dicom_uid);

-- 诊断报告表
IF OBJECT_ID('cf_diagnosis_report', 'U') IS NOT NULL DROP TABLE cf_diagnosis_report;
CREATE TABLE cf_diagnosis_report (
    report_id VARCHAR(32) PRIMARY KEY,
    exam_id VARCHAR(32) NOT NULL,
    patient_id VARCHAR(32) NOT NULL,
    clinical_data TEXT NULL,
    exam_findings TEXT NULL,
    diagnosis TEXT NULL,
    report_status VARCHAR(20) DEFAULT 'draft',
    create_doctor VARCHAR(50) NULL,
    review_doctor VARCHAR(50) NULL,
    create_time DATETIME DEFAULT GETDATE(),
    review_time DATETIME NULL,
    update_time DATETIME NULL,
    del_flag CHAR(1) DEFAULT '0',
    FOREIGN KEY (exam_id) REFERENCES cf_examination(exam_id),
    FOREIGN KEY (patient_id) REFERENCES cf_patient(patient_id)
);
CREATE INDEX idx_exam_report ON cf_diagnosis_report(exam_id);
CREATE INDEX idx_patient_report ON cf_diagnosis_report(patient_id);

-- 影像分享表
IF OBJECT_ID('cf_image_share', 'U') IS NOT NULL DROP TABLE cf_image_share;
CREATE TABLE cf_image_share (
    share_id VARCHAR(32) PRIMARY KEY,
    share_no VARCHAR(32) NOT NULL UNIQUE,
    exam_id VARCHAR(32) NOT NULL,
    access_code VARCHAR(6) NOT NULL,
    expire_date DATETIME NOT NULL,
    view_count INT DEFAULT 0,
    download_count INT DEFAULT 0,
    allow_download CHAR(1) DEFAULT '0',
    create_time DATETIME DEFAULT GETDATE(),
    create_user VARCHAR(50) NULL,
    FOREIGN KEY (exam_id) REFERENCES cf_examination(exam_id)
);
CREATE INDEX idx_share_no ON cf_image_share(share_no);
CREATE INDEX idx_expire ON cf_image_share(expire_date);

-- AI解读表
IF OBJECT_ID('cf_ai_interpret', 'U') IS NOT NULL DROP TABLE cf_ai_interpret;
CREATE TABLE cf_ai_interpret (
    ai_interpret_id VARCHAR(32) PRIMARY KEY,
    report_id VARCHAR(32) NOT NULL,
    exam_id VARCHAR(32) NOT NULL,
    patient_id VARCHAR(32) NOT NULL,
    ai_content TEXT NULL,
    original_diagnosis TEXT NULL,
    model_provider VARCHAR(50) NULL,
    model_name VARCHAR(50) NULL,
    token_usage INT DEFAULT 0,
    create_time DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (report_id) REFERENCES cf_diagnosis_report(report_id),
    FOREIGN KEY (exam_id) REFERENCES cf_examination(exam_id),
    FOREIGN KEY (patient_id) REFERENCES cf_patient(patient_id)
);
CREATE INDEX idx_report_ai ON cf_ai_interpret(report_id);

-- =============================================
-- 3. 初始化数据
-- =============================================

-- 初始化院区数据
SET IDENTITY_INSERT sys_dept ON;
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag)
VALUES (100, 0, '0', '总院', 0, 'admin', '13800138000', 'admin@dsite.com', '0', '0');
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag)
VALUES (101, 100, '0,100', '放射科', 1, 'doctor', '13800138001', 'radiology@dsite.com', '0', '0');
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag)
VALUES (102, 100, '0,100', '骨科', 2, 'doctor2', '13800138002', 'orthopedics@dsite.com', '0', '0');
SET IDENTITY_INSERT sys_dept OFF;

-- 初始化用户数据 (密码: admin123)
SET IDENTITY_INSERT sys_user ON;
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phone, sex, avatar, password, status, create_by, create_time, del_flag)
VALUES (1, 100, 'admin', '管理员', '00', 'admin@dsite.com', '13800138000', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sW/ddyQV7YW', '0', 'admin', GETDATE(), '0');
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phone, sex, avatar, password, status, create_by, create_time, del_flag)
VALUES (2, 101, 'radiologist', '放射科医生', '00', 'doctor@dsite.com', '13800138001', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sW/ddyQV7YW', '0', 'admin', GETDATE(), '0');
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phone, sex, avatar, password, status, create_by, create_time, del_flag)
VALUES (3, 102, 'clinician', '临床医生', '00', 'clinician@dsite.com', '13800138002', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sW/ddyQV7YW', '0', 'admin', GETDATE(), '0');
SET IDENTITY_INSERT sys_user OFF;

-- 初始化角色数据
SET IDENTITY_INSERT sys_role ON;
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, create_by, create_time, remark)
VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', 'admin', GETDATE(), '超级管理员');
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, create_by, create_time, remark)
VALUES (2, '放射科医生', 'radiologist', 2, '2', 1, 1, '0', 'admin', GETDATE(), '放射科医生');
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, create_by, create_time, remark)
VALUES (3, '临床医生', 'clinician', 3, '2', 1, 1, '0', 'admin', GETDATE(), '临床医生');
SET IDENTITY_INSERT sys_role OFF;

-- 初始化用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO sys_user_role (user_id, role_id) VALUES (3, 3);

-- 初始化菜单数据
SET IDENTITY_INSERT sys_menu ON;
-- 系统管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (1, '系统管理', 0, 1, 'system', NULL, 'M', '0', '0', NULL, 'system', 'admin', GETDATE(), '系统管理目录');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', 'C', '0', '0', 'system:user:list', 'user', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (102, '院区管理', 1, 3, 'dept', 'system/dept/index', 'C', '0', '0', 'system:dept:list', 'tree-table', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (103, '菜单管理', 1, 4, 'menu', 'system/menu/index', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (104, '字典管理', 1, 5, 'dict', 'system/dict/index', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (105, '参数管理', 1, 6, 'config', 'system/config/index', 'C', '0', '0', 'system:config:list', 'edit', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (106, '日志管理', 1, 7, 'log', NULL, 'M', '0', '0', NULL, 'log', 'admin', GETDATE(), '日志管理目录');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (107, '操作日志', 106, 1, 'operlog', 'monitor/operlog/index', 'C', '0', '0', 'monitor:operlog:list', 'log', 'admin', GETDATE(), '');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (108, '登录日志', 106, 2, 'logininfor', 'monitor/logininfor/index', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', GETDATE(), '');

-- 业务管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2, '患者管理', 0, 2, 'patient', 'cloudfilm/patient/index', 'C', '0', '0', 'cloudfilm:patient:list', 'user', 'admin', GETDATE(), '患者管理');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (3, '检查管理', 0, 3, 'examination', 'cloudfilm/examination/index', 'C', '0', '0', 'cloudfilm:examination:list', 'documentation', 'admin', GETDATE(), '检查管理');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (4, '影像管理', 0, 4, 'image', 'cloudfilm/image/index', 'C', '0', '0', 'cloudfilm:image:list', 'film', 'admin', GETDATE(), '影像管理');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (5, '报告管理', 0, 5, 'report', 'cloudfilm/report/index', 'C', '0', '0', 'cloudfilm:report:list', 'form', 'admin', GETDATE(), '报告管理');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (6, '分享管理', 0, 6, 'share', 'cloudfilm/share/index', 'C', '0', '0', 'cloudfilm:share:list', 'share', 'admin', GETDATE(), '分享管理');
SET IDENTITY_INSERT sys_menu OFF;

-- 初始化角色菜单关联 (管理员拥有所有菜单)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 100);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 101);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 103);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 104);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 106);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 107);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 108);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 5);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 6);

-- 放射科医生角色菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 5);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 6);

-- 临床医生角色菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (3, 5);

-- 初始化字典数据
SET IDENTITY_INSERT sys_dict_type ON;
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', GETDATE(), '用户性别列表');
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (2, '检查状态', 'exam_status', '0', 'admin', GETDATE(), '检查单状态');
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (3, '检查类型', 'exam_type', '0', 'admin', GETDATE(), '检查类型列表');
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (4, '报告状态', 'report_status', '0', 'admin', GETDATE(), '报告状态');
SET IDENTITY_INSERT sys_dict_type OFF;

SET IDENTITY_INSERT sys_dict_data ON;
-- 性别
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (1, 1, '男', '0', 'sys_user_sex', '', 'default', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (2, 2, '女', '1', 'sys_user_sex', '', 'default', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (3, 3, '未知', '2', 'sys_user_sex', '', 'default', 'N', '0', 'admin', GETDATE());
-- 检查状态
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (4, 1, '待检查', 'pending', 'exam_status', '', 'warning', 'Y', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (5, 2, '检查中', 'in_progress', 'exam_status', '', 'warning', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (6, 3, '已完成', 'completed', 'exam_status', '', 'success', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (7, 4, '已出报告', 'reported', 'exam_status', '', 'success', 'N', '0', 'admin', GETDATE());
-- 检查类型
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (8, 1, 'CT', 'CT', 'exam_type', '', 'default', 'Y', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (9, 2, 'MRI', 'MRI', 'exam_type', '', 'default', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (10, 3, 'X光', 'XRAY', 'exam_type', '', 'default', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (11, 4, '超声', 'US', 'exam_type', '', 'default', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (12, 5, '内镜', 'ENDO', 'exam_type', '', 'default', 'N', '0', 'admin', GETDATE());
-- 报告状态
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (13, 1, '草稿', 'draft', 'report_status', '', 'info', 'Y', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (14, 2, '待审核', 'pending', 'report_status', '', 'warning', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (15, 3, '已通过', 'approved', 'report_status', '', 'success', 'N', '0', 'admin', GETDATE());
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (16, 4, '已驳回', 'rejected', 'report_status', '', 'danger', 'N', '0', 'admin', GETDATE());
SET IDENTITY_INSERT sys_dict_data OFF;

-- 初始化参数配置
SET IDENTITY_INSERT sys_config ON;
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, remark)
VALUES (1, '主框架页-默认皮肤', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', GETDATE(), '默认皮肤');
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, remark)
VALUES (2, '用户管理-初始密码', 'sys.user.initPassword', 'admin123', 'Y', 'admin', GETDATE(), '初始化密码');
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, remark)
VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', GETDATE(), '侧边栏主题');
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, remark)
VALUES (4, '分享有效期默认天数', 'share.expire.days', '7', 'Y', 'admin', GETDATE', '分享链接默认有效期天数');
SET IDENTITY_INSERT sys_config OFF;

PRINT '数据库初始化完成！';
